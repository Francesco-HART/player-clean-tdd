package com.example.musicapp.playlist.usecases

import com.example.musicapp.album.AlbumBuilder
import com.example.musicapp.album.AlbumFixture
import com.example.musicapp.album.createFixtureAlbum
import com.example.musicapp.player.MusicBuilder
import com.example.musicapp.player.PlayerFixture
import com.example.musicapp.player.createFixturePlayMusic
import com.example.musicapp.playlist.PlayListBuilder
import com.example.musicapp.playlist.PlayListFixture
import com.example.musicapp.playlist.createFixturePlayList
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import com.example.musicapp.v2.playlist.application.usecases.AddAlbumMusicsToPlayListCommand
import com.example.musicapp.v2.playlist.application.usecases.CantAddAlubmToThisPlayListError
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.BeforeTest

class AddAlbumMusicsUsecaseTest {
    private lateinit var fixture: PlayListFixture
    private lateinit var playerFixture: PlayerFixture
    private lateinit var albumFixture: AlbumFixture


    @BeforeTest
    fun setUp() {
        val musicGateway = InMemoryMusicGateway()
        fixture = createFixturePlayList(musicGateway)
        playerFixture = createFixturePlayMusic(musicGateway, fixture.playListRepository)
        albumFixture = createFixtureAlbum(musicGateway)
    }

    @Test
    fun `robin add all music album in his playlist`() = runTest {
        val album = AlbumBuilder().build()

        val music1 = MusicBuilder().withAlbum(album.id).withId(1).build()
        val music2 = MusicBuilder().withAlbum(album.id).withId(2).build()

        val playList = PlayListBuilder("robin").withName(
            "My playlist"
        ).build()

        val excpectPlayList = PlayListBuilder("robin").withName(
            playList.name
        ).withSong(
            arrayListOf(music1, music2)
        ).build()

        playerFixture.givenMusicExists(arrayOf(music1, music2))
        fixture.givenExistingPlaylist(arrayListOf(playList))
        albumFixture.givenExistingAlbums(arrayListOf(album))
        fixture.whenAddAlbumMusics(
            AddAlbumMusicsToPlayListCommand(
                albumId = album.id.toString(),
                playListName = playList.name,
                userId = playList.userId

            )
        )
        fixture.thenPlayListMusicsShouldBe(excpectPlayList)
    }

    @Test
    fun `robin can't add all music album to lea  playlist`() = runTest {
        val album = AlbumBuilder().build()

        val music1 = MusicBuilder().withAlbum(album.id).withId(1).build()
        val music2 = MusicBuilder().withAlbum(album.id).withId(2).build()

        val playListRobin = PlayListBuilder("robin").withName(
            "My playlist"
        ).build()

        val playListLea = PlayListBuilder("lea").withName(
            "lea playlist"
        ).withSong(
            arrayListOf(music1, music2)
        ).build()

        playerFixture.givenMusicExists(arrayOf(music1, music2))
        fixture.givenExistingPlaylist(arrayListOf(playListRobin, playListLea))
        albumFixture.givenExistingAlbums(arrayListOf(album))
        fixture.whenAddAlbumMusics(
            AddAlbumMusicsToPlayListCommand(
                albumId = album.id.toString(),
                playListName = playListLea.name,
                userId = playListRobin.userId
            )
        )
        fixture.thenErrorShouldBe(CantAddAlubmToThisPlayListError::class.java)
    }


    @Test
    fun `robin add all music album in his playlist, music could not be duplicated`() = runTest {
        val album = AlbumBuilder().build()

        val music1 = MusicBuilder().withAlbum(album.id).withId(1).build()
        val music2 = MusicBuilder().withAlbum(album.id).withId(2).build()


        val playList = PlayListBuilder("robin").withSong(
            arrayListOf(music1, music2)
        ).withName(
            "My playlist"
        ).build()

        val excpectPlayList = PlayListBuilder("robin").withName(
            playList.name
        ).withSong(
            arrayListOf(music1, music2)
        ).build()

        playerFixture.givenMusicExists(arrayOf(music1, music2))
        fixture.givenExistingPlaylist(arrayListOf(playList))
        albumFixture.givenExistingAlbums(arrayListOf(album))
        fixture.whenAddAlbumMusics(
            AddAlbumMusicsToPlayListCommand(
                albumId = album.id.toString(),
                playListName = playList.name,
                userId = playList.userId
            )
        )
        fixture.thenPlayListMusicsShouldBe(excpectPlayList)
    }
}