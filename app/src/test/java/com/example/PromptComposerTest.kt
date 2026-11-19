package com.example

import com.example.data.PromptComposer
import com.example.data.PromptCompositionInput
import com.example.model.PickedFileInfo
import com.example.model.PromptCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptComposerTest {

    @Test
    fun catalogHasAllDimensionsAndNonEmptyVariables() {
        assertEquals(7, PromptCatalog.dimensions.size)
        val allFormulas = PromptCatalog.dimensions.flatMap { it.formulas }
        assertEquals(12, allFormulas.size)
        allFormulas.forEach { formula ->
            assertTrue("Formula ${formula.id} must have variables", formula.variables.isNotEmpty())
            formula.variables.forEach { variable ->
                assertTrue("Variable ${variable.name} must have options", variable.options.isNotEmpty())
                assertTrue(
                    "Pattern of ${formula.id} must contain {${variable.name}}",
                    formula.pattern.contains("{${variable.name}}")
                )
            }
        }
    }

    @Test
    fun composeReplacesSignatureDateFrameAndVariables() {
        val formula = PromptCatalog.findFormulaById("morning_scenery")!!
        val result = PromptComposer.compose(
            PromptCompositionInput(
                formula = formula,
                signatureText = "小美~祝福您",
                showSignature = true,
                showDate = true,
                showFrame = true,
                selectedFrame = "水墨暈染漸層雲霧圖框",
                variableValues = mapOf(
                    "圖片長寬比例" to "16:9 (橫式)",
                    "媒材與渲染技術" to "東方水墨暈染禪意風格 (Zen ink wash painting)"
                ),
                contextText = "日出東方，萬事順遂",
                contextUrl = "https://example.com/ref",
                pickedFiles = listOf(PickedFileInfo("content://1", "sample.jpg")),
                formattedDate = "2026年11月18日"
            )
        )

        assertTrue(result.contains("小美~祝福您"))
        assertFalse(result.contains("琦閔~祝福您"))
        assertTrue(result.contains("2026年11月18日"))
        assertTrue(result.contains("水墨暈染漸層雲霧圖框"))
        assertTrue(result.contains("1/8 尺寸"))
        assertTrue(result.contains("16:9 (橫式)"))
        assertTrue(result.contains("【附加情境與金句指定】"))
        assertTrue(result.contains("日出東方，萬事順遂"))
        assertTrue(result.contains("https://example.com/ref"))
        assertTrue(result.contains("sample.jpg"))
        assertFalse(result.contains("{圖片長寬比例}"))
        assertFalse(result.contains("{今日日期}"))
    }

    @Test
    fun composeOmitsSignatureDateAndFrameWhenDisabled() {
        val formula = PromptCatalog.findFormulaById("evening_nightscape")!!
        val result = PromptComposer.compose(
            PromptCompositionInput(
                formula = formula,
                signatureText = "測試署名",
                showSignature = false,
                showDate = false,
                showFrame = true,
                selectedFrame = "隨機創意圖框",
                variableValues = emptyMap(),
                contextText = "",
                contextUrl = "",
                pickedFiles = emptyList(),
                formattedDate = "2026年11月18日"
            )
        )

        assertFalse(result.contains("測試署名"))
        assertFalse(result.contains("琦閔"))
        assertFalse(result.contains("2026年11月18日"))
        assertFalse(result.contains("圖框特效排版"))
        assertFalse(result.contains("附加情境與金句指定"))
    }

    @Test
    fun composeVideoAndVocalPatternsHandleSignatureAndDateToggles() {
        val videoFormula = PromptCatalog.findFormulaById("video_greeting")!!
        val videoNoSigWithDate = PromptComposer.compose(
            PromptCompositionInput(
                formula = videoFormula,
                signatureText = "小美",
                showSignature = false,
                showDate = true,
                showFrame = false,
                selectedFrame = "",
                variableValues = emptyMap(),
                contextText = "",
                contextUrl = "",
                pickedFiles = emptyList(),
                formattedDate = "2026年11月18日"
            )
        )
        assertTrue(videoNoSigWithDate.contains("影片右下角常駐顯示今日日期：2026年11月18日"))
        assertFalse(videoNoSigWithDate.contains("琦閔"))

        val vocalFormula = PromptCatalog.findFormulaById("music_vocal")!!
        val vocalNoSig = PromptComposer.compose(
            PromptCompositionInput(
                formula = vocalFormula,
                signatureText = "小美",
                showSignature = false,
                showDate = false,
                showFrame = false,
                selectedFrame = "",
                variableValues = emptyMap(),
                contextText = "",
                contextUrl = "",
                pickedFiles = emptyList(),
                formattedDate = "2026年11月18日"
            )
        )
        assertFalse(vocalNoSig.contains("結尾的 [Outro] 歌詞必須包含專屬署名"))
        assertFalse(vocalNoSig.contains("琦閔"))
    }

    @Test
    fun suggestedFileNameMatchesCategoryPrefix() {
        assertEquals("AI早安提示詞_2026年11月18日.txt", PromptComposer.suggestedFileName("絕美晨間風景", "2026年11月18日"))
        assertEquals("AI午安提示詞_2026年11月18日.txt", PromptComposer.suggestedFileName("下午茶與精緻甜點", "2026年11月18日"))
        assertEquals("AI晚安提示詞_2026年11月18日.txt", PromptComposer.suggestedFileName("室內療癒助眠", "2026年11月18日"))
        assertEquals("AI生日提示詞_2026年11月18日.txt", PromptComposer.suggestedFileName("奢華派對與蛋糕", "2026年11月18日"))
        assertEquals("AI問候提示詞_2026年11月18日.txt", PromptComposer.suggestedFileName("通用節日慶典", "2026年11月18日"))
    }
}
