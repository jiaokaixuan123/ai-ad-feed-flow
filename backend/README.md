# AI 广告信息流 — 后端

## 快速启动

```bash
cd backend
pip install -r requirements.txt

# 启动服务（模拟器通过 10.0.2.2:8000 访问）
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

## AI 摘要/标签预生成

```bash
# 需要通义千问 API Key（阿里云百炼控制台获取）
export DASHSCOPE_API_KEY=sk-xxx

python generate_ai.py
# 生成结果自动写入 ai_cache.json 和 data.py 的 AI_CACHE
```

## 接口文档

启动后访问 http://localhost:8000/docs

| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | `/feed` | 分页获取广告列表 |
| POST | `/search` | 对话式搜索（需配置 API Key）|
| GET  | `/health` | 健康检查 |

## 客户端切换

客户端默认用 Mock 数据（`AppGraph.kt` 中 `USE_MOCK = true`）。

后端启动后，将其改为 `false` 即可切换为真实网络请求：

```kotlin
// AppGraph.kt
private const val USE_MOCK = false
```

模拟器中后端地址为 `http://10.0.2.2:8000/`（已在 `AppGraph.kt` 预置）。
