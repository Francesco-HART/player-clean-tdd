package com.example.musicapp.playlist.usecases

import com.example.musicapp.album.AlbumFixture
import com.example.musicapp.album.createFixtureAlbum
import com.example.musicapp.player.PlayerFixture
import com.example.musicapp.player.createFixturePlayMusic
import com.example.musicapp.playlist.PlayListBuilder
import com.example.musicapp.playlist.PlayListFixture
import com.example.musicapp.playlist.createFixturePlayList
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import com.example.musicapp.v2.playlist.application.usecases.RemovePlayListCommand
import com.example.musicapp.v2.playlist.domain.CantRemoveThisPlayListError
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.BeforeTest

class RemovePlayListUsecaseTest {

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
    fun `robin can remove playlist`() = runTest {
        val playList = PlayListBuilder("robin").build()
        fixture.givenExistingPlaylist(
            arrayListOf(
                playList
            )
        )
        fixture.whenRemovePlayList(
            RemovePlayListCommand(
                playList.name,
                "robin"
            )
        )
        fixture.thenPlayListShouldDontExist(playList)
    }

    @Test
    fun `robin can't remove lea playlist`() = runTest {
        val playListRobin = PlayListBuilder("robin").build()
        val playListLea = PlayListBuilder("lea").build()

        fixture.givenExistingPlaylist(
            arrayListOf(
                playListRobin, playListLea
            )
        )
        fixture.whenRemovePlayList(
            RemovePlayListCommand(
                playListRobin.name,
                "lea"
            )
        )
        fixture.thenErrorShouldBe(CantRemoveThisPlayListError::class.java)
    }
}