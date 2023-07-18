package com.example.musicapp.v2.player.application.usecases

import com.example.musicapp.v2.player.application.DateProvider
import com.example.musicapp.v2.player.application.EventBusProvider
import com.example.musicapp.v2.loader.application.LoadMusicDomainService
import com.example.musicapp.v2.loader.domain.MusicFile
import com.example.musicapp.v2.player.application.PlayerProvider
import com.example.musicapp.v2.player.application.SavePlayerStateDomainService
import com.example.musicapp.v2.player.domain.ChangeMusicNotification
import com.example.musicapp.v2.player.domain.Music
import com.example.musicapp.v2.player.domain.Player

class PlayPreviousMusicUsecase(
    private val playerProvider: PlayerProvider,
    private val dateProvider: DateProvider,
    private val eventBus: EventBusProvider,
    private val loadMusicDomainService: LoadMusicDomainService,
    private val savePlayerStateDomainService: SavePlayerStateDomainService
) {

    suspend fun execute() {
        val player = getPlayer()
        val newPlayer = createNewPlayer(player)
        val musicToPlay = executePlayerPreviousPlayCommand(newPlayer)
        val filePathToPlay =
            loadMusicDomainService.execute(MusicFile(musicToPlay.name, musicToPlay.file)).path
        newPlayer.play(filePathToPlay, dateProvider.getDateTimeNow())
        savePlayerStateAndNotify(newPlayer)
        handlePlayerMusicChange(player, newPlayer)
    }

    private suspend fun getPlayer(): Player {
        return playerProvider.getPlayer()
    }

    private fun createNewPlayer(player: Player): Player {
        return Player.fromData(player.data().copy())
    }

    private fun executePlayerPreviousPlayCommand(player: Player): Music {
        return player.previousPlay(dateProvider.getDateTimeNow())
    }

    private suspend fun savePlayerStateAndNotify(player: Player) {
        savePlayerStateDomainService.execute(player)
    }

    private suspend fun handlePlayerMusicChange(previousPlayer: Player, actualPlayer: Player) {
        if (previousPlayer.file != actualPlayer.file) {
            eventBus.publish(ChangeMusicNotification(previousPlayer))
        }
    }
}