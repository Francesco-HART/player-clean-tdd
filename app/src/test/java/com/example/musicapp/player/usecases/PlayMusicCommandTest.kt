package com.example.musicapp.player.usecases

import com.example.musicapp.player.MusicBuilder
import com.example.musicapp.player.PlayerFixture
import com.example.musicapp.player.createFixturePlayMusic
import com.example.musicapp.playlist.PlayListFixture
import com.example.musicapp.playlist.createFixturePlayList
import com.example.musicapp.v2.player.application.ChangePlayerStateEvent
import com.example.musicapp.v2.player.application.usecases.PlayMusicCommand
import com.example.musicapp.v2.player.domain.MusicNotFoundError
import com.example.musicapp.v2.player.domain.PlayerBuilder
import com.example.musicapp.v2.player.domain.PlayerStatus
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.time.LocalDateTime
import kotlin.test.BeforeTest
import org.testng.annotations.AfterTest
import org.junit.Test


class PlayMusicCommandTest {

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
    fun `Load Music before Playing if is not already load`() = runTest {
        val musicToPlay = MusicBuilder()
            .build()

        fixture.givenMusicExists(arrayOf(musicToPlay))

        fixture.whenPlayMusicCommandIsExecuted(PlayMusicCommand(musicToPlay.id))

        fixture.thenMusicShouldBeLoaded(
            PlayerBuilder().withFile(musicToPlay.file).build(),
            musicToPlay.name
        )

    }

    @Test
    fun `Play music who exist`() = runTest {
        val musicToPlay = MusicBuilder()
            .build()

        fixture.givenLoadedMusicExists(arrayOf(musicToPlay))
        fixture.givenMusicExists(arrayOf(musicToPlay))
        fixture.whenPlayMusicCommandIsExecuted(PlayMusicCommand(musicToPlay.id))
        fixture.thenPlayedMusicShouldBe(musicToPlay)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Can't Play music who dose not exist`() = runTest {
        val musicToPlay = MusicBuilder()
            .build()
        fixture.givenMusicExists(arrayOf())
        fixture.whenPlayMusicCommandIsExecuted(PlayMusicCommand(musicToPlay.id))
        fixture.thenErrorShouldBe(MusicNotFoundError::class.java)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When music is played, player state is playing`() = runTest {
        val givenDateIs = LocalDateTime.now()

        val musicToPlay = MusicBuilder()
            .build()

        val player = PlayerBuilder()
            .withFile(musicToPlay.file)
            .withStatus(PlayerStatus.PLAYING)
            .withStartPlayAt(givenDateIs)
            .withMusicList(arrayOf(musicToPlay))
            .build()

        fixture.givenDateIs(givenDateIs)
        fixture.givenLoadedMusicExists(arrayOf(musicToPlay))
        fixture.givenMusicExists(arrayOf(musicToPlay))
        fixture.whenPlayMusicCommandIsExecuted(PlayMusicCommand(musicToPlay.id))
        fixture.thenPlayerShouldBe(player)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Playe music send notification when new music is played`() = runTest {
        val givenDateIs = LocalDateTime.now()

        val musicToPlay = MusicBuilder()
            .build()

        val player = PlayerBuilder()
            .withFile(musicToPlay.file)
            .withStartPlayAt(givenDateIs)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(arrayOf(musicToPlay))

            .build()

        fixture.givenDateIs(givenDateIs)
        fixture.givenLoadedMusicExists(arrayOf(musicToPlay))
        fixture.givenMusicExists(arrayOf(musicToPlay))
        fixture.whenPlayMusicCommandIsExecuted(PlayMusicCommand(musicToPlay.id))
        fixture.thenPlayerNotificationShouldBeSend(ChangePlayerStateEvent(player))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Playe music set start play at Date now`() = runTest {
        val givenDateIs = LocalDateTime.now()

        val musicToPlay = MusicBuilder()
            .build()


        val player = PlayerBuilder()
            .withFile(musicToPlay.file)
            .withStatus(PlayerStatus.PLAYING)
            .withStartPlayAt(givenDateIs)
            .withMusicList(arrayOf(musicToPlay))
            .build()

        fixture.givenDateIs(givenDateIs)

        fixture.givenLoadedMusicExists(arrayOf(musicToPlay))
        fixture.givenMusicExists(arrayOf(musicToPlay))
        fixture.whenPlayMusicCommandIsExecuted(PlayMusicCommand(musicToPlay.id))
        fixture.thenPlayerShouldBe(player)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Play music list is set`() = runTest {
        val givenDateIs = LocalDateTime.now()

        val musicToPlay1 = MusicBuilder()
            .withId(1)
            .build()

        val musicToPlay2 = MusicBuilder()
            .withId(2)
            .build()

        val musicToPlay3 = MusicBuilder()
            .withId(3)
            .build()


        val player = PlayerBuilder()
            .withFile(musicToPlay1.file)
            .withStatus(PlayerStatus.PLAYING)
            .withStartPlayAt(givenDateIs)
            .withMusicList(arrayOf(musicToPlay1, musicToPlay2, musicToPlay3))
            .build()

        fixture.givenDateIs(givenDateIs)
        fixture.givenMusicExists(
            arrayOf(
                musicToPlay1, musicToPlay2, musicToPlay3
            )
        )
        fixture.givenLoadedMusicExists(arrayOf(musicToPlay1))
        fixture.whenPlayMusicCommandIsExecuted(PlayMusicCommand(musicToPlay1.id))
        fixture.thenPlayerShouldBe(player)
    }
}





