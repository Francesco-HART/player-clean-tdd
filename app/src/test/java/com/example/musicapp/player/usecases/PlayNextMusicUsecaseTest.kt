package com.example.musicapp.player.usecases

import com.example.musicapp.player.MusicBuilder
import com.example.musicapp.player.PlayerFixture
import com.example.musicapp.player.createFixturePlayMusic
import com.example.musicapp.playlist.PlayListFixture
import com.example.musicapp.playlist.createFixturePlayList
import com.example.musicapp.v2.player.application.ChangePlayerStateEvent
import com.example.musicapp.v2.player.domain.ChangeMusicNotification
import com.example.musicapp.v2.player.domain.PlayerBuilder
import com.example.musicapp.v2.player.domain.PlayerStatus
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import org.testng.annotations.AfterTest
import org.junit.Test


class PlayNextMusicUsecaseTest {
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
    fun `first music played, on next play second music on the queue is played`() = runTest {

        val music1 = MusicBuilder()
            .withFile("file1")
            .withName("music1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .withName("music2")
            .build()

        val musics = arrayOf(music1, music2)

        val palyer = PlayerBuilder()
            .withFile(music1.file)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(musics)
            .build()

        val excpect = PlayerBuilder()
            .withFile(music2.file)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(musics)
            .build()

        fixture.givenPlayerIs(palyer)
        fixture.givenMusicExists(musics)
        fixture.givenLoadedMusicExists(musics)

        fixture.whenPlayNextMusic()

        fixture.thenPlayerShouldBe(excpect)
    }

    @Test
    fun `first music played, need to be loaded before be played`() = runTest {

        val music1 = MusicBuilder()
            .withFile("file1")
            .withName("music1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .withName("music2")
            .build()

        val musics = arrayOf(music1, music2)

        val palyer = PlayerBuilder()
            .withFile(music1.file)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(musics)
            .build()

        val excpect = PlayerBuilder()
            .withFile(music2.file)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(musics)
            .build()

        fixture.givenPlayerIs(palyer)
        fixture.givenMusicExists(musics)

        fixture.whenPlayNextMusic()

        fixture.thenMusicShouldBeLoaded(
            player = excpect,
            music2.name
        )
    }

    @Test
    fun `last music in the queue is played, on next play first music queue`() = runTest {
        val music1 = MusicBuilder()
            .withFile("file1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .build()

        val musics = arrayOf(music1, music2)

        val palyer = PlayerBuilder()
            .withFile(music2.file)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(musics)
            .build()

        val excpect = PlayerBuilder()
            .withFile(music1.file)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(musics)
            .build()

        fixture.givenPlayerIs(palyer)
        fixture.givenMusicExists(musics)
        fixture.givenLoadedMusicExists(musics)


        fixture.whenPlayNextMusic()

        fixture.thenPlayerShouldBe(excpect)
    }


    @Test
    fun `on change music send notification for indicate player state changement`() = runTest {
        val music1 = MusicBuilder()
            .withFile("file1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .build()

        val musics = arrayOf(music1, music2)

        val palyer = PlayerBuilder()
            .withFile(music2.file)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(musics)
            .build()

        val excpect = PlayerBuilder()
            .withFile(music1.file)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(musics)
            .build()

        fixture.givenPlayerIs(palyer)
        fixture.givenMusicExists(musics)
        fixture.givenLoadedMusicExists(musics)


        fixture.whenPlayNextMusic()

        fixture.thenPlayerNotificationShouldBeSend(ChangePlayerStateEvent(excpect))
    }


    @Test
    fun `change music notification is send`() = runTest {
        val music1 = MusicBuilder()
            .withFile("file1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .build()

        val musics = arrayOf(music1, music2)

        val palyer = PlayerBuilder()
            .withFile(music2.file)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(musics)
            .build()

        val excpect = PlayerBuilder()
            .withFile(music1.file)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(musics)
            .build()

        fixture.givenPlayerIs(palyer)
        fixture.givenMusicExists(musics)
        fixture.givenLoadedMusicExists(musics)


        fixture.whenPlayNextMusic()

        fixture.thenChangeMusicNotificationIsSend(ChangeMusicNotification(palyer))
    }
}