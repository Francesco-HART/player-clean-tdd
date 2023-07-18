package com.example.musicapp.player


import com.example.musicapp.v2.player.application.ChangePlayerStateEvent
import com.example.musicapp.v2.player.application.EventBusProvider
import com.example.musicapp.v2.loader.application.LoadMusicDomainService
import com.example.musicapp.v2.loader.application.usecases.PreLoadMusicsUsecase
import com.example.musicapp.v2.player.application.SavePlayerStateDomainService
import com.example.musicapp.v2.player.application.usecases.ChangePlayerTimeLineUsecase
import com.example.musicapp.v2.player.application.usecases.PausePlayerUsecase
import com.example.musicapp.v2.player.application.usecases.PlayNextMusicUsecase
import com.example.musicapp.v2.player.application.usecases.PlayPreviousMusicUsecase
import com.example.musicapp.v2.player.domain.Player
import com.example.musicapp.v2.loader.infra.InMemoryFileLoaderRepository
import com.example.musicapp.v2.player.application.usecases.AddAnalyticUsecase
import com.example.musicapp.v2.player.infra.InMemoryAnalytic
import com.example.musicapp.v2.player.infra.StubDateProvider
import com.example.musicapp.v2.player.infra.StubEventBus
import com.example.musicapp.v2.player.infra.StubPlayerProvider
import com.example.musicapp.v2.playlist.domain.PlayList
import com.example.musicapp.v2.playlist.infra.InMemoryPlayListRepository
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.time.LocalDateTime


interface PlayerViewFixture {
    fun givenDateIs(date: LocalDateTime)
    fun givenExistingPlaylist(playList: ArrayList<PlayList>)
    fun givenStateIs(state: PlayerViewModelState)

    suspend fun whenChangePlayerEventIsEmit(player: Player)

    fun givenChangePlayerEventIsSubscribed()
    fun thenStateShouldBe(
        excpectState: PlayerViewModelState,
        givenState: PlayerViewModelState
    )


    fun thenPlayerShouldBe(
        excpectPlayer: Player,
        player: Player
    )

    val playListRepository: InMemoryPlayListRepository
    val eventBus: EventBusProvider
    var state: PlayerViewModelState
    val pausePlayerUsecase: PausePlayerUsecase
    val playNextMusicUsecase: PlayNextMusicUsecase
    val playPreviousMusicUsecase: PlayPreviousMusicUsecase
    val changePlayerTimeLineUsecase: ChangePlayerTimeLineUsecase
    val addAnaliticsUsecase: AddAnalyticUsecase
    val preLoadMusicUsecase: PreLoadMusicsUsecase
}


@OptIn(ExperimentalCoroutinesApi::class)
fun createFixturePlayerView(): PlayerViewFixture {


    var state: PlayerViewModelState = PlayerViewModelState.NeverPlayed

    lateinit var throwError: Throwable

    val eventBus = StubEventBus()

    val dateProvider = StubDateProvider(
        LocalDateTime.of(2020, 1, 1, 0, 0, 0)
    )

    val playListRepository = InMemoryPlayListRepository()
    val playerProvider = StubPlayerProvider()
    val loadFileRepository = InMemoryFileLoaderRepository()
    val savePlayerStateDomainService = SavePlayerStateDomainService(
        playerProvider = playerProvider,
        eventBus = eventBus
    )
    val analyticRepository = InMemoryAnalytic()

    val loadMusicDomainService = LoadMusicDomainService(
        loadFileRepository
    )

    val playPreviousMusicUsecase = PlayPreviousMusicUsecase(
        playerProvider = playerProvider,
        dateProvider = dateProvider,
        eventBus = eventBus,
        loadMusicDomainService,
        savePlayerStateDomainService = savePlayerStateDomainService
    )

    val playNextMusicUsecase = PlayNextMusicUsecase(
        playerProvider = playerProvider,
        dateProvider = dateProvider,
        eventBus = eventBus,
        loadMusicDomainService,
        savePlayerStateDomainService = savePlayerStateDomainService
    )

    val pausePlayerUsecase = PausePlayerUsecase(
        playerProvider = playerProvider,
        dateProvider = dateProvider,
        savePlayerStateService = savePlayerStateDomainService
    )

    val changePlayerTimeLineUsecase = ChangePlayerTimeLineUsecase(
        playerProvider = playerProvider,
        savePlayerUsecase = savePlayerStateDomainService
    )

    val addAnaliticsUsecase = AddAnalyticUsecase(
        analyticRepository, dateProvider
    )

    val preLoadMusicUsecase = PreLoadMusicsUsecase(
        playerProvider,
        loadFileRepository,
        loadMusicDomainService,
    )

    return object : PlayerViewFixture {

        override val preLoadMusicUsecase: PreLoadMusicsUsecase
            get() = preLoadMusicUsecase

        override val addAnaliticsUsecase: AddAnalyticUsecase
            get() = addAnaliticsUsecase

        override val changePlayerTimeLineUsecase: ChangePlayerTimeLineUsecase
            get() = changePlayerTimeLineUsecase

        override val playPreviousMusicUsecase: PlayPreviousMusicUsecase
            get() = playPreviousMusicUsecase

        override val playNextMusicUsecase: PlayNextMusicUsecase
            get() = playNextMusicUsecase

        override val pausePlayerUsecase: PausePlayerUsecase
            get() = pausePlayerUsecase

        override var state: PlayerViewModelState = state

        override val eventBus: EventBusProvider
            get() = eventBus


        override val playListRepository: InMemoryPlayListRepository
            get() = playListRepository

        override fun givenStateIs(givenState: PlayerViewModelState) {
            state = givenState
        }

        override fun givenDateIs(date: LocalDateTime) {
            dateProvider.now = date
        }

        override fun givenExistingPlaylist(playList: ArrayList<PlayList>) {
            playListRepository.addMany(playList)
        }

        override suspend fun whenChangePlayerEventIsEmit(player: Player) = runTest {
            eventBus.publish(ChangePlayerStateEvent(player))
        }


        override fun thenPlayerShouldBe(
            excpectPlayer: Player,
            player: Player
        ) {
            TestCase.assertEquals(
                makePlayerWithoutArray(excpectPlayer),
                makePlayerWithoutArray(player),
            )
        }

        override fun thenStateShouldBe(
            excpectState: PlayerViewModelState,
            givenState: PlayerViewModelState
        ) {

            givenState::class.isInstance(excpectState)
            TestCase.assertEquals(
                excpectState.playerIsCheap,
                givenState.playerIsCheap
            )
        }

        override fun givenChangePlayerEventIsSubscribed() {
            println(eventBus.subscribers)
            assert(eventBus.subscribers == 1)
        }

        fun makePlayerWithoutArray(player: Player): PlayerWithoutArrey {
            return PlayerWithoutArrey(
                file = player.file,
                status = player.status,
                playingTimeInMs = player.playingTimeInMs,
                listeningTimeInMs = player.listeningTimeInMs,
                startPlayAt = player.startPlayAt,
                timePlayInMs = player.playingTimeInMs,
            )
        }
    }

}