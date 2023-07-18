package com.example.musicapp.player.usecases

import com.example.musicapp.player.MusicBuilder
import com.example.musicapp.player.PlayerFixture
import com.example.musicapp.player.createFixturePlayMusic
import com.example.musicapp.playlist.PlayListFixture
import com.example.musicapp.playlist.createFixturePlayList
import com.example.musicapp.v2.player.application.ChangePlayerStateEvent
import com.example.musicapp.v2.player.application.usecases.ChangePlayerTimeLineCommand
import com.example.musicapp.v2.player.domain.PlayerBuilder
import com.example.musicapp.v2.player.domain.PlayerStatus
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import org.junit.Test

class ChangePlayerTimeLineUsecaseTest {
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

    @AfterTest
    fun tearDown() {
        fixture.eventBusProvider.removeAllSubscription()
    }

    @Test
    fun `Player play time is equal to new time select by timeline`() = runTest {
        val music = MusicBuilder()
            .withDurationInS(20)
            .build()

        val player = PlayerBuilder()
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(arrayOf(music))
            .withFile(music.file)
            .withPlayingTimeInMs(1000)
            .build()

        val excepted = PlayerBuilder().withStatus(PlayerStatus.PLAYING)
            .withMusicList(arrayOf(music))
            .withFile(music.file)
            .withPlayingTimeInMs(2000)
            .build()

        fixture.givenPlayerIs(player)
        fixture.whenChangePlayerTimeLineUsecaseIsExecuted(ChangePlayerTimeLineCommand(2000))
        fixture.thenPlayerShouldBe(excepted)
    }

    @Test
    fun `Player play time can't be sup to file duration, play time is set to 4s before end file duration`() =
        runTest {

            val music = MusicBuilder()
                .withDurationInS(20)
                .build()

            val player = PlayerBuilder()
                .withStatus(PlayerStatus.PLAYING)
                .withMusicList(arrayOf(music))
                .withFile(music.file)
                .withPlayingTimeInMs(10000)
                .build()

            val excepted = PlayerBuilder().withStatus(PlayerStatus.PLAYING)
                .withMusicList(arrayOf(music))
                .withFile(music.file)
                .withPlayingTimeInMs(16000)
                .build()

            fixture.givenPlayerIs(player)
            fixture.whenChangePlayerTimeLineUsecaseIsExecuted(ChangePlayerTimeLineCommand(20000))
            fixture.thenPlayerShouldBe(excepted)

            fixture.whenChangePlayerTimeLineUsecaseIsExecuted(ChangePlayerTimeLineCommand(20001))
            fixture.thenPlayerShouldBe(excepted)
        }

    @Test
    fun `change player state notification is send when change timeline`() = runTest {
        val music = MusicBuilder()
            .withDurationInS(20)
            .build()

        val player = PlayerBuilder()
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(arrayOf(music))
            .withFile(music.file)
            .withPlayingTimeInMs(1000)
            .build()

        val excepted = PlayerBuilder().withStatus(PlayerStatus.PLAYING)
            .withMusicList(arrayOf(music))
            .withFile(music.file)
            .withPlayingTimeInMs(2000)
            .build()

        fixture.givenPlayerIs(player)
        fixture.whenChangePlayerTimeLineUsecaseIsExecuted(ChangePlayerTimeLineCommand(2000))
        fixture.thenPlayerNotificationShouldBeSend(ChangePlayerStateEvent(excepted))
    }

    @Test
    fun `can't change timeline to time inf 0`() = runTest {
        val music = MusicBuilder()
            .withDurationInS(20)
            .build()

        val player = PlayerBuilder()
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(arrayOf(music))
            .withFile(music.file)
            .withPlayingTimeInMs(1000)
            .build()

        val excepted = PlayerBuilder().withStatus(PlayerStatus.PLAYING)
            .withMusicList(arrayOf(music))
            .withFile(music.file)
            .withPlayingTimeInMs(0)
            .build()

        fixture.givenPlayerIs(player)
        fixture.whenChangePlayerTimeLineUsecaseIsExecuted(ChangePlayerTimeLineCommand(-1))
        fixture.thenPlayerShouldBe(excepted)
    }
}