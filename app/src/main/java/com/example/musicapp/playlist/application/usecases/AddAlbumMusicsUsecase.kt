package com.example.musicapp.v2.playlist.application.usecases

import com.example.musicapp.v2.player.application.MusicGateway
import com.example.musicapp.v2.player.domain.Music
import com.example.musicapp.v2.playlist.application.PlayListRepository
import com.example.musicapp.v2.playlist.domain.CantRemoveThisPlayListError
import com.example.musicapp.v2.playlist.domain.PlayList
import java.util.ArrayList


data class AddAlbumMusicsToPlayListCommand(
    val albumId: String,
    val playListName: String,
    val userId: String
)

class CantAddAlubmToThisPlayListError : Throwable() {}

class AddAlbumMusicsUsecase(
    private val playListRepository: PlayListRepository,
    private val musicGateway: MusicGateway
) {

    suspend fun execute(dto: AddAlbumMusicsToPlayListCommand) {
        val musics = getAllAlbumMusics(dto)
        val playList = getPlayListByName(dto)
        validateUserOwnerShip(playList, dto.userId)
        addAllMusicsOnPlayList(playList, musics)
        saveNewPlayList(playList)
    }

    private suspend fun saveNewPlayList(playList: PlayList) {
        playListRepository.save(playList)
    }

    private fun addAllMusicsOnPlayList(
        playList: PlayList,
        musics: ArrayList<Music>
    ) {
        playList.addMusics(musics)
    }

    private suspend fun getAllAlbumMusics(dto: AddAlbumMusicsToPlayListCommand): ArrayList<Music> {
        val musics = musicGateway.getAllOfAlbum(dto.albumId)
        return musics
    }

    private suspend fun getPlayListByName(dto: AddAlbumMusicsToPlayListCommand): PlayList {
        val playList = playListRepository.getPlayListByName(dto.playListName)
        return playList
    }

    private fun validateUserOwnerShip(
        playList: PlayList,
        ownerId: String
    ) {
        if (playList.userId != ownerId) {
            throw CantAddAlubmToThisPlayListError()
        }
    }
}