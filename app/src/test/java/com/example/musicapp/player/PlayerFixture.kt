package com.example.musicapp.player

import com.example.musicapp.v2.player.application.ChangePlayerStateEvent
import com.example.musicapp.v2.player.application.EventBusProvider
import com.example.musicapp.v2.loader.application.LoadMusicDomainService
import com.example.musicapp.v2.loader.domain.MusicFile
import com.example.musicapp.v2.player.application.PlayerProvider
import com.example.musicapp.v2.player.application.SavePlayerStateDomainService
import com.example.musicapp.v2.player.application.usecases.ChangePlayerTimeLineCommand
import com.example.musicapp.v2.player.application.usecases.ChangePlayerTimeLineUsecase
import com.example.musicapp.v2.player.application.usecases.OnPlayerEndUsecase
import com.example.musicapp.v2.player.application.usecases.PausePlayerUsecase
import com.example.musicapp.v2.player.application.usecases.PlayAlbumCommand
import com.example.musicapp.v2.player.application.usecases.PlayAlbumUsecase
import com.example.musicapp.v2.player.application.usecases.PlayMusicCommand
import com.example.musicapp.v2.player.application.usecases.PlayMusicUsecase
import com.example.musicapp.v2.player.application.usecases.PlayNextMusicUsecase
import com.example.musicapp.v2.player.application.usecases.PlayPlayListCommand
import com.example.musicapp.v2.player.application.usecases.PlayPlayListUsecase
import com.example.musicapp.v2.player.application.usecases.PlayPreviousMusicUsecase
import com.example.musicapp.v2.player.domain.ChangeMusicNotification
import com.example.musicapp.v2.player.domain.EndMusicEvent
import com.example.musicapp.v2.player.domain.Music
import com.example.musicapp.v2.player.domain.Player
import com.example.musicapp.v2.player.domain.PlayerData
import com.example.musicapp.v2.player.domain.PlayerStatus
import com.example.musicapp.v2.loader.infra.InMemoryFileLoaderRepository
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import com.example.musicapp.v2.player.infra.StubDateProvider
import com.example.musicapp.v2.player.infra.StubEventBus
import com.example.musicapp.v2.player.infra.StubPlayerProvider
import com.example.musicapp.v2.playlist.application.PlayListRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import java.time.LocalDateTime

interface PlayerFixture {
    fun givenDateIs(date: LocalDateTime)
    fun givenPlayerIs(player: Player)
    fun givenMusicExists(music: Array<Music>)

    fun givenLoadedMusicExists(music: Array<Music>)
    suspend fun givenEndMusicEventIsFollow()
    suspend fun whenPlayMusicCommandIsExecuted(playMusicCommand: PlayMusicCommand)
    suspend fun whenPausePlayerCommandIsExecuted()
    suspend fun whenEndMusicEventIsEmmitted(player: Player)
    suspend fun whenPlayNextMusic()
    suspend fun whenPlayPreviousMusic()
    suspend fun whenChangePlayerTimeLineUsecaseIsExecuted(changePlayerTimeLineCommand: ChangePlayerTimeLineCommand)
    suspend fun whenPlayPlayList(playPlayListCommand: PlayPlayListCommand)
    suspend fun whenPlayAlbum(playAlbumCommand: PlayAlbumCommand)

    suspend fun thenPlayedMusicShouldBe(music: Music)
    fun thenPlayerShouldBe(player: Player)
    fun thenErrorShouldBe(errorClass: Class<out Throwable>)
    fun thenPlayerNotificationShouldBeSend(emittedValue: ChangePlayerStateEvent)
    fun thenChangeMusicNotificationIsSend(emittedValue: ChangeMusicNotification)
    fun thenChangeMusicNotificationShouldNotBeSend()
    fun thenPlayerAfterEndEventShouldBe(player: Player)
    fun thenPlayerMusicsShouldBe(player: Player)
    fun thenMusicShouldBeLoaded(player: Player, excpectLoadedMusic: String)


    var eventBusProvider: EventBusProvider
    var playerProvider: PlayerProvider

}


