package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.PromptDimension
import com.example.model.PromptFormula

@Composable
fun FormulaDrawerContent(
    dimensions: List<PromptDimension>,
    expandedDimensionIds: Set<String>,
    selectedFormulaId: String,
    showCloseButton: Boolean,
    onToggleDimension: (String) -> Unit,
    onSelectFormula: (PromptFormula) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = "全天候語庫 (終極版)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "選擇分類與問候公式",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (showCloseButton) {
                    IconButton(
                        onClick = onCloseDrawer,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("drawer_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "關閉語庫選單"
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                items(dimensions, key = { it.id }) { dimension ->
                    val isExpanded = expandedDimensionIds.contains(dimension.id)
                    DimensionAccordionGroup(
                        dimension = dimension,
                        isExpanded = isExpanded,
                        selectedFormulaId = selectedFormulaId,
                        onToggle = { onToggleDimension(dimension.id) },
                        onSelectFormula = onSelectFormula
                    )
                }
            }
        }
    }
}

@Composable
private fun DimensionAccordionGroup(
    dimension: PromptDimension,
    isExpanded: Boolean,
    selectedFormulaId: String,
    onToggle: () -> Unit,
    onSelectFormula: (PromptFormula) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                .clickable(onClick = onToggle)
                .minimumInteractiveComponentSize()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .testTag("dimension_header_${dimension.id}"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dimension.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (isExpanded) "收合分類" else "展開分類",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                dimension.formulas.forEach { formula ->
                    val isSelected = formula.id == selectedFormulaId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectFormula(formula) }
                            .background(
                                if (isSelected) {
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                            .minimumInteractiveComponentSize()
                            .padding(end = 16.dp, top = 12.dp, bottom = 12.dp)
                            .testTag("formula_item_${formula.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                                .background(
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = formula.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        modifier = Modifier.padding(start = 24.dp)
                    )
                }
            }
        }
    }
}
