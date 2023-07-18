package com.example.musicapp.v2.loader.application.usecases

import com.example.musicapp.v2.loader.domain.FileLoaderRepository
import com.example.musicapp.v2.loader.application.LoadMusicDomainService
import com.example.musicapp.v2.loader.domain.MusicFile
import com.example.musicapp.v2.player.application.PlayerProvider
import com.example.musicapp.v2.player.domain.Music

class PreLoadMusicsUsecase(
    private val playerProvider: PlayerProvider,
    private val loadProvider: FileLoaderRepository,
    private val loadMusicsUsecase: LoadMusicDomainService
) {
    suspend fun execute() {
        val player = playerProvider.getPlayer()

        val musicPlayedIndex = player.playedIndex
        val musics = player.musics

        removePreviousMusicFile(musicPlayedIndex, musics.asList())

        val fileIndexToLoad = getFileIndexToLoad(musicPlayedIndex, musics.asList())

        fileIndexToLoad.forEach { index ->
            val music = musics[index]
            val musicFileToSave = MusicFile(music.name, music.file)
            loadMusicsUsecase.execute(musicFileToSave)
        }
    }

    private suspend fun removePreviousMusicFile(musicPlayedIndex: Int, musics: List<Music>) {
        if (musicPlayedIndex >= 2) {
            val previousMusic = musics[musicPlayedIndex - 2]
            loadProvider.remove(previousMusic.name)
        }
    }

    private fun getFileIndexToLoad(musicPlayedIndex: Int, musics: List<Music>): List<Int> {
        val lastIndex = musics.size - 1
        return when (musicPlayedIndex) {
            0 -> listOf(0, 1, 2)
            lastIndex -> listOf(lastIndex - 1, lastIndex)
            lastIndex - 1 -> listOf(lastIndex - 1, lastIndex, lastIndex + 1)
            else -> listOf(
                musicPlayedIndex - 1,
                musicPlayedIndex,
                musicPlayedIndex + 1,
                musicPlayedIndex + 2
            )
        }
    }
}

