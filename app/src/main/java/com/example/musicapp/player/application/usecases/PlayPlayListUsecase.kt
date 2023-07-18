package com.example.musicapp.v2.player.application.usecases

import com.example.musicapp.v2.player.application.DateProvider
import com.example.musicapp.v2.loader.application.LoadMusicDomainService
import com.example.musicapp.v2.loader.domain.MusicFile
import com.example.musicapp.v2.player.application.PlayerProvider
import com.example.musicapp.v2.player.application.SavePlayerStateDomainService
import com.example.musicapp.v2.player.domain.Music
import com.example.musicapp.v2.player.domain.Player
import com.example.musicapp.v2.playlist.application.PlayListRepository


data class PlayPlayListCommand(
    val playListName: String
)

class PlayPlayListUsecase(
    private val playerProvider: PlayerProvider,
    private val playListRepository: PlayListRepository,
    private val dateProvider: DateProvider,
    private val loadMusicDomainService: LoadMusicDomainService,
    private val savePlayerStateDomainService: SavePlayerStateDomainService
) {
    suspend fun execute(command: PlayPlayListCommand) {
        val player = playerProvider.getPlayer()
        var musics = retrievePlayListMusics(command.playListName)
        setupPlayer(player, musics)
        val fileToPlay = generateFilePathWithLoadedFile(player)
        playNewMusic(player, fileToPlay)
        saveAndNotifyPlayerNewState(player)
    }

    private fun playNewMusic(
        player: Player,
        fileToPlay: MusicFile
    ) {
        player.play(fileToPlay.path, dateProvider.getDateTimeNow())
    }

    private suspend fun generateFilePathWithLoadedFile(player: Player): MusicFile {
        val fileToPlay = loadMusicDomainService.execute(
            MusicFile(
                player.musics[0].name, player.musics[0].file
            )
        )
        return fileToPlay
    }

    private suspend fun saveAndNotifyPlayerNewState(player: Player) {
        savePlayerStateDomainService.execute(player)
    }

    private suspend fun retrievePlayListMusics(playListName: String): Array<Music> {
        return playListRepository.getMusics(playListName)
    }

    private fun setupPlayer(player: Player, musics: Array<Music>) {
        player.generateMusicsToPlay(musics)
    }
}