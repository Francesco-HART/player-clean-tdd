package com.example.musicapp.v2.player.application.usecases

import com.example.musicapp.v2.player.application.PlayerProvider
import com.example.musicapp.v2.player.application.SavePlayerStateDomainService


data class ChangePlayerTimeLineCommand(
    val time: Long
)

class ChangePlayerTimeLineUsecase(
    private val playerProvider: PlayerProvider,
    private val savePlayerUsecase: SavePlayerStateDomainService
) {
    suspend fun execute(dto: ChangePlayerTimeLineCommand) {
        val player = this.playerProvider.getPlayer()
        player.changePlatingTimeLine(dto.time.toInt())
        savePlayerUsecase.execute(player)
    }
}