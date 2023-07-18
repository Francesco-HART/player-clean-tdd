package com.example.musicapp.player.usecases

import com.example.musicapp.player.MusicBuilder
import com.example.musicapp.player.PlayerFixture
import com.example.musicapp.player.createFixturePlayMusic
import com.example.musicapp.playlist.PlayListFixture
import com.example.musicapp.playlist.createFixturePlayList
import com.example.musicapp.v2.player.application.ChangePlayerStateEvent
import com.example.musicapp.v2.player.domain.PlayerBuilder
import com.example.musicapp.v2.player.domain.PlayerStatus
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.testng.annotations.AfterTest
import java.time.LocalDateTime
import kotlin.test.BeforeTest


class PlayerPauseUsecaseTest {
    private lateinit var fixture: PlayerFixture
    private lateinit var playListFixture: PlayListFixture


    @BeforeTest
    fun setUp() {
        val musicGateway = InMemoryMusicGateway()
        playListFixture = createFixturePlayList(
            musicGateway
        )
        fixture = createFixturePlayMusic(musicGateway, playListFixture.playListRepository)
    }

    @Test
    fun `should pause player`() = runTest {
        val player = PlayerBuilder()
            .withStatus(PlayerStatus.PAUSED)
            .build()

        fixture.whenPausePlayerCommandIsExecuted()
        fixture.thenPlayerShouldBe(player)
    }


    // Shift gear down
    /*  @Test
      fun `when play start play equal end play`() {
          val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0)
          assertEquals(calculatetimePlayInMs(localDateTime, localDateTime), 0)
      }

      @Test
      fun `when end end play sup 1ms to start play, result is 1`() {
          val startPlayAt = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0)
          val endPlayAt = startPlayAt.plus(1, ChronoUnit.MILLIS)
          assertEquals(1, calculatetimePlayInMs(startPlayAt, endPlayAt))
      }*/

    @Test
    fun `should set time play before pause action`() = runTest {
        val dateStartPlay = LocalDateTime.of(2020, 1, 1, 0, 0, 2)
        val givenDateEndIs = LocalDateTime.of(2020, 1, 1, 0, 0, 4)

        val musicToPlay = MusicBuilder()
            .build()

        val player = PlayerBuilder()
            .withFile(musicToPlay.file)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(arrayOf(musicToPlay))
            .withStartPlayAt(dateStartPlay)
            .build()

        val expectPlayer = PlayerBuilder()
            .withFile(musicToPlay.file)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(arrayOf(musicToPlay))
            .withStartPlayAt(dateStartPlay)
            .withListeningTimeInMs(2000)
            .withPlayingTimeInMs(2000)
            .build()

        fixture.givenDateIs(
            dateStartPlay
        )
        fixture.givenPlayerIs(player)
        fixture.givenMusicExists(arrayOf(musicToPlay))


        fixture.givenDateIs(
            givenDateEndIs
        )
        fixture.whenPausePlayerCommandIsExecuted()
        fixture.thenPlayerShouldBe(expectPlayer)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Play music send notification when player state change`() = runTest {

        val givenDateIs = LocalDateTime.of(
            2020,
            1,
            1,
            0,
            0,
            0,
            0
        )

        val musicToPlay = MusicBuilder()
            .build()

        val player = PlayerBuilder()
            .withStatus(PlayerStatus.PLAYING)
            .withStartPlayAt(givenDateIs)
            .withMusicList(arrayOf(musicToPlay)).build()

        val expectPlayer = PlayerBuilder()
            .withStatus(PlayerStatus.PAUSED)
            .withStartPlayAt(givenDateIs)
            .withMusicList(arrayOf(musicToPlay)).build()


        fixture.givenDateIs(givenDateIs)
        fixture.givenPlayerIs(player)
        fixture.givenMusicExists(arrayOf(musicToPlay))
        fixture.whenPausePlayerCommandIsExecuted()
        fixture.thenPlayerNotificationShouldBeSend(
            ChangePlayerStateEvent(expectPlayer)
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Time played is correctly increment with pause interval`() = runTest {
        val dateStartPlay = LocalDateTime.of(2020, 1, 1, 0, 0, 2)


        val enDateFirstPause = LocalDateTime.of(2020, 1, 1, 0, 0, 4)

        val dateSecondStartPlay = LocalDateTime.of(2020, 1, 1, 0, 0, 6)


        val enDateSecondPause = LocalDateTime.of(2020, 1, 1, 0, 0, 8)


        val musicToPlay = MusicBuilder()
            .build()

        val playerBuilder = PlayerBuilder()
            .withStatus(PlayerStatus.PLAYING)
            .withStartPlayAt(dateStartPlay)
            .withMusicList(arrayOf(musicToPlay))

        fixture.givenMusicExists(arrayOf(musicToPlay))


        fixture.givenDateIs(enDateFirstPause)
        fixture.givenPlayerIs(
            playerBuilder.withStartPlayAt(
                dateSecondStartPlay
            ).withStartPlayAt(
                dateStartPlay
            ).withListeningTimeInMs(2000).withPlayingTimeInMs(2000).build()
        )
        fixture.whenPausePlayerCommandIsExecuted()


        fixture.givenDateIs(enDateSecondPause)
        fixture.givenPlayerIs(
            playerBuilder.withStartPlayAt(
                dateSecondStartPlay
            ).withStartPlayAt(
                dateSecondStartPlay
            ).build()
        )
        fixture.whenPausePlayerCommandIsExecuted()

        fixture.thenPlayerShouldBe(
            playerBuilder
                .withStatus(PlayerStatus.PAUSED)
                .withStartPlayAt(
                    dateSecondStartPlay
                )
                .withListeningTimeInMs(4000)
                .withPlayingTimeInMs(4000)
                .build()
        )

    }
}