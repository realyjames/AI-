package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.PersistedSettings
import com.example.data.PreferencesRepository
import com.example.data.PromptComposer
import com.example.data.PromptCompositionInput
import com.example.data.TranslationService
import com.example.model.CommonOptions
import com.example.model.ContextSupplementTab
import com.example.model.PickedFileInfo
import com.example.model.PromptCatalog
import com.example.model.PromptDimension
import com.example.model.PromptFormula
import com.example.model.PromptVariable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PromptUiState(
    val dimensions: List<PromptDimension> = PromptCatalog.dimensions,
    val expandedDimensionIds: Set<String> = setOf(PromptCatalog.dimensions.first().id),
    val selectedFormula: PromptFormula = PromptCatalog.defaultFormula(),
    val signatureSlots: List<String> = PersistedSettings.defaultSignatureSlots(),
    val selectedSignatureIndex: Int = 0,
    val isSignatureDropdownExpanded: Boolean = false,
    val showSignature: Boolean = true,
    val showDate: Boolean = true,
    val showFrame: Boolean = true,
    val frameOptions: List<String> = CommonOptions.defaultFrames,
    val selectedFrame: String = CommonOptions.defaultFrames.first(),
    val customVariableOptions: Map<String, List<String>> = emptyMap(),
    val selectedVariableValues: Map<String, String> = emptyMap(),
    val translatingVariableName: String? = null,
    val activeContextTab: ContextSupplementTab = ContextSupplementTab.TEXT,
    val contextText: String = "",
    val contextUrl: String = "",
    val pickedFiles: List<PickedFileInfo> = emptyList(),
    val finalPrompt: String = "",
    val formattedDate: String = PromptComposer.currentFormattedDate(),
    val isLoaded: Boolean = false
) {
    val activeSignatureDisplay: String
        get() = signatureSlots.getOrNull(selectedSignatureIndex)?.trim().orEmpty()
            .ifEmpty { "琦閔~祝福您" }

    fun optionsForVariable(variable: PromptVariable): List<String> =
        customVariableOptions[variable.name]?.takeIf { it.isNotEmpty() } ?: variable.options

    fun selectedValueForVariable(variable: PromptVariable): String {
        val options = optionsForVariable(variable)
        val current = selectedVariableValues[variable.name]
        return if (current != null && options.contains(current)) current else options.firstOrNull().orEmpty()
    }
}

sealed interface PromptUiEvent {
    data class ShowMessage(val message: String) : PromptUiEvent
    data object ScrollToOutput : PromptUiEvent
}

