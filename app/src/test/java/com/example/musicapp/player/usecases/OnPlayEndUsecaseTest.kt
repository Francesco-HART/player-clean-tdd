package com.example.musicapp.player.usecases

import com.example.musicapp.player.MusicBuilder
import com.example.musicapp.player.PlayerFixture
import com.example.musicapp.player.createFixturePlayMusic
import com.example.musicapp.playlist.PlayListFixture
import com.example.musicapp.playlist.createFixturePlayList
import com.example.musicapp.v2.player.domain.PlayerBuilder
import com.example.musicapp.v2.player.domain.PlayerStatus
import com.example.musicapp.v2.player.infra.InMemoryMusicGateway
import kotlinx.coroutines.test.runTest
import java.time.LocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import org.junit.Test

class OnPlayEndUsecaseTest {
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
    fun `When first played music is end , Play second next music in the queue`() = runTest {
        val givenNowis = LocalDateTime.of(2020, 1, 1, 0, 0, 0)

        val musicToPlay = MusicBuilder()
            .build()

        val musicplayed = MusicBuilder()
            .build()

        val playerBuilder = PlayerBuilder()
            .withFile(musicplayed.file)
            .withStatus(PlayerStatus.PLAYING)
            .withStartPlayAt(givenNowis)
            .withMusicList(arrayOf(musicplayed, musicToPlay))


        fixture.givenMusicExists(arrayOf(musicplayed, musicToPlay))
        fixture.givenLoadedMusicExists(arrayOf(musicplayed, musicToPlay))

        fixture.givenPlayerIs(playerBuilder.build())
        fixture.givenEndMusicEventIsFollow()

        fixture.whenEndMusicEventIsEmmitted(playerBuilder.build())

        fixture.thenPlayerShouldBe(playerBuilder.withFile(musicToPlay.file).build())
    }


    /*    @Test
        fun `When second played music is end , Play the 3 music in the queue`() = runTest {
            val givenNowis = LocalDateTime.of(2020, 1, 1, 0, 0, 0)


            val firstMusic = MusicBuilder()
                .withFile("Music1")
                .build()

            val musicplayed = MusicBuilder()
                .withFile("Music3")
                .build()

            val musicToPlay = MusicBuilder()
                .withFile("Music2")
                .build()


            val player = PlayerBuilder()
                .withFile(musicplayed.file)
                .withStatus(PlayerStatus.PLAYING)
                .withStartPlayAt(givenNowis)
                .withMusicList(arrayOf(firstMusic, musicplayed, musicToPlay)).build()


            val excpectPlayer = PlayerBuilder()
                .withFile(musicToPlay.file)
                .withStatus(PlayerStatus.PLAYING)
                .withStartPlayAt(givenNowis)
                .withMusicList(arrayOf(firstMusic, musicplayed, musicToPlay)).build()


            fixture.givenMusicExists(arrayOf(firstMusic, musicplayed, musicToPlay))
            fixture.givenPlayerIs(player)
            async {
                fixture.givenEndMusicEventIsFollow()
                fixture.whenEndMusicEventIsEmmitted(player)
                fixture.thenPlayerShouldBe(excpectPlayer)
            }.await()

        }*/

    /*@OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `When last music to play  is end , Play the first music in the queue`() = runTest {
        val givenNowis = LocalDateTime.of(2020, 1, 1, 0, 0, 0)

        val musicToPlay = MusicBuilder()

            .withFile("Music1")
            .build()

        val secondMusic = MusicBuilder()
            .withFile("Music2")
            .build()

        val musicplayed = MusicBuilder()
            .withFile("Music3")
            .build()


        val musics = arrayOf(musicToPlay, secondMusic, musicplayed)

        val player = PlayerBuilder()
            .withFile(musics[2].file)
            .withStatus(PlayerStatus.PLAYING)
            .withStartPlayAt(givenNowis)
            .withMusicList(musics)
            .build()

        val excpectPlayer = PlayerBuilder()
            .withStatus(PlayerStatus.PLAYING)
            .withStartPlayAt(givenNowis)
            .withMusicList(musics)
            .withFile(musics[0].file)
            .build()


        fixture.givenMusicExists(musics)
        fixture.givenPlayerIs(player)
        fixture.givenEndMusicEventIsFollow()
        fixture.whenEndMusicEventIsEmmitted(player)
        fixture.thenPlayerAfterEndEventShouldBe(excpectPlayer)
    }*/

    /*
        @Test
        fun `Play music send notification when new music is played`() = runTest {
            val givenNowis = LocalDateTime.of(2020, 1, 1, 0, 0, 0)

            val musicToPlay = MusicBuilder()
                .build()

            val musicplayed = MusicBuilder()
                .build()

            val player = PlayerBuilder()
                .withFile(musicplayed.file)
                .withStatus(PlayerStatus.PLAYING)
                .withStartPlayAt(givenNowis)
                .withMusicList(arrayOf(musicplayed, musicToPlay)).build()

            fixture.givenMusicExists(arrayOf(musicplayed, musicToPlay))
            fixture.givenPlayerIs(player)
            fixture.givenEndMusicEventIsFollow()
            fixture.whenEndMusicEventIsEmmitted(player)
            fixture.thenPlayerNotificationShouldBeSend(ChangePlayerStateEvent(player))
        }*/
}