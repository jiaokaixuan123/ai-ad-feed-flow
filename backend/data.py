"""静态广告数据 + AI 预生成的摘要/标签缓存。"""

from typing import Optional

# 原始广告数据（由乙维护）
RAW_ADS: list[dict] = [
    # --- 精选频道 ---
    {"id": "ad_featured_01", "channel": "featured", "type": "big_image",
     "title": "Flow Studio Pro 无线降噪耳机", "brand": "Flow Studio 1",
     "cover": "https://picsum.photos/seed/fs01/800/450",
     "description": "沉浸式降噪，全天候续航，带你进入专注状态。"},
    {"id": "ad_featured_02", "channel": "featured", "type": "small_image",
     "title": "Flow Studio 轻量跑鞋 2024", "brand": "Flow Studio 2",
     "cover": "https://picsum.photos/seed/fs02/400/300",
     "description": "超轻鞋面 + 弹力中底，城市通勤首选。"},
    {"id": "ad_featured_03", "channel": "featured", "type": "image_text",
     "title": "Flow Studio 智能水杯", "brand": "Flow Studio 3",
     "cover": "https://picsum.photos/seed/fs03/400/300",
     "images": ["https://picsum.photos/seed/fs03a/300/200",
                "https://picsum.photos/seed/fs03b/300/200"],
     "description": "温度提示 + APP 联动，让你保持合理饮水习惯。"},
    {"id": "ad_featured_04", "channel": "featured", "type": "video",
     "title": "Flow Studio 极速充电宝", "brand": "Flow Studio 4",
     "cover": "https://picsum.photos/seed/fs04/800/450",
     "video_url": "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
     "description": "10000mAh 小身材，30W 快充，一分钟充 3 小时续航。"},
    {"id": "ad_featured_05", "channel": "featured", "type": "big_image",
     "title": "Flow Studio 人体工学椅", "brand": "Flow Studio 1",
     "cover": "https://picsum.photos/seed/fs05/800/450",
     "description": "腰背支撑 + 4D 扶手，久坐不累。"},
    {"id": "ad_featured_06", "channel": "featured", "type": "small_image",
     "title": "Flow Studio 便携咖啡机", "brand": "Flow Studio 2",
     "cover": "https://picsum.photos/seed/fs06/400/300",
     "description": "胶囊式设计，办公室 / 户外随时享受现磨咖啡。"},
    # --- 电商频道 ---
    {"id": "ad_ecom_01", "channel": "ecom", "type": "big_image",
     "title": "AIFeed Mart 学生党平价球鞋", "brand": "AIFeed Mart 1",
     "cover": "https://picsum.photos/seed/ec01/800/450",
     "description": "百元内性价比之王，多色可选。"},
    {"id": "ad_ecom_02", "channel": "ecom", "type": "small_image",
     "title": "AIFeed Mart 限时秒杀背包", "brand": "AIFeed Mart 2",
     "cover": "https://picsum.photos/seed/ec02/400/300",
     "description": "容量大 + 防泼水，通勤出行全搞定。"},
    {"id": "ad_ecom_03", "channel": "ecom", "type": "image_text",
     "title": "AIFeed Mart 平价护肤套装", "brand": "AIFeed Mart 3",
     "cover": "https://picsum.photos/seed/ec03/400/300",
     "images": ["https://picsum.photos/seed/ec03a/300/200",
                "https://picsum.photos/seed/ec03b/300/200"],
     "description": "学生党、职场新人必备，温和不刺激。"},
    {"id": "ad_ecom_04", "channel": "ecom", "type": "video",
     "title": "AIFeed Mart 折叠键盘鼠标套装", "brand": "AIFeed Mart 4",
     "cover": "https://picsum.photos/seed/ec04/800/450",
     "video_url": "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
     "description": "蓝牙 5.0，超薄折叠，外出轻办公必备。"},
    {"id": "ad_ecom_05", "channel": "ecom", "type": "big_image",
     "title": "AIFeed Mart 迷你台灯", "brand": "AIFeed Mart 1",
     "cover": "https://picsum.photos/seed/ec05/800/450",
     "description": "护眼无频闪，宿舍自习都适合。"},
    {"id": "ad_ecom_06", "channel": "ecom", "type": "small_image",
     "title": "AIFeed Mart 便携充电鼠标", "brand": "AIFeed Mart 2",
     "cover": "https://picsum.photos/seed/ec06/400/300",
     "description": "Type-C 充电，30 天续航，静音点击。"},
    # --- 本地频道 ---
    {"id": "ad_local_01", "channel": "local", "type": "big_image",
     "title": "City Picks 附近新开烤肉店", "brand": "City Picks 1",
     "cover": "https://picsum.photos/seed/lc01/800/450",
     "description": "周末限时套餐 8 折，两人同行立减 20。"},
    {"id": "ad_local_02", "channel": "local", "type": "small_image",
     "title": "City Picks 社区健身房月卡", "brand": "City Picks 2",
     "cover": "https://picsum.photos/seed/lc02/400/300",
     "description": "新店开业特惠，首月 99 元，设施齐全。"},
    {"id": "ad_local_03", "channel": "local", "type": "image_text",
     "title": "City Picks 周末亲子农场体验", "brand": "City Picks 3",
     "cover": "https://picsum.photos/seed/lc03/400/300",
     "images": ["https://picsum.photos/seed/lc03a/300/200",
                "https://picsum.photos/seed/lc03b/300/200"],
     "description": "采摘 + 烧烤 + 萌宠，全程 3 小时，亲子必玩。"},
    {"id": "ad_local_04", "channel": "local", "type": "video",
     "title": "City Picks 街边现磨咖啡车", "brand": "City Picks 4",
     "cover": "https://picsum.photos/seed/lc04/800/450",
     "video_url": "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
     "description": "当季果汁拿铁 + 限定周边，附近打卡好去处。"},
    {"id": "ad_local_05", "channel": "local", "type": "big_image",
     "title": "City Picks 社区洗衣店优惠", "brand": "City Picks 1",
     "cover": "https://picsum.photos/seed/lc05/800/450",
     "description": "上门取送，次日达，首次免运费。"},
    {"id": "ad_local_06", "channel": "local", "type": "small_image",
     "title": "City Picks 附近羽毛球馆", "brand": "City Picks 2",
     "cover": "https://picsum.photos/seed/lc06/400/300",
     "description": "非高峰时段低至 30 元/小时，在线预约即享。"},
]

# AI 摘要 + 标签缓存（由 generate_ai.py 离线生成后写入此处）
# 格式：{ ad_id: { "summary": str, "tags": [str] } }
AI_CACHE: dict[str, dict] = {}


def get_ai_data(ad_id: str) -> tuple[str, list[str]]:
    """返回 (summary, tags)，缓存缺失时降级回标题。"""
    cached = AI_CACHE.get(ad_id)
    if cached:
        return cached.get("summary", ""), cached.get("tags", [])
    ad = next((a for a in RAW_ADS if a["id"] == ad_id), None)
    return (ad["title"] if ad else ""), []
