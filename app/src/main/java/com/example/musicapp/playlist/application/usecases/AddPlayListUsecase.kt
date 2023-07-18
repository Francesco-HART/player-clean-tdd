package com.example.musicapp.v2.playlist.application.usecases

import com.example.musicapp.v2.playlist.application.PlayListRepository
import com.example.musicapp.v2.playlist.domain.PlayList
import com.example.musicapp.v2.playlist.domain.PlayListData
import com.example.musicapp.v2.playlist.domain.PlaylistNameAlreadyUseError


data class AddPlayListCommand(
    val name: String,
    val userId: String
)

class AddPlayListUsecase(
    private val playListRepository: PlayListRepository
) {
    suspend fun execute(dto: AddPlayListCommand) {
        playListNameAlreadyUsed(dto)
        playListRepository.save(
            PlayList.fromData(
                createPlayListData(dto)
            )
        )
    }

    private fun createPlayListData(dto: AddPlayListCommand) =
        PlayListData(
            dto.name,
            dto.userId,
            arrayListOf()
        )

    private suspend fun playListNameAlreadyUsed(dto: AddPlayListCommand) {
        try {
            playListRepository.getPlayListByName(dto.name)
            throw PlaylistNameAlreadyUseError()
        } catch (e: Throwable) {
            if (e is PlaylistNameAlreadyUseError)
                throw e
        }
    }
}