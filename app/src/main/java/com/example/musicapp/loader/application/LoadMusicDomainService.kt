package com.example.musicapp.v2.loader.application

import com.example.musicapp.v2.loader.domain.FileLoaderRepository
import com.example.musicapp.v2.loader.domain.MusicFile

class LoadMusicDomainService(
    private val fileLoaderRepository: FileLoaderRepository
) {
    suspend fun execute(dto: MusicFile): MusicFile {
        return if (fileLoaderRepository.isLoader(dto.name)) fileLoaderRepository.findByName(
            dto.name
        ) else fileLoaderRepository.loadFile(dto.name, dto.path)
    }
}