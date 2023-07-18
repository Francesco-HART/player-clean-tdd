package com.example.musicapp.v2.player.application.usecases


import com.example.musicapp.v2.player.application.DateProvider
import com.example.musicapp.v2.player.application.PlayerProvider
import com.example.musicapp.v2.player.application.SavePlayerStateDomainService
import com.example.musicapp.v2.player.domain.Player


class PausePlayerUsecase(
    private val playerProvider: PlayerProvider,
    private val dateProvider: DateProvider,
    private val savePlayerStateService: SavePlayerStateDomainService
) {
    suspend fun execute() {
        var player = getCurrentPlayerState()
        pausePlayerAction(player)


        // This two lines can be isolated to domain service ddd
        //savePlayerState(player)
        //sendEventOnNewPlayerState(player)
        savePlayerStateService.execute(player)
    }

    private fun pausePlayerAction(player: Player) {
        player.pause(dateProvider.getDateTimeNow())
    }

    private suspend fun getCurrentPlayerState(): Player {
        var player = playerProvider.getPlayer()
        return player
    }
}