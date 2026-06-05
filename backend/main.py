"""
AI 广告信息流后端 — FastAPI
运行：uvicorn main:app --host 0.0.0.0 --port 8000 --reload
"""

from fastapi import FastAPI, Query, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from data import RAW_ADS, get_ai_data

app = FastAPI(title="AI Ad Feed API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

VALID_CHANNELS = {"featured", "ecom", "local"}


# ──────────────── 响应模型 ────────────────

class AdItemOut(BaseModel):
    id: str
    type: str
    title: str
    brand: str
    summary: str
    tags: list[str]
    cover: str
    videoUrl: str | None = None
    images: list[str] = []
    description: str


class FeedResponse(BaseModel):
    page: int
    hasMore: bool
    items: list[AdItemOut]


class SearchRequest(BaseModel):
    query: str


class SearchResult(BaseModel):
    id: str
    reason: str


class SearchResponse(BaseModel):
    results: list[SearchResult]


# ──────────────── 工具函数 ────────────────

def build_ad_out(raw: dict) -> AdItemOut:
    summary, tags = get_ai_data(raw["id"])
    return AdItemOut(
        id=raw["id"],
        type=raw["type"],
        title=raw["title"],
        brand=raw.get("brand", ""),
        summary=summary,
        tags=tags,
        cover=raw["cover"],
        videoUrl=raw.get("video_url"),
        images=raw.get("images", []),
        description=raw.get("description", ""),
    )


# ──────────────── 接口 ────────────────

@app.get("/feed", response_model=FeedResponse)
def get_feed(
    channel: str = Query(..., description="频道：featured | ecom | local"),
    page: int = Query(1, ge=1),
    size: int = Query(20, ge=1, le=50),
):
    if channel not in VALID_CHANNELS:
        raise HTTPException(status_code=400, detail=f"未知频道：{channel}")

    channel_ads = [a for a in RAW_ADS if a["channel"] == channel]
    total = len(channel_ads)
    start = (page - 1) * size
    end = start + size
    page_items = channel_ads[start:end]

    return FeedResponse(
        page=page,
        hasMore=end < total,
        items=[build_ad_out(ad) for ad in page_items],
    )


@app.post("/search", response_model=SearchResponse)
async def search_ads(req: SearchRequest):
    """
    用 Qwen 做对话式搜索：把全部广告信息拼入 prompt，
    让模型返回匹配的 ad id + 理由（JSON）。
    """
    import os, json
    from openai import AsyncOpenAI

    api_key = os.getenv("DASHSCOPE_API_KEY")
    if not api_key:
        raise HTTPException(status_code=503, detail="DASHSCOPE_API_KEY 未配置")

    client = AsyncOpenAI(
        api_key=api_key,
        base_url="https://dashscope.aliyuncs.com/compatible-mode/v1",
    )

    ads_summary = "\n".join(
        f"[{a['id']}] {a['title']} 品牌:{a.get('brand','')} 描述:{a.get('description','')}"
        for a in RAW_ADS
    )

    prompt = f"""你是一个广告搜索助手。用户查询："{req.query}"
以下是广告列表：
{ads_summary}

请返回与查询最相关的广告 id 列表，按相关度降序排列，最多返回 5 条。
以 JSON 数组格式输出，每项包含 id 和 reason 字段，不要有其他内容。
例：[{{"id":"ad_ecom_01","reason":"平价运动鞋，符合学生党需求"}}]"""

    try:
        response = await client.chat.completions.create(
            model="qwen-plus",
            messages=[{"role": "user", "content": prompt}],
            temperature=0.2,
        )
        raw = response.choices[0].message.content.strip()
        # 提取 JSON 数组（防止模型输出多余文字）
        start = raw.find("[")
        end = raw.rfind("]") + 1
        results = json.loads(raw[start:end])
        return SearchResponse(results=[SearchResult(**r) for r in results])
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"AI 搜索失败：{e}")


@app.get("/health")
def health():
    return {"status": "ok"}
