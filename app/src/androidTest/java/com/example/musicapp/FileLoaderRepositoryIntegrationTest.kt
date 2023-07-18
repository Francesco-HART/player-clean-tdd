package com.example.musicapp

import com.example.musicapp.v2.loader.domain.FileLoaderRepository
import com.example.musicapp.v2.player.infra.LocalFileLoaderRepository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class FileLoaderRepositoryIntegrationTest {
    private lateinit var context: Context
    private lateinit var fileLoaderRepository: FileLoaderRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        fileLoaderRepository = LocalFileLoaderRepository(context)
    }

    @After
    fun cleanup() {
        // Supprimer tous les fichiers de test du répertoire de cache après chaque test
        val cacheDir = context.cacheDir
        val files = cacheDir.listFiles()
        files?.forEach { file ->
            file.delete()
        }
    }

    @Test
    fun testLoadFile() = runBlocking {
        val name = "test_file.mp3"
        val loadingPath = "https://music.gryt.tech/medias/uploads/2023/06/07/TLC_-_No_Scrubs_Official_Video.mp3"

        val musicFile = fileLoaderRepository.loadFile(name, loadingPath)

        assertEquals(name, musicFile.name)
        assertTrue(File(musicFile.path).exists())
    }

    @Test
    fun testFindByName() = runBlocking {
        val name = "test_file.mp3"
        val loadingPath = "https://music.gryt.tech/medias/uploads/2023/06/07/TLC_-_No_Scrubs_Official_Video.mp3"

        val musicFile = fileLoaderRepository.loadFile(name, loadingPath)
        val foundMusicFile = fileLoaderRepository.findByName(name)

        assertEquals(musicFile.name, foundMusicFile.name)
        assertEquals(musicFile.path, foundMusicFile.path)
    }

    @Test
    fun testIsLoader() = runBlocking {
        val name = "test_file.mp3"
        val loadingPath = "https://music.gryt.tech/medias/uploads/2023/06/07/TLC_-_No_Scrubs_Official_Video.mp3"

        assertFalse(fileLoaderRepository.isLoader(name))

        fileLoaderRepository.loadFile(name, loadingPath)

        assertTrue(fileLoaderRepository.isLoader(name))
    }

    @Test
    fun testRemove() = runBlocking {
        val name = "test_file.mp3"
        val loadingPath = "https://music.gryt.tech/medias/uploads/2023/06/07/TLC_-_No_Scrubs_Official_Video.mp3"

        fileLoaderRepository.loadFile(name, loadingPath)

        assertTrue(fileLoaderRepository.isLoader(name))

        fileLoaderRepository.remove(name)

        assertFalse(fileLoaderRepository.isLoader(name))
    }
}
