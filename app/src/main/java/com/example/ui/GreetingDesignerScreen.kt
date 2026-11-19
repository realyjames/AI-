package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ContextSupplementCard
import com.example.ui.components.FormulaDrawerContent
import com.example.ui.components.GeneralSettingsCard
import com.example.ui.components.OutputSectionCard
import com.example.ui.components.VariablesSectionCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreetingDesignerScreen(
    viewModel: PromptViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PromptUiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                PromptUiEvent.ScrollToOutput -> {
                    scrollState.animateScrollTo(scrollState.maxValue)
                }
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 840.dp

        val mainScaffold: @Composable () -> Unit = {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        navigationIcon = {
                            if (!isExpandedScreen) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch { drawerState.open() }
                                    },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("open_drawer_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "開啟語庫選單"
                                    )
                                }
                            }
                        },
                        title = {
                            Column {
                                Text(
                                    text = "AI 全媒體問候設計師",
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = uiState.selectedFormula.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 720.dp)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Active Formula Header + Quick Formula Strip
                        FormulaHeaderCard(
                            uiState = uiState,
                            onSelectFormula = { formula ->
                                viewModel.selectFormula(formula)
                            },
                            onOpenDrawer = {
                                if (!isExpandedScreen) {
                                    coroutineScope.launch { drawerState.open() }
                                }
                            },
                            showDrawerPrompt = !isExpandedScreen
                        )

                        GeneralSettingsCard(
                            uiState = uiState,
                            onToggleSignatureDropdown = viewModel::toggleSignatureDropdown,
                            onSelectSignatureSlot = viewModel::selectSignatureSlot,
                            onUpdateSignatureSlot = viewModel::updateSignatureSlot,
                            onSetShowSignature = viewModel::setShowSignature,
                            onSetShowDate = viewModel::setShowDate,
                            onSetShowFrame = viewModel::setShowFrame,
                            onSelectFrame = viewModel::selectFrame,
                            onAddCustomFrame = viewModel::addCustomFrame,
                            onDeleteSelectedFrame = viewModel::deleteSelectedFrame,
                            onRestoreDefaultFrames = viewModel::restoreDefaultFrames
                        )

                        VariablesSectionCard(
                            uiState = uiState,
                            onSelectOption = viewModel::selectVariableOption,
                            onAddCustomOption = viewModel::addCustomVariableOption,
                            onDeleteSelectedOption = viewModel::deleteSelectedVariableOption,
                            onRestoreDefaultOptions = viewModel::restoreDefaultVariableOptions
                        )

                        ContextSupplementCard(
                            activeTab = uiState.activeContextTab,
                            contextText = uiState.contextText,
                            contextUrl = uiState.contextUrl,
                            pickedFiles = uiState.pickedFiles,
                            onSelectTab = viewModel::selectContextTab,
                            onContextTextChange = viewModel::updateContextText,
                            onContextUrlChange = viewModel::updateContextUrl,
                            onAddPickedFiles = viewModel::addPickedFiles,
                            onRemovePickedFile = viewModel::removePickedFile
                        )

                        OutputSectionCard(
                            formulaName = uiState.selectedFormula.name,
                            finalPrompt = uiState.finalPrompt,
                            formattedDate = uiState.formattedDate,
                            onFinalPromptChange = viewModel::updateFinalPrompt,
                            onGeneratePrompt = viewModel::generatePrompt,
                            onNotify = { msg ->
                                coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        if (isExpandedScreen) {
            Row(modifier = Modifier.fillMaxSize()) {
                FormulaDrawerContent(
                    dimensions = uiState.dimensions,
                    expandedDimensionIds = uiState.expandedDimensionIds,
                    selectedFormulaId = uiState.selectedFormula.id,
                    showCloseButton = false,
                    onToggleDimension = viewModel::toggleDimensionExpanded,
                    onSelectFormula = viewModel::selectFormula,
                    onCloseDrawer = {},
                    modifier = Modifier.width(300.dp)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Box(modifier = Modifier.weight(1f)) {
                    mainScaffold()
                }
            }
        } else {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.widthIn(max = 310.dp),
                        windowInsets = WindowInsets(0, 0, 0, 0)
                    ) {
                        FormulaDrawerContent(
                            dimensions = uiState.dimensions,
                            expandedDimensionIds = uiState.expandedDimensionIds,
                            selectedFormulaId = uiState.selectedFormula.id,
                            showCloseButton = true,
                            onToggleDimension = viewModel::toggleDimensionExpanded,
                            onSelectFormula = { formula ->
                                viewModel.selectFormula(formula)
                                coroutineScope.launch { drawerState.close() }
                            },
                            onCloseDrawer = {
                                coroutineScope.launch { drawerState.close() }
                            }
                        )
                    }
                }
            ) {
                mainScaffold()
            }
        }
    }
}

@Composable
private fun FormulaHeaderCard(
    uiState: PromptUiState,
    onSelectFormula: (com.example.model.PromptFormula) -> Unit,
    onOpenDrawer: () -> Unit,
    showDrawerPrompt: Boolean
) {
    val activeDimension = remember(uiState.selectedFormula.id, uiState.dimensions) {
        uiState.dimensions.firstOrNull { dim ->
            dim.formulas.any { it.id == uiState.selectedFormula.id }
        }
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activeDimension?.name ?: "全天候語庫",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = uiState.selectedFormula.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (showDrawerPrompt) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .clickable(onClick = onOpenDrawer)
                            .testTag("switch_category_pill")
                    ) {
                        Text(
                            text = "切換語庫 ▾",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(10.dp))

            val allFormulas = remember(uiState.dimensions) {
                uiState.dimensions.flatMap { it.formulas }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allFormulas.forEach { formula ->
                    val selected = formula.id == uiState.selectedFormula.id
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectFormula(formula) },
                        label = {
                            Text(
                                text = formula.name,
                                maxLines = 1
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.testTag("quick_formula_chip_${formula.id}")
                    )
                }
            }
        }
    }
}
