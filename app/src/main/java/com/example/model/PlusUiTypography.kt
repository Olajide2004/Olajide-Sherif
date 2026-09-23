package com.example.model

data class PlusUiSnippet(
    val id: String,
    val name: String,
    val className: String,
    val category: String,
    val description: String,
    val htmlCode: String
)

object PlusUiTypography {

    const val CAT_TEXT_LAYOUT = "Text and Layout"
    const val CAT_BUTTONS = "Buttons"
    const val CAT_DOWNLOAD_BOX = "Download Box"
    const val CAT_SAFELINK = "Safelink"
    const val CAT_ALERTS_NOTES = "Alerts and Notes"
    const val CAT_CODE = "Code"
    const val CAT_SPOILER_TOC = "Spoiler, Accordion, ToC"
    const val CAT_LISTS = "Lists"
    const val CAT_TABLES = "Tables"
    const val CAT_QUOTES = "Quotes"
    const val CAT_IMAGES_VIDEO = "Images and Video"
    const val CAT_TABS = "Tabs"
    const val CAT_PRODUCT_INFO = "Product and Info Blocks"
    const val CAT_QUICK_COPY = "Quick Copy Set"

    val CATEGORIES = listOf(
        CAT_IMAGES_VIDEO,
        CAT_TEXT_LAYOUT,
        CAT_BUTTONS,
        CAT_DOWNLOAD_BOX,
        CAT_SAFELINK,
        CAT_ALERTS_NOTES,
        CAT_CODE,
        CAT_SPOILER_TOC,
        CAT_LISTS,
        CAT_TABLES,
        CAT_QUOTES,
        CAT_TABS,
        CAT_PRODUCT_INFO,
        CAT_QUICK_COPY
    )

