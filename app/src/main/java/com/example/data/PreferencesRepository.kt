package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.CommonOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

private val Context.promptDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "greeting_prompt_prefs"
)

data class PersistedSettings(
    val signatureSlots: List<String> = defaultSignatureSlots(),
    val selectedSignatureIndex: Int = 0,
    val showSignature: Boolean = true,
    val showDate: Boolean = true,
    val showFrame: Boolean = true,
    val frameOptions: List<String> = CommonOptions.defaultFrames,
    val selectedFrame: String = CommonOptions.defaultFrames.first(),
    val customVariableOptions: Map<String, List<String>> = emptyMap(),
    val lastFormulaId: String = "morning_scenery"
) {
    companion object {
        fun defaultSignatureSlots(): List<String> =
            List(10) { index -> if (index == 0) "琦閔~祝福您" else "" }
    }
}

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val SIGNATURE_SLOTS = stringPreferencesKey("savedSlots")
        val SELECTED_SIG_INDEX = intPreferencesKey("selectedSigIndex")
        val SHOW_SIGNATURE = booleanPreferencesKey("showSignature")
        val SHOW_DATE = booleanPreferencesKey("showDate")
        val SHOW_FRAME = booleanPreferencesKey("showFrame")
        val FRAME_OPTIONS = stringPreferencesKey("savedFrameOptions")
        val SELECTED_FRAME = stringPreferencesKey("selectedFrame")
        val LAST_FORMULA_ID = stringPreferencesKey("lastFormulaId")
        const val VAR_PREFIX = "savedVar_"
    }

    val settingsFlow: Flow<PersistedSettings> = context.promptDataStore.data.map { prefs ->
        val slots = decodeStringList(prefs[Keys.SIGNATURE_SLOTS])
            ?.take(10)
            ?.let { list ->
                if (list.size < 10) list + List(10 - list.size) { "" } else list
            }
            ?: PersistedSettings.defaultSignatureSlots()

        val selectedSigIdx = (prefs[Keys.SELECTED_SIG_INDEX] ?: 0).coerceIn(0, 9)
        val frameOptions = decodeStringList(prefs[Keys.FRAME_OPTIONS])
            ?.takeIf { it.isNotEmpty() }
            ?: CommonOptions.defaultFrames

        val savedSelectedFrame = prefs[Keys.SELECTED_FRAME]
        val selectedFrame = if (savedSelectedFrame != null && frameOptions.contains(savedSelectedFrame)) {
            savedSelectedFrame
        } else {
            frameOptions.first()
        }

        val customVars = buildMap {
            prefs.asMap().forEach { (key, value) ->
                if (key.name.startsWith(Keys.VAR_PREFIX) && value is String) {
                    val varName = key.name.removePrefix(Keys.VAR_PREFIX)
                    decodeStringList(value)?.let { list ->
                        if (list.isNotEmpty()) put(varName, list)
                    }
                }
            }
        }

        PersistedSettings(
            signatureSlots = slots,
            selectedSignatureIndex = selectedSigIdx,
            showSignature = prefs[Keys.SHOW_SIGNATURE] ?: true,
            showDate = prefs[Keys.SHOW_DATE] ?: true,
            showFrame = prefs[Keys.SHOW_FRAME] ?: true,
            frameOptions = frameOptions,
            selectedFrame = selectedFrame,
            customVariableOptions = customVars,
            lastFormulaId = prefs[Keys.LAST_FORMULA_ID] ?: "morning_scenery"
        )
    }

    suspend fun saveSignatureSlots(slots: List<String>, selectedIndex: Int) {
        val normalized = List(10) { i -> slots.getOrElse(i) { "" } }
        context.promptDataStore.edit { prefs ->
            prefs[Keys.SIGNATURE_SLOTS] = encodeStringList(normalized)
            prefs[Keys.SELECTED_SIG_INDEX] = selectedIndex.coerceIn(0, 9)
        }
    }

    suspend fun saveToggles(showSignature: Boolean, showDate: Boolean, showFrame: Boolean) {
        context.promptDataStore.edit { prefs ->
            prefs[Keys.SHOW_SIGNATURE] = showSignature
            prefs[Keys.SHOW_DATE] = showDate
            prefs[Keys.SHOW_FRAME] = showFrame
        }
    }

    suspend fun saveFrameOptions(options: List<String>, selectedFrame: String) {
        context.promptDataStore.edit { prefs ->
            prefs[Keys.FRAME_OPTIONS] = encodeStringList(options)
            prefs[Keys.SELECTED_FRAME] = selectedFrame
        }
    }

    suspend fun saveVariableOptions(varName: String, options: List<String>) {
        context.promptDataStore.edit { prefs ->
            prefs[stringPreferencesKey(Keys.VAR_PREFIX + varName)] = encodeStringList(options)
        }
    }

    suspend fun saveLastFormulaId(formulaId: String) {
        context.promptDataStore.edit { prefs ->
            prefs[Keys.LAST_FORMULA_ID] = formulaId
        }
    }

    private fun encodeStringList(list: List<String>): String {
        val array = JSONArray()
        list.forEach { array.put(it) }
        return array.toString()
    }

    private fun decodeStringList(json: String?): List<String>? {
        if (json.isNullOrBlank()) return null
        return try {
            val array = JSONArray(json)
            List(array.length()) { idx -> array.optString(idx) }
        } catch (_: Exception) {
            null
        }
    }
}
