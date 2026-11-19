package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.ContextSupplementTab
import com.example.model.PickedFileInfo

@Composable
fun ContextSupplementCard(
    activeTab: ContextSupplementTab,
    contextText: String,
    contextUrl: String,
    pickedFiles: List<PickedFileInfo>,
    onSelectTab: (ContextSupplementTab) -> Unit,
    onContextTextChange: (String) -> Unit,
    onContextUrlChange: (String) -> Unit,
    onAddPickedFiles: (List<PickedFileInfo>) -> Unit,
    onRemovePickedFile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        val files = uris.map { uri ->
            PickedFileInfo(
                uriString = uri.toString(),
                displayName = resolveDisplayName(context, uri)
            )
        }
        onAddPickedFiles(files)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle(
                icon = Icons.Filled.EditNote,
                title = "📝 情境與金句補充 (選填)"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContextSupplementTab.entries.forEach { tab ->
                    val isSelected = tab == activeTab
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectTab(tab) }
                            .minimumInteractiveComponentSize()
                            .testTag("context_tab_${tab.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (activeTab) {
                ContextSupplementTab.TEXT -> {
                    OutlinedTextField(
                        value = contextText,
                        onValueChange = onContextTextChange,
                        placeholder = {
                            Text("例如：請將問候語設定為『日落星起，願你卸下一身的疲憊。晚安。』...")
                        },
                        minLines = 3,
                        maxLines = 6,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("context_text_input")
                    )
                }

                ContextSupplementTab.URL -> {
                    OutlinedTextField(
                        value = contextUrl,
                        onValueChange = onContextUrlChange,
                        placeholder = { Text("請貼上小紅書或網頁 URL...") },
                        leadingIcon = {
                            Icon(Icons.Filled.Link, contentDescription = null)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("context_url_input")
                    )
                }

                ContextSupplementTab.FILE -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("pick_reference_files_button")
                        ) {
                            Icon(Icons.Filled.UploadFile, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("選取參考檔案 (可複選)")
                        }

                        if (pickedFiles.isNotEmpty()) {
                            pickedFiles.forEachIndexed { idx, file ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.AttachFile,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
                                            Text(
                                                text = file.displayName,
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        IconButton(
                                            onClick = { onRemovePickedFile(file.uriString) },
                                            modifier = Modifier.testTag("remove_picked_file_$idx")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "移除檔案"
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            text = "*系統將於提示詞中指示 AI 分析上傳檔案做為排版參考。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun resolveDisplayName(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) {
                        result = cursor.getString(idx)
                    }
                }
            }
    }
    return result ?: uri.lastPathSegment ?: "參考檔案"
}
