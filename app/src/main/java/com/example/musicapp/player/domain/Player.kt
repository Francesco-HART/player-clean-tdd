package com.example.musicapp.v2.player.domain

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Collections

fun convertSToMs(time: Int): Int {
    return time * 1000
}

val TEN_SECONDS_IN_MS = 10000
val SECOND_TO_REMOVE_BEFORE_END = 4

data class PlayerData(
    val file: String,
    val listeningTimeInMs: Int,
    val playingTimeInMs: Int,
    val status: PlayerStatus,
    val musics: Array<Music>,
    val startPlayAt: LocalDateTime,
    val playedIndex: Int
)

data class EndMusicEvent(
    val playerData: PlayerData
)

data class ChangeMusicNotification(
    val player: Player
)

class Player(
    var file: String,
    var listeningTimeInMs: Int,
    var playingTimeInMs: Int,
    var status: PlayerStatus,
    var musics: Array<Music>,
    var startPlayAt: LocalDateTime,
    var playedIndex: Int
) {


    fun play(file: String, startPlayAt: LocalDateTime) {
        save(
            PlayerBuilder()
                .withFile(file)
                .withListeningTimeInMs(this.listeningTimeInMs)
                .withStatus(PlayerStatus.PLAYING)
                .withMusicList(this.musics)
                .withStartPlayAt(startPlayAt)
                .withPlayedIndex(this.playedIndex)
                .build()
        )
    }


    fun setPlayingTime(time: Int) {
        this.playingTimeInMs = if (time < 0) 0 else time
    }

    fun pause(now: LocalDateTime) {

        val timePlaye = playingTimeInMs + calculatetimePlayInMs(now).toInt()

        val listeningTime = listeningTimeInMs + calculatetimePlayInMs(now).toInt()

        save(
            PlayerBuilder()
                .withFile(file)
                .withPlayingTimeInMs(timePlaye)
                .withListeningTimeInMs(listeningTime)
                .withStatus(PlayerStatus.PAUSED)
                .withMusicList(this.musics)
                .withStartPlayAt(startPlayAt)
                .build()
        )
    }

    private fun save(
        playerData: Player,
    ) {
        this.file = playerData.file
        this.playingTimeInMs = playerData.playingTimeInMs
        this.startPlayAt = playerData.startPlayAt
        this.status = playerData.status
        this.musics = playerData.musics
        this.listeningTimeInMs = playerData.listeningTimeInMs
        this.playedIndex = playerData.playedIndex
    }


    private fun shuffleMusicsWithoutChangingFirstMusic(musics: Array<Music>): Array<Music> {
        var musicListCopy = musics.copyOf().asList()

        val firstElement = musicListCopy[0]
        val remainingElements = musicListCopy.subList(1, musicListCopy.size)

        Collections.shuffle(remainingElements)

        val randomizedList = mutableListOf<Music>().apply {
            add(firstElement)
            addAll(remainingElements)
        }.toTypedArray()
        return randomizedList
    }


    private fun calculatetimePlayInMs(endPlayAt: LocalDateTime): Long {

        val startInMs = this.startPlayAt.atZone(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()

        val endInMs = endPlayAt.atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return endInMs - startInMs!!
    }


    fun data(): PlayerData {
        return PlayerData(
            this.file,
            this.listeningTimeInMs,
            this.playingTimeInMs,
            this.status,
            this.musics,
            this.startPlayAt,
            this.playedIndex
        )
    }

    fun generateMusicsToPlay(musics: Array<Music>) {
        this.musics = shuffleMusicsWithoutChangingFirstMusic(musics)
    }

    fun playNextMusic(): Music {

        var musicIndexToPlay = playedIndex + 1

        if (musicIndexToPlay >= musics.size) {
            musicIndexToPlay = 0
        }
        return musics[musicIndexToPlay]

    }

    fun previousPlay(now: LocalDateTime): Music {

        if (playingTimeInMs <= TEN_SECONDS_IN_MS && playedIndex > 0) {
            return musics[playedIndex - 1]
        }

        playingTimeInMs = 0

        return musics[playedIndex]

    }

    fun changePlatingTimeLine(time: Int) {

        var timeToSet = time

        val musicPlayed = musics[playedIndex]

        if (musicPlayed == null) {
            playingTimeInMs = 0
        }


        if (convertSToMs(musicPlayed!!.durationInS) <= time) {
            timeToSet = convertSToMs((musicPlayed.durationInS - SECOND_TO_REMOVE_BEFORE_END))
        }


        this.setPlayingTime(timeToSet)
    }

    companion object {
        fun fromData(playerData: PlayerData) = Player(
            playerData.file,
            playerData.listeningTimeInMs,
            playerData.playingTimeInMs,
            playerData.status,
            playerData.musics,
            playerData.startPlayAt,
            playerData.playedIndex
        )
    }
}

enum class PlayerStatus {
    PLAYING, PAUSED, STOPPED
}