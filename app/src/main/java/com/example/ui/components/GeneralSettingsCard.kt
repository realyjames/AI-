package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.PromptUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeneralSettingsCard(
    uiState: PromptUiState,
    onToggleSignatureDropdown: () -> Unit,
    onSelectSignatureSlot: (Int) -> Unit,
    onUpdateSignatureSlot: (Int, String) -> Unit,
    onSetShowSignature: (Boolean) -> Unit,
    onSetShowDate: (Boolean) -> Unit,
    onSetShowFrame: (Boolean) -> Unit,
    onSelectFrame: (String) -> Unit,
    onAddCustomFrame: (String) -> Unit,
    onDeleteSelectedFrame: () -> Unit,
    onRestoreDefaultFrames: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFramePicker by remember { mutableStateOf(false) }
    var isEditingFrames by remember { mutableStateOf(false) }
    var customFrameText by remember { mutableStateOf("") }
    var pendingConfirmDialog by remember { mutableStateOf<FrameConfirmAction?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle(
                icon = Icons.Filled.Settings,
                title = "⚙️ 通用排版設定"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Signature header + switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🔹 自訂署名 (打字即記憶)",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "顯示署名",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = uiState.showSignature,
                        onCheckedChange = onSetShowSignature,
                        modifier = Modifier.testTag("show_signature_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 10-slot collapsible signature selector
            Surface(
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onToggleSignatureDropdown)
                            .minimumInteractiveComponentSize()
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("signature_dropdown_toggle"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "當前選用：${uiState.activeSignatureDisplay}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (uiState.isSignatureDropdownExpanded) {
                                Icons.Filled.ExpandLess
                            } else {
                                Icons.Filled.ExpandMore
                            },
                            contentDescription = "展開或收合署名欄位"
                        )
                    }

                    AnimatedVisibility(
                        visible = uiState.isSignatureDropdownExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.signatureSlots.forEachIndexed { index, slotValue ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = uiState.selectedSignatureIndex == index,
                                        onClick = { onSelectSignatureSlot(index) },
                                        modifier = Modifier.testTag("sig_radio_$index")
                                    )
                                    OutlinedTextField(
                                        value = slotValue,
                                        onValueChange = { onUpdateSignatureSlot(index, it) },
                                        placeholder = {
                                            Text(
                                                if (index == 0) "欄位 1 (例如: 琦閔~祝福您)" else "欄位 ${index + 1}"
                                            )
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("sig_input_$index")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Show date switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSetShowDate(!uiState.showDate) }
                    .minimumInteractiveComponentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🔹 於圖片/影片中顯示日期",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = uiState.showDate,
                    onCheckedChange = onSetShowDate,
                    modifier = Modifier.testTag("show_date_switch")
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // 3. Frame selector & edit controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🔹 署名與日期圖框 (自訂清單)",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "啟用圖框",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = uiState.showFrame,
                        onCheckedChange = onSetShowFrame,
                        modifier = Modifier.testTag("show_frame_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val frameIdx = uiState.frameOptions.indexOf(uiState.selectedFrame)
                val frameDisplay = if (uiState.selectedFrame == "隨機創意圖框" || frameIdx < 0) {
                    uiState.selectedFrame
                } else {
                    val num = if (frameIdx < 10) "0$frameIdx" else "$frameIdx"
                    "$num. ${uiState.selectedFrame}"
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showFramePicker = true }
                        .minimumInteractiveComponentSize()
                        .testTag("frame_selector_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = frameDisplay,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "選擇圖框樣式"
                        )
                    }
                }

                OutlinedButton(
                    onClick = { isEditingFrames = !isEditingFrames },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("edit_frame_button")
                ) {
                    Icon(
                        imageVector = if (isEditingFrames) Icons.Filled.Close else Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isEditingFrames) "取消" else "編輯")
                }
            }

            AnimatedVisibility(visible = isEditingFrames) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customFrameText,
                            onValueChange = { customFrameText = it },
                            placeholder = { Text("輸入欲新增的圖框樣式...") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_frame_input")
                        )
                        Button(
                            onClick = {
                                onAddCustomFrame(customFrameText)
                                customFrameText = ""
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("add_frame_button")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("新增")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { pendingConfirmDialog = FrameConfirmAction.DELETE },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            modifier = Modifier.testTag("delete_frame_button")
                        ) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("刪除選項")
                        }
                        OutlinedButton(
                            onClick = { pendingConfirmDialog = FrameConfirmAction.RESTORE_KEEP },
                            modifier = Modifier.testTag("restore_frame_keep_button")
                        ) {
                            Icon(Icons.Filled.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("復原(保留新增)")
                        }
                        OutlinedButton(
                            onClick = { pendingConfirmDialog = FrameConfirmAction.RESTORE_CLEAR },
                            modifier = Modifier.testTag("restore_frame_clear_button")
                        ) {
                            Icon(Icons.Filled.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("復原(清空新增)")
                        }
                    }
                }
            }
        }
    }

    if (showFramePicker) {
        OptionPickerBottomSheet(
            title = "選擇署名與日期圖框",
            options = uiState.frameOptions,
            selectedOption = uiState.selectedFrame,
            addPlaceholder = "輸入欲新增的圖框樣式...",
            isTranslating = false,
            showZeroBasedNumbers = true,
            onSelect = onSelectFrame,
            onAddCustom = onAddCustomFrame,
            onDismiss = { showFramePicker = false }
        )
    }

    pendingConfirmDialog?.let { action ->
        val (dialogTitle, dialogBody, confirmLabel) = when (action) {
            FrameConfirmAction.DELETE -> Triple(
                "刪除圖框選項",
                "確定要將「${uiState.selectedFrame}」從清單中永久刪除嗎？",
                "刪除"
            )
            FrameConfirmAction.RESTORE_KEEP -> Triple(
                "復原預設圖框",
                "確定要恢復所有系統預設圖框嗎？（您自行新增的圖框將會被保留）",
                "確定恢復"
            )
            FrameConfirmAction.RESTORE_CLEAR -> Triple(
                "重置全部圖框",
                "警告：確定要將所有圖框選項完全恢復為系統最初預設值嗎？（您自行新增的圖框將會被永久清除！）",
                "完全重置"
            )
        }
        AlertDialog(
            onDismissRequest = { pendingConfirmDialog = null },
            title = { Text(dialogTitle) },
            text = { Text(dialogBody) },
            confirmButton = {
                TextButton(
                    onClick = {
                        when (action) {
                            FrameConfirmAction.DELETE -> onDeleteSelectedFrame()
                            FrameConfirmAction.RESTORE_KEEP -> {
                                onRestoreDefaultFrames(true)
                                isEditingFrames = false
                            }
                            FrameConfirmAction.RESTORE_CLEAR -> {
                                onRestoreDefaultFrames(false)
                                isEditingFrames = false
                            }
                        }
                        pendingConfirmDialog = null
                    }
                ) {
                    Text(confirmLabel)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingConfirmDialog = null }) {
                    Text("取消")
                }
            }
        )
    }
}

private enum class FrameConfirmAction {
    DELETE,
    RESTORE_KEEP,
    RESTORE_CLEAR
}

@Composable
fun SectionTitle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
