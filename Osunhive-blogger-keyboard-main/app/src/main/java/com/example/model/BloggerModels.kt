package com.example.model

enum class AutoTypeState {
    IDLE,
    COUNTDOWN,
    TYPING,
    PAUSED,
    COMPLETED
}

enum class KeyboardLayout {
    QWERTY,
    SYMBOLS,
    BLOGGER_TAGS,
    KEYWORDS,
    OSUNHIVE_UI
}

data class AutoTypeConfig(
    val speedCharsPerSec: Int = 20,
    val jitterPercent: Int = 15,
    val countdownSeconds: Int = 3,
    val chunkMode: Boolean = false,
    val chunkSize: Int = 10,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)

data class SnippetItem(
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val isHtml: Boolean = false
)

data class BatchCopyInfo(
    val currentBatch: Int,
    val totalBatches: Int,
    val batchText: String,
    val startIndex: Int,
    val endIndex: Int
)

data class LoadedFileInfo(
    val name: String,
    val type: String,
    val characterCount: Int,
    val lineCount: Int
)

object BloggerPresets {
    val TEMPLATES = listOf(
        SnippetItem(
            id = "tmpl_osunhive_ui",
            title = "Osunhive UI Blog Post (Plus UI Format)",
            category = "Templates",
            isHtml = true,
            content = OsunhiveUiTypography.SAMPLE_BLOG_POST_HTML
        ),
        SnippetItem(
            id = "tmpl_seo_post",
            title = "Blogger SEO Post Template",
            category = "Templates",
            isHtml = true,
            content = """<!-- Post Title: Top Android Auto-Typing Tips for Blogger -->
<!-- Blogger Labels: Technology, Android Tools, Blogging, Content Creation -->

<div class="post-body-container" style="font-family: Arial, sans-serif; line-height: 1.7; color: #222;">
    <h2 style="color: #1a73e8; border-bottom: 2px solid #e8f0fe; padding-bottom: 6px;">Introduction</h2>
    <p>Discover the best ways to automate keywords and content injection in <strong>Blogger.com</strong> posts. Utilizing automated typing workflows dramatically saves preparation time and enhances keyword consistency across articles.</p>

    <blockquote class="tr_bq" style="background: #f8f9fa; border-left: 4px solid #1a73e8; margin: 16px 0; padding: 12px 18px; font-style: italic;">
        "Consistency in keyword optimization and structure is key to higher search engine rankings on Blogger."
    </blockquote>

    <h2 style="color: #1a73e8;">Key Benefits & Targeted Keywords</h2>
    <p>Here are the core focus keywords applied throughout this tutorial:</p>
    <ul>
        <li><mark style="background-color: #fff9c4; padding: 2px 4px;">auto typing keywords</mark> - Accelerate drafting</li>
        <li><mark style="background-color: #fff9c4; padding: 2px 4px;">blogger html injection</mark> - Rich format control</li>
        <li><mark style="background-color: #fff9c4; padding: 2px 4px;">content automation</mark> - Batch publishing efficiency</li>
    </ul>

    <h2 style="color: #1a73e8;">Step-by-Step Implementation</h2>
    <p>To insert content safely into your Blogger editor:</p>
    <ol>
        <li>Load your HTML or plain text file into the workbench.</li>
        <li>Select your target keywords and inject them into designated headers.</li>
        <li>Use the <strong>Batch Copy</strong> tool to paste seamlessly into Blogger's post composer.</li>
    </ol>

    <div style="background: #e8f0fe; border-radius: 8px; padding: 16px; margin: 20px 0; text-align: center;">
        <h3 style="margin-top: 0; color: #174ea6;">Ready to Optimize Your Blog?</h3>
        <p style="margin-bottom: 0;">Publish high-quality articles consistently with clean HTML markup and targeted keyword density.</p>
    </div>
</div>"""
        ),
        SnippetItem(
            id = "tmpl_tech_review",
            title = "Blogger Product / App Review",
            category = "Templates",
            isHtml = true,
            content = """<!-- Blogger Labels: Reviews, Apps, Productivity -->
<div class="review-box" style="font-family: sans-serif; line-height: 1.6;">
    <h2 style="color: #d93025;">Application Review: Efficiency & Speed</h2>
    <p>In this review, we test whether automated typing and keyword injection can streamline everyday blogging workflows.</p>

    <div class="spec-table" style="background: #f1f3f4; border-radius: 6px; padding: 14px; margin: 14px 0;">
        <p><strong>Verdict:</strong> 4.8 / 5.0</p>
        <p><strong>Primary Focus:</strong> Blogger Content Automation</p>
        <p><strong>Highlights:</strong> Zero-clipboard limit batch copy, simulated physical typing, instant HTML injection.</p>
    </div>

    <h3>Pros &amp; Cons</h3>
    <p><strong>What works great:</strong> Fast keyword insertion, customizable human jitter, seamless HTML preview.</p>
</div>"""
        ),
        SnippetItem(
            id = "tmpl_keywords_block",
            title = "SEO Keywords & Meta Block",
            category = "Templates",
            isHtml = true,
            content = """<!-- ============================================ -->
<!-- BLOGGER POST LABELS & KEYWORDS FOR INJECTION -->
<!-- Labels: Android, Blogger SEO, Content Marketing, Auto Typing, Productivity -->
<!-- Target Keywords: auto typing keywords, blogger html generator, content injection -->
<!-- ============================================ -->
<div class="blogger-seo-tags" style="font-size: 12px; color: #5f6368; border-top: 1px solid #dadce0; margin-top: 24px; padding-top: 10px;">
    <strong>Related Tags:</strong>
    <span>#AutoTyping</span>, <span>#BloggerTips</span>, <span>#HTMLInjection</span>, <span>#Keywords</span>, <span>#Blogging2026</span>
</div>"""
        )
    )