    val ALL_SNIPPETS = listOf(
        // 1. Images and Video (Highlight / Priority Category)
        PlusUiSnippet(
            id = "vid_yt_responsive",
            name = "YouTube Responsive (16:9)",
            className = "videoYt",
            category = CAT_IMAGES_VIDEO,
            description = "Responsive 16:9 YouTube video embed container",
            htmlCode = """<div class="videoYt"><iframe src="https://www.youtube.com/embed/VIDEO_ID" allowfullscreen></iframe></div>"""
        ),
        PlusUiSnippet(
            id = "vid_yt_4_3",
            name = "YouTube Video (4:3 Ratio)",
            className = "videoYt",
            category = CAT_IMAGES_VIDEO,
            description = "YouTube player with custom 4:3 aspect ratio style",
            htmlCode = """<div class="videoYt" style="--ratio:4/3"><iframe src="https://www.youtube.com/embed/VIDEO_ID" allowfullscreen></iframe></div>"""
        ),
        PlusUiSnippet(
            id = "img_full_width",
            name = "Full-Width Image",
            className = "full",
            category = CAT_IMAGES_VIDEO,
            description = "Edge-to-edge full width image",
            htmlCode = """<img class="full" src="IMAGE-URL" alt="Description"/>"""
        ),
        PlusUiSnippet(
            id = "img_zoomable",
            name = "Zoomable Image",
            className = "zmImg",
            category = CAT_IMAGES_VIDEO,
            description = "Click-to-zoom interactive image container",
            htmlCode = """<span class="zmImg"><img src="IMAGE-URL" alt="Description"/></span>"""
        ),
        PlusUiSnippet(
            id = "img_figure_caption",
            name = "Figure with Caption",
            className = "figure",
            category = CAT_IMAGES_VIDEO,
            description = "Image with small faded caption underneath",
            htmlCode = """<figure><img src="IMAGE-URL" alt="Description"/><figcaption>Caption text goes here</figcaption></figure>"""
        ),
        PlusUiSnippet(
            id = "img_caption_alt",
            name = "Image Caption (Alt Style)",
            className = "psCaption",
            category = CAT_IMAGES_VIDEO,
            description = "Caption text with psCaption style",
            htmlCode = """<span class="psCaption">Caption description</span>"""
        ),
        PlusUiSnippet(
            id = "img_grid_2col",
            name = "Grid 2-Columns",
            className = "psImg",
            category = CAT_IMAGES_VIDEO,
            description = "Two images displayed side by side per row",
            htmlCode = """<div class="psImg"><img src="IMAGE_URL_1" alt="Image 1"/><img src="IMAGE_URL_2" alt="Image 2"/></div>"""
        ),
        PlusUiSnippet(
            id = "img_grid_3col_scroll",
            name = "Grid 3-Columns (Scroll)",
            className = "psImg scImg",
            category = CAT_IMAGES_VIDEO,
            description = "Three images per row with mobile horizontal scrolling",
            htmlCode = """<div class="psImg scImg"><img src="IMAGE_URL_1" alt="1"/><img src="IMAGE_URL_2" alt="2"/><img src="IMAGE_URL_3" alt="3"/></div>"""
        ),
        PlusUiSnippet(
            id = "img_gallery_strip",
            name = "Gallery Strip (310px)",
            className = "glImg",
            category = CAT_IMAGES_VIDEO,
            description = "Horizontal scroll gallery strip (310px tall)",
            htmlCode = """<div class="glImg"><img src="IMAGE_URL_1"/><img src="IMAGE_URL_2"/><img src="IMAGE_URL_3"/></div>"""
        ),
        PlusUiSnippet(
            id = "img_gallery_h200",
            name = "Gallery Strip (200px Height)",
            className = "glImg h200",
            category = CAT_IMAGES_VIDEO,
            description = "Compact horizontal scrolling image strip (200px height)",
            htmlCode = """<div class="glImg h200"><img src="IMAGE_URL_1"/><img src="IMAGE_URL_2"/></div>"""
        ),

        // 2. Text and Layout
        PlusUiSnippet(
            id = "txt_h2",
            name = "Heading 2",
            className = "h2",
            category = CAT_TEXT_LAYOUT,
            description = "Bold major section heading with spacing",
            htmlCode = """<h2>Section Title</h2>"""
        ),
        PlusUiSnippet(
            id = "txt_h2_prefix",
            name = "Heading with Prefix",
            className = "data-before",
            category = CAT_TEXT_LAYOUT,
            description = "Heading with automated prefix symbol",
            htmlCode = """<h2 data-before="#">Section Title</h2>"""
        ),
        PlusUiSnippet(
            id = "txt_h2_suffix",
            name = "Heading with Suffix (NEW)",
            className = "data-after",
            category = CAT_TEXT_LAYOUT,
            description = "Heading with tag or badge suffix",
            htmlCode = """<h2 data-after="NEW">Section Title</h2>"""
        ),
        PlusUiSnippet(
            id = "txt_p_indent",
            name = "Paragraph Indent",
            className = "pIndent",
            category = CAT_TEXT_LAYOUT,
            description = "First line indented paragraph for essay format",
            htmlCode = """<p class="pIndent">Your paragraph with indented opening line starts here.</p>"""
        ),
        PlusUiSnippet(
            id = "txt_dropcap",
            name = "Drop Cap",
            className = "dropCap",
            category = CAT_TEXT_LAYOUT,
            description = "Large editorial initial letter",
            htmlCode = """<span class="dropCap">T</span>he rest of your article narrative continues smoothly from this large drop cap."""
        ),
        PlusUiSnippet(
            id = "txt_hr_divider",
            name = "Divider (Dots)",
            className = "hr",
            category = CAT_TEXT_LAYOUT,
            description = "Centered dots visual post break",
            htmlCode = """<hr/>"""
        ),
        PlusUiSnippet(
            id = "txt_pref_source",
            name = "Reference Text (Source)",
            className = "pRef",
            category = CAT_TEXT_LAYOUT,
            description = "Small, faded reference or source line",
            htmlCode = """<span class="pRef">Source: Author / Research publication</span>"""
        ),
        PlusUiSnippet(
            id = "txt_kbd",
            name = "Keyboard Key (<kbd>)",
            className = "kbd",
            category = CAT_TEXT_LAYOUT,
            description = "Tactile keycap style for shortcuts",
            htmlCode = """<kbd>Ctrl</kbd> + <kbd>C</kbd>"""
        ),
        PlusUiSnippet(
            id = "txt_inline_code",
            name = "Inline Code (<code>)",
            className = "code",
            category = CAT_TEXT_LAYOUT,
            description = "Highlighted monospace inline snippet",
            htmlCode = """<code>myFunction()</code>"""
        ),
        PlusUiSnippet(
            id = "txt_extl_arrow",
            name = "External Link (Arrow)",
            className = "extL",
            category = CAT_TEXT_LAYOUT,
            description = "External link with an arrow indicator icon",
            htmlCode = """<a class="extL" href="https://example.com" target="_blank" rel="noopener">Visit Resource</a>"""
        ),
        PlusUiSnippet(
            id = "txt_extl_chain",
            name = "External Link (Chain)",
            className = "extL alt",
            category = CAT_TEXT_LAYOUT,
            description = "External link with chain link icon",
            htmlCode = """<a class="extL alt" href="https://example.com" target="_blank" rel="noopener">Direct Link</a>"""
        ),
        PlusUiSnippet(
            id = "txt_extl_sec",
            name = "External Link (Secure)",
            className = "extL sec",
            category = CAT_TEXT_LAYOUT,
            description = "External link with security lock icon",
            htmlCode = """<a class="extL sec" href="https://example.com" target="_blank" rel="noopener">Secure Portal</a>"""
        ),

        // 3. Buttons
        PlusUiSnippet(
            id = "btn_solid",
            name = "Solid Button",
            className = "button",
            category = CAT_BUTTONS,
            description = "Clean solid primary theme button",
            htmlCode = """<a class="button" href="URL">Get Started</a>"""
        ),
        PlusUiSnippet(
            id = "btn_outline",
            name = "Outline Button",
            className = "button ln",
            category = CAT_BUTTONS,
            description = "Bordered transparent outline button",
            htmlCode = """<a class="button ln" href="URL">Read Details</a>"""
        ),
        PlusUiSnippet(
            id = "btn_download_icon",
            name = "Download Button (Icon)",
            className = "button",
            category = CAT_BUTTONS,
            description = "Solid button with cloud download icon",
            htmlCode = """<a class="button" href="URL"><i class="icon dl"></i>Download Now</a>"""
        ),
        PlusUiSnippet(
            id = "btn_demo_icon",
            name = "Demo Button (Icon)",
            className = "button",
            category = CAT_BUTTONS,
            description = "Button with paper-plane demo icon",
            htmlCode = """<a class="button" href="URL"><i class="icon demo"></i>Live Demo</a>"""
        ),
        PlusUiSnippet(
            id = "btn_outline_icon",
            name = "Outline + Download Icon",
            className = "button ln",
            category = CAT_BUTTONS,
            description = "Outline button featuring download icon",
            htmlCode = """<a class="button ln" href="URL"><i class="icon dl"></i>Download</a>"""
        ),
        PlusUiSnippet(
            id = "btn_row_group",
            name = "Button Row (btnF)",
            className = "btnF",
            category = CAT_BUTTONS,
            description = "Centered paired action row (Download + Demo)",
            htmlCode = """<div class="btnF"><a class="button" href="URL"><i class="icon dl"></i>Download</a><a class="button ln" href="URL"><i class="icon demo"></i>Demo</a></div>"""
        ),

        // 4. Download Box
        PlusUiSnippet(
            id = "dlbox_standard",
            name = "Download Box (ZIP)",
            className = "dlBox",
            category = CAT_DOWNLOAD_BOX,
            description = "File badge + file name + size + download button",
            htmlCode = """<div class="dlBox">
  <div class="fT" data-text="ZIP"></div>
  <div class="fN">
    <span>File-name.zip</span>
    <span class="fS">12 MB</span>
  </div>
  <a class="button" href="DOWNLOAD-URL" aria-label="Download"><i class="icon dl"></i></a>
</div>"""
        ),
        PlusUiSnippet(
            id = "dlbox_apk",
            name = "Download Box (APK)",
            className = "dlBox",
            category = CAT_DOWNLOAD_BOX,
            description = "APK download card with icon button",
            htmlCode = """<div class="dlBox">
  <div class="fT" data-text="APK"></div>
  <div class="fN">
    <span>BloggerAutoTyper-v1.0.0.apk</span>
    <span class="fS">24 MB</span>
  </div>
  <a class="button" href="DOWNLOAD-URL" aria-label="Download"><i class="icon dl"></i></a>
</div>"""
        ),
        PlusUiSnippet(
            id = "dlbox_safelink",
            name = "Download Box + Safelink",
            className = "dlBox safeL",
            category = CAT_DOWNLOAD_BOX,
            description = "Download box routed through template Safelink countdown",
            htmlCode = """<div class="dlBox">
  <div class="fT" data-text="ZIP"></div>
  <div class="fN">
    <span>Archive-Package.zip</span>
    <span class="fS">18 MB</span>
  </div>
  <a class="button safeL" href="REAL-DOWNLOAD-URL" aria-label="Download"><i class="icon dl"></i></a>
</div>"""
        ),
        PlusUiSnippet(
            id = "dlbox_thumb",
            name = "Download Box (Thumbnail)",
            className = "dlBox fT lazy",
            category = CAT_DOWNLOAD_BOX,
            description = "Download box with image thumbnail instead of text",
            htmlCode = """<div class="dlBox">
  <div class="fT lazy" style="background-image:url(IMAGE-URL)"></div>
  <div class="fN">
    <span>Graphic-Asset.zip</span>
    <span class="fS">45 MB</span>
  </div>
  <a class="button safeL" href="DOWNLOAD-URL" aria-label="Download"><i class="icon dl"></i></a>
</div>"""
        ),

        // 5. Safelink
        PlusUiSnippet(
            id = "safel_btn",
            name = "Safelink Button",
            className = "button safeL",
            category = CAT_SAFELINK,
            description = "Button linking through automated countdown page",
            htmlCode = """<a class="button safeL" href="REAL-URL">Download via Safelink</a>"""
        ),
        PlusUiSnippet(
            id = "safel_btn_icon",
            name = "Safelink + Download Icon",
            className = "button safeL",
            category = CAT_SAFELINK,
            description = "Safelink button with cloud download icon",
            htmlCode = """<a class="button safeL" href="REAL-URL"><i class="icon dl"></i>Download (Safelink)</a>"""
        ),
        PlusUiSnippet(
            id = "safel_text",
            name = "Safelink Text Link",
            className = "safeL",
            category = CAT_SAFELINK,
            description = "Plain text link routing to countdown page",
            htmlCode = """<a class="safeL" href="REAL-URL">Get file via Safelink</a>"""
        ),
        PlusUiSnippet(
            id = "safel_data_button",
            name = "Safelink Button (data-href)",
            className = "safeL",
            category = CAT_SAFELINK,
            description = "Non-link button with data-href attribute",
            htmlCode = """<button class="safeL" data-href="REAL-URL">Download</button>"""
        ),

        // 6. Alerts and Notes
        PlusUiSnippet(
            id = "alert_info",
            name = "Alert Info (Blue)",
            className = "alert info",
            category = CAT_ALERTS_NOTES,
            description = "Blue info box for highlights and notes",
            htmlCode = """<div class="alert info"><strong>Info</strong> Essential information or update note.</div>"""
        ),
        PlusUiSnippet(
            id = "alert_success",
            name = "Alert Success (Green)",
            className = "alert success",
            category = CAT_ALERTS_NOTES,
            description = "Green success callout box",
            htmlCode = """<div class="alert success"><strong>Success</strong> Action completed successfully!</div>"""
        ),
        PlusUiSnippet(
            id = "alert_warning",
            name = "Alert Warning (Yellow)",
            className = "alert warning",
            category = CAT_ALERTS_NOTES,
            description = "Yellow warning callout box",
            htmlCode = """<div class="alert warning"><strong>Warning</strong> Please take note of this advisory.</div>"""
        ),
        PlusUiSnippet(
            id = "alert_error",
            name = "Alert Error (Red)",
            className = "alert error",
            category = CAT_ALERTS_NOTES,
            description = "Red error or critical advisory box",
            htmlCode = """<div class="alert error"><strong>Error</strong> Critical error or requirement not met.</div>"""
        ),
        PlusUiSnippet(
            id = "alert_plain",
            name = "Alert Plain (Accent)",
            className = "alert",
            category = CAT_ALERTS_NOTES,
            description = "Accent colored box without icons",
            htmlCode = """<div class="alert">General informational notice for readers.</div>"""
        ),
        PlusUiSnippet(
            id = "alert_outline",
            name = "Alert Outline (Bordered)",
            className = "alert info outline",
            category = CAT_ALERTS_NOTES,
            description = "Bordered notice box with transparent background",
            htmlCode = """<div class="alert info outline">Bordered notification without background fill.</div>"""
        ),
        PlusUiSnippet(
            id = "note_standard",
            name = "Note (* Icon)",
            className = "note",
            category = CAT_ALERTS_NOTES,
            description = "Blue editorial note with an asterisk icon",
            htmlCode = """<p class="note">Helpful editorial note to clarify post details.</p>"""
        ),
        PlusUiSnippet(
            id = "note_warning",
            name = "Note Warning (! Icon)",
            className = "note wr",
            category = CAT_ALERTS_NOTES,
            description = "Red cautionary note with an exclamation icon",
            htmlCode = """<p class="note wr">Important warning regarding this configuration.</p>"""
        ),

        // 7. Code
        PlusUiSnippet(
            id = "code_block",
            name = "Code Block (<pre><code>)",
            className = "pre code",
            category = CAT_CODE,
            description = "Dark code container with top bar",
            htmlCode = """<pre><code>// Insert code here
console.log("Hello Blogger");</code></pre>"""
        ),
        PlusUiSnippet(
            id = "code_highlight_lang",
            name = "Code with Language",
            className = "language-html",
            category = CAT_CODE,
            description = "Syntax highlighted code block",
            htmlCode = """<pre><code class="language-html">&lt;div class="container"&gt;Content&lt;/div&gt;</code></pre>"""
        ),
        PlusUiSnippet(
            id = "code_header_label",
            name = "Code + Header Label",
            className = "data-result-language",
            category = CAT_CODE,
            description = "Code block showing language label in header",
            htmlCode = """<pre data-result-language="HTML"><code>&lt;p&gt;Example markup&lt;/p&gt;</code></pre>"""
        ),
        PlusUiSnippet(
            id = "code_header_comment",
            name = "Code + File Name / Comment",
            className = "data-comment",
            category = CAT_CODE,
            description = "Header bar displaying filename e.g. index.html",
            htmlCode = """<pre data-comment="index.html"><code>&lt;!DOCTYPE html&gt;</code></pre>"""
        ),
        PlusUiSnippet(
            id = "code_footer_source",
            name = "Code + Footer Source",
            className = "data-source",
            category = CAT_CODE,
            description = "Code block with bottom footer source reference",
            htmlCode = """<pre data-source="Source: github.com/user/repo"><code>git clone https://github.com/user/repo.git</code></pre>"""
        ),
        PlusUiSnippet(
            id = "code_mark_highlight",
            name = "Code Mark (Highlight)",
            className = "mark",
            category = CAT_CODE,
            description = "Blinking dashed underline inside code",
            htmlCode = """<pre><code>val key = <mark>YOUR_API_KEY</mark>;</code></pre>"""
        ),
        PlusUiSnippet(
            id = "code_mark_colors",
            name = "Code Mark Color (Red/Blue/Gold)",
            className = "mark red",
            category = CAT_CODE,
            description = "Colored highlighted text inside code",
            htmlCode = """<pre><code><mark class="gold">val featured = true;</mark></code></pre>"""
        ),
        PlusUiSnippet(
            id = "code_mark_block",
            name = "Code Mark Block",
            className = "mark block blue",
            category = CAT_CODE,
            description = "Solid colored background block highlight in code",
            htmlCode = """<pre><code><mark class="block blue">importantFunction();</mark></code></pre>"""
        ),

        // 8. Spoiler, Accordion, ToC
        PlusUiSnippet(
            id = "sp_spoiler",
            name = "Spoiler (Click to Reveal)",
            className = "sp",
            category = CAT_SPOILER_TOC,
            description = "Collapsible spoiler container",
            htmlCode = """<details class="sp"><summary>Click to reveal</summary><p>Hidden content revealed upon tap.</p></details>"""
        ),
        PlusUiSnippet(
            id = "sp_spoiler_labels",
            name = "Spoiler (Show / Hide Labels)",
            className = "sp",
            category = CAT_SPOILER_TOC,
            description = "Spoiler with custom Show/Hide summary text",
            htmlCode = """<details class="sp"><summary data-show="Show Details" data-hide="Hide Details">Additional Information</summary><p>Expanded details here.</p></details>"""
        ),
        PlusUiSnippet(
            id = "sp_accordion",
            name = "Accordion (Arrow Toggle)",
            className = "ac",
            category = CAT_SPOILER_TOC,
            description = "Clean accordion with arrow indicator",
            htmlCode = """<details class="ac"><summary>Frequently Asked Question?</summary><p>Answer text explaining the query in full detail.</p></details>"""
        ),
        PlusUiSnippet(
            id = "sp_accordion_plus_minus",
            name = "Accordion (+ / - Toggle)",
            className = "ac alt",
            category = CAT_SPOILER_TOC,
            description = "Accordion using plus and minus icons",
            htmlCode = """<details class="ac alt"><summary>Toggle Option</summary><p>Content details.</p></details>"""
        ),
        PlusUiSnippet(
            id = "sp_accordion_group",
            name = "Accordion Group (showH)",
            className = "showH",
            category = CAT_SPOILER_TOC,
            description = "Joined list of multiple accordions",
            htmlCode = """<div class="showH">
  <details class="ac"><summary>Question 1</summary><p>Answer 1</p></details>
  <details class="ac"><summary>Question 2</summary><p>Answer 2</p></details>
</div>"""
        ),
        PlusUiSnippet(
            id = "sp_toc_auto",
            name = "Table of Contents (Auto-Built)",
            className = "sp toc",
            category = CAT_SPOILER_TOC,
            description = "Auto-generated TOC from post headings via template JS",
            htmlCode = """<details class="sp toc"><summary>Table of Contents</summary><div class="aToc"></div></details>"""
        ),
        PlusUiSnippet(
            id = "sp_toc_bullets",
            name = "ToC (Bullet List Style)",
            className = "sp toc s1",
            category = CAT_SPOILER_TOC,
            description = "TOC formatted with bullet points",
            htmlCode = """<details class="sp toc s1"><summary>Contents</summary><div class="aToc"></div></details>"""
        ),
        PlusUiSnippet(
            id = "sp_related_posts",
            name = "Auto Related Posts (arp)",
            className = "sp arp",
            category = CAT_SPOILER_TOC,
            description = "Loads related posts dynamically by label",
            htmlCode = """<details class="sp arp"><summary>Related Posts</summary><div class="aRel" data-label="Tutorial" data-max-results="5"></div></details>"""
        ),

        // 9. Lists
        PlusUiSnippet(
            id = "list_steps",
            name = "Steps List (Connected Circles)",
            className = "steps",
            category = CAT_LISTS,
            description = "Numbered circular badges connected with vertical lines",
            htmlCode = """<ol class="steps">
  <li>First milestone or setup instruction</li>
  <li>Second stage of the implementation</li>
  <li>Final verification and launch</li>
</ol>"""
        ),
        PlusUiSnippet(
            id = "list_pros",
            name = "Pros List (+ Markers)",
            className = "pros",
            category = CAT_LISTS,
            description = "List featuring green plus markers",
            htmlCode = """<ul class="pros">
  <li>Lightning fast execution</li>
  <li>High reliability and zero bloat</li>
  <li>Fully responsive across mobile devices</li>
</ul>"""
        ),
        PlusUiSnippet(
            id = "list_cons",
            name = "Cons List (- Markers)",
            className = "cons",
            category = CAT_LISTS,
            description = "List featuring red negative markers",
            htmlCode = """<ul class="cons">
  <li>Requires initial setup</li>
  <li>HTML view mandatory in Blogger editor</li>
</ul>"""
        ),
        PlusUiSnippet(
            id = "list_no_bullets",
            name = "Plain List (noList)",
            className = "noList",
            category = CAT_LISTS,
            description = "List without bullet points",
            htmlCode = """<ul class="noList">
  <li>Item A</li>
  <li>Item B</li>
</ul>"""
        ),

        // 10. Tables
        PlusUiSnippet(
            id = "tbl_full_styled",
            name = "Table (Bordered + Striped + Hover)",
            className = "table bordered stripped hovered",
            category = CAT_TABLES,
            description = "Complete styled responsive table container",
            htmlCode = """<div class="table bordered stripped hovered">
  <table>
    <thead>
      <tr><th>Name</th><th>Version</th><th>Size</th></tr>
    </thead>
    <tbody>
      <tr><td>Blogger Auto Typer</td><td>1.0.0</td><td>24 MB</td></tr>
      <tr><td>Plus UI Theme</td><td>3.7.0</td><td>1.2 MB</td></tr>
    </tbody>
  </table>
</div>"""
        ),
        PlusUiSnippet(
            id = "tbl_primary_header",
            name = "Table with Primary Header",
            className = "table primary",
            category = CAT_TABLES,
            description = "Table with theme primary colored header",
            htmlCode = """<div class="table primary bordered">
  <table>
    <thead><tr><th>Item</th><th>Detail</th></tr></thead>
    <tbody><tr><td>Row 1</td><td>Value 1</td></tr></tbody>
  </table>
</div>"""
        ),
        PlusUiSnippet(
            id = "tbl_sticky",
            name = "Table (Sticky Header)",
            className = "table sticky mh300",
            category = CAT_TABLES,
            description = "Scrollable table with frozen header row",
            htmlCode = """<div class="table bordered sticky mh300">
  <table>
    <thead><tr><th>Param</th><th>Default</th><th>Description</th></tr></thead>
    <tbody><tr><td>speed</td><td>20</td><td>Typing characters per second</td></tr></tbody>
  </table>
</div>"""
        ),

        // 11. Quotes
        PlusUiSnippet(
            id = "quote_default",
            name = "Blockquote (Left Border)",
            className = "blockquote",
            category = CAT_QUOTES,
            description = "Standard blockquote with left border accent",
            htmlCode = """<blockquote>Editorial quote highlighting a crucial sentence.</blockquote>"""
        ),
        PlusUiSnippet(
            id = "quote_s1",
            name = "Blockquote Style 1 (Tinted)",
            className = "s1",
            category = CAT_QUOTES,
            description = "Colored border with subtle tinted background",
            htmlCode = """<blockquote class="s1"><p>Focus on writing great content; the formatting handles itself.</p><span>Author Name</span></blockquote>"""
        ),
        PlusUiSnippet(
            id = "quote_s2",
            name = "Blockquote Style 2 (Large Marks)",
            className = "s2",
            category = CAT_QUOTES,
            description = "Stylized quotation marks with top and bottom borders",
            htmlCode = """<blockquote class="s2"><p>Simplicity is the ultimate sophistication.</p><span>Leonardo da Vinci</span></blockquote>"""
        ),

        // 12. Tabs
        PlusUiSnippet(
            id = "tabs_radio_block",
            name = "Tabs (Interactive Panel)",
            className = "tabs",
            category = CAT_TABS,
            description = "Pure CSS switchable radio tabs",
            htmlCode = """<div class="tabs">
  <input id="t-0" type="radio" name="tabs1" checked="checked"/>
  <input id="t-1" type="radio" name="tabs1"/>
  <div>
    <label for="t-0" data-text="Tab One"></label>
    <label for="t-1" data-text="Tab Two"></label>
  </div>
  <div class="c-0">Content of tab one</div>
  <div class="c-1">Content of tab two</div>
</div>"""
        ),
        PlusUiSnippet(
            id = "tabs_sticky",
            name = "Sticky Tab Bar",
            className = "tabs stick",
            category = CAT_TABS,
            description = "Tabs with bar that sticks beneath post header",
            htmlCode = """<div class="tabs stick">
  <input id="t-0" type="radio" name="tabs2" checked="checked"/>
  <input id="t-1" type="radio" name="tabs2"/>
  <div>
    <label for="t-0" data-text="Overview"></label>
    <label for="t-1" data-text="Specifications"></label>
  </div>
  <div class="c-0">Overview content</div>
  <div class="c-1">Specifications content</div>
</div>"""
        ),
        PlusUiSnippet(
            id = "tabs_code_multi",
            name = "Multi-Code Tabs (HTML/CSS/JS)",
            className = "pre tabs",
            category = CAT_TABS,
            description = "Code blocks organized inside switchable tabs",
            htmlCode = """<div class="pre">
  <div class="tabs">
    <input id="c-0" type="radio" name="ctabs" checked="checked"/>
    <input id="c-1" type="radio" name="ctabs"/>
    <div>
      <label for="c-0" data-text="HTML"></label>
      <label for="c-1" data-text="CSS"></label>
    </div>
    <div class="c-0"><pre><code>&lt;div class="box"&gt;&lt;/div&gt;</code></pre></div>
    <div class="c-1"><pre><code>.box { padding: 10px; }</code></pre></div>
  </div>
</div>"""
        ),

        // 13. Product and Info Blocks
        PlusUiSnippet(
            id = "pinfo_price",
            name = "Price Callout",
            className = "pPric",
            category = CAT_PRODUCT_INFO,
            description = "Large colored price display with small label",
            htmlCode = """<div class="pPric" data-text="Price">$9.99</div>"""
        ),
        PlusUiSnippet(
            id = "pinfo_columns",
            name = "Info Columns (2-Column)",
            className = "pInfo",
            category = CAT_PRODUCT_INFO,
            description = "Side-by-side key specifications block",
            htmlCode = """<div class="pInfo"><div><small data-text="Version"></small>1.0.0</div><div><small data-text="Size"></small>24 MB</div></div>"""
        ),
        PlusUiSnippet(
            id = "pinfo_stacked",
            name = "Info (Stacked Rows)",
            className = "pInfo o",
            category = CAT_PRODUCT_INFO,
            description = "Single-column stacked info list",
            htmlCode = """<div class="pInfo o"><div><small data-text="License"></small>Free / Open Source</div><div><small data-text="Developer"></small>Olajide Sherif</div></div>"""
        ),
        PlusUiSnippet(
            id = "pinfo_store_links",
            name = "Store Links (pMart)",
            className = "pMart",
            category = CAT_PRODUCT_INFO,
            description = "Row of store / platform badge links",
            htmlCode = """<div class="pMart"><small>Available on</small><a href="URL"><img src="https://via.placeholder.com/120x40" alt="Platform"/></a></div>"""
        ),
        PlusUiSnippet(
            id = "pinfo_padding_block",
            name = "Padding Block (pPad)",
            className = "pPad",
            category = CAT_PRODUCT_INFO,
            description = "10px top and bottom breathing padding",
            htmlCode = """<div class="pPad"><p>Spaced section content.</p></div>"""
        ),
        PlusUiSnippet(
            id = "pinfo_related_box",
            name = "Related Links Box (pRelate)",
            className = "pRelate",
            category = CAT_PRODUCT_INFO,
            description = "Bordered 'Read also' callout with links list",
            htmlCode = """<div class="pRelate"><b>Read also:</b><ul><li><a href="URL">Previous Tutorial Article</a></li><li><a href="URL">Theme Installation Guide</a></li></ul></div>"""
        ),

        // 14. Quick Copy Set
        PlusUiSnippet(
            id = "quick_complete_set",
            name = "Complete Post Bundle",
            className = "quickSet",
            category = CAT_QUICK_COPY,
            description = "Starter template containing headings, video, download box, and safelink",
            htmlCode = """<h2>Introduction</h2>
<p class="pIndent"><span class="dropCap">W</span>elcome to today's complete guide. Everything here is formatted with clean Plus UI 3.7.0 classes for maximum readability.</p>

<div class="videoYt"><iframe src="https://www.youtube.com/embed/dQw4w9WgXcQ" allowfullscreen></iframe></div>

<div class="alert info"><strong>Notice:</strong> Switch Blogger to HTML View before inserting these snippets.</div>

<div class="dlBox">
  <div class="fT" data-text="ZIP"></div>
  <div class="fN"><span>Resource-Package.zip</span><span class="fS">12 MB</span></div>
  <a class="button safeL" href="https://example.com/download" aria-label="Download"><i class="icon dl"></i></a>
</div>

<details class="sp"><summary>Frequently Asked Questions</summary><p>This post is optimized for Blogger HTML view.</p></details>"""
        )
    )

    fun getSnippetsForCategory(category: String): List<PlusUiSnippet> {
        return ALL_SNIPPETS.filter { it.category == category }
    }
}
