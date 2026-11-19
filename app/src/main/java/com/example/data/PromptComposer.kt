package com.example.data

import com.example.model.PickedFileInfo
import com.example.model.PromptFormula
import java.util.Calendar

data class PromptCompositionInput(
    val formula: PromptFormula,
    val signatureText: String,
    val showSignature: Boolean,
    val showDate: Boolean,
    val showFrame: Boolean,
    val selectedFrame: String,
    val variableValues: Map<String, String>,
    val contextText: String,
    val contextUrl: String,
    val pickedFiles: List<PickedFileInfo>,
    val formattedDate: String = PromptComposer.currentFormattedDate()
)

object PromptComposer {
    fun currentFormattedDate(calendar: Calendar = Calendar.getInstance()): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "${year}年${month}月${day}日"
    }

    fun compose(input: PromptCompositionInput): String {
        var result = input.formula.pattern
        val effectiveSignature = input.signatureText.trim().ifEmpty { "琦閔~祝福您" }

        if (input.showSignature) {
            result = result.replace(Regex("琦閔~祝福您|琦閔"), effectiveSignature)
        } else {
            result = result.replace(
                Regex("\n2\\. 圖片右下方必須顯示創意字體.*?的署名：「琦閔~祝福您」。"),
                ""
            )
            result = result.replace("影片右下角常駐浮水印：「琦閔」以及", "影片右下角常駐顯示")
            result = result.replace("影片右下角常駐浮水印：「琦閔」", "")
            result = result.replace(
                "\n結尾的 [Outro] 歌詞必須包含專屬署名：「願你擁有美好的一刻，這裡是琦閔的祝福。」",
                ""
            )
            result = result.replace("署名正下方清楚顯示今日日期", "圖片右下方清楚顯示今日日期")
        }

        if (input.showDate) {
            result = result.replace("{今日日期}", input.formattedDate)
        } else {
            result = result.replace("\n3. 署名正下方清楚顯示今日日期：{今日日期}。", "")
            result = result.replace("\n3. 圖片右下方清楚顯示今日日期：{今日日期}。", "")
            result = result.replace("以及今日日期：{今日日期}", "")
            result = result.replace("影片右下角常駐顯示今日日期：{今日日期}。", "")
        }

        if (input.showFrame && (input.showSignature || input.showDate)) {
            val frameStyle = input.selectedFrame.trim().ifEmpty { "隨機創意圖框" }
            val frameInstruction =
                "\n👉 【圖框特效排版】：請將上述署名與日期完美整合設計於一個「$frameStyle」之中，並強制設定該圖框大小為佔整體圖片空間版面的 1/8 尺寸，確保字體清晰且與整體視覺風格無縫融合。"

            result = when {
                result.contains("【文字與排版設定】") -> {
                    result.replace(
                        Regex("(【文字與排版設定】[\\s\\S]*?)(?=\\n\\n【|$)")
                    ) { match -> "${match.value}$frameInstruction" }
                }
                result.contains("【文字與後製指示 (若工具支援)】") -> {
                    result.replace(
                        Regex("(【文字與後製指示 \\(若工具支援\\)】[\\s\\S]*?)(?=\\n\\n【|$)")
                    ) { match -> "${match.value}$frameInstruction" }
                }
                else -> {
                    "$result\n\n【圖框特效排版】\n請將署名與日期完美整合設計於一個「$frameStyle」之中，並設定為整體版面 1/8 的尺寸。"
                }
            }
        }

        input.formula.variables.forEach { variable ->
            val selected = input.variableValues[variable.name]?.trim().orEmpty()
                .ifEmpty { variable.options.firstOrNull().orEmpty() }
            result = result.replace("{${variable.name}}", selected)
        }

        val trimmedText = input.contextText.trim()
        val trimmedUrl = input.contextUrl.trim()
        val hasFiles = input.pickedFiles.isNotEmpty()

        if (trimmedText.isNotEmpty() || trimmedUrl.isNotEmpty() || hasFiles) {
            val contextBuilder = StringBuilder("\n\n【附加情境與金句指定】\n")
            if (trimmedText.isNotEmpty()) {
                contextBuilder.append("- 核心內容：$trimmedText\n")
            }
            if (trimmedUrl.isNotEmpty()) {
                contextBuilder.append("- 風格參考：$trimmedUrl\n")
            }
            if (hasFiles) {
                val names = input.pickedFiles.joinToString("、") { it.displayName }
                contextBuilder.append("- 需分析上傳之檔案做為排版參考（$names）。\n")
            }
            result += contextBuilder.toString()
        }

        return result
    }

    fun suggestedFileName(formulaName: String, formattedDate: String): String {
        val timePrefix = when {
            formulaName.contains("早安") || formulaName.contains("晨間") -> "早安"
            formulaName.contains("午安") || formulaName.contains("午後") || formulaName.contains("下午茶") -> "午安"
            formulaName.contains("晚安") || formulaName.contains("夜景") || formulaName.contains("助眠") -> "晚安"
            formulaName.contains("生日") || formulaName.contains("慶生") || formulaName.contains("蛋糕") || formulaName.contains("禮物") -> "生日"
            else -> "問候"
        }
        return "AI${timePrefix}提示詞_${formattedDate}.txt"
    }
}
