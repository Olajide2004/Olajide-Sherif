package com.example.model

data class OsunhiveUiSnippet(
    val id: String,
    val name: String,
    val className: String,
    val category: String,
    val description: String,
    val htmlCode: String
)

object OsunhiveUiTypography {

    /**
     * Production-ready CSS stylesheet for Osunhive UI typography format for Blogspot.
     * Can be pasted into Blogger Theme > Edit HTML (in <b:skin> or <style>)
     * or embedded directly into a post.
     */
    val FULL_CSS_STYLESHEET = """
<style id="osunhive-ui-typography">
/* ==========================================================================
   Osunhive UI Typography & Format Classes for Blogspot Posts
   Adapted from Plus UI Typography Standards for Blogger.com
   ========================================================================== */

/* Post Body Root Container */
.osunhive-ui, .oh-post-body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    font-size: 16px;
    line-height: 1.75;
    color: #2c3e50;
    word-break: break-word;
}

/* Headings with Modern Letter-Spacing & Accents */
.osunhive-ui h1, .oh-h1 {
    font-size: 2rem;
    font-weight: 700;
    line-height: 1.3;
    margin: 1.8rem 0 1rem;
    color: #1a252f;
    letter-spacing: -0.02em;
}

.osunhive-ui h2, .oh-h2 {
    font-size: 1.6rem;
    font-weight: 700;
    line-height: 1.35;
    margin: 1.8rem 0 0.8rem;
    color: #1a252f;
    padding-bottom: 8px;
    border-bottom: 2px solid #f1c40f;
    position: relative;
}

.osunhive-ui h3, .oh-h3 {
    font-size: 1.3rem;
    font-weight: 600;
    line-height: 1.4;
    margin: 1.5rem 0 0.6rem;
    color: #2c3e50;
}

.osunhive-ui h4, .oh-h4 {
    font-size: 1.1rem;
    font-weight: 600;
    margin: 1.2rem 0 0.5rem;
    color: #34495e;
}

/* Lead / Introduction Paragraph */
.oh-lead {
    font-size: 1.15rem;
    line-height: 1.8;
    color: #4a5568;
    font-weight: 400;
    margin-bottom: 1.4rem;
}

/* Drop Cap (Large Editorial First Letter) */
.oh-dropcap {
    float: left;
    font-size: 3.5rem;
    line-height: 0.8;
    font-weight: 800;
    padding-top: 4px;
    padding-right: 10px;
    padding-bottom: 2px;
    color: #d4af37;
    font-family: Georgia, "Times New Roman", serif;
}

/* Alert & Notice Boxes */
.oh-alert {
    display: flex;
    align-items: flex-start;
    padding: 14px 18px;
    margin: 18px 0;
    border-radius: 8px;
    font-size: 0.95rem;
    line-height: 1.6;
    border-left: 5px solid;
    box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.oh-alert-icon {
    font-size: 1.25rem;
    margin-right: 12px;
    flex-shrink: 0;
    line-height: 1.4;
}
.oh-alert-body {
    flex: 1;
}

/* Alert Variants */
.oh-alert.oh-alert-info {
    background-color: #ebf5fb;
    border-left-color: #2980b9;
    color: #1f618d;
}
.oh-alert.oh-alert-success {
    background-color: #eafaf1;
    border-left-color: #27ae60;
    color: #196f3d;
}
.oh-alert.oh-alert-warning {
    background-color: #fef9e7;
    border-left-color: #f39c12;
    color: #9a7d0a;
}
.oh-alert.oh-alert-danger {
    background-color: #fdedec;
    border-left-color: #e74c3c;
    color: #922b21;
}

/* Action Buttons */
.oh-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 10px 22px;
    margin: 6px 4px 6px 0;
    font-size: 0.92rem;
    font-weight: 600;
    text-decoration: none !important;
    border-radius: 6px;
    transition: all 0.2s ease-in-out;
    cursor: pointer;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.oh-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}
.oh-btn-primary {
    background-color: #d4af37;
    color: #1a252f !important;
}
.oh-btn-secondary {
    background-color: #2c3e50;
    color: #ffffff !important;
}
.oh-btn-outline {
    background-color: transparent;
    color: #2c3e50 !important;
    border: 2px solid #2c3e50;
}
.oh-btn-download {
    background-color: #27ae60;
    color: #ffffff !important;
}
.oh-btn-demo {
    background-color: #2980b9;
    color: #ffffff !important;
}

/* Blockquotes & Editorial Quotes */
.oh-blockquote {
    margin: 20px 0;
    padding: 16px 22px;
    background-color: #f8f9fa;
    border-left: 4px solid #d4af37;
    border-radius: 0 8px 8px 0;
    font-style: italic;
    color: #4a5568;
    position: relative;
}
.oh-blockquote p {
    margin: 0 0 6px 0;
}
.oh-cite {
    display: block;
    font-size: 0.85rem;
    font-style: normal;
    font-weight: 600;
    color: #718096;
    margin-top: 6px;
}
.oh-quote-card {
    background: #ffffff;
    border: 1px solid #e2e8f0;
    border-left: 5px solid #3182ce;
    border-radius: 8px;
    padding: 18px 24px;
    margin: 22px 0;
    box-shadow: 0 2px 6px rgba(0,0,0,0.04);
}

/* Code Blocks & Inline Code */
.oh-code {
    background-color: #edf2f7;
    color: #d93025;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
    font-size: 0.88em;
}
.oh-code-box {
    background-color: #1e293b;
    border-radius: 8px;
    margin: 20px 0;
    overflow: hidden;
    box-shadow: 0 4px 10px rgba(0,0,0,0.15);
}
.oh-code-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #0f172a;
    padding: 8px 16px;
    color: #94a3b8;
    font-size: 0.8rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}
.oh-pre {
    margin: 0;
    padding: 16px;
    overflow-x: auto;
    color: #e2e8f0;
    font-family: SFMono-Regular, Menlo, Monaco, Consolas, monospace;
    font-size: 0.9rem;
    line-height: 1.5;
}

/* Collapsible Accordion / FAQ */
.oh-accordion {
    background: #ffffff;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    margin: 12px 0;
    overflow: hidden;
    transition: all 0.2s ease;
}
.oh-summary {
    padding: 14px 18px;
    font-weight: 600;
    color: #1e293b;
    cursor: pointer;
    background-color: #f8fafc;
    user-select: none;
    outline: none;
}
.oh-summary:hover {
    background-color: #f1f5f9;
}
.oh-content {
    padding: 16px 18px;
    border-top: 1px solid #e2e8f0;
    color: #475569;
    font-size: 0.95rem;
}

/* Responsive Striped Table */
.oh-table-wrapper {
    overflow-x: auto;
    margin: 20px 0;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
}
.oh-table {
    width: 100%;
    border-collapse: collapse;
    font-size: 0.92rem;
    text-align: left;
}
.oh-table th {
    background-color: #1e293b;
    color: #ffffff;
    padding: 12px 16px;
    font-weight: 600;
}
.oh-table td {
    padding: 12px 16px;
    border-bottom: 1px solid #e2e8f0;
    color: #334155;
}
.oh-table tr:nth-child(even) {
    background-color: #f8fafc;
}
.oh-table tr:hover {
    background-color: #f1f5f9;
}

/* Badges & Highlights */
.oh-badge {
    display: inline-block;
    padding: 3px 10px;
    font-size: 0.75rem;
    font-weight: 700;
    border-radius: 20px;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-right: 6px;
}
.oh-badge-gold { background: #fef3c7; color: #92400e; }
.oh-badge-green { background: #dcfce7; color: #166534; }
.oh-badge-blue { background: #dbeafe; color: #1e40af; }

.oh-mark {
    background: #fef08a;
    color: #1e293b;
    padding: 2px 5px;
    border-radius: 3px;
    font-weight: 500;
}

/* Feature Checklist */
.oh-list-check {
    list-style: none;
    padding-left: 0;
    margin: 16px 0;
}
.oh-list-check li {
    position: relative;
    padding-left: 28px;
    margin-bottom: 8px;
    line-height: 1.6;
}
.oh-list-check li::before {
    content: "✓";
    position: absolute;
    left: 4px;
    color: #16a34a;
    font-weight: 800;
}
</style>
""".trimIndent()

