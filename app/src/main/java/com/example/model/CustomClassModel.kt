package com.example.model

data class CustomTypographyClass(
    val id: String,
    val name: String,
    val className: String,
    val description: String,
    val cssRules: String,
    val htmlTemplate: String,
    val category: String = "Plus UI 3.7.0"
)

object CustomClassPresets {
    val INITIAL_CUSTOM_CLASSES = listOf(
        CustomTypographyClass(
            id = "custom_video_featured",
            name = "Featured Video Hero Card",
            className = "video-hero-card",
            category = "Images and Video",
            description = "Responsive featured video player card with dark bezel and caption",
            cssRules = """
.video-hero-card {
    background: #0f172a;
    border-radius: 12px;
    padding: 16px;
    margin: 24px 0;
    box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}
.video-hero-card .videoYt {
    border-radius: 8px;
    overflow: hidden;
}
.video-hero-card .caption {
    color: #94a3b8;
    font-size: 0.88rem;
    margin-top: 10px;
    text-align: center;
}
            """.trimIndent(),
            htmlTemplate = """<div class="video-hero-card"><div class="videoYt"><iframe src="https://www.youtube.com/embed/{{videoId}}" allowfullscreen></iframe></div><div class="caption">{{caption}}</div></div>"""
        ),
        CustomTypographyClass(
            id = "custom_download_safelink",
            name = "Safelink Download Showcase",
            className = "dl-safelink-card",
            category = "Download Box",
            description = "Download box with instant Safelink countdown integration",
            cssRules = """
.dl-safelink-card {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    padding: 18px;
    margin: 20px 0;
}
            """.trimIndent(),
            htmlTemplate = """<div class="dlBox"><div class="fT" data-text="{{type}}"></div><div class="fN"><span>{{filename}}</span><span class="fS">{{size}}</span></div><a class="button safeL" href="{{url}}" aria-label="Download"><i class="icon dl"></i></a></div>"""
        ),
        CustomTypographyClass(
            id = "custom_author_sig",
            name = "Author Bio & Signature",
            className = "author-signature-box",
            category = "Text and Layout",
            description = "Elegantly framed author profile card with bio text",
            cssRules = """
.author-signature-box {
    background: #fdfaf3;
    border: 1.5px solid #d4af37;
    border-radius: 10px;
    padding: 16px;
    margin: 22px 0;
    display: flex;
    align-items: center;
    gap: 14px;
}
.author-signature-box .bio {
    font-size: 0.95rem;
    color: #4a5568;
    line-height: 1.6;
}
            """.trimIndent(),
            htmlTemplate = """<div class="author-signature-box"><div class="bio"><strong>Author:</strong> {{text}}</div></div>"""
        ),
        CustomTypographyClass(
            id = "custom_affiliate_notice",
            name = "Disclaimer Callout",
            className = "post-disclaimer-note",
            category = "Alerts and Notes",
            description = "Soft amber disclosure note compliant with AdSense & FTC",
            cssRules = """
.post-disclaimer-note {
    background: #fff8e1;
    border-left: 4px solid #ffa000;
    color: #5d4037;
    font-size: 0.88rem;
    padding: 10px 14px;
    border-radius: 4px;
    margin: 16px 0;
    font-style: italic;
}
            """.trimIndent(),
            htmlTemplate = """<div class="post-disclaimer-note">ℹ️ <strong>Disclaimer:</strong> {{text}}</div>"""
        )
    )

    fun generateFullCombinedCss(customClasses: List<CustomTypographyClass>): String {
        return buildString {
            append("<style id=\"plus-ui-custom-classes\">\n")
            append("/* ==========================================================================\n")
            append("   Plus UI 3.7.0 Extended Post Formatting Classes for Blogger\n")
            append("   ========================================================================== */\n\n")
            customClasses.forEach { cls ->
                append("/* Class: ${cls.name} (.${cls.className}) - Category: ${cls.category} */\n")
                append(cls.cssRules)
                append("\n\n")
            }
            append("</style>\n")
        }
    }
}
