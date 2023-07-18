package com.example.musicapp.loader.usecases

import com.example.musicapp.loader.LoaderFixture
import com.example.musicapp.loader.createFixtureLoader
import com.example.musicapp.player.MusicBuilder
import com.example.musicapp.player.PlayerFixture
import com.example.musicapp.player.createFixturePlayMusic
import com.example.musicapp.v2.loader.domain.MusicFile
import com.example.musicapp.v2.player.domain.PlayerBuilder
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import com.example.musicapp.v2.playlist.infra.InMemoryPlayListRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.BeforeTest

class PreLoadMusicsUsecasesTest {
    private lateinit var fixture: LoaderFixture
    private lateinit var playerFixture: PlayerFixture


    @BeforeTest
    fun setUp() {
        playerFixture = createFixturePlayMusic(
            InMemoryMusicGateway(),
            InMemoryPlayListRepository()
        )
        fixture = createFixtureLoader(playerFixture.playerProvider)
    }

    @Test
    fun `the two second music and precedent music, wile be loaded`() = runTest {

        val music1 = MusicBuilder().withName("0").withFile(
            "file:///android_asset/0.mp3"
        ).build()
        val music2 = MusicBuilder().withName("1").withFile(
            "file:///android_asset/1.mp3"
        ).build()

        val music3 = MusicBuilder().withName("2").withFile(
            "file:///android_asset/2.mp3"
        ).build()

        val music4 = MusicBuilder().withName("3").withFile(
            "file:///android_asset/3.mp3"
        ).build()

        val player = PlayerBuilder().withPlayedIndex(1).withMusicList(
            arrayOf(
                music1,
                music2,
                music3,
                music4
            )
        ).build()

        val loadedMusics = arrayOf(
            music1,
            music2,
            music3,
            music4
        )

        playerFixture.givenPlayerIs(player)
        fixture.whenPreLoadMusics()
        fixture.thenMusicsLoadedShouldBe(loadedMusics)
    }

    @Test
    fun `at first music,play should ony load two next musics`() = runTest {

        val music1 = MusicBuilder().withName("0").withFile(
            "file:///android_asset/0.mp3"
        ).build()
        val music2 = MusicBuilder().withName("1").withFile(
            "file:///android_asset/1.mp3"
        ).build()

        val music3 = MusicBuilder().withName("2").withFile(
            "file:///android_asset/2.mp3"
        ).build()

        val music4 = MusicBuilder().withName("3").withFile(
            "file:///android_asset/3.mp3"
        ).build()

        val player = PlayerBuilder().withPlayedIndex(0).withMusicList(
            arrayOf(
                music1,
                music2,
                music3,
                music4
            )
        ).build()

        val loadedMusics = arrayOf(
            music1,
            music2,
            music3,
        )

        playerFixture.givenPlayerIs(player)
        fixture.whenPreLoadMusics()
        fixture.thenMusicsLoadedShouldBe(loadedMusics)
    }


    @Test
    fun `at 3 music,play should ony load two next musics and the previous music`() = runTest {

        val music1 = MusicBuilder().withName("0").withFile(
            "file:///android_asset/0.mp3"
        ).build()
        val music2 = MusicBuilder().withName("1").withFile(
            "file:///android_asset/1.mp3"
        ).build()

        val music3 = MusicBuilder().withName("2").withFile(
            "file:///android_asset/2.mp3"
        ).build()

        val music4 = MusicBuilder().withName("3").withFile(
            "file:///android_asset/3.mp3"
        ).build()

        val music5 = MusicBuilder().withName("4").withFile(
            "file:///android_asset/4.mp3"
        ).build()

        val player = PlayerBuilder().withPlayedIndex(2).withMusicList(
            arrayOf(
                music1,
                music2,
                music3,
                music4,
                music5
            )
        ).build()

        val loadedMusics = arrayOf(
            music2,
            music3,
            music4,
            music5
        )

        playerFixture.givenPlayerIs(player)
        fixture.whenPreLoadMusics()
        fixture.thenMusicsLoadedShouldBe(loadedMusics)
    }


    @Test
    fun `cache music at index - 2 is remove`() = runTest {

        val music1 = MusicBuilder().withName("0").withFile(
            "file:///android_asset/0.mp3"
        ).build()
        val music2 = MusicBuilder().withName("1").withFile(
            "file:///android_asset/1.mp3"
        ).build()

        val music3 = MusicBuilder().withName("2").withFile(
            "file:///android_asset/2.mp3"
        ).build()

        val music4 = MusicBuilder().withName("3").withFile(
            "file:///android_asset/3.mp3"
        ).build()

        val music5 = MusicBuilder().withName("4").withFile(
            "file:///android_asset/4.mp3"
        ).build()

        val player = PlayerBuilder().withPlayedIndex(2).withMusicList(
            arrayOf(
                music1,
                music2,
                music3,
                music4,
                music5
            )
        ).build()

        val loadedMusics = arrayOf(
            music2,
            music3,
            music4,
            music5
        )

        playerFixture.givenPlayerIs(player)
        fixture.givenExistingLoadedMusics(
            arrayOf(
                MusicFile(
                    music1.name,
                    music1.file
                ),
            )
        )
        fixture.whenPreLoadMusics()
        fixture.thenMusicsLoadedShouldBe(loadedMusics)
    }


    @Test
    fun `at the last index of the playlist`() = runTest {

        val music1 = MusicBuilder().withName("0").withFile(
            "file:///android_asset/0.mp3"
        ).build()
        val music2 = MusicBuilder().withName("1").withFile(
            "file:///android_asset/1.mp3"
        ).build()

        val music3 = MusicBuilder().withName("2").withFile(
            "file:///android_asset/2.mp3"
        ).build()

        val music4 = MusicBuilder().withName("3").withFile(
            "file:///android_asset/3.mp3"
        ).build()

        val music5 = MusicBuilder().withName("4").withFile(
            "file:///android_asset/4.mp3"
        ).build()

        val player = PlayerBuilder().withPlayedIndex(4).withMusicList(
            arrayOf(
                music1,
                music2,
                music3,
                music4,
                music5
            )
        ).build()

        val loadedMusics = arrayOf(
            music4,
            music5
        )

        playerFixture.givenPlayerIs(player)
        fixture.whenPreLoadMusics()
        fixture.thenMusicsLoadedShouldBe(loadedMusics)
    }


    /* @Test
     fun `one before the last index of the playlist`() = runTest {

         val music1 = MusicBuilder().withName("0").withFile(
             "file:///android_asset/0.mp3"
         ).build()
         val music2 = MusicBuilder().withName("1").withFile(
             "file:///android_asset/1.mp3"
         ).build()

         val music3 = MusicBuilder().withName("2").withFile(
             "file:///android_asset/2.mp3"
         ).build()

         val music4 = MusicBuilder().withName("3").withFile(
             "file:///android_asset/3.mp3"
         ).build()

         val music5 = MusicBuilder().withName("4").withFile(
             "file:///android_asset/4.mp3"
         ).build()

         val player = PlayerBuilder().withPlayedIndex(3).withMusicList(
             arrayOf(
                 music1,
                 music2,
                 music3,
                 music4,
                 music5
             )
         ).build()

         val loadedMusics = arrayOf(
             music3,
             music4,
             music5
         )

         playerFixture.givenPlayerIs(player)
         fixture.whenPreLoadMusics()
         fixture.thenMusicsLoadedShouldBe(loadedMusics)
     }*/
}