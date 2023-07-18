package com.example.musicapp.v2.player.infra

import android.content.Context
import com.example.musicapp.v2.loader.domain.FileLoadException
import com.example.musicapp.v2.loader.domain.FileLoaderRepository
import com.example.musicapp.v2.loader.domain.MusicFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URL

class LocalFileLoaderRepository(private val context: Context) : FileLoaderRepository {
    override suspend fun loadFile(name: String, loadingPath: String): MusicFile =
        withContext(Dispatchers.IO) {
            val cachedFile = getCachedFile(name)
            if (cachedFile != null) {
                return@withContext MusicFile(name, cachedFile.absolutePath)
            } else {
                val downloadedFile = downloadFile(loadingPath)
                if (downloadedFile != null) {
                    val cachedFilePath = cacheFile(downloadedFile, name)
                    return@withContext MusicFile(name, cachedFilePath)
                } else {
                    throw FileLoadException("Failed to download file")
                }
            }
        }

    override suspend fun findByName(name: String): MusicFile = withContext(Dispatchers.IO) {
        val cachedFile = getCachedFile(name)
        if (cachedFile != null) {
            return@withContext MusicFile(name, cachedFile.absolutePath)
        } else {
            throw FileNotFoundException("File not found in cache")
        }
    }

    override suspend fun isLoader(name: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext getCachedFile(name) != null
    }

    override suspend fun remove(name: String): Unit = withContext(Dispatchers.IO) {
        val cachedFile = getCachedFile(name)
        cachedFile?.delete()
    }

    private fun getCachedFile(name: String): File? {
        val cacheDir = context.cacheDir
        val cachedFilePath = File(cacheDir, name)
        return if (cachedFilePath.exists()) {
            cachedFilePath
        } else {
            null
        }
    }

    private fun downloadFile(loadingPath: String): File? {
        val cacheDir = context.cacheDir
        val tempFile = File(cacheDir, "temp_file")
        val url = URL(loadingPath)
        val connection = url.openConnection()
        connection.connect()
        val inputStream = connection.getInputStream()
        val outputStream = FileOutputStream(tempFile)
        val buffer = ByteArray(4096)
        var bytesRead = inputStream.read(buffer)
        while (bytesRead != -1) {
            outputStream.write(buffer, 0, bytesRead)
            bytesRead = inputStream.read(buffer)
        }
        outputStream.close()
        inputStream.close()
        return if (tempFile.renameTo(File(cacheDir, tempFile.name))) {
            File(cacheDir, tempFile.name)
        } else {
            null
        }
    }

    private fun cacheFile(file: File, name: String): String {
        val cacheDir = context.cacheDir
        val cachedFile = File(cacheDir, name)
        file.copyTo(cachedFile, true)
        return cachedFile.absolutePath
    }
}