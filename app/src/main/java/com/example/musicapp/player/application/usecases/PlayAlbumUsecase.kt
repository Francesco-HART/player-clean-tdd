package com.example.musicapp.v2.player.application.usecases

import com.example.musicapp.v2.player.application.DateProvider
import com.example.musicapp.v2.loader.application.LoadMusicDomainService
import com.example.musicapp.v2.loader.domain.MusicFile
import com.example.musicapp.v2.player.application.MusicGateway
import com.example.musicapp.v2.player.application.PlayerProvider
import com.example.musicapp.v2.player.application.SavePlayerStateDomainService
import com.example.musicapp.v2.player.domain.Music
import com.example.musicapp.v2.player.domain.Player


data class PlayAlbumCommand(
    val albumId: Int
)

class PlayAlbumUsecase(
    private val playerProvider: PlayerProvider,
    private val musicGateway: MusicGateway,
    private val dateProvider: DateProvider,
    private val loadMusicDomainService: LoadMusicDomainService,
    private val savePlayerStateDomainService: SavePlayerStateDomainService
) {
    suspend fun execute(command: PlayAlbumCommand) {
        var musics = retrieveAlbumMusics(command.albumId.toString())
        val player = playerProvider.getPlayer()
        musics = generatePlayerMusics(player, musics)
        val filePathToPlay =
            loadMusicDomainService.execute(MusicFile(musics[0].name, musics[0].file)).path
        println(
            filePathToPlay
        )
        player.play(filePathToPlay, dateProvider.getDateTimeNow())
        saveAndNotifyPlayerStateChangement(player)
    }

    private suspend fun saveAndNotifyPlayerStateChangement(player: Player) {
        savePlayerStateDomainService.execute(player)
    }

    private suspend fun retrieveAlbumMusics(albumId: String): Array<Music> {
        return musicGateway.getAllOfAlbum(albumId).toTypedArray()
    }

    private fun generatePlayerMusics(player: Player, musics: Array<Music>): Array<Music> {
        player.generateMusicsToPlay(musics)
        return player.musics
    }
}