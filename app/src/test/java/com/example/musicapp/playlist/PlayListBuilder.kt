package com.example.musicapp.playlist

import com.example.musicapp.v2.player.domain.Music
import com.example.musicapp.v2.playlist.domain.PlayList
import com.example.musicapp.v2.playlist.domain.PlayListData

class PlayListBuilder(private val userId: String) {
    var playList = PlayListData(
        name = "My Playlist",
        userId = userId,
        songs = arrayListOf()
    )

    fun withUserId(userId: String): PlayListBuilder {
        playList = playList.copy(userId = userId)
        return this
    }

    fun withName(name: String): PlayListBuilder {
        playList = playList.copy(name = name)
        return this
    }

    fun withSong(song: ArrayList<Music>): PlayListBuilder {
        playList = playList.copy(songs = song)
        return this
    }

    fun build(): PlayList {
        return PlayList.fromData(playList)
    }

}