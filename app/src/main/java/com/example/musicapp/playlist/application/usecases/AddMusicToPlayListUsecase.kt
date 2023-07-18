package com.example.musicapp.v2.playlist.application.usecases

import com.example.musicapp.v2.player.application.MusicGateway
import com.example.musicapp.v2.player.domain.Music
import com.example.musicapp.v2.player.domain.MusicNotFoundError
import com.example.musicapp.v2.playlist.application.PlayListRepository
import com.example.musicapp.v2.playlist.domain.PlayList


data class AddMusicToPlayListCommand(val musicId: Int, val playListName: String)

class AddMusicToPlayListUsecase(
    private val playListRepository: PlayListRepository,
    private val musicGateway: MusicGateway,
) {
    suspend fun execute(command: AddMusicToPlayListCommand) {
        val music = getMusicById(command.musicId)
        val playList = getPlayListByName(command.playListName)

        playList.addSong(music)
        playListRepository.save(playList)

    }

    private suspend fun getMusicById(musicId: Int): Music {
        return try {
            musicGateway.getOneById(musicId)
        } catch (e: Throwable) {
            throw MusicNotFoundError()
        }
    }

    private suspend fun getPlayListByName(playListName: String): PlayList {
        return playListRepository.getPlayListByName(playListName)
    }
}