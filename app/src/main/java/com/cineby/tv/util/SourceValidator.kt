package com.cineby.tv.util

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

object SourceValidator {
    fun isValidHttpsUrl(url: String): Boolean {
        val parsed = url.trim().toHttpUrlOrNull() ?: return false
        return parsed.scheme == "https" && parsed.host.isNotBlank()
    }
}