    /**
     * Full sample blog post formatted using the Osunhive UI typography suite.
     */
    val SAMPLE_BLOG_POST_HTML = """
<!-- ============================================================== -->
<!-- OSUNHIVE UI BLOGGER POST TEMPLATE (Plus UI Typography Format) -->
<!-- Labels: OsunhiveUI, Typography, BloggerTips, WebDesign, SEO    -->
<!-- ============================================================== -->

<div class="osunhive-ui oh-post-body">
    <!-- Header with Badges -->
    <div style="margin-bottom: 14px;">
        <span class="oh-badge oh-badge-gold">Featured</span>
        <span class="oh-badge oh-badge-green">Verified</span>
        <span class="oh-badge oh-badge-blue">Osunhive UI</span>
    </div>

    <!-- Opening Lead Paragraph with Drop Cap -->
    <p class="oh-lead">
        <span class="oh-dropcap">W</span>elcome to the complete Osunhive UI typography format for Blogger and Blogspot posts. Engineered following modern Plus UI standards, this typography system gives your articles a crisp, professional editorial hierarchy with zero external dependencies.
    </p>

    <!-- Information Alert Box -->
    <div class="oh-alert oh-alert-info">
        <div class="oh-alert-icon">ℹ️</div>
        <div class="oh-alert-body">
            <strong>Osunhive UI Note:</strong> All classes are pre-styled and mobile-responsive. Simply insert elements directly into your Blogger post editor.
        </div>
    </div>

    <h2 class="oh-h2">1. Elegant Editorial Typography</h2>
    <p>Articles formatted with clean typography retain readers 3x longer. By combining <code class="oh-code">.oh-dropcap</code>, <mark class="oh-mark">focused highlighted keywords</mark>, and balanced spacing, your blog posts achieve magazine-quality layout.</p>

    <!-- Blockquote with Citation -->
    <blockquote class="oh-blockquote">
        <p>"Typography is the craft of endowing human language with a durable visual form. Osunhive UI brings that craftsmanship straight to Blogspot."</p>
        <cite class="oh-cite">— Osunhive UI Design Guidelines</cite>
    </blockquote>

    <h2 class="oh-h2">2. Callout &amp; Notification Boxes</h2>
    <p>Use callout boxes to highlight important tips, warnings, or downloads for your readers:</p>

    <!-- Success Tip Alert -->
    <div class="oh-alert oh-alert-success">
        <div class="oh-alert-icon">✅</div>
        <div class="oh-alert-body">
            <strong>Pro Tip:</strong> Add targeted keywords inside your section headers to boost Google Search indexing without keyword stuffing.
        </div>
    </div>

    <!-- Warning Alert -->
    <div class="oh-alert oh-alert-warning">
        <div class="oh-alert-icon">⚠️</div>
        <div class="oh-alert-body">
            <strong>Caution:</strong> Ensure you do not remove your blog's main theme styles before applying custom classes.
        </div>
    </div>

    <!-- Danger Alert -->
    <div class="oh-alert oh-alert-danger">
        <div class="oh-alert-icon">⛔</div>
        <div class="oh-alert-body">
            <strong>Important:</strong> Always backup your Blogger XML template before making structural alterations.
        </div>
    </div>

    <h2 class="oh-h2">3. Download &amp; Action Buttons</h2>
    <p>Direct your audience with high-conversion call-to-action buttons:</p>
    <div style="margin: 16px 0;">
        <a href="#download" class="oh-btn oh-btn-download">⬇️ Download Template</a>
        <a href="#demo" class="oh-btn oh-btn-demo">🚀 Live Demo</a>
        <a href="#docs" class="oh-btn oh-btn-primary">Get Started</a>
        <a href="#learn" class="oh-btn oh-btn-outline">Learn More</a>
    </div>

    <h2 class="oh-h2">4. Code Snippet Container</h2>
    <p>Display HTML, CSS, or JavaScript code cleanly with the Osunhive code block:</p>
    <div class="oh-code-box">
        <div class="oh-code-header">
            <span>HTML • Osunhive UI Button</span>
        </div>
        <pre class="oh-pre"><code>&lt;!-- Osunhive UI Primary Button --&gt;
&lt;a href="https://example.com" class="oh-btn oh-btn-primary"&gt;
    Click to Read More
&lt;/a&gt;</code></pre>
    </div>

    <h2 class="oh-h2">5. Comparison Table</h2>
    <p>Here is how Osunhive UI compares with default Blogger formatting:</p>
    <div class="oh-table-wrapper">
        <table class="oh-table">
            <thead>
                <tr>
                    <th>Feature</th>
                    <th>Default Blogger</th>
                    <th>Osunhive UI</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>Drop Caps</strong></td>
                    <td>None</td>
                    <td><span class="oh-badge oh-badge-green">Native</span></td>
                </tr>
                <tr>
                    <td><strong>Callout Boxes</strong></td>
                    <td>Plain blockquote</td>
                    <td><span class="oh-badge oh-badge-gold">4 Color Variants</span></td>
                </tr>
                <tr>
                    <td><strong>Buttons</strong></td>
                    <td>Default hyperlinks</td>
                    <td><span class="oh-badge oh-badge-blue">5 Custom CTA Styles</span></td>
                </tr>
                <tr>
                    <td><strong>Code Blocks</strong></td>
                    <td>Unstyled text</td>
                    <td><span class="oh-badge oh-badge-green">Terminal Header</span></td>
                </tr>
            </tbody>
        </table>
    </div>

    <h2 class="oh-h2">6. Frequently Asked Questions (FAQ)</h2>
    <!-- Collapsible Accordion 1 -->
    <details class="oh-accordion">
        <summary class="oh-summary">Does Osunhive UI work on all Blogger templates?</summary>
        <div class="oh-content">
            <p>Yes. Osunhive UI classes are 100% self-contained CSS. It runs seamlessly on Plus UI, Median UI, Fineshop templates, or any standard Blogspot theme.</p>
        </div>
    </details>

    <!-- Collapsible Accordion 2 -->
    <details class="oh-accordion">
        <summary class="oh-summary">How do I insert the CSS stylesheet?</summary>
        <div class="oh-content">
            <p>Simply copy the <code class="oh-code">&lt;style id="osunhive-ui-typography"&gt;</code> block into your Blogger Theme HTML before <code class="oh-code">&lt;/head&gt;</code>, or paste it directly inside the HTML tab of your post.</p>
        </div>
    </details>

    <h2 class="oh-h2">7. Core Features Checklist</h2>
    <ul class="oh-list-check">
        <li>100% responsive on phones, tablets, and desktops</li>
        <li>Optimized font hierarchy for superior reader retention and SEO</li>
        <li>Zero external JavaScript or heavy frameworks required</li>
        <li>Seamless integration with the Blogger Auto Typer keyboard</li>
    </ul>

    <!-- Footer Quote Card -->
    <div class="oh-quote-card">
        <p style="margin: 0; font-weight: 600; color: #1e293b;">
            Crafted for creators using the Osunhive UI typography format. Happy blogging!
        </p>
    </div>
</div>
""".trimIndent()

