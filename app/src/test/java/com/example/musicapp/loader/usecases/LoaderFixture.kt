package com.example.musicapp.loader

import com.example.musicapp.v2.loader.application.usecases.PreLoadMusicsUsecase
import com.example.musicapp.v2.loader.application.LoadMusicDomainService
import com.example.musicapp.v2.loader.domain.MusicFile
import com.example.musicapp.v2.player.application.PlayerProvider
import com.example.musicapp.v2.player.domain.Music
import com.example.musicapp.v2.loader.infra.InMemoryFileLoaderRepository
import com.example.musicapp.v2.player.infra.StubDateProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import java.time.LocalDateTime


interface LoaderFixture {
    fun givenDateIs(date: LocalDateTime)
    fun givenExistingLoadedMusics(playList: Array<MusicFile>)
    suspend fun whenPreLoadMusics()
    fun thenMusicsLoadedShouldBe(playList: Array<Music>)
    fun thenErrorShouldBe(errorClass: Class<out Throwable>)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun createFixtureLoader(playerProvider: PlayerProvider): LoaderFixture {
    lateinit var throwError: Throwable

    val dateProvider = StubDateProvider(
        LocalDateTime.of(2020, 1, 1, 0, 0, 0)
    )

    val loaderRepository = InMemoryFileLoaderRepository()

    val loadMusicsUsecase = LoadMusicDomainService(
        loaderRepository
    )

    val preLoadMusicsUsecase = PreLoadMusicsUsecase(
        playerProvider,
        loaderRepository,
        loadMusicsUsecase = loadMusicsUsecase
    )


    return object : LoaderFixture {

        override fun givenDateIs(date: LocalDateTime) {
            dateProvider.now = date
        }

        override fun givenExistingLoadedMusics(files: Array<MusicFile>) {
            files.forEach {
                loaderRepository.addFile(it)
            }
        }

        override suspend fun whenPreLoadMusics() = runTest {
            async(
                Dispatchers.Default
            ) {
                try {
                    preLoadMusicsUsecase.execute()
                } catch (e: Throwable) {
                    throwError = e
                }
            }.await()
        }

        override fun thenMusicsLoadedShouldBe(excpectedFils: Array<Music>) {
            val excpectedFileLoads: Array<String> = excpectedFils.map { it.name }.toTypedArray()
            val existingLoadedFileNames: Array<String> =
                loaderRepository.fileLoads.map { it.name }.toTypedArray()

            excpectedFileLoads.sort()
            existingLoadedFileNames.sort()

            assertArrayEquals(
                excpectedFileLoads,
                existingLoadedFileNames
            )
        }


        override fun thenErrorShouldBe(errorClass: Class<out Throwable>) {
            Assert.assertTrue(errorClass.isInstance(throwError))
        }
    }
}