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
import com.example.musicapp.v2.player.domain.TEN_SECONDS_IN_MS
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.testng.annotations.AfterTest
import kotlin.test.BeforeTest

class PlayPreviousMusicUsecaseTest {
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
    fun `second music played, on previous play first music on the queue is played`() = runTest {
        val music1 = MusicBuilder()
            .withFile("file1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .build()

        val musics = arrayOf(music1, music2)

        val player = PlayerBuilder()
            .withFile(music2.file)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(musics)
            .build()

        val excpect = PlayerBuilder()
            .withFile(music1.file)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(musics)
            .build()

        fixture.givenPlayerIs(player)
        fixture.givenMusicExists(musics)
        fixture.givenLoadedMusicExists(musics)

        fixture.whenPlayPreviousMusic()

        fixture.thenPlayerShouldBe(excpect)
    }

    @Test
    fun `load music before playing music`() = runTest {
        val music1 = MusicBuilder()
            .withFile("file1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .build()

        val musics = arrayOf(music1, music2)

        val player = PlayerBuilder()
            .withFile(music2.file)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(musics)
            .build()

        val excpect = PlayerBuilder()
            .withFile(music1.file)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(musics)
            .build()

        fixture.givenPlayerIs(player)
        fixture.givenMusicExists(musics)

        fixture.whenPlayPreviousMusic()

        fixture.thenMusicShouldBeLoaded(excpect, music1.name)
    }


    @Test
    fun `3 music in playing, on previous play second music on the queue is played`() = runTest {

        val music1 = MusicBuilder()
            .withFile("file1")
            .withName("music1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .withName("music2")
            .build()

        val music3 = MusicBuilder()
            .withFile("file3")
            .withName("music3")
            .build()

        val musics = arrayOf(music1, music2, music3)

        val palyer = PlayerBuilder()
            .withFile(music3.file)
            .withPlayedIndex(2)
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

        fixture.whenPlayPreviousMusic()

        fixture.thenPlayerShouldBe(excpect)
    }


    @Test
    fun `when is first music, on previous restart music`() = runTest {

        val music1 = MusicBuilder()
            .withFile("file1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .build()

        val music3 = MusicBuilder()
            .withFile("file3")
            .build()

        val musics = arrayOf(music1, music2, music3)

        val palyer = PlayerBuilder()
            .withFile(music1.file)
            .withPlayingTimeInMs(1000)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(musics)
            .build()

        val excpect = PlayerBuilder()
            .withFile(music1.file)
            .withPlayingTimeInMs(0)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(musics)
            .build()

        fixture.givenPlayerIs(palyer)
        fixture.givenMusicExists(musics)
        fixture.givenLoadedMusicExists(musics)

        fixture.whenPlayPreviousMusic()

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
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(musics)
            .build()

        val excpect = PlayerBuilder()
            .withFile(music1.file)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(musics)
            .build()

        fixture.givenMusicExists(musics)
        fixture.givenPlayerIs(palyer)
        fixture.givenLoadedMusicExists(musics)

        fixture.whenPlayPreviousMusic()

        fixture.thenPlayerNotificationShouldBeSend(ChangePlayerStateEvent(excpect))
    }

    @Test
    fun `when playingTime sup 10s, don't change music and restart music`() = runTest {

        val music1 = MusicBuilder()
            .withFile("file1")
            .withName("name1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .withName("name2")
            .build()

        val music3 = MusicBuilder()
            .withFile("file3")
            .withName("name3")
            .build()

        val musics = arrayOf(music1, music2, music3)

        val palyer = PlayerBuilder()
            .withFile(music2.file)
            .withPlayedIndex(1)
            .withPlayingTimeInMs(TEN_SECONDS_IN_MS + 1)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(musics)
            .build()

        val excpect = PlayerBuilder()
            .withFile(music2.file)
            .withPlayingTimeInMs(0)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(musics)
            .build()

        fixture.givenPlayerIs(palyer)
        fixture.givenMusicExists(musics)
        fixture.givenLoadedMusicExists(musics)

        fixture.whenPlayPreviousMusic()

        fixture.thenPlayerShouldBe(excpect)
    }

    @Test
    fun `when playingTime inf or equal to 10s,  change to previous music`() = runTest {

        val music1 = MusicBuilder()
            .withFile("file1")
            .withName("name1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .withName("name2")
            .build()

        val music3 = MusicBuilder()
            .withFile("file3")
            .withName("name3")
            .build()

        val musics = arrayOf(music1, music2, music3)

        val palyer = PlayerBuilder()
            .withFile(music2.file)
            .withPlayingTimeInMs(TEN_SECONDS_IN_MS)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(musics)
            .build()

        val excpect = PlayerBuilder()
            .withFile(music1.file)
            .withPlayingTimeInMs(0)
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(musics)
            .build()

        fixture.givenPlayerIs(palyer)
        fixture.givenMusicExists(musics)
        fixture.givenLoadedMusicExists(musics)

        fixture.whenPlayPreviousMusic()

        fixture.thenPlayerShouldBe(excpect)
    }


    @Test
    fun `send change music notification`() = runTest {

        val music1 = MusicBuilder()
            .withFile("file1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .build()

        val music3 = MusicBuilder()
            .withFile("file3")
            .build()

        val musics = arrayOf(music1, music2, music3)

        val palyer = PlayerBuilder()
            .withFile(music2.file)
            .withPlayingTimeInMs(TEN_SECONDS_IN_MS)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(musics)
            .build()

        fixture.givenPlayerIs(palyer)
        fixture.givenMusicExists(musics)
        fixture.givenLoadedMusicExists(musics)

        fixture.whenPlayPreviousMusic()

        fixture.thenChangeMusicNotificationIsSend(ChangeMusicNotification(palyer))
    }


    @Test
    fun `don't send change music notification if music dose not change`() = runTest {

        val music1 = MusicBuilder()
            .withFile("file1")
            .withName("name1")
            .build()

        val music2 = MusicBuilder()
            .withFile("file2")
            .withName("name2")
            .build()

        val music3 = MusicBuilder()
            .withFile("file3")
            .withName("name3")
            .build()

        val musics = arrayOf(music1, music2, music3)

        val palyer = PlayerBuilder()
            .withFile(music2.file)
            .withPlayedIndex(1)
            .withPlayingTimeInMs(TEN_SECONDS_IN_MS + 1)
            .withStatus(PlayerStatus.PAUSED)
            .withMusicList(musics)
            .build()


        fixture.givenPlayerIs(palyer)
        fixture.givenMusicExists(musics)
        fixture.givenLoadedMusicExists(musics)

        fixture.whenPlayPreviousMusic()

        fixture.thenChangeMusicNotificationShouldNotBeSend()
    }
}