package com.example.musicapp.player.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.musicapp.TestObserver
import com.example.musicapp.player.MusicBuilder
import com.example.musicapp.player.PlayerViewFixture
import com.example.musicapp.player.PlayerViewModel
import com.example.musicapp.player.PlayerViewModelState
import com.example.musicapp.player.createFixturePlayerView
import com.example.musicapp.testObserver
import com.example.musicapp.v2.player.domain.PlayerBuilder
import com.example.musicapp.v2.player.domain.PlayerStatus
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.BeforeTest


class PlayerViewModelTest {

    lateinit var fixture: PlayerViewFixture
    lateinit var model: PlayerViewModel
    lateinit var observer: TestObserver<PlayerViewModelState>


    @BeforeTest
    fun setUp() = runTest {
        fixture = createFixturePlayerView()
        model = PlayerViewModel(
            fixture.eventBus,
            fixture.pausePlayerUsecase,
            fixture.playNextMusicUsecase,
            fixture.playPreviousMusicUsecase,
            fixture.changePlayerTimeLineUsecase,
            fixture.addAnaliticsUsecase,
            fixture.preLoadMusicUsecase
        )
        observer = model.getState().testObserver()
    }

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `init player page`() = runTest {
        model.initViewModel()
        val expectPlayer = PlayerBuilder().withPlayingTimeInMs(0).withListeningTimeInMs(0)
            .withStatus(PlayerStatus.PAUSED).build()
        val excpectState = PlayerViewModelState.Loading(
            player = expectPlayer,
            playerIsCheap = false,
        )

        val givenState = (observer.observedValues.first() as PlayerViewModelState.Loading)
        fixture.thenStateShouldBe(excpectState, givenState)
        fixture.thenPlayerShouldBe(excpectState.player, givenState.player)
    }

    @Test
    fun `toggle dose not change state page display if state is not loading or success`() = runTest {
        model.toggleDisplay()
        // Add 1 event because of this event change
        model.setState(PlayerViewModelState.NeverPlayed)

        assert(
            observer.observedValues.size == 1
        )
    }

    @Test
    fun `toggle change state page display if state is loading`() = runTest {
        val player = PlayerBuilder().build()

        // Add 1 event because of this event change
        model.setState(PlayerViewModelState.Loading(player, false))

        model.toggleDisplay()

        assertEquals(
            PlayerViewModelState.Loading(player, true),
            observer.observedValues[1]
        )
    }


    @Test
    fun `toggle change state page display if state is success and dose not change player value`() =
        runTest {
            val player = PlayerBuilder().build()

            // Add 1 event because of this event change
            model.setState(
                PlayerViewModelState.Success(
                    player = player,
                    playerIsCheap = false,
                    error = null
                )
            )

            model.toggleDisplay()

            assertEquals(
                PlayerViewModelState.Success(
                    player = player,
                    playerIsCheap = true,
                    error = null
                ),
                observer.observedValues[1]
            )
        }


    @Test
    fun `on previous played optimistically set to loading and actual playing time equal to 0`() =
        runTest {
            model.setState(
                PlayerViewModelState.Success(
                    player = PlayerBuilder().withPlayingTimeInMs(1000).build(),
                    playerIsCheap = false,
                    error = null
                )
            )


            val excpectState = PlayerViewModelState.Loading(
                player = PlayerBuilder().build(),
                playerIsCheap = false,
            )

            model.playPreviousMusic()

            val actuelState = getLastObservedState() as PlayerViewModelState.Loading

            fixture.thenStateShouldBe(
                excpectState,
                actuelState
            )

            fixture.thenPlayerShouldBe(
                excpectState.player,
                actuelState.player
            )
        }

    @Test
    fun `on next state not change if precedent state is neverUsed`() =
        runTest {

            model.setState(
                PlayerViewModelState.NeverPlayed
            )

            val excpectState = PlayerViewModelState.NeverPlayed

            model.playPreviousMusic()

            val actuelState = getLastObservedState() as PlayerViewModelState.NeverPlayed

            fixture.thenStateShouldBe(
                excpectState,
                actuelState
            )
        }


    /*  @Test
      fun `on next state set to loading and actual playing time music to next + 1 and pause`() =
          runTest {

              val arrayOfPlayerMusics = arrayOf(
                  MusicBuilder().withName("1").build(),
                  MusicBuilder().withName("2").build(),
              )

              val player =
                  PlayerBuilder().withMusicList(
                      arrayOfPlayerMusics
                  ).build()


              model.setState(
                  PlayerViewModelState.Success(
                      player = player,
                      playerIsCheap = false,
                      error = null
                  )
              )

              val excpectState = PlayerViewModelState.Loading(
                  player = PlayerBuilder().withMusicList(
                      arrayOfPlayerMusics
                  ).withPlayedIndex(playedIndex = player.playedIndex + 1)
                      .build(),
                  playerIsCheap = false,
              )

              model.playNextMusic()

              val actuelState = getLastObservedState() as PlayerViewModelState.Loading

              fixture.thenStateShouldBe(
                  excpectState,
                  actuelState
              )

              fixture.thenPlayerShouldBe(
                  excpectState.player,
                  actuelState.player
              )
          }*/

    private fun getLastObservedState() = observer.observedValues.last()


    // TODO: Can't test event because of the eventBus.subscribe is complicated to abstracted. I don't exacty understand how it works (same problem in OnPlayEndUsecaseTest.kt)
    // I need to wait event arrive before run thenShouldBe
    // TODO: If you have a solution, please tell me F <3

    /*
            @Test
             fun `on player event emission player is set on playerview state`() = runTest {

                 val player = PlayerBuilder().build()



                 fixture.givenStateIs(PlayerViewModelState.Loading)

                 async {
                     fixture.whenChangePlayerEventIsEmit(
                         player = player
                     )
                 }.await()

                 fixture.whenChangePlayerEventIsEmit(
                     player
                 )

                 fixture.thenStateShouldBe(
                     PlayerViewModelState.Success(
                         player = player,
                         playerIsCheap = false,
                         error = null
                     ),
                     observer.observedValues.last()
                 )
             }*/
}