package com.example.musicapp.v2.player.application.usecases

import com.example.musicapp.v2.player.application.DateProvider
import com.example.musicapp.v2.loader.application.LoadMusicDomainService
import com.example.musicapp.v2.loader.domain.MusicFile
import com.example.musicapp.v2.player.application.MusicGateway
import com.example.musicapp.v2.player.application.PlayerProvider
import com.example.musicapp.v2.player.application.SavePlayerStateDomainService
import com.example.musicapp.v2.player.domain.Music
import com.example.musicapp.v2.player.domain.MusicNotFoundError
import com.example.musicapp.v2.player.domain.Player
import java.time.LocalDateTime


data class PlayMusicCommand(val id: Int)

class PlayMusicUsecase(
    private val musicGateway: MusicGateway,
    private val playerProvider: PlayerProvider,
    private val dateProvider: DateProvider,
    private val loadMusicDomainService: LoadMusicDomainService,
    private val savePlayerStateService: SavePlayerStateDomainService
) {

    suspend fun execute(playCommand: PlayMusicCommand) {
        val player = getCurrentPlayerState()

        val music = getMusic(playCommand)

        val musics = getAllMusics()

        player.generateMusicsToPlay(musics)

        val fileToPlay = loadMusicBeforePlaying(player, music)

        playMusic(fileToPlay, this.dateProvider.getDateTimeNow(), player)

        // This two lines can be isolated to domain service ddd
        savePlayerStateService.execute(player)
    }

    private suspend fun loadMusicBeforePlaying(
        player: Player,
        music: Music
    ): String {
        return loadMusicDomainService.execute(MusicFile(music.name, music.file)).path
    }

    private suspend fun getCurrentPlayerState(): Player {
        val player = playerProvider.getPlayer()
        return player
    }

    private suspend fun getAllMusics(): Array<Music> {
        val musics = musicGateway.getAll()
        return musics
    }

    private fun playMusic(fileToPlay: String, now: LocalDateTime, player: Player) {
        player.play(
            fileToPlay,
            now,
        )
    }

    private suspend fun getMusic(
        playCommand: PlayMusicCommand
    ): Music {
        try {
            return musicGateway.getOneById(playCommand.id)
        } catch (e: Throwable) {
            throw MusicNotFoundError()
        }
    }
}