class PromptViewModel(
    private val repository: PreferencesRepository,
    private val translationService: TranslationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptUiState())
    val uiState: StateFlow<PromptUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PromptUiEvent>()
    val events: SharedFlow<PromptUiEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            val saved = repository.settingsFlow.first()
            val formula = PromptCatalog.findFormulaById(saved.lastFormulaId)
                ?: PromptCatalog.defaultFormula()
            val owningDimensionId = PromptCatalog.dimensions
                .firstOrNull { dim -> dim.formulas.any { it.id == formula.id } }
                ?.id ?: PromptCatalog.dimensions.first().id

            _uiState.update { current ->
                val initialValues = buildDefaultSelectionMap(formula, saved.customVariableOptions)
                current.copy(
                    selectedFormula = formula,
                    expandedDimensionIds = setOf(owningDimensionId),
                    signatureSlots = saved.signatureSlots,
                    selectedSignatureIndex = saved.selectedSignatureIndex,
                    showSignature = saved.showSignature,
                    showDate = saved.showDate,
                    showFrame = saved.showFrame,
                    frameOptions = saved.frameOptions,
                    selectedFrame = saved.selectedFrame,
                    customVariableOptions = saved.customVariableOptions,
                    selectedVariableValues = initialValues,
                    isLoaded = true
                )
            }
        }
    }

    private fun buildDefaultSelectionMap(
        formula: PromptFormula,
        customMap: Map<String, List<String>>,
        existing: Map<String, String> = emptyMap()
    ): Map<String, String> = buildMap {
        formula.variables.forEach { variable ->
            val options = customMap[variable.name]?.takeIf { it.isNotEmpty() } ?: variable.options
            val prev = existing[variable.name]
            val chosen = if (prev != null && options.contains(prev)) prev else options.firstOrNull().orEmpty()
            put(variable.name, chosen)
        }
    }

    fun toggleDimensionExpanded(dimensionId: String) {
        _uiState.update { state ->
            val updated = if (state.expandedDimensionIds.contains(dimensionId)) {
                state.expandedDimensionIds - dimensionId
            } else {
                state.expandedDimensionIds + dimensionId
            }
            state.copy(expandedDimensionIds = updated)
        }
    }

    fun selectFormula(formula: PromptFormula) {
        _uiState.update { state ->
            val updatedVars = buildDefaultSelectionMap(
                formula = formula,
                customMap = state.customVariableOptions,
                existing = state.selectedVariableValues
            )
            state.copy(
                selectedFormula = formula,
                selectedVariableValues = updatedVars
            )
        }
        viewModelScope.launch {
            repository.saveLastFormulaId(formula.id)
        }
    }

    fun toggleSignatureDropdown() {
        _uiState.update { it.copy(isSignatureDropdownExpanded = !it.isSignatureDropdownExpanded) }
    }

    fun updateSignatureSlot(index: Int, value: String) {
        if (index !in 0..9) return
        _uiState.update { state ->
            val updated = state.signatureSlots.toMutableList().apply { this[index] = value }
            state.copy(signatureSlots = updated)
        }
        viewModelScope.launch {
            val current = _uiState.value
            repository.saveSignatureSlots(current.signatureSlots, current.selectedSignatureIndex)
        }
    }

    fun selectSignatureSlot(index: Int) {
        if (index !in 0..9) return
        _uiState.update { it.copy(selectedSignatureIndex = index) }
        viewModelScope.launch {
            val current = _uiState.value
            repository.saveSignatureSlots(current.signatureSlots, index)
        }
    }

    fun setShowSignature(show: Boolean) {
        _uiState.update { it.copy(showSignature = show) }
        persistToggles()
    }

    fun setShowDate(show: Boolean) {
        _uiState.update { it.copy(showDate = show) }
        persistToggles()
    }

    fun setShowFrame(show: Boolean) {
        _uiState.update { it.copy(showFrame = show) }
        persistToggles()
    }

    private fun persistToggles() {
        viewModelScope.launch {
            val s = _uiState.value
            repository.saveToggles(s.showSignature, s.showDate, s.showFrame)
        }
    }

    fun selectFrame(frame: String) {
        _uiState.update { it.copy(selectedFrame = frame) }
        viewModelScope.launch {
            val s = _uiState.value
            repository.saveFrameOptions(s.frameOptions, frame)
        }
    }

    fun addCustomFrame(rawName: String) {
        val trimmed = rawName.trim()
        if (trimmed.isEmpty()) {
            emitMessage("請輸入圖框樣式名稱！")
            return
        }
        val state = _uiState.value
        if (state.frameOptions.contains(trimmed)) {
            selectFrame(trimmed)
            emitMessage("此圖框已存在於清單中，已為您選取。")
            return
        }
        val updated = state.frameOptions + trimmed
        _uiState.update { it.copy(frameOptions = updated, selectedFrame = trimmed) }
        viewModelScope.launch {
            repository.saveFrameOptions(updated, trimmed)
            _events.emit(PromptUiEvent.ShowMessage("✅ 已成功新增至圖框選項清單！"))
        }
    }

    fun deleteSelectedFrame() {
        val state = _uiState.value
        if (state.frameOptions.size <= 1) {
            emitMessage("至少需保留一個圖框選項！")
            return
        }
        val target = state.selectedFrame
        val updated = state.frameOptions.filterNot { it == target }
        val nextSelected = updated.first()
        _uiState.update { it.copy(frameOptions = updated, selectedFrame = nextSelected) }
        viewModelScope.launch {
            repository.saveFrameOptions(updated, nextSelected)
            _events.emit(PromptUiEvent.ShowMessage("已從清單中刪除「$target」"))
        }
    }

    fun restoreDefaultFrames(keepCustom: Boolean) {
        val defaults = CommonOptions.defaultFrames
        val state = _uiState.value
        val updated = if (keepCustom) {
            val customOnly = state.frameOptions.filterNot { defaults.contains(it) }
            (defaults + customOnly).distinct()
        } else {
            defaults
        }
        val nextSelected = if (updated.contains(state.selectedFrame)) state.selectedFrame else updated.first()
        _uiState.update { it.copy(frameOptions = updated, selectedFrame = nextSelected) }
        viewModelScope.launch {
            repository.saveFrameOptions(updated, nextSelected)
            val msg = if (keepCustom) {
                "✅ 已恢復系統預設清單，並保留您的自訂選項！"
            } else {
                "✅ 已完全恢復為系統預設清單！(自訂項目已清除)"
            }
            _events.emit(PromptUiEvent.ShowMessage(msg))
        }
    }

    fun selectVariableOption(variableName: String, option: String) {
        _uiState.update { state ->
            state.copy(
                selectedVariableValues = state.selectedVariableValues + (variableName to option)
            )
        }
    }

    fun addCustomVariableOption(variable: PromptVariable, rawInput: String) {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) {
            emitMessage("請輸入【${variable.name}】的中文名稱！")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(translatingVariableName = variable.name) }
            val formatted = translationService.formatWithAutoTranslation(trimmed)
            val state = _uiState.value
            val currentOpts = state.optionsForVariable(variable)

            if (currentOpts.contains(formatted)) {
                _uiState.update {
                    it.copy(
                        translatingVariableName = null,
                        selectedVariableValues = it.selectedVariableValues + (variable.name to formatted)
                    )
                }
                _events.emit(PromptUiEvent.ShowMessage("此選項已存在於清單中，已為您選取。"))
                return@launch
            }

            val updatedOpts = currentOpts + formatted
            _uiState.update {
                it.copy(
                    translatingVariableName = null,
                    customVariableOptions = it.customVariableOptions + (variable.name to updatedOpts),
                    selectedVariableValues = it.selectedVariableValues + (variable.name to formatted)
                )
            }
            repository.saveVariableOptions(variable.name, updatedOpts)
            _events.emit(PromptUiEvent.ShowMessage("✅ 已成功新增選項：$formatted"))
        }
    }

    fun deleteSelectedVariableOption(variable: PromptVariable) {
        val state = _uiState.value
        val currentOpts = state.optionsForVariable(variable)
        if (currentOpts.size <= 1) {
            emitMessage("至少需保留一個選項！")
            return
        }
        val selected = state.selectedValueForVariable(variable)
        val updatedOpts = currentOpts.filterNot { it == selected }
        val nextSelected = updatedOpts.first()
        _uiState.update {
            it.copy(
                customVariableOptions = it.customVariableOptions + (variable.name to updatedOpts),
                selectedVariableValues = it.selectedVariableValues + (variable.name to nextSelected)
            )
        }
        viewModelScope.launch {
            repository.saveVariableOptions(variable.name, updatedOpts)
            _events.emit(PromptUiEvent.ShowMessage("已刪除「$selected」"))
        }
    }

    fun restoreDefaultVariableOptions(variable: PromptVariable, keepCustom: Boolean) {
        val state = _uiState.value
        val defaults = variable.options
        val currentOpts = state.optionsForVariable(variable)
        val updatedOpts = if (keepCustom) {
            val customOnly = currentOpts.filterNot { defaults.contains(it) }
            (defaults + customOnly).distinct()
        } else {
            defaults
        }
        val currentSelected = state.selectedValueForVariable(variable)
        val nextSelected = if (updatedOpts.contains(currentSelected)) currentSelected else updatedOpts.first()
        _uiState.update {
            it.copy(
                customVariableOptions = it.customVariableOptions + (variable.name to updatedOpts),
                selectedVariableValues = it.selectedVariableValues + (variable.name to nextSelected)
            )
        }
        viewModelScope.launch {
            repository.saveVariableOptions(variable.name, updatedOpts)
            val msg = if (keepCustom) {
                "✅ 已恢復【${variable.name}】預設清單並保留自訂項目！"
            } else {
                "✅ 已完全恢復【${variable.name}】預設清單！"
            }
            _events.emit(PromptUiEvent.ShowMessage(msg))
        }
    }

    fun selectContextTab(tab: ContextSupplementTab) {
        _uiState.update { it.copy(activeContextTab = tab) }
    }

    fun updateContextText(text: String) {
        _uiState.update { it.copy(contextText = text) }
    }

    fun updateContextUrl(url: String) {
        _uiState.update { it.copy(contextUrl = url) }
    }

    fun addPickedFiles(files: List<PickedFileInfo>) {
        if (files.isEmpty()) return
        _uiState.update { state ->
            val existingUris = state.pickedFiles.map { it.uriString }.toSet()
            val merged = state.pickedFiles + files.filterNot { existingUris.contains(it.uriString) }
            state.copy(pickedFiles = merged)
        }
    }

    fun removePickedFile(uriString: String) {
        _uiState.update { state ->
            state.copy(pickedFiles = state.pickedFiles.filterNot { it.uriString == uriString })
        }
    }

    fun updateFinalPrompt(text: String) {
        _uiState.update { it.copy(finalPrompt = text) }
    }

    fun generatePrompt() {
        val state = _uiState.value
        val composed = PromptComposer.compose(
            PromptCompositionInput(
                formula = state.selectedFormula,
                signatureText = state.activeSignatureDisplay,
                showSignature = state.showSignature,
                showDate = state.showDate,
                showFrame = state.showFrame,
                selectedFrame = state.selectedFrame,
                variableValues = state.selectedVariableValues,
                contextText = state.contextText,
                contextUrl = state.contextUrl,
                pickedFiles = state.pickedFiles,
                formattedDate = state.formattedDate
            )
        )
        _uiState.update {
            it.copy(
                finalPrompt = composed,
                isSignatureDropdownExpanded = false
            )
        }
        viewModelScope.launch {
            _events.emit(PromptUiEvent.ScrollToOutput)
        }
    }

    private fun emitMessage(message: String) {
        viewModelScope.launch {
            _events.emit(PromptUiEvent.ShowMessage(message))
        }
    }

    class Factory(
        private val repository: PreferencesRepository,
        private val translationService: TranslationService
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PromptViewModel(repository, translationService) as T
        }
    }
}
