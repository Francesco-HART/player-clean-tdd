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

class PlayNextMusicUsecase(
    private val playerProvider: PlayerProvider,
    private val dateProvider: DateProvider,
    private val eventBus: EventBusProvider,
    private val loadMusicDomainService: LoadMusicDomainService,
    private val savePlayerStateDomainService: SavePlayerStateDomainService
) {
    suspend fun execute() {
        val player = getCurrentPlayer()
        sendCurrentPlayerStateEvent(player)
        val newPlayer = instanciateNewPlayer(player)
        callNewPlayerNextMusic(newPlayer)
        val nextMusic = callNewPlayerNextMusic(newPlayer)
        val fileToPlay = generateFilePathWithLoadedFile(nextMusic)
        playNewMusic(newPlayer, fileToPlay)
        saveAndNotifyNewPlayerState(newPlayer)
    }

    private fun playNewMusic(
        newPlayer: Player,
        fileToPlay: MusicFile
    ) {
        newPlayer.play(
            fileToPlay.path,
            dateProvider.getDateTimeNow()
        )
    }

    private suspend fun generateFilePathWithLoadedFile(nextMusic: Music): MusicFile {
        val fileToPlay = loadMusicDomainService.execute(MusicFile(nextMusic.name, nextMusic.file))
        return fileToPlay
    }

    private suspend fun sendCurrentPlayerStateEvent(player: Player) {
        eventBus.publish(ChangeMusicNotification(player))
    }

    private suspend fun saveAndNotifyNewPlayerState(newPlayer: Player) {
        savePlayerStateDomainService.execute(newPlayer)
    }

    private fun callNewPlayerNextMusic(newPlayer: Player): Music {
        return newPlayer.playNextMusic()
    }

    private fun instanciateNewPlayer(player: Player): Player {
        val newPlayer = Player.fromData(player.data().copy())
        return newPlayer
    }

    private suspend fun getCurrentPlayer(): Player {
        val player = this.playerProvider.getPlayer()
        return player
    }
}