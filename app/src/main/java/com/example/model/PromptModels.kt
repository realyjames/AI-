package com.example.model

data class PromptVariable(
    val name: String,
    val options: List<String>
)

data class PromptFormula(
    val id: String,
    val name: String,
    val pattern: String,
    val variables: List<PromptVariable>
)

data class PromptDimension(
    val id: String,
    val name: String,
    val formulas: List<PromptFormula>
)

enum class ContextSupplementTab(val label: String) {
    TEXT("指定文字"),
    URL("參考網址"),
    FILE("檔案模擬")
}

data class PickedFileInfo(
    val uriString: String,
    val displayName: String
)
