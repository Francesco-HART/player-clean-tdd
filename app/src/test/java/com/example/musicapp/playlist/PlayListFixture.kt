package com.example.musicapp.playlist

import com.example.musicapp.v2.album.application.AlbumGateway
import com.example.musicapp.v2.player.application.MusicGateway
import com.example.musicapp.v2.player.infra.StubDateProvider
import com.example.musicapp.v2.playlist.application.usecases.AddAlbumMusicsToPlayListCommand
import com.example.musicapp.v2.playlist.application.usecases.AddAlbumMusicsUsecase
import com.example.musicapp.v2.playlist.application.usecases.AddMusicToPlayListCommand
import com.example.musicapp.v2.playlist.application.usecases.AddMusicToPlayListUsecase
import com.example.musicapp.v2.playlist.application.usecases.AddPlayListCommand
import com.example.musicapp.v2.playlist.application.usecases.AddPlayListUsecase
import com.example.musicapp.v2.playlist.application.usecases.RemovePlayListCommand
import com.example.musicapp.v2.playlist.application.usecases.RemovePlayListUsecase
import com.example.musicapp.v2.playlist.domain.PlayList
import com.example.musicapp.v2.playlist.infra.InMemoryPlayListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import java.time.LocalDateTime


interface PlayListFixture {
    fun givenDateIs(date: LocalDateTime)
    fun givenExistingPlaylist(playList: ArrayList<PlayList>)

    suspend fun whenAddPlayList(dto: AddPlayListCommand)
    suspend fun whenAddMusicToMyPlayList(dto: AddMusicToPlayListCommand)
    suspend fun whenRemovePlayList(dto: RemovePlayListCommand)
    suspend fun whenAddAlbumMusics(addAlbumToPlayListCommand: AddAlbumMusicsToPlayListCommand)

    fun thenPlayListShouldBe(playList: PlayList)
    fun thenPlayListShouldDontExist(playList: PlayList)
    fun thenPlayListMusicsShouldBe(playList: PlayList)
    fun thenErrorShouldBe(errorClass: Class<out Throwable>)
    val playListRepository: InMemoryPlayListRepository
}

@OptIn(ExperimentalCoroutinesApi::class)
fun createFixturePlayList(musicGateway: MusicGateway): PlayListFixture {
    lateinit var throwError: Throwable

    val dateProvider = StubDateProvider(
        LocalDateTime.of(2020, 1, 1, 0, 0, 0)
    )

    val playListRepository = InMemoryPlayListRepository()
    val addPlayListUsecase = AddPlayListUsecase(playListRepository)
    val addMusicToPlayListUsecase = AddMusicToPlayListUsecase(playListRepository, musicGateway)
    val removePlayListUsecase = RemovePlayListUsecase(playListRepository)
    val addAlbumMusicsUsecase = AddAlbumMusicsUsecase(playListRepository, musicGateway)



    return object : PlayListFixture {

        override val playListRepository: InMemoryPlayListRepository
            get() = playListRepository

        override fun givenDateIs(date: LocalDateTime) {
            dateProvider.now = date
        }

        override fun givenExistingPlaylist(playList: ArrayList<PlayList>) {
            playListRepository.addMany(playList)
        }

        override suspend fun whenRemovePlayList(dto: RemovePlayListCommand) = runTest {
            async(
                Dispatchers.Default
            ) {
                try {
                    removePlayListUsecase.execute(dto)
                } catch (e: Throwable) {
                    throwError = e
                }
            }.await()
        }

        override suspend fun whenAddPlayList(dto: AddPlayListCommand) = runTest {
            async(
                Dispatchers.Default
            ) {
                try {
                    addPlayListUsecase.execute(dto)
                } catch (e: Throwable) {
                    throwError = e
                }
            }.await()
        }


        override suspend fun whenAddMusicToMyPlayList(dto: AddMusicToPlayListCommand) = runTest {
            async(
                Dispatchers.Default
            ) {
                try {
                    addMusicToPlayListUsecase.execute(dto)
                } catch (e: Throwable) {
                    throwError = e
                }
            }.await()
        }


        override suspend fun whenAddAlbumMusics(dto: AddAlbumMusicsToPlayListCommand) = runTest {
            async(
                Dispatchers.Default
            ) {
                try {
                    addAlbumMusicsUsecase.execute(dto)
                } catch (e: Throwable) {
                    throwError = e
                }
            }.await()
        }

        override fun thenPlayListShouldBe(playList: PlayList) {
            Assert.assertEquals(
                playList.data(),
                playListRepository.findMyPlayLists(playList.name).data()
            )
        }

        override fun thenPlayListShouldDontExist(playList: PlayList) {

            try {
                playListRepository.findMyPlayLists(playList.name)
                assert(false)
            } catch (e: NullPointerException) {
                assert(true)
            }
        }

        override fun thenPlayListMusicsShouldBe(playList: PlayList) {

            var excpectedArray = playList.songs.toArray()
            var actualArray = playListRepository.findMyPlayLists(playList.name).songs.toArray()

            Assert.assertEquals(excpectedArray.size, actualArray.size)
            assertArrayEquals(
                excpectedArray,
                actualArray
            )
        }

        override fun thenErrorShouldBe(errorClass: Class<out Throwable>) {
            Assert.assertTrue(errorClass.isInstance(throwError))
        }
    }
}