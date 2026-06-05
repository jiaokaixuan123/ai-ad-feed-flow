"""
AI 摘要 + 结构化标签离线预生成脚本
用法：DASHSCOPE_API_KEY=xxx python generate_ai.py

成功后将结果写入 data.py 的 AI_CACHE，同时备份到 ai_cache.json。
"""

import os
import json
import time
from openai import OpenAI
from data import RAW_ADS

CACHE_FILE = "ai_cache.json"
MAX_RETRIES = 3
RETRY_DELAY = 2  # 秒

# JSON schema 期望的输出结构
EXPECTED_KEYS = {"summary", "tags"}
TAG_CATEGORIES = {"品类", "风格", "受众", "场景"}


def validate_result(result: dict) -> bool:
    """校验 AI 输出是否符合预期 schema。"""
    if not isinstance(result, dict):
        return False
    if not EXPECTED_KEYS.issubset(result.keys()):
        return False
    if not isinstance(result["summary"], str) or not result["summary"].strip():
        return False
    if not isinstance(result["tags"], list) or len(result["tags"]) == 0:
        return False
    return True


def generate_for_ad(client: OpenAI, ad: dict) -> dict | None:
    """调用 Qwen 为单条广告生成摘要和标签，失败重试。"""
    prompt = f"""请为以下广告生成摘要和结构化标签，严格以 JSON 格式输出，不要有其他内容。

广告信息：
- 标题：{ad["title"]}
- 品牌：{ad.get("brand", "")}
- 描述：{ad.get("description", "")}
- 频道：{ad["channel"]}

输出格式（仅 JSON，无 markdown 代码块）：
{{
  "summary": "一句话介绍这条广告的核心亮点（20-40字）",
  "tags": ["品类标签", "风格标签", "受众标签", "场景标签"]
}}

要求：
1. summary 简洁有力，突出产品价值
2. tags 包含 4 个标签，分别覆盖品类/风格/受众/场景
3. 只输出 JSON，不要有任何解释或代码块标记"""

    for attempt in range(1, MAX_RETRIES + 1):
        try:
            response = client.chat.completions.create(
                model="qwen-plus",
                messages=[{"role": "user", "content": prompt}],
                temperature=0.3,
            )
            raw = response.choices[0].message.content.strip()

            # 去掉可能的 markdown 代码块
            if raw.startswith("```"):
                raw = raw.split("```")[1]
                if raw.startswith("json"):
                    raw = raw[4:]

            result = json.loads(raw.strip())
            if validate_result(result):
                return result
            print(f"  [第{attempt}次] schema 校验失败，输出：{raw[:100]}")
        except json.JSONDecodeError as e:
            print(f"  [第{attempt}次] JSON 解析失败：{e}")
        except Exception as e:
            print(f"  [第{attempt}次] API 调用失败：{e}")

        if attempt < MAX_RETRIES:
            time.sleep(RETRY_DELAY)

    return None


def main():
    api_key = os.getenv("DASHSCOPE_API_KEY")
    if not api_key:
        raise SystemExit("错误：请先设置 DASHSCOPE_API_KEY 环境变量")

    client = OpenAI(
        api_key=api_key,
        base_url="https://dashscope.aliyuncs.com/compatible-mode/v1",
    )

    # 读取已有缓存（幂等，不重复调用）
    cache: dict[str, dict] = {}
    if os.path.exists(CACHE_FILE):
        with open(CACHE_FILE, "r", encoding="utf-8") as f:
            cache = json.load(f)
        print(f"已加载缓存：{len(cache)} 条")

    total = len(RAW_ADS)
    for i, ad in enumerate(RAW_ADS, 1):
        ad_id = ad["id"]
        if ad_id in cache:
            print(f"[{i}/{total}] {ad_id} — 已缓存，跳过")
            continue

        print(f"[{i}/{total}] 正在生成：{ad_id} ({ad['title'][:20]}...)")
        result = generate_for_ad(client, ad)

        if result:
            cache[ad_id] = result
            print(f"  ✓ summary: {result['summary'][:30]}... tags: {result['tags']}")
            # 每条成功后立即写入，防止中断丢失进度
            with open(CACHE_FILE, "w", encoding="utf-8") as f:
                json.dump(cache, f, ensure_ascii=False, indent=2)
        else:
            print(f"  ✗ {ad_id} 生成失败，将使用降级标题")

        time.sleep(0.5)  # 避免触发限流

    # 将缓存写回 data.py（直接修改 AI_CACHE 字典内容）
    inject_cache_into_data_module(cache)
    print(f"\n完成！成功生成 {len(cache)}/{total} 条，缓存文件：{CACHE_FILE}")


def inject_cache_into_data_module(cache: dict):
    """将 cache 内容注入到 data.py 的 AI_CACHE 中。"""
    with open("data.py", "r", encoding="utf-8") as f:
        content = f.read()

    cache_str = json.dumps(cache, ensure_ascii=False, indent=4)
    new_block = f"AI_CACHE: dict[str, dict] = {cache_str}"

    import re
    pattern = r"AI_CACHE: dict\[str, dict\] = \{[^}]*\}"
    if re.search(pattern, content, re.DOTALL):
        content = re.sub(pattern, new_block, content, flags=re.DOTALL)
    else:
        content = content.replace(
            "AI_CACHE: dict[str, dict] = {}",
            new_block
        )

    with open("data.py", "w", encoding="utf-8") as f:
        f.write(content)
    print("data.py 中的 AI_CACHE 已更新")


if __name__ == "__main__":
    main()
