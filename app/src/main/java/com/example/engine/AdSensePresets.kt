package com.example.engine

data class AdSenseAdUnit(
    val id: String,
    val title: String,
    val description: String,
    val format: String,
    val templateGenerator: (pubId: String, slotId: String) -> String
)

object AdSensePresets {

    const val DEFAULT_ADSENSE_URL = "https://www.google.com/adsense/"
    const val ADSENSE_LOGIN_URL = "https://www.google.com/adsense/new/u/0/"
    const val ADSENSE_SITES_URL = "https://www.google.com/adsense/new/u/0/sites"
    const val ADSENSE_REPORTS_URL = "https://www.google.com/adsense/new/u/0/reporting"
    const val ADSENSE_AD_UNITS_URL = "https://www.google.com/adsense/new/u/0/myads/units"

    val UNITS = listOf(
        AdSenseAdUnit(
            id = "unit_responsive_display",
            title = "Responsive Display Ad (Recommended)",
            description = "Automatically adjusts size across mobile, tablet, and desktop for maximum fill rate and CTR.",
            format = "Display",
            templateGenerator = { pubId, slotId ->
                """
<!-- Google AdSense - Responsive Display Unit -->
<div class="oh-ad-container" style="margin: 20px auto; text-align: center; overflow: hidden; min-height: 90px; background: #fafafa; border: 1px dashed #e2e8f0; border-radius: 8px; padding: 6px;">
  <span style="display: block; font-size: 10px; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.8px; margin-bottom: 4px;">Advertisement</span>
  <ins class="adsbygoogle"
       style="display:block"
       data-ad-client="$pubId"
       data-ad-slot="$slotId"
       data-ad-format="auto"
       data-full-width-responsive="true"></ins>
  <script>
       (adsbygoogle = window.adsbygoogle || []).push({});
  </script>
</div>
                """.trimIndent()
            }
        ),
        AdSenseAdUnit(
            id = "unit_in_article",
            title = "In-Article Native Ad Unit",
            description = "Blends seamlessly between post paragraphs and headers (H2/H3) with zero layout shift.",
            format = "In-Article",
            templateGenerator = { pubId, slotId ->
                """
<!-- Google AdSense - In-Article Native Unit -->
<div class="oh-ad-inarticle" style="margin: 24px auto; text-align: center; min-height: 120px; padding: 8px 0;">
  <ins class="adsbygoogle"
       style="display:block; text-align:center;"
       data-ad-layout="in-article"
       data-ad-format="fluid"
       data-ad-client="$pubId"
       data-ad-slot="$slotId"></ins>
  <script>
       (adsbygoogle = window.adsbygoogle || []).push({});
  </script>
</div>
                """.trimIndent()
            }
        ),
        AdSenseAdUnit(
            id = "unit_multiplex_grid",
            title = "Multiplex Grid (Matched Content)",
            description = "Grid-based recommendation ad unit ideal for the bottom of articles above comments.",
            format = "Multiplex",
            templateGenerator = { pubId, slotId ->
                """
<!-- Google AdSense - Multiplex Matched Content Grid -->
<div class="oh-ad-multiplex" style="margin: 28px auto; text-align: center; min-height: 250px; background: #ffffff; border-radius: 8px; padding: 12px 6px;">
  <span style="display: block; font-size: 11px; font-weight: bold; color: #475569; margin-bottom: 8px; text-align: left;">Recommended For You</span>
  <ins class="adsbygoogle"
       style="display:block"
       data-ad-format="autorelaxed"
       data-ad-client="$pubId"
       data-ad-slot="$slotId"></ins>
  <script>
       (adsbygoogle = window.adsbygoogle || []).push({});
  </script>
</div>
                """.trimIndent()
            }
        ),
        AdSenseAdUnit(
            id = "unit_auto_ads_head",
            title = "Auto Ads Header Script (<head>)",
            description = "Google AI automatically analyzes your Blogger layout and places ads in high-earning spots.",
            format = "Script",
            templateGenerator = { pubId, _ ->
                """
<!-- Google AdSense Auto Ads Script (Place inside <head>...</head>) -->
<script async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=$pubId"
     crossorigin="anonymous"></script>
                """.trimIndent()
            }
        )
    )
}
