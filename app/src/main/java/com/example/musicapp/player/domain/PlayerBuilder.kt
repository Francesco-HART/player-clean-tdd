package com.example.musicapp.v2.player.domain

import java.time.LocalDateTime

class PlayerBuilder {
    var player: PlayerData = PlayerData(
        "",
        0,
        0,
        PlayerStatus.STOPPED,
        arrayOf(),
        LocalDateTime.of(
            2020,
            1,
            1,
            0,
            0,
            0,
            0,

            ),
        0
    )


    fun withFile(file: String): PlayerBuilder {
        player = player.copy(file = file)
        return this
    }

    fun withStatus(status: PlayerStatus): PlayerBuilder {
        player = player.copy(status = status)
        return this
    }

    fun withListeningTimeInMs(listeningTimeInMs: Int): PlayerBuilder {
        player = player.copy(listeningTimeInMs = listeningTimeInMs)
        return this
    }

    fun withStartPlayAt(givenDateIs: LocalDateTime): PlayerBuilder {
        player = player.copy(startPlayAt = givenDateIs)
        return this
    }

    fun withPlayedIndex(playedIndex: Int): PlayerBuilder {
        player = player.copy(playedIndex = playedIndex)
        return this
    }

    fun withMusicList(
        musics: Array<Music>
    ): PlayerBuilder {
        player = player.copy(musics = musics)
        return this
    }

    fun withPlayingTimeInMs(i: Int): PlayerBuilder {
        player = player.copy(playingTimeInMs = i)
        return this
    }

    fun build(): Player {
        return Player.fromData(this.player)
    }
}