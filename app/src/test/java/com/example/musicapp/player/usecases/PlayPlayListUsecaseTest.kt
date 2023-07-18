package com.example.musicapp.player.usecases

import com.example.musicapp.player.MusicBuilder
import com.example.musicapp.player.PlayerFixture
import com.example.musicapp.player.createFixturePlayMusic
import com.example.musicapp.playlist.PlayListBuilder
import com.example.musicapp.playlist.PlayListFixture
import com.example.musicapp.playlist.createFixturePlayList
import com.example.musicapp.v2.player.application.ChangePlayerStateEvent
import com.example.musicapp.v2.player.application.usecases.PlayPlayListCommand
import com.example.musicapp.v2.player.domain.Music
import com.example.musicapp.v2.player.domain.PlayerBuilder
import com.example.musicapp.v2.player.domain.PlayerStatus
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.testng.annotations.AfterTest
import java.time.LocalDateTime
import kotlin.test.BeforeTest

class PlayPlayListUsecaseTest {
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
    fun `robin play is playlist`() = runTest {
        val givenDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0)
        val music1PlayList1 = MusicBuilder()
            .withId(1)
            .build()
        val songs: ArrayList<Music> = arrayListOf(music1PlayList1)
        val playlist = PlayListBuilder("robin").withSong(
            songs
        ).withName("A").build()

        val exceptPlayer = PlayerBuilder()
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(songs.toTypedArray())
            .withFile(music1PlayList1.file)
            .build()
        fixture.givenDateIs(givenDate)
        fixture.givenMusicExists(
            songs.toTypedArray()
        )
        fixture.givenLoadedMusicExists(songs.toTypedArray())

        playListFixture.givenExistingPlaylist(
            arrayListOf(playlist)
        )

        fixture.whenPlayPlayList(PlayPlayListCommand(playListName = playlist.name))

        fixture.thenPlayerShouldBe(
            exceptPlayer
        )
    }

    @Test
    fun `played music should be loaded before be played`() = runTest {
        val givenDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0)
        val music1PlayList1 = MusicBuilder()
            .withId(1)
            .build()
        val songs: ArrayList<Music> = arrayListOf(music1PlayList1)
        val playlist = PlayListBuilder("robin").withSong(
            songs
        ).withName("A").build()

        val exceptPlayer = PlayerBuilder()
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(songs.toTypedArray())
            .withFile(music1PlayList1.file)
            .build()
        fixture.givenDateIs(givenDate)
        fixture.givenMusicExists(
            songs.toTypedArray()
        )

        playListFixture.givenExistingPlaylist(
            arrayListOf(playlist)
        )

        fixture.whenPlayPlayList(PlayPlayListCommand(playListName = playlist.name))

        fixture.thenMusicShouldBeLoaded(
            exceptPlayer, music1PlayList1.name
        )
    }

    @Test
    fun `robin play play list, the queue contain all playlist musics`() = runTest {
        val givenDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0)


        val music1PlayList1 = MusicBuilder()
            .withId(1)
            .build()


        val music2PlayList1 = MusicBuilder()
            .withId(2)
            .build()

        val music1PlayList2 = MusicBuilder()
            .withId(1)
            .build()


        val playList1 = PlayListBuilder("robin").withSong(
            arrayListOf(
                music1PlayList1,
                music2PlayList1,
            )
        ).withName("a").build()
        val playList2 = PlayListBuilder("lea").withSong(
            arrayListOf(
                music1PlayList2
            )
        ).withName("b").build()


        val exceptPlay = PlayerBuilder().withStartPlayAt(givenDate).withMusicList(
            arrayOf(
                music1PlayList1,
                music2PlayList1,
            )
        ).withFile(music1PlayList1.file).withStatus(PlayerStatus.PLAYING).build()


        fixture.givenDateIs(givenDate)
        fixture.givenMusicExists(
            arrayOf(
                music1PlayList1,
                music2PlayList1,
                music1PlayList2
            )
        )
        fixture.givenLoadedMusicExists(
            arrayOf(
                music1PlayList1,
                music2PlayList1,
                music1PlayList2
            )
        )

        playListFixture.givenExistingPlaylist(
            arrayListOf(
                playList1, playList2
            )
        )
        fixture.whenPlayPlayList(PlayPlayListCommand(playList1.name))

        fixture.thenPlayerMusicsShouldBe(
            exceptPlay
        )

        fixture.thenPlayerShouldBe(
            exceptPlay
        )
    }


    @Test
    fun `robin play is playlist, player change state notification is send`() = runTest {
        val givenDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0)
        val music1PlayList1 = MusicBuilder()
            .withId(1)
            .build()
        val songs: ArrayList<Music> = arrayListOf(music1PlayList1)
        val playlist = PlayListBuilder("robin").withSong(
            songs
        ).withName("A").build()

        val exceptPlayer = PlayerBuilder()
            .withStatus(PlayerStatus.PLAYING)
            .withMusicList(songs.toTypedArray())
            .withFile(music1PlayList1.file)
            .build()
        fixture.givenDateIs(givenDate)
        fixture.givenMusicExists(
            songs.toTypedArray()
        )
        fixture.givenLoadedMusicExists(songs.toTypedArray())
        playListFixture.givenExistingPlaylist(
            arrayListOf(playlist)
        )

        fixture.whenPlayPlayList(PlayPlayListCommand(playListName = playlist.name))

        fixture.thenPlayerNotificationShouldBeSend(
            ChangePlayerStateEvent(exceptPlayer)
        )
    }
}