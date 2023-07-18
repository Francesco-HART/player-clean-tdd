package com.example.musicapp.playlist.usecases

import com.example.musicapp.album.AlbumFixture
import com.example.musicapp.album.createFixtureAlbum
import com.example.musicapp.player.MusicBuilder
import com.example.musicapp.player.PlayerFixture
import com.example.musicapp.player.createFixturePlayMusic
import com.example.musicapp.playlist.PlayListBuilder
import com.example.musicapp.playlist.PlayListFixture
import com.example.musicapp.playlist.createFixturePlayList
import com.example.musicapp.v2.player.domain.MusicNotFoundError
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import com.example.musicapp.v2.playlist.application.usecases.AddMusicToPlayListCommand
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.BeforeTest

class AddMusicToPlayListUsecaseTest {

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
    fun `add one music on my playlist`() = runTest {
        val music = MusicBuilder().withName("Music 1").build()
        val playList = PlayListBuilder("robin").build()
        val excpectPlayList = PlayListBuilder("robin").withSong(
            arrayListOf(music)
        ).build()

        playerFixture.givenMusicExists(arrayOf(music))
        fixture.givenExistingPlaylist(arrayListOf(playList))
        fixture.whenAddMusicToMyPlayList(AddMusicToPlayListCommand(music.id, playList.name))
        fixture.thenPlayListMusicsShouldBe(excpectPlayList)
    }


    @Test
    fun `music can't be duplicated on my playlist`() = runTest {
        val music = MusicBuilder().build()
        val arrayofMusics = arrayListOf(music)
        val playlist = PlayListBuilder("robin").withSong(
            arrayofMusics
        ).build()

        val arrayListOfPlayList = arrayListOf(playlist)


        fixture.givenExistingPlaylist(arrayListOfPlayList)
        playerFixture.givenMusicExists(arrayofMusics.toTypedArray())
        fixture.whenAddMusicToMyPlayList(
            AddMusicToPlayListCommand(
                music.id,
                playlist.name
            )
        )
        fixture.thenPlayListShouldBe(
            PlayListBuilder("robin").withSong(
                arrayListOf(music)
            ).build()
        )
    }


    @Test
    fun `return error if music dose not exist`() = runTest {
        val music = MusicBuilder().withId(
            123
        ).build()
        val existingMusic = MusicBuilder().withId(
            1
        ).build()

        val arrayofMusics = arrayListOf(existingMusic)
        val playlist = PlayListBuilder("robin").build()

        val arrayListOfPlayList = arrayListOf(playlist)


        fixture.givenExistingPlaylist(arrayListOfPlayList)
        playerFixture.givenMusicExists(arrayofMusics.toTypedArray())
        fixture.whenAddMusicToMyPlayList(
            AddMusicToPlayListCommand(
                music.id,
                playlist.name
            )
        )
        fixture.thenErrorShouldBe(
            MusicNotFoundError::class.java
        )
    }
}