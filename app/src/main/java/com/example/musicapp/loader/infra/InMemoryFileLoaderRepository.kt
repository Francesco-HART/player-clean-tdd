package com.example.musicapp.v2.loader.infra

import com.example.musicapp.v2.loader.domain.FileLoaderRepository
import com.example.musicapp.v2.loader.domain.MusicFile

class InMemoryFileLoaderRepository : FileLoaderRepository {
    val fileLoads = mutableListOf<MusicFile>()


    fun findMyByName(name: String): MusicFile {
        println(
            name
        )
        return fileLoads.find { it.name == name }!!
    }

    override suspend fun loadFile(name: String, loadPath: String): MusicFile {
        val file = MusicFile(
            name = name,
            path = loadPath + "newpath"
        )
        fileLoads.add(file)
        return file ?: throw Exception("File not found")
    }

    override suspend fun findByName(name: String): MusicFile {
        return findMyByName(name)
    }

    fun addFile(musicFile: MusicFile) {
        fileLoads.add(musicFile)
    }

    override suspend fun isLoader(name: String): Boolean {
        return fileLoads.indexOfFirst { it.name == name } != -1
    }

    override suspend fun remove(name: String) {
        val index = fileLoads.indexOfFirst { it.name == name }
        if (index != -1) fileLoads.removeAt(index)

    }
}