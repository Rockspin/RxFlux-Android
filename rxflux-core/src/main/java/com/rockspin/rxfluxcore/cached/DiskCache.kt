package com.rockspin.rxfluxcore.cached

import com.rockspin.rxfluxcore.State
import java.io.File
import java.io.FileNotFoundException

abstract class SerializedCache<VS : State>(private val diskCache: DiskCache, private val key: String) :
    ViewStateCache<VS> {

    final override fun loadViewState(): VS =
        try {
            diskCache.getContentsFromUserCache(key)?.let {
                deserialiser(it)
            }
        } catch (e: Exception) {
            //Timber.d(e, "error serializing state")
            null
        } ?: defaultState

    final override fun save(viewState: VS) {
        diskCache.saveContentsToUserCache(key, serialiser(viewState))
    }

    abstract fun serialiser(viewState: VS): String
    abstract fun deserialiser(data: String): VS
    abstract val defaultState: VS
}


class DiskCache(private val cacheDir: String) {

    companion object {
        private const val USER_CACHE_FOLDER = "userCache"
    }

    fun saveContentsToUserCache(key: String, contents: String) {
        val file = getUserCacheFile(key)
        file.writeText(contents)
    }

    fun getContentsFromUserCache(key: String): String? {
        val file = getUserCacheFile(key)
        return try {
            file.readText()
        } catch (e: FileNotFoundException) {
            return null
        }
    }

    fun clearUserCache() {
        val rootDir = File(cacheDir, "$USER_CACHE_FOLDER${File.separator}")
        rootDir.deleteRecursively()
    }

    private fun getUserCacheFile(key: String): File {
        val rootDir = File(cacheDir, "$USER_CACHE_FOLDER${File.separator}")
        rootDir.mkdirs()
        return File(rootDir, key)
    }
}

