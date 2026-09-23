package com.example.model

data class CustomTypographyClass(
    val id: String,
    val name: String,
    val className: String, // e.g. "my-author-badge"
    val description: String,
    val cssRules: String, // e.g. "background: #fff3e0; border-left: 4px solid #ff9800; padding: 12px; border-radius: 6px;"
    val htmlTemplate: String // e.g. "<div class=\"my-author-badge\"><p>{{text}}</p></div>"
)

object CustomClassPresets {
    val INITIAL_CUSTOM_CLASSES = listOf(
        CustomTypographyClass(
            id = "custom_author_sig",
            name = "Author Signature Box",
            className = "custom-author-box",
            description = "Elegantly framed author profile card with gold border and bio text",
            cssRules = """
.custom-author-box {
    background: #fdfaf3;
    border: 1.5px solid #d4af37;
    border-radius: 10px;
    padding: 16px;
    margin: 22px 0;
    display: flex;
    align-items: center;
    gap: 14px;
}
.custom-author-box .author-bio {
    font-size: 0.95rem;
    color: #4a5568;
    line-height: 1.6;
}
            """.trimIndent(),
            htmlTemplate = """<div class="custom-author-box"><div class="author-bio"><strong>Written by Editor:</strong> {{text}}</div></div>"""
        ),
        CustomTypographyClass(
            id = "custom_affiliate_notice",
            name = "Affiliate / Disclaimer Callout",
            className = "custom-disclaimer",
            description = "Soft amber disclosure note compliant with Google AdSense & FTC",
            cssRules = """
.custom-disclaimer {
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
            htmlTemplate = """<div class="custom-disclaimer">ℹ️ <strong>Disclaimer:</strong> {{text}}</div>"""
        ),
        CustomTypographyClass(
            id = "custom_download_cta",
            name = "Gradient Download Box",
            className = "custom-download-card",
            description = "High-CTR gradient container with download action button",
            cssRules = """
.custom-download-card {
    background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
    color: #ffffff;
    border-radius: 12px;
    padding: 20px;
    margin: 24px 0;
    text-align: center;
    border: 1px solid #334155;
}
.custom-download-card h4 {
    color: #f1c40f;
    margin: 0 0 8px 0;
    font-size: 1.2rem;
}
.custom-download-card p {
    color: #cbd5e1;
    font-size: 0.9rem;
    margin-bottom: 14px;
}
            """.trimIndent(),
            htmlTemplate = """<div class="custom-download-card"><h4>{{title}}</h4><p>{{text}}</p><a href="#" class="oh-btn oh-btn-download">Download Resource Now</a></div>"""
        ),
        CustomTypographyClass(
            id = "custom_promo_banner",
            name = "OsunHive Featured Promo",
            className = "custom-promo-banner",
            description = "OsunHive community promo banner driving visits to osunhive.name.ng",
            cssRules = """
.custom-promo-banner {
    background: #111827;
    border: 2px solid #d4af37;
    border-radius: 10px;
    padding: 16px;
    margin: 20px 0;
    color: #f9fafb;
    text-align: center;
}
.custom-promo-banner a {
    color: #f1c40f;
    font-weight: bold;
    text-decoration: underline;
}
            """.trimIndent(),
            htmlTemplate = """<div class="custom-promo-banner"><strong>🚀 Powered by OsunHive UI:</strong> Discover free templates & tools at <a href="https://www.osunhive.name.ng" target="_blank" rel="noopener">osunhive.name.ng</a> or join <a href="https://t.me/Osunhive" target="_blank" rel="noopener">t.me/Osunhive</a></div>"""
        )
    )

    fun generateFullCombinedCss(customClasses: List<CustomTypographyClass>): String {
        return buildString {
            append(OsunhiveUiTypography.FULL_CSS_STYLESHEET)
            append("\n\n<style id=\"custom-user-typography-classes\">\n")
            append("/* ==========================================================================\n")
            append("   User Custom Typography Classes for Blogger / Blogspot Posts\n")
            append("   ========================================================================== */\n\n")
            customClasses.forEach { cls ->
                append("/* Class: ${cls.name} (.${cls.className}) */\n")
                append(cls.cssRules)
                append("\n\n")
            }
            append("</style>\n")
        }
    }
}
