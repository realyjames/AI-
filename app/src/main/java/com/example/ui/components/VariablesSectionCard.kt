package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.PromptVariable
import com.example.ui.PromptUiState

@Composable
fun VariablesSectionCard(
    uiState: PromptUiState,
    onSelectOption: (String, String) -> Unit,
    onAddCustomOption: (PromptVariable, String) -> Unit,
    onDeleteSelectedOption: (PromptVariable) -> Unit,
    onRestoreDefaultOptions: (PromptVariable, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val variables = uiState.selectedFormula.variables
    if (variables.isEmpty()) return

    var activePickerVariable by remember { mutableStateOf<PromptVariable?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle(
                icon = Icons.Filled.Palette,
                title = "🎨 巨量參數設定"
            )

            Spacer(modifier = Modifier.height(14.dp))

            variables.forEachIndexed { index, variable ->
                val options = uiState.optionsForVariable(variable)
                val selectedValue = uiState.selectedValueForVariable(variable)
                val isTranslating = uiState.translatingVariableName == variable.name

                VariableSelectorRow(
                    index = index,
                    variable = variable,
                    optionsCount = options.size,
                    selectedValue = selectedValue,
                    isTranslating = isTranslating,
                    onOpenPicker = { activePickerVariable = variable },
                    onAddCustom = { input -> onAddCustomOption(variable, input) },
                    onDeleteSelected = { onDeleteSelectedOption(variable) },
                    onRestoreDefaults = { keepCustom -> onRestoreDefaultOptions(variable, keepCustom) }
                )

                if (index < variables.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

    activePickerVariable?.let { targetVar ->
        val options = uiState.optionsForVariable(targetVar)
        val selected = uiState.selectedValueForVariable(targetVar)
        val isTranslating = uiState.translatingVariableName == targetVar.name

        OptionPickerBottomSheet(
            title = "選擇「${targetVar.name}」",
            options = options,
            selectedOption = selected,
            addPlaceholder = "輸入中文，系統將自動翻譯加上 (英文)...",
            isTranslating = isTranslating,
            onSelect = { chosen -> onSelectOption(targetVar.name, chosen) },
            onAddCustom = { raw -> onAddCustomOption(targetVar, raw) },
            onDismiss = { activePickerVariable = null }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VariableSelectorRow(
    index: Int,
    variable: PromptVariable,
    optionsCount: Int,
    selectedValue: String,
    isTranslating: Boolean,
    onOpenPicker: () -> Unit,
    onAddCustom: (String) -> Unit,
    onDeleteSelected: () -> Unit,
    onRestoreDefaults: (Boolean) -> Unit
) {
    var isEditing by remember(variable.name) { mutableStateOf(false) }
    var customInput by remember(variable.name) { mutableStateOf("") }
    var pendingConfirm by remember { mutableStateOf<VarConfirmAction?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🔹 ${variable.name} (自訂清單)",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${optionsCount} 項",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onOpenPicker)
                    .minimumInteractiveComponentSize()
                    .testTag("var_selector_$index")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedValue,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "開啟${variable.name}選項清單"
                    )
                }
            }

            OutlinedButton(
                onClick = { isEditing = !isEditing },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("var_edit_button_$index")
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Filled.Close else Icons.Filled.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isEditing) "取消" else "編輯")
            }
        }

        AnimatedVisibility(visible = isEditing) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customInput,
                        onValueChange = { customInput = it },
                        placeholder = { Text("輸入中文，自動翻譯並加上 (英文)...") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("var_custom_input_$index")
                    )
                    Button(
                        onClick = {
                            if (customInput.isNotBlank()) {
                                onAddCustom(customInput)
                                customInput = ""
                            }
                        },
                        enabled = !isTranslating,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("var_add_button_$index")
                    ) {
                        if (isTranslating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("翻譯中")
                        } else {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("新增")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { pendingConfirm = VarConfirmAction.DELETE },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        modifier = Modifier.testTag("var_delete_button_$index")
                    ) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("刪除選項")
                    }
                    OutlinedButton(
                        onClick = { pendingConfirm = VarConfirmAction.RESTORE_KEEP },
                        modifier = Modifier.testTag("var_restore_keep_button_$index")
                    ) {
                        Icon(Icons.Filled.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("復原(保留新增)")
                    }
                    OutlinedButton(
                        onClick = { pendingConfirm = VarConfirmAction.RESTORE_CLEAR },
                        modifier = Modifier.testTag("var_restore_clear_button_$index")
                    ) {
                        Icon(Icons.Filled.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("復原(清空新增)")
                    }
                }
            }
        }
    }

    pendingConfirm?.let { action ->
        val (title, body, confirmText) = when (action) {
            VarConfirmAction.DELETE -> Triple(
                "刪除參數選項",
                "確定要將「$selectedValue」從【${variable.name}】清單中永久刪除嗎？",
                "刪除"
            )
            VarConfirmAction.RESTORE_KEEP -> Triple(
                "復原預設清單",
                "確定要恢復【${variable.name}】的系統預設選項嗎？（您自行新增的選項將會被保留）",
                "確定恢復"
            )
            VarConfirmAction.RESTORE_CLEAR -> Triple(
                "重置參數清單",
                "警告：確定要將【${variable.name}】完全恢復為系統最初預設值嗎？（您自行新增的選項將會被永久清除！）",
                "完全重置"
            )
        }
        AlertDialog(
            onDismissRequest = { pendingConfirm = null },
            title = { Text(title) },
            text = { Text(body) },
            confirmButton = {
                TextButton(
                    onClick = {
                        when (action) {
                            VarConfirmAction.DELETE -> onDeleteSelected()
                            VarConfirmAction.RESTORE_KEEP -> {
                                onRestoreDefaults(true)
                                isEditing = false
                            }
                            VarConfirmAction.RESTORE_CLEAR -> {
                                onRestoreDefaults(false)
                                isEditing = false
                            }
                        }
                        pendingConfirm = null
                    }
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingConfirm = null }) {
                    Text("取消")
                }
            }
        )
    }
}

private enum class VarConfirmAction {
    DELETE,
    RESTORE_KEEP,
    RESTORE_CLEAR
}
