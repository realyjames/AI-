package com.example.model

internal object MediaMusicCatalog {
    val videoDimension = PromptDimension(
        id = "video",
        name = "🎬 動態影音腳本 (Sora/Runway)",
        formulas = listOf(
            PromptFormula(
                id = "video_greeting",
                name = "動態問候短影音",
                pattern = "身為專業的 AI 動態影像導演，請為我生成一段長寬比「{圖片長寬比例}」，約 5-10 秒的高畫質問候短影音提示詞。\n\n【動態影像設定】\n畫面風格採用「{動態視覺風格}」。主體呈現「{核心動態事件}」，請採用「{電影級運鏡手法}」來引導觀眾視線。整體影片的步調保持「{影片節奏與氛圍}」。\n\n【文字與後製指示 (若工具支援)】\n影片後半段，畫面中央需平滑淡入優美的英文字體 (早/午/晚) 問候語。影片右下角常駐浮水印：「琦閔」以及今日日期：{今日日期}。",
                variables = listOf(
                    PromptVariable("圖片長寬比例", CommonOptions.commonAspectRatioOptions),
                    PromptVariable(
                        "動態視覺風格",
                        listOf(
                            "水墨畫暈染流動特效 (Ink wash animation - 禪意)",
                            "青綠山水畫的3D動態演繹 (3D Shan shui - 禪意)",
                            "8K 真實電影攝影 (Cinematic realism)",
                            "萬花筒式的幾何碎形鏡像複製特效 (Kaleidoscope fractal)",
                            "賽博龐克立體全息投影動態 (3D Hologram projection)",
                            "動態排版字體飛舞特效 (Kinetic typography)",
                            "局部動態微圖影風格 (Cinemagraph, only one element moving)",
                            "熱像儀色彩干擾與故障特效 (Thermal vision glitch)",
                            "復古 8-bit 街機遊戲像素動態 (Retro 8-bit intro)",
                            "左右眼紅藍立體 3D 視覺特效 (Stereoscopic 3D effect)",
                            "吉卜力手繪動畫風 (Ghibli animation)",
                            "3D 虛幻引擎高光渲染 (UE5 CGI)",
                            "微縮模型移軸動態 (Tilt-shift time-lapse)",
                            "逐格黏土動畫 (Claymation stop-motion)",
                            "2D 賽璐璐復古動畫 (Retro cel animation)",
                            "油畫筆觸流動特效 (Oil painting flow)",
                            "賽博龐克霓虹閃爍動態 (Cyberpunk neon motion)",
                            "微觀粒子特效流動 (Particle simulation)",
                            "水彩畫顏料在水中擴散的動態 (Watercolor bleed)",
                            "萬花筒般的幾何碎形無限放大 (Fractal zoom)",
                            "老舊 VHS 錄影帶雜訊風格 (VHS glitch)",
                            "萬物皆為毛線編織的動態世界 (Yarn world)"
                        )
                    ),
                    PromptVariable(
                        "核心動態事件",
                        listOf(
                            "早：熱茶杯口緩緩飄升的白煙與落下的竹葉 (禪意)",
                            "晚：一滴墨水在宣紙上暈開化作夜空 (禪意)",
                            "早：熱咖啡杯口緩緩飄升的白煙與灑落的光束",
                            "早：朝陽從雲海中升起，光線逐漸照亮大地",
                            "午：微風吹過窗簾，陽光在書本上緩慢移動",
                            "午：花瓣在微風中輕輕搖曳，蝴蝶飛過",
                            "晚：璀璨的煙火在夜空中接連綻放",
                            "晚：螢火蟲在森林中閃爍飛舞，溪水潺潺流動",
                            "晚：壁爐中的木柴燃燒，火光溫暖地跳動",
                            "早：含苞待放的玫瑰在縮時攝影下緩緩綻放",
                            "午：一杯加了冰塊的氣泡水，氣泡不斷向上湧出",
                            "晚：極光在佈滿繁星的夜空中如絲帶般舞動",
                            "午：一隻熟睡的貓咪呼吸時肚子微微起伏",
                            "晚：城市街道的車軌光影在縮時攝影下流動",
                            "早：一滴晶瑩的露水從綠葉邊緣滑落滴入平靜水面",
                            "午：一杯黑咖啡倒入牛奶後產生美麗的渲染大理石紋路",
                            "晚：無數發光的水母在深海中緩慢向上游動",
                            "早：破繭而出的蝴蝶第一次拍打翅膀",
                            "午：秋風掃落葉，金黃色的葉片在空中旋轉飛舞",
                            "晚：古老時鐘的齒輪精確地咬合轉動",
                            "早：冰川上的冰柱在陽光下緩慢融化滴水",
                            "午：墨水滴入清水中如雲霧般擴散纏繞",
                            "晚：流星雨劃過天際並在夜空中留下殘影",
                            "午：黑膠唱片機的唱針緩緩放下開始旋轉"
                        )
                    ),
                    PromptVariable(
                        "電影級運鏡手法",
                        listOf(
                            "緩慢平滑的向前推進 (Dolly in slowly)",
                            "環繞主體的史詩級環繞運鏡 (Orbit shot)",
                            "由背景模糊到前景清晰的焦點轉換 (Rack focus)",
                            "平穩跟隨的橫向平移 (Tracking pan)",
                            "穿越障礙物的飛行視角 (FPV drone flight)",
                            "上帝視角緩慢垂直拉遠 (Top-down zoom out)",
                            "極速拉近的衝擊性運鏡 (Crash zoom)",
                            "模擬手持攝影的輕微晃動感 (Handheld camera shake)",
                            "動態視角旋轉 (Roll shot)",
                            "從水下突破水面的上升視角 (Underwater to surface)",
                            "宛如駭客任務的子彈時間環繞 (Bullet time 360)",
                            "與主體等速前進的跟拍 (Tracking shot)",
                            "從微觀極速拉遠到宏觀宇宙的視角 (Macro to Cosmic zoom)",
                            "彷彿昆蟲飛行般的不規則穿梭運鏡 (Erratic insect flight)",
                            "鏡面世界翻轉特效運鏡 (Mirror flip transition)",
                            "一鏡到底的穿梭運鏡 (One-take continuous shot)"
                        )
                    ),
                    PromptVariable(
                        "影片節奏與氛圍",
                        listOf(
                            "如參禪般極度緩慢、靜心平氣的步調 (禪意)",
                            "極度緩慢、寧靜且療癒的步調 (Slow-motion relaxing)",
                            "輕快、充滿活力的早晨節奏 (Upbeat and energetic)",
                            "猶如電影般富有詩意的流暢感 (Poetic cinematic flow)",
                            "充滿魔法與奇幻感的夢幻步調 (Magical and dreamy)",
                            "配合重低音節奏的動感剪輯步調 (Rhythmic & dynamic)",
                            "時間飛逝的縮時攝影急促感 (Time-lapse urgent)",
                            "充滿復古懷舊感的定格跳躍節奏 (Stop-motion choppy)",
                            "倒放時空的奇幻錯覺節奏 (Reverse playback)",
                            "行雲流水般的無縫轉場節奏 (Seamless transition flow)",
                            "心跳般規律搏動的視覺節奏 (Heartbeat pulsating pace)"
                        )
                    )
                )
            )
        )
    )