fun createFixturePlayMusic(
    musicGateway: InMemoryMusicGateway,
    playListRepository: PlayListRepository
): PlayerFixture {


    lateinit var throwError: Throwable

    val fileLoaderProvider = InMemoryFileLoaderRepository()

    val dateProvider = StubDateProvider(
        LocalDateTime.of(2020, 1, 1, 0, 0, 0)
    )

    val playerProvider = StubPlayerProvider()

    val stubEventBusChangeState = StubEventBus()
    val stubEventBusEndPlay = StubEventBus()


    val savePlayerStateDomainService = SavePlayerStateDomainService(
        playerProvider,
        stubEventBusChangeState
    )

    val loadMusicUsecase =
        LoadMusicDomainService(fileLoaderProvider)

    val playMusicUsecase =
        PlayMusicUsecase(
            musicGateway,
            playerProvider,
            dateProvider,
            loadMusicUsecase,
            savePlayerStateDomainService
        )


    val pausePlayerUsecase =
        PausePlayerUsecase(playerProvider, dateProvider, savePlayerStateDomainService)

    val playNextMusicUsecase = PlayNextMusicUsecase(
        playerProvider,
        dateProvider,
        stubEventBusChangeState,
        loadMusicDomainService = loadMusicUsecase,
        savePlayerStateDomainService
    )

    val playPlayListUsecase = PlayPlayListUsecase(
        playerProvider,
        playListRepository,
        dateProvider,
        loadMusicUsecase,
        savePlayerStateDomainService
    )

    val playPreviousMusicUsecase = PlayPreviousMusicUsecase(
        playerProvider,
        dateProvider,
        stubEventBusChangeState,
        loadMusicDomainService = loadMusicUsecase,
        savePlayerStateDomainService
    )

    val playAlbumUsecase = PlayAlbumUsecase(
        playerProvider,
        musicGateway,
        dateProvider,
        loadMusicDomainService = loadMusicUsecase,
        savePlayerStateDomainService
    )

    val changePlayerTimeLineUsecase = ChangePlayerTimeLineUsecase(
        playerProvider,
        savePlayerStateDomainService
    )

    val onPlayerEndUsecase = OnPlayerEndUsecase(
        dateProvider,
        stubEventBusEndPlay,
        loadMusicDomainService = loadMusicUsecase,
        savePlayerStateDomainService
    )

    return object : PlayerFixture {

        override var eventBusProvider: EventBusProvider = stubEventBusEndPlay

        override var playerProvider: PlayerProvider = playerProvider

        override fun givenDateIs(date: LocalDateTime) {
            dateProvider.now = date
        }

        override fun givenMusicExists(music: Array<Music>) {
            musicGateway.addingMusics(music)
        }

        override fun givenPlayerIs(player: Player) {
            playerProvider.player = Player.fromData(player.data().copy())
        }

        override fun givenLoadedMusicExists(music: Array<Music>) {
            music.forEach {
                fileLoaderProvider.addFile(
                    MusicFile(
                        it.name,
                        it.file
                    )
                )
            }
            println(
                "givenLoadedMusicExists: ${fileLoaderProvider.fileLoads}"
            )

        }

        override suspend fun whenPlayMusicCommandIsExecuted(playMusicCommand: PlayMusicCommand) =
            runTest {
                async(
                    Dispatchers.Default
                ) {
                    try {
                        playMusicUsecase.execute(
                            playMusicCommand
                        )
                    } catch (e: Throwable) {
                        throwError = e
                    }
                }.await()
            }

        override suspend fun givenEndMusicEventIsFollow() = runTest {
            async { onPlayerEndUsecase.execute() }.await()
        }

        override suspend fun whenEndMusicEventIsEmmitted(player: Player) = runTest {
            async(
                Dispatchers.Default
            ) {
                stubEventBusEndPlay.publish(EndMusicEvent(player.data()))
            }.await()

        }

        override suspend fun whenPausePlayerCommandIsExecuted() = runTest {
            async(
                Dispatchers.Default
            ) {
                try {
                    pausePlayerUsecase.execute()
                } catch (e: Throwable) {
                    throwError = e
                }

            }.await()
        }

        override suspend fun whenPlayNextMusic() = runTest {
            async(
                Dispatchers.Default
            ) {
                try {
                    playNextMusicUsecase.execute()
                } catch (e: Throwable) {
                    throwError = e
                }

            }.await()
        }

        override suspend fun whenPlayPreviousMusic() = runTest {
            async(
                Dispatchers.Default
            ) {
                try {
                    playPreviousMusicUsecase.execute()
                } catch (e: Throwable) {
                    throwError = e
                }

            }.await()
        }

        override suspend fun whenChangePlayerTimeLineUsecaseIsExecuted(changePlayerTimeLineCommand: ChangePlayerTimeLineCommand) =
            runTest {
                async(
                    Dispatchers.Default
                ) {
                    try {
                        changePlayerTimeLineUsecase.execute(changePlayerTimeLineCommand)
                    } catch (e: Throwable) {
                        throwError = e
                    }

                }.await()
            }


        override suspend fun whenPlayPlayList(command: PlayPlayListCommand) = runTest {
            async(
                Dispatchers.Default
            ) {
                try {
                    playPlayListUsecase.execute(command)
                } catch (e: Throwable) {
                    throwError = e
                }

            }.await()
        }

        override suspend fun whenPlayAlbum(playAlbumCommand: PlayAlbumCommand) = runTest {
            async(
                Dispatchers.Default
            ) {
                try {
                    playAlbumUsecase.execute(playAlbumCommand)
                } catch (e: Throwable) {
                    throwError = e
                }

            }.await()
        }

        override suspend fun thenPlayedMusicShouldBe(music: Music) =
            runTest {
                val currentMusic = playerProvider.getCurrentMusic()

                withContext(Dispatchers.Default) {
                    assertEquals(music.file, currentMusic)
                }
            }

        override fun thenPlayerShouldBe(player: Player) = runTest {
            val playerProviderValue = async { playerProvider.getPlayer() }.await()
            assertEquals(player.data().musics.size, playerProviderValue.musics.size)
            assertEqualIgnoringMusics(
                player.data(),
                playerProviderValue.data()
            )
        }

        override fun thenPlayerAfterEndEventShouldBe(player: Player) = runTest {
            val playerProviderValue = async { playerProvider.getPlayer() }.await()
            assertEquals(player.data().musics.size, playerProviderValue.musics.size)
            assertEqualIgnoringMusics(
                player.data().copy(),
                playerProviderValue.data().copy()
            )
        }

        override fun thenErrorShouldBe(errorClass: Class<out Throwable>) {
            assertTrue(errorClass.isInstance(throwError))
        }

        override fun thenPlayerNotificationShouldBeSend(playerEmitted: ChangePlayerStateEvent) =
            runTest {

                val stubEventValue = (stubEventBusChangeState.sendEvents.find { event ->
                    event is ChangePlayerStateEvent
                } as ChangePlayerStateEvent).player

                assertEquals(true, stubEventBusChangeState.isPublishCalled)
                compareArrays(
                    playerEmitted.player.musics,
                    stubEventValue.musics
                )
                assertEqualIgnoringMusics(
                    playerEmitted.player.data(),
                    stubEventValue.data()
                )
            }

        override fun thenChangeMusicNotificationShouldNotBeSend() {
            assertEquals(true, stubEventBusChangeState.value !is ChangeMusicNotification)
        }

        override fun thenPlayerMusicsShouldBe(player: Player) {
            var excpectedArray = player.musics
            var actualArray = playerProvider.player.musics

            Assert.assertEquals(excpectedArray.size, actualArray.size)
            assertArrayEquals(
                excpectedArray,
                actualArray
            )
        }

        override fun thenMusicShouldBeLoaded(player: Player, expectLoadedPath: String) {
            println(
                fileLoaderProvider.fileLoads
            )
            val filePath: MusicFile = fileLoaderProvider.findMyByName(expectLoadedPath)
            assertEquals(player.file + "newpath", filePath.path)
        }


        override fun thenChangeMusicNotificationIsSend(emittedValue: ChangeMusicNotification) =
            runTest {
                val stubEventValue = (stubEventBusChangeState.sendEvents.find { event ->
                    event is ChangeMusicNotification
                } as ChangeMusicNotification).player

                assertEquals(true, stubEventBusChangeState.isPublishCalled)
                compareArrays(
                    emittedValue.player.musics,
                    stubEventValue.musics
                )
                assertEqualIgnoringMusics(
                    emittedValue.player.data(),
                    stubEventValue.data()
                )
            }

        private fun compareArrays(array1: Array<Music>, array2: Array<Music>) {
            assertArrayEquals(
                array1,
                array2
            )
        }
    }
}

data class PlayerWithoutArrey(
    val file: String,
    val timePlayInMs: Int,
    val status: PlayerStatus,
    val startPlayAt: LocalDateTime,
    val playingTimeInMs: Int,
    val listeningTimeInMs: Int
)

fun assertEqualIgnoringMusics(expected: PlayerData, actual: PlayerData) {
    assertEquals(
        PlayerWithoutArrey(
            expected.file,
            expected.listeningTimeInMs,
            expected.status,
            expected.startPlayAt,
            expected.playingTimeInMs,
            expected.listeningTimeInMs
        ), PlayerWithoutArrey(
            actual.file,
            actual.listeningTimeInMs,
            actual.status,
            actual.startPlayAt,
            actual.playingTimeInMs,
            actual.listeningTimeInMs
        )
    )
}