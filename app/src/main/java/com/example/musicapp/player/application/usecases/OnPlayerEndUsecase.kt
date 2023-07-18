package com.example.musicapp.v2.player.application.usecases

import com.example.musicapp.v2.player.application.DateProvider
import com.example.musicapp.v2.player.application.EventBusProvider
import com.example.musicapp.v2.loader.application.LoadMusicDomainService
import com.example.musicapp.v2.loader.domain.MusicFile
import com.example.musicapp.v2.player.application.SavePlayerStateDomainService
import com.example.musicapp.v2.player.domain.EndMusicEvent
import com.example.musicapp.v2.player.domain.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class OnPlayerEndUsecase(
    private val dateProvider: DateProvider,
    private val eventBus: EventBusProvider,
    private val loadMusicDomainService: LoadMusicDomainService,
    private val savePlayerStateDomainService: SavePlayerStateDomainService
) {

    suspend fun execute() {
        GlobalScope.launch(Dispatchers.Default) {
            eventBus.subscribe<EndMusicEvent> {
                GlobalScope.launch(Dispatchers.Default) {
                    val player = Player.fromData(it.playerData)
                    // law of demeter
                    val musicToPlay = player.playNextMusic()
                    val filePathToPlay = loadMusicDomainService.execute(
                        MusicFile(
                            musicToPlay.name,
                            musicToPlay.file
                        )
                    ).path
                    player.play(filePathToPlay, dateProvider.getDateTimeNow())
                    GlobalScope.launch(Dispatchers.Default) {
                        async {
                            savePlayerStateDomainService.execute(
                                player
                            )
                        }.await()
                    }
                }
            }
        }
    }
}