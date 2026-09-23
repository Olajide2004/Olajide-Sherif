package com.example.engine

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.graphics.toColorInt

object BloggerChromeTabHelper {

    private const val DEFAULT_BLOGGER_URL = "https://draft.blogger.com/go/create-post"
    private const val BLOGGER_HOME_URL = "https://www.blogger.com/"

    fun openBloggerInChromeTab(
        context: Context,
        url: String = DEFAULT_BLOGGER_URL
    ) {
        try {
            val goldColor = "#D4AF37".toColorInt()
            val darkNavColor = "#121212".toColorInt()

            val defaultColorScheme = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(goldColor)
                .setNavigationBarColor(darkNavColor)
                .build()

            val customTabsIntent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(defaultColorScheme)
                .setShowTitle(true)
                .setUrlBarHidingEnabled(true)
                .build()

            // Try to set Chrome package directly
            customTabsIntent.intent.setPackage("com.android.chrome")
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            customTabsIntent.launchUrl(context, Uri.parse(url))
        } catch (_: Exception) {
            // Fallback without package restriction
            try {
                val goldColor = "#D4AF37".toColorInt()
                val fallbackTabs = CustomTabsIntent.Builder()
                    .setDefaultColorSchemeParams(
                        CustomTabColorSchemeParams.Builder().setToolbarColor(goldColor).build()
                    )
                    .setShowTitle(true)
                    .build()
                fallbackTabs.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                fallbackTabs.launchUrl(context, Uri.parse(url))
            } catch (_: Exception) {
                // Generic browser Intent fallback
                try {
                    val genericIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(genericIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Cannot open browser: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
