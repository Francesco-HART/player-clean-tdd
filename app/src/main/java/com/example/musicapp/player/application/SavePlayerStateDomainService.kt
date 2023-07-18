package com.example.musicapp.v2.player.application

import com.example.musicapp.v2.player.domain.Player


data class ChangePlayerStateEvent(val player: Player)
class SavePlayerStateDomainService(
    private val playerProvider: PlayerProvider,
    private val eventBus: EventBusProvider
) {

    suspend fun execute(player: Player) {
        playerProvider.save(player)
        sendEventOnNewPlayerState(player)
    }

    private suspend fun sendEventOnNewPlayerState(player: Player) {
        eventBus.publish(ChangePlayerStateEvent(player))
    }
}