    val DEFAULT_SNIPPETS = listOf(
        SnippetItem("tag_p", "<p> Paragraph", "<p>{{content}}</p>", "HTML Tags", true),
        SnippetItem("tag_h2", "<h2> Heading 2", "<h2>{{title}}</h2>", "HTML Tags", true),
        SnippetItem("tag_h3", "<h3> Heading 3", "<h3>{{title}}</h3>", "HTML Tags", true),
        SnippetItem("tag_bq", "<blockquote> Quote", "<blockquote class=\"tr_bq\">{{content}}</blockquote>", "HTML Tags", true),
        SnippetItem("tag_link", "<a> Hyperlink", "<a href=\"https://\" target=\"_blank\" rel=\"noopener\">{{content}}</a>", "HTML Tags", true),
        SnippetItem("tag_img", "<img> Image Separator", "<div class=\"separator\" style=\"clear: both; text-align: center;\"><img border=\"0\" src=\"https://placeholder.com\" alt=\"{{keyword}}\" /></div>", "HTML Tags", true),
        SnippetItem("tag_mark", "<mark> Highlight", "<mark style=\"background-color: #fff9c4; padding: 2px 4px;\">{{keyword}}</mark>", "HTML Tags", true),
        SnippetItem("tag_hr", "<hr /> Divider", "<hr style=\"border: 0; height: 1px; background: #e0e0e0; margin: 20px 0;\" />", "HTML Tags", true),
        SnippetItem("tag_code", "<code> Code Snippet", "<pre style=\"background: #272822; color: #f8f8f2; padding: 12px; border-radius: 6px; overflow-x: auto;\"><code>{{code}}</code></pre>", "HTML Tags", true),
        SnippetItem("kw_blogger", "Blogger.com SEO", "blogger seo, blogger post editor, html post formatting, custom labels", "SEO Keywords", false),
        SnippetItem("kw_auto", "Auto Typing Keywords", "auto typing keywords, content injection, automated blogger writing, keyboard simulation", "SEO Keywords", false),
        SnippetItem("kw_cta", "Call to Action Box", "<div style=\"background: #e8f0fe; border-left: 5px solid #1a73e8; padding: 12px 16px; margin: 16px 0;\"><p style=\"margin: 0; font-weight: bold;\">Liked this post? Leave a comment below and share with fellow bloggers!</p></div>", "Call To Action", true)
    )
}