    /**
     * Individual typography snippet elements for 1-click insertion and keyboard layout.
     */
    val PRESETS = listOf(
        OsunhiveUiSnippet(
            id = "oh_dropcap",
            name = "Drop Cap Letter",
            className = ".oh-dropcap",
            category = "Text & Headings",
            description = "Large decorative capital letter for opening paragraph",
            htmlCode = """<p class="oh-lead"><span class="oh-dropcap">T</span>his is an opening paragraph with an elegant editorial drop cap letter.</p>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_lead",
            name = "Lead Paragraph",
            className = ".oh-lead",
            category = "Text & Headings",
            description = "High-impact opening paragraph with enhanced readability",
            htmlCode = """<p class="oh-lead">Enter a compelling summary or introductory overview of your blog post here.</p>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_h2",
            name = "H2 Section Heading",
            className = ".oh-h2",
            category = "Text & Headings",
            description = "Primary section heading with signature accent underline",
            htmlCode = """<h2 class="oh-h2">Section Title Here</h2>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_h3",
            name = "H3 Subsection Heading",
            className = ".oh-h3",
            category = "Text & Headings",
            description = "Secondary subsection title with clean tracking",
            htmlCode = """<h3 class="oh-h3">Subsection Title Here</h3>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_alert_info",
            name = "Info Alert Box",
            className = ".oh-alert.oh-alert-info",
            category = "Alerts & Callouts",
            description = "Blue informational note box with icon",
            htmlCode = """<div class="oh-alert oh-alert-info"><div class="oh-alert-icon">ℹ️</div><div class="oh-alert-body"><strong>Note:</strong> Enter important informational notice here.</div></div>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_alert_success",
            name = "Success Tip Box",
            className = ".oh-alert.oh-alert-success",
            category = "Alerts & Callouts",
            description = "Green success tip box for pro recommendations",
            htmlCode = """<div class="oh-alert oh-alert-success"><div class="oh-alert-icon">✅</div><div class="oh-alert-body"><strong>Pro Tip:</strong> Enter your helpful tip or recommended best practice here.</div></div>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_alert_warning",
            name = "Warning Alert Box",
            className = ".oh-alert.oh-alert-warning",
            category = "Alerts & Callouts",
            description = "Amber caution notice to prevent reader mistakes",
            htmlCode = """<div class="oh-alert oh-alert-warning"><div class="oh-alert-icon">⚠️</div><div class="oh-alert-body"><strong>Caution:</strong> Enter cautionary notice or requirement details here.</div></div>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_alert_danger",
            name = "Danger / Important Box",
            className = ".oh-alert.oh-alert-danger",
            category = "Alerts & Callouts",
            description = "Red critical notice for vital reader precautions",
            htmlCode = """<div class="oh-alert oh-alert-danger"><div class="oh-alert-icon">⛔</div><div class="oh-alert-body"><strong>Important:</strong> Enter critical requirement or vital alert here.</div></div>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_btn_primary",
            name = "Primary Button",
            className = ".oh-btn.oh-btn-primary",
            category = "Buttons & CTAs",
            description = "High-contrast gold primary action button",
            htmlCode = """<a href="#" class="oh-btn oh-btn-primary">Click to Continue</a>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_btn_download",
            name = "Download Button",
            className = ".oh-btn.oh-btn-download",
            category = "Buttons & CTAs",
            description = "Green download button with icon",
            htmlCode = """<a href="#download" class="oh-btn oh-btn-download">⬇️ Download File</a>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_btn_demo",
            name = "Live Demo Button",
            className = ".oh-btn.oh-btn-demo",
            category = "Buttons & CTAs",
            description = "Blue interactive preview / demo button",
            htmlCode = """<a href="#demo" class="oh-btn oh-btn-demo">🚀 View Live Demo</a>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_btn_outline",
            name = "Outline Button",
            className = ".oh-btn.oh-btn-outline",
            category = "Buttons & CTAs",
            description = "Minimalist bordered action button",
            htmlCode = """<a href="#" class="oh-btn oh-btn-outline">Read More</a>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_blockquote",
            name = "Editorial Blockquote",
            className = ".oh-blockquote",
            category = "Quotes & Blocks",
            description = "Styled quote with left accent border and citation",
            htmlCode = """<blockquote class="oh-blockquote"><p>"Enter inspirational or authoritative quotation here."</p><cite class="oh-cite">— Author / Source Name</cite></blockquote>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_code_box",
            name = "Code Box Container",
            className = ".oh-code-box",
            category = "Code & Syntax",
            description = "Terminal-styled dark code container with header bar",
            htmlCode = """<div class="oh-code-box"><div class="oh-code-header"><span>HTML</span></div><pre class="oh-pre"><code>&lt;!-- Code snippet goes here --&gt;</code></pre></div>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_accordion",
            name = "Collapsible FAQ Accordion",
            className = ".oh-accordion",
            category = "Interactive & Tables",
            description = "Clean expandable details accordion for FAQs",
            htmlCode = """<details class="oh-accordion"><summary class="oh-summary">Frequently Asked Question Title?</summary><div class="oh-content"><p>Detailed answer and explanation goes here.</p></div></details>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_table",
            name = "Responsive Data Table",
            className = ".oh-table",
            category = "Interactive & Tables",
            description = "Striped, rounded responsive table with dark header",
            htmlCode = """<div class="oh-table-wrapper"><table class="oh-table"><thead><tr><th>Header 1</th><th>Header 2</th><th>Header 3</th></tr></thead><tbody><tr><td>Data 1</td><td>Data 2</td><td>Data 3</td></tr><tr><td>Data 4</td><td>Data 5</td><td>Data 6</td></tr></tbody></table></div>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_list_check",
            name = "Feature Checklist",
            className = ".oh-list-check",
            category = "Lists & Badges",
            description = "Bullet checklist with green checkmark icons",
            htmlCode = """<ul class="oh-list-check"><li>First key benefit or item</li><li>Second key benefit or item</li><li>Third key benefit or item</li></ul>"""
        ),
        OsunhiveUiSnippet(
            id = "oh_badges",
            name = "Badges & Marker",
            className = ".oh-badge, .oh-mark",
            category = "Lists & Badges",
            description = "Pill badges (Gold, Green, Blue) and text highlighter",
            htmlCode = """<span class="oh-badge oh-badge-gold">Featured</span> <span class="oh-badge oh-badge-green">Verified</span> <mark class="oh-mark">highlighted text</mark>"""
        )
    )
}
