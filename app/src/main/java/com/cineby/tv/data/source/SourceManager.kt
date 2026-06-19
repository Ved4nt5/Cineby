package com.cineby.tv.data.source

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cineby.tv.data.model.SourceConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "source_config")

@Singleton
class SourceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {
    private val activeSourceKey = stringPreferencesKey("active_source")
    private val fallbackKey = stringPreferencesKey("fallback_sources")

    val sourceConfig: Flow<SourceConfig> = context.dataStore.data.map { prefs ->
        SourceConfig(
            activeUrl = prefs[activeSourceKey] ?: DEFAULT_SOURCE,
            fallbackUrls = (prefs[fallbackKey] ?: DEFAULT_FALLBACK_JSON).toFallbackUrls()
        )
    }

    suspend fun setActiveSource(url: String): Result<Unit> {
        if (!isValidSource(url)) return Result.failure(IllegalArgumentException("Invalid URL"))
        context.dataStore.edit { prefs -> prefs[activeSourceKey] = url.trimEnd('/') }
        return Result.success(Unit)
    }

    suspend fun setFallbackSources(urls: List<String>) {
        val validUrls = urls.map { it.trimEnd('/') }.filter(::isValidSource)
        context.dataStore.edit { prefs ->
            prefs[fallbackKey] = JSONArray(validUrls).toString()
        }
    }

    suspend fun testConnection(url: String): Boolean {
        if (!isValidSource(url)) return false
        val request = Request.Builder().url(url).get().build()
        return runCatching { okHttpClient.newCall(request).execute().isSuccessful }.getOrDefault(false)
    }

    fun isValidSource(url: String): Boolean {
        val parsed = url.trim().toHttpUrlOrNull() ?: return false
        return parsed.scheme == "https"
    }

    fun exportConfig(config: SourceConfig): String {
        return JSONObject()
            .put("active", config.activeUrl)
            .put("fallback", JSONArray(config.fallbackUrls))
            .toString(2)
    }

    suspend fun importConfig(json: String): Result<Unit> {
        return runCatching {
            val root = JSONObject(json)
            val active = root.getString("active")
            val fallback = root.optJSONArray("fallback") ?: JSONArray()
            setActiveSource(active).getOrThrow()
            setFallbackSources(List(fallback.length()) { index -> fallback.optString(index) })
        }
    }

    companion object {
        const val DEFAULT_SOURCE = "https://cineby.at"
        private const val DEFAULT_FALLBACK_JSON = "[\"https://cineby.at\",\"https://cineby.xyz\"]"
    }
}

private fun String.toFallbackUrls(): List<String> {
    val array = runCatching { JSONArray(this) }.getOrDefault(JSONArray())
    return List(array.length()) { index -> array.optString(index) }
}
