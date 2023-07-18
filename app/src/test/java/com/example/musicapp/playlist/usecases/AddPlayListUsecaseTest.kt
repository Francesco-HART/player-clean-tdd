package com.example.musicapp.playlist.usecases

import com.example.musicapp.album.AlbumFixture
import com.example.musicapp.album.createFixtureAlbum
import com.example.musicapp.player.PlayerFixture
import com.example.musicapp.player.createFixturePlayMusic
import com.example.musicapp.playlist.PlayListBuilder
import com.example.musicapp.playlist.PlayListFixture
import com.example.musicapp.playlist.createFixturePlayList
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import com.example.musicapp.v2.playlist.application.usecases.AddPlayListCommand
import com.example.musicapp.v2.playlist.domain.PlaylistNameAlreadyUseError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.BeforeTest

class AddPlayListUsecaseTest {
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
    fun `when save playlist, playlist is added`() = runTest {
        val playList = PlayListBuilder("robin").build()
        fixture.whenAddPlayList(
            AddPlayListCommand(
                playList.name,
                playList.userId
            )
        )
        fixture.thenPlayListShouldBe(playList)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `can't create playlit if name already use`() = runTest {
        val playList = PlayListBuilder("robin").build()

        fixture.givenExistingPlaylist(
            arrayListOf(playList)
        )

        fixture.whenAddPlayList(
            AddPlayListCommand(
                playList.name,
                playList.userId
            )
        )
        fixture.thenErrorShouldBe(PlaylistNameAlreadyUseError::class.java)
    }
}