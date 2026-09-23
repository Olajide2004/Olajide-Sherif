package com.example

import com.example.engine.CodeAutoCompleteEngine
import com.example.engine.ConversionOptions
import com.example.engine.DocumentFormattingPreset
import com.example.engine.DocumentToHtmlConverter
import com.example.model.CustomClassPresets
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testDocumentToHtmlConverter_basicFormatting() {
    val sampleText = """
      My Tech Journey
      
      This is the introduction paragraph that explains everything.
      
      Section 1: Architecture
      Here are the main points:
      - Point one
      - Point two
      
      > Important quote about modern development
    """.trimIndent()

    val options = ConversionOptions(
      preset = DocumentFormattingPreset.OSUNHIVE_MAGAZINE,
      addDropCap = true,
      addLeadParagraph = true,
      addJumpBreak = true,
      addOsunhiveBacklink = true
    )

    val html = DocumentToHtmlConverter.convertDocumentToBloggerHtml(sampleText, "Tech Post", options)

    assertTrue("Should contain document title", html.contains("Tech Post"))
    assertTrue("Should contain OsunHive lead class", html.contains("oh-lead"))
    assertTrue("Should contain dropcap class", html.contains("oh-dropcap"))
    assertTrue("Should contain Blogger jump break", html.contains("<!--more-->"))
    assertTrue("Should contain OsunHive backlink", html.contains("https://www.osunhive.name.ng"))
    assertTrue("Should format list", html.contains("oh-list"))
  }

  @Test
  fun testCodeAutoComplete_returnsSuggestions() {
    val suggestions = CodeAutoCompleteEngine.getSuggestions("<h", 2, CustomClassPresets.INITIAL_CUSTOM_CLASSES)
    assertTrue("Should suggest h2 tag or html elements", suggestions.isNotEmpty())
  }

  @Test
  fun testCustomClassPresets_hasInitialClasses() {
    val classes = CustomClassPresets.INITIAL_CUSTOM_CLASSES
    assertTrue("Should contain preset custom classes", classes.isNotEmpty())
    assertTrue("Should contain author box", classes.any { it.className == "custom-author-box" })
  }
}
