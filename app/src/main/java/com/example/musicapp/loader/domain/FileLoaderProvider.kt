package com.example.musicapp.v2.loader.domain


data class MusicFile(
    val name: String,
    val path: String,
)


class FileLoadException(message: String) : Exception(message)

class FileNotFoundException(message: String) : Exception(message)
interface FileLoaderRepository {
    suspend fun loadFile(name: String, loadingPath: String): MusicFile
    suspend fun findByName(name: String): MusicFile
    suspend fun isLoader(name: String): Boolean
    suspend fun remove(name: String)
}