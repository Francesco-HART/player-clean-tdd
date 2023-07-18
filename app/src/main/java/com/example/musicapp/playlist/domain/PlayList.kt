package com.example.musicapp.v2.playlist.domain

import com.example.musicapp.v2.player.domain.Music


class PlaylistNameAlreadyUseError : Throwable() {}
class CantRemoveThisPlayListError : Throwable() {}

data class PlayListData(
    var name: String,
    var userId: String,
    var songs: ArrayList<Music>
)

class PlayList(
    var name: String,
    var userId: String,
    var songs: ArrayList<Music>
) {

    fun data(): PlayListData {
        return PlayListData(
            this.name,
            this.userId,
            this.songs
        )
    }

    fun addMusics(musics: ArrayList<Music>) {
        musics.forEach { music ->
            addSong(music)
        }
    }

    fun addSong(music: Music) {
        if (!songs.contains(music)) {
            songs.add(music)
        }
    }

    companion object {
        fun fromData(
            data: PlayListData
        ): PlayList {
            return PlayList(
                data.name,
                data.userId,
                data.songs
            )
        }
    }

}