package com.example.musicapp.player.usecases

import com.example.musicapp.album.AlbumBuilder
import com.example.musicapp.player.MusicBuilder
import com.example.musicapp.player.PlayerFixture
import com.example.musicapp.player.createFixturePlayMusic
import com.example.musicapp.playlist.PlayListFixture
import com.example.musicapp.playlist.createFixturePlayList
import com.example.musicapp.v2.player.application.ChangePlayerStateEvent
import com.example.musicapp.v2.player.application.usecases.PlayAlbumCommand
import com.example.musicapp.v2.player.domain.PlayerBuilder
import com.example.musicapp.v2.player.domain.PlayerStatus
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.testng.annotations.AfterTest
import java.time.LocalDateTime
import kotlin.test.BeforeTest

class PlayAlbumUsecaseTest {
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
    fun `robin play album list, the queue contain all album musics`() = runTest {
        val givenDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0)
        val album1 = AlbumBuilder().withId(1).build()
        val album2 = AlbumBuilder().withId(2).build()


        val music1Album1 = MusicBuilder()
            .withId(1)
            .withName("music1Album1")
            .withAlbum(album1.id)
            .build()


        val music2Album1 = MusicBuilder()
            .withId(2)
            .withName("music2Album1")
            .withAlbum(album1.id)
            .build()

        val music1Album2 = MusicBuilder()
            .withId(1)
            .withName("music1Album2")
            .withAlbum(album2.id)
            .build()


        val exceptPlay = PlayerBuilder().withStartPlayAt(givenDate).withMusicList(
            arrayOf(
                music1Album1,
                music2Album1,
            )
        ).withFile(music1Album1.file).withStatus(PlayerStatus.PLAYING).build()


        fixture.givenDateIs(givenDate)
        fixture.givenMusicExists(
            arrayOf(
                music1Album1,
                music2Album1,
                music1Album2
            )
        )
        fixture.givenLoadedMusicExists(
            arrayOf(
                music1Album1,
                music2Album1,
                music1Album2
            )
        )

        fixture.whenPlayAlbum(PlayAlbumCommand(album1.id))

        fixture.thenPlayerMusicsShouldBe(
            exceptPlay
        )

        fixture.thenPlayerShouldBe(
            exceptPlay
        )
    }


    @Test
    fun `music need to be loaded before be played`() = runTest {
        val givenDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0)
        val album1 = AlbumBuilder().withId(1).build()
        val album2 = AlbumBuilder().withId(2).build()


        val music1Album1 = MusicBuilder()
            .withId(1)
            .withName("music1Album1")
            .withAlbum(album1.id)
            .build()


        val music2Album1 = MusicBuilder()
            .withId(2)
            .withName("music2Album1")
            .withAlbum(album1.id)
            .build()

        val music1Album2 = MusicBuilder()
            .withId(1)
            .withName("music1Album2")
            .withAlbum(album2.id)
            .build()


        val exceptPlay = PlayerBuilder().withStartPlayAt(givenDate).withMusicList(
            arrayOf(
                music1Album1,
                music2Album1,
            )
        ).withFile(music1Album1.file).withStatus(PlayerStatus.PLAYING).build()


        fixture.givenDateIs(givenDate)
        fixture.givenMusicExists(
            arrayOf(
                music1Album1,
                music2Album1,
                music1Album2
            )
        )

        fixture.whenPlayAlbum(PlayAlbumCommand(album1.id))

        fixture.thenMusicShouldBeLoaded(
            exceptPlay,
            music1Album1.name
        )
    }

    @Test
    fun `player change state notification is send`() = runTest {
        val givenDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0)
        val album1 = AlbumBuilder().withId(1).build()


        val music1Album1 = MusicBuilder()
            .withId(1)
            .withAlbum(album1.id)
            .build()


        val exceptPlay = PlayerBuilder().withStartPlayAt(givenDate).withMusicList(
            arrayOf(
                music1Album1,
            )
        ).withFile(music1Album1.file).withStatus(PlayerStatus.PLAYING).build()


        fixture.givenDateIs(givenDate)
        fixture.givenMusicExists(
            arrayOf(
                music1Album1,
            )
        )
        fixture.givenLoadedMusicExists(
            arrayOf(
                music1Album1,
            )
        )

        fixture.whenPlayAlbum(PlayAlbumCommand(album1.id))

        fixture.thenPlayerMusicsShouldBe(
            exceptPlay
        )

        fixture.thenPlayerNotificationShouldBeSend(
            ChangePlayerStateEvent(exceptPlay)
        )
    }
}