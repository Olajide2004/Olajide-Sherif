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
    PLUS_UI
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
            id = "tmpl_plus_ui_post",
            title = "OsunHive UI Complete Article",
            category = "Templates",
            isHtml = true,
            content = """<h2>Mastering Blogger Post Formatting & Auto-Typing</h2>
<p class="pIndent"><span class="dropCap">W</span>elcome to the complete tutorial on creating beautiful, responsive Blogspot posts using <strong>OsunHive UI</strong> shortcodes. Everything in this post uses pure HTML classes, rendering seamlessly without template errors.</p>

<div class="videoYt"><iframe src="https://www.youtube.com/embed/dQw4w9WgXcQ" allowfullscreen></iframe></div>

<div class="alert info"><strong>Notice:</strong> Always switch your Blogger editor to <strong>HTML view</strong> before pasting shortcodes.</div>

<h2>1. Key Features & Automation Steps</h2>
<ol class="steps">
  <li>Open Blogger.com and switch your post composer to HTML view.</li>
  <li>Insert your chosen OsunHive UI shortcodes (Video, Download Box, Safelink).</li>
  <li>Use <strong>File Auto Batch Paste</strong> or the Typewriter Keyboard to inject content without lag.</li>
</ol>

<h2>2. Download Resources & App Packages</h2>
<div class="dlBox">
  <div class="fT" data-text="APK"></div>
  <div class="fN">
    <span>BloggerAutoTyper-v1.0.0.apk</span>
    <span class="fS">24 MB</span>
  </div>
  <a class="button safeL" href="https://github.com/Olajide2004/Olajide-Sherif/releases" aria-label="Download"><i class="icon dl"></i></a>
</div>

<div class="btnF">
  <a class="button" href="https://github.com/Olajide2004/Olajide-Sherif"><i class="icon dl"></i>GitHub Repo</a>
  <a class="button ln" href="https://example.com/demo"><i class="icon demo"></i>Live Demo</a>
</div>

<h2>3. Performance Specifications</h2>
<div class="table bordered stripped hovered">
  <table>
    <thead><tr><th>Feature</th><th>Implementation</th><th>Status</th></tr></thead>
    <tbody>
      <tr><td>Video Embed</td><td>Responsive 16:9 .videoYt</td><td>Verified</td></tr>
      <tr><td>Safelink Protection</td><td>Countdown class .safeL</td><td>Active</td></tr>
      <tr><td>Batch Pasting</td><td>Chunked auto paste</td><td>Ready</td></tr>
    </tbody>
  </table>
</div>

<details class="sp"><summary>Frequently Asked Questions</summary><p>OsunHive UI classes work in standard Blogger templates without plugin dependencies.</p></details>"""
        ),
        SnippetItem(
            id = "tmpl_video_showcase",
            title = "Video & Media Showcase Post",
            category = "Templates",
            isHtml = true,
            content = """<h2>Featured Video & Media Presentation</h2>
<p class="pIndent">Embed YouTube videos and media galleries with fluid responsive layouts.</p>

<div class="videoYt"><iframe src="https://www.youtube.com/embed/VIDEO_ID" allowfullscreen></iframe></div>

<span class="psCaption">Official video tutorial demonstration</span>

<div class="psImg"><img src="https://via.placeholder.com/600x350" alt="Scene A"/><img src="https://via.placeholder.com/600x350" alt="Scene B"/></div>"""
        ),
        SnippetItem(
            id = "tmpl_product_spec",
            title = "Product & Software Download Card",
            category = "Templates",
            isHtml = true,
            content = """<h2>Application Release & Download Specifications</h2>
<div class="pInfo"><div><small data-text="Version"></small>1.0.0</div><div><small data-text="Size"></small>24 MB</div></div>

<div class="dlBox">
  <div class="fT" data-text="ZIP"></div>
  <div class="fN"><span>Source-Release.zip</span><span class="fS">15 MB</span></div>
  <a class="button safeL" href="https://github.com/Olajide2004/Olajide-Sherif/releases" aria-label="Download"><i class="icon dl"></i></a>
</div>"""
        )
    )

    val DEFAULT_SNIPPETS = listOf(
        SnippetItem("tag_video", "🎬 YouTube Video", "<div class=\"videoYt\"><iframe src=\"https://www.youtube.com/embed/VIDEO_ID\" allowfullscreen></iframe></div>", "Media", true),
        SnippetItem("tag_dlbox", "📥 Download Box", "<div class=\"dlBox\"><div class=\"fT\" data-text=\"ZIP\"></div><div class=\"fN\"><span>File.zip</span><span class=\"fS\">12 MB</span></div><a class=\"button safeL\" href=\"#\" aria-label=\"Download\"><i class=\"icon dl\"></i></a></div>", "Media", true),
        SnippetItem("tag_safel", "🔒 Safelink Button", "<a class=\"button safeL\" href=\"REAL-URL\">Download</a>", "Links", true),
        SnippetItem("tag_alert", "⚠️ Alert Info", "<div class=\"alert info\"><strong>Info</strong> Update note here.</div>", "Formatting", true),
        SnippetItem("tag_steps", "🔢 Steps List", "<ol class=\"steps\"><li>Step 1</li><li>Step 2</li></ol>", "Formatting", true),
        SnippetItem("tag_table", "📊 Styled Table", "<div class=\"table bordered stripped\"><table><thead><tr><th>Header</th></tr></thead><tbody><tr><td>Data</td></tr></tbody></table></div>", "Formatting", true),
        SnippetItem("tag_spoiler", "📑 Spoiler", "<details class=\"sp\"><summary>Click to reveal</summary><p>Hidden text</p></details>", "Formatting", true),
        SnippetItem("tag_code", "💻 Code Block", "<pre><code class=\"language-html\">&lt;p&gt;Code&lt;/p&gt;</code></pre>", "Formatting", true)
    )
}
