package com.example.musicapp.v2.playlist.application.usecases

import com.example.musicapp.v2.playlist.application.PlayListRepository
import com.example.musicapp.v2.playlist.domain.CantRemoveThisPlayListError
import com.example.musicapp.v2.playlist.domain.PlayList

data class RemovePlayListCommand(
    val name: String,
    val userId: String
)


class RemovePlayListUsecase(
    private val playListRepository: PlayListRepository
) {
    suspend fun execute(dto: RemovePlayListCommand) {
        val playList = getPlayListByName(dto.name)
        validateUserOwnerShip(playList, dto.userId)
        deletePlayList(playList)
    }

    private suspend fun deletePlayList(playList: PlayList) {
        playListRepository.delete(
            playList
        )
    }

    private fun validateUserOwnerShip(
        playList: PlayList,
        ownerId: String
    ) {
        if (playList.userId != ownerId) {
            throw CantRemoveThisPlayListError()
        }
    }

    private suspend fun getPlayListByName(name: String): PlayList {
        val playList = playListRepository.getPlayListByName(name)
        return playList
    }
}