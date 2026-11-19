package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class TranslationService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .build()
) {
    /**
     * Mirrors the HTML's auto-translation behavior:
     * If [rawInput] does not already contain '(' or '（', fetches English translation
     * and returns "$rawInput ($english)". Falls back gracefully to [rawInput] on error.
     */
    suspend fun formatWithAutoTranslation(rawInput: String): String {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return ""
        if (trimmed.contains("(") || trimmed.contains("（")) {
            return trimmed
        }

        return withContext(Dispatchers.IO) {
            try {
                val encoded = URLEncoder.encode(trimmed, "UTF-8")
                val url =
                    "https://translate.googleapis.com/translate_a/single?client=gtx&sl=zh-TW&tl=en&dt=t&q=$encoded"
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext trimmed
                    val body = response.body?.string().orEmpty()
                    val root = JSONArray(body)
                    val sentences = root.optJSONArray(0) ?: return@withContext trimmed
                    val translatedBuilder = StringBuilder()
                    for (i in 0 until sentences.length()) {
                        val segment = sentences.optJSONArray(i)?.optString(0).orEmpty()
                        translatedBuilder.append(segment)
                    }
                    val enVal = translatedBuilder.toString().trim()
                    if (enVal.isNotEmpty() && !enVal.equals(trimmed, ignoreCase = true)) {
                        "$trimmed ($enVal)"
                    } else {
                        trimmed
                    }
                }
            } catch (_: Exception) {
                trimmed
            }
        }
    }
}