    val musicDimension = PromptDimension(
        id = "music",
        name = "🎵 AI 音樂生成腳本 (Suno/Udio)",
        formulas = listOf(
            PromptFormula(
                id = "music_bgm",
                name = "情境純音樂配樂 (BGM)",
                pattern = "身為專業的 AI 音樂製作人，請為我生成一段高品質的純音樂提示詞 (Prompt)。\n\n【音樂風格設定】\n請融合「{音樂流派與曲風}」與「{主導樂器}」。整體節奏設定為「{BPM與節奏感}」。\n\n【氛圍與聽感】\n這首曲子需要傳達「{情感與氛圍}」的感覺，並在背景加入微弱的「{環境音效 (Foley/ASMR)}」以增加沉浸感。\n\n【結構指示】\n無需人聲 (Instrumental only)。請以平緩的 Intro 開始，中段稍微揚起，最後以溫柔的 Outro 漸弱收尾。",
                variables = listOf(
                    PromptVariable(
                        "音樂流派與曲風",
                        listOf(
                            "Lo-Fi 放鬆節拍 (Lo-Fi Chillhop)",
                            "冥想環境音樂 (Ambient Meditation - 禪意)",
                            "輕爵士與Bossa Nova (Smooth Jazz)",
                            "原聲木吉他民謠 (Acoustic Folk)",
                            "東方禪意新世紀音樂 (Oriental Zen New Age)",
                            "史詩級電影配樂 (Cinematic Orchestral)",
                            "復古城市流行 (80s City Pop)",
                            "賽博龐克合成器波 (Cyberpunk Synthwave)",
                            "夢幻流行 (Dream Pop)",
                            "古典鋼琴獨奏 (Classical Piano Solo)",
                            "R&B 靈魂樂 (R&B Soul)"
                        )
                    ),
                    PromptVariable(
                        "主導樂器",
                        listOf(
                            "溫暖柔和的呢喃鋼琴 (Felt Piano)",
                            "清脆的原聲木吉他 (Acoustic Guitar)",
                            "悠遠的尺八與古箏 (Shakuhachi & Guzheng - 禪意)",
                            "慵懶微醺的薩克斯風 (Saxophone)",
                            "夢幻縹緲的合成器 (Ethereal Synth)",
                            "宏大激昂的弦樂交響群 (Sweeping Strings)",
                            "空靈的頌缽與手碟 (Singing Bowl & Handpan - 禪意)",
                            "溫潤深沉的大提琴 (Cello)",
                            "輕盈的水晶音樂/八音盒 (Music Box)",
                            "帶有底噪的復古黑膠取樣 (Vinyl Sampled Beats)"
                        )
                    ),
                    PromptVariable(
                        "BPM與節奏感",
                        listOf(
                            "60 BPM - 如心跳般極緩慢的冥想節奏 (Very Slow)",
                            "80 BPM - 慵懶愜意的散步節拍 (Chill/Walking pace)",
                            "100 BPM - 輕快搖擺的 R&B 節奏 (Groovy)",
                            "120 BPM - 充滿朝氣與活力的早晨節拍 (Upbeat/Energetic)",
                            "自由節拍 - 無固定鼓點的流動感 (Free Tempo/Rubato)",
                            "Lo-Fi 經典的慵懶鼓點 (Sluggish Lo-Fi Beats)",
                            "輕柔的華爾滋三拍子 (Gentle Waltz 3/4 time)"
                        )
                    ),
                    PromptVariable(
                        "情感與氛圍",
                        listOf(
                            "治癒身心與內心絕對平靜 (Healing & Inner Peace - 禪意)",
                            "充滿朝氣、希望與迎向陽光 (Hopeful & Energetic)",
                            "深夜的孤獨、深邃與內省 (Late Night Solitude)",
                            "咖啡廳的慵懶、愜意與微醺 (Cozy Cafe Vibe)",
                            "宏大、史詩且敬畏自然 (Epic & Awe-inspiring)",
                            "童話般的溫馨與無憂無慮 (Fairy-tale Wholesome)",
                            "浪漫、唯美與深深的眷戀 (Romantic & Dreamy)",
                            "充滿神秘與賽博龐克的未來感 (Mysterious Sci-Fi)"
                        )
                    ),
                    PromptVariable(
                        "環境音效 (Foley/ASMR)",
                        listOf(
                            "無環境音效 (None)",
                            "柴火劈啪燃燒的溫暖白噪音 (Crackling Fire)",
                            "遠處規律拍打沙灘的海浪聲 (Ocean Waves)",
                            "清晨森林的清脆鳥鳴與微風 (Morning Birds)",
                            "雨滴打在玻璃窗上的淅瀝聲 (Rain on Window)",
                            "咖啡廳背景的模糊交談與杯盤聲 (Cafe Murmur)",
                            "深山寺廟遠處傳來的悠遠鐘聲 (Distant Temple Bell - 禪意)",
                            "夏夜田野間的蟲鳴與青蛙叫聲 (Summer Night Crickets)",
                            "老舊黑膠唱片的沙沙底噪聲 (Vinyl Crackle)"
                        )
                    )
                )
            ),
            PromptFormula(
                id = "music_vocal",
                name = "專屬問候人聲歌曲 (Vocal)",
                pattern = "身為專業的 AI 音樂製作人與作詞家，請為我生成一段完整的人聲歌曲提示詞 (Prompt) 與歌詞。\n\n【音樂風格設定】\n曲風為「{音樂流派與曲風}」，由「{歌手聲線}」演唱。主導樂器為「{主導樂器}」，節奏為「{BPM與節奏感}」。\n\n【氛圍與聽感】\n整體氛圍為「{情感與氛圍}」。\n\n【歌詞結構指示】\n請為我撰寫包含 [Verse]、[Chorus]、[Outro] 的短版歌詞，主題環繞著美好的問候與生活祝福。\n結尾的 [Outro] 歌詞必須包含專屬署名：「願你擁有美好的一刻，這裡是琦閔的祝福。」",
                variables = listOf(
                    PromptVariable(
                        "歌手聲線",
                        listOf(
                            "溫柔空靈的治癒系女聲 (Soft soothing female vocal)",
                            "帶有磁性與故事感的沙啞男聲 (Husky magnetic male vocal)",
                            "慵懶微醺的爵士女伶 (Lazy jazz female singer)",
                            "清新溫暖的民謠男聲 (Fresh indie male vocal)",
                            "充滿爆發力的靈魂樂女聲 (Powerful soul female vocal)",
                            "空靈的精靈系詠唱 (Ethereal elven chanting - 無歌詞)",
                            "稚嫩可愛的童聲合唱 (Cute children's choir)"
                        )
                    ),
                    PromptVariable(
                        "音樂流派與曲風",
                        listOf(
                            "溫馨原聲民謠 (Acoustic Folk Pop)",
                            "輕快 R&B 靈魂樂 (R&B Soul)",
                            "獨立流行樂 (Indie Pop)",
                            "輕爵士與微醺藍調 (Smooth Jazz & Blues)",
                            "復古城市流行 (City Pop)",
                            "療癒系童謠 (Lullaby / Nursery Rhyme)"
                        )
                    ),
                    PromptVariable(
                        "主導樂器",
                        listOf(
                            "清脆的原聲木吉他 (Acoustic Guitar)",
                            "明亮的流行鋼琴 (Pop Piano)",
                            "慵懶微醺的薩克斯風 (Saxophone)",
                            "輕快的烏克麗麗 (Ukulele)",
                            "溫潤深沉的大提琴 (Cello)",
                            "帶有復古感的電吉他 (Clean Electric Guitar)"
                        )
                    ),
                    PromptVariable(
                        "BPM與節奏感",
                        listOf(
                            "80 BPM - 慵懶愜意的散步節拍 (Chill/Walking pace)",
                            "100 BPM - 輕快搖擺的節奏 (Groovy)",
                            "120 BPM - 充滿朝氣與活力的早晨節拍 (Upbeat/Energetic)",
                            "自由節拍 - 隨人聲情感起伏的無固定節拍 (Rubato)"
                        )
                    ),
                    PromptVariable(
                        "情感與氛圍",
                        listOf(
                            "充滿朝氣、希望與迎向陽光 (Hopeful & Energetic)",
                            "咖啡廳的慵懶、愜意與微醺 (Cozy Cafe Vibe)",
                            "溫暖治癒、如同老友般的擁抱 (Warm & Healing)",
                            "深夜的深邃、平靜與安撫 (Calm & Soothing)"
                        )
                    )
                )
            )
        )
    )
}
