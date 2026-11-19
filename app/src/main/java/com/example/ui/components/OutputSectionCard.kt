package com.example.ui.components

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PromptComposer
import com.example.ui.theme.JetBrainsMonoFontFamily

@Composable
fun OutputSectionCard(
    formulaName: String,
    finalPrompt: String,
    formattedDate: String,
    onFinalPromptChange: (String) -> Unit,
    onGeneratePrompt: () -> Unit,
    onNotify: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(finalPrompt.toByteArray(Charsets.UTF_8))
                }
                onNotify("💾 已成功匯出 TXT 檔案！")
            } catch (e: Exception) {
                onNotify("匯出失敗：${e.localizedMessage ?: "無法寫入檔案"}")
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle(
                icon = Icons.Filled.AutoAwesome,
                title = "✨ 最終生成高階提示詞"
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onGeneratePrompt,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_prompt_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.RocketLaunch,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🚀 組合生成高階提示詞",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = finalPrompt,
                onValueChange = onFinalPromptChange,
                placeholder = { Text("點擊上方按鈕生成提示詞...") },
                textStyle = TextStyle(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                minLines = 8,
                maxLines = 16,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("final_prompt_output")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (finalPrompt.isBlank()) {
                                onNotify("請先生成提示詞！")
                            } else {
                                clipboardManager.setText(AnnotatedString(finalPrompt))
                                onNotify("✅ 提示詞已複製到剪貼簿！")
                            }
                        },
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("copy_prompt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("複製提示詞", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            if (finalPrompt.isBlank()) {
                                onNotify("請先生成提示詞！")
                            } else {
                                val fileName = PromptComposer.suggestedFileName(formulaName, formattedDate)
                                createDocumentLauncher.launch(fileName)
                            }
                        },
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("download_txt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("匯出 TXT", fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = {
                        if (finalPrompt.isBlank()) {
                            onNotify("請先生成提示詞！")
                        } else {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "AI 全媒體問候提示詞 - $formulaName")
                                putExtra(Intent.EXTRA_TEXT, finalPrompt)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "分享提示詞到..."))
                        }
                    },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("share_prompt_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("分享提示詞給 AI 工具 / 好友", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "系統日期：$formattedDate",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
