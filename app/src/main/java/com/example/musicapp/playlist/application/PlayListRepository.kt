package com.example.musicapp.v2.playlist.application

import com.example.musicapp.v2.player.domain.Music
import com.example.musicapp.v2.playlist.domain.PlayList

interface PlayListRepository {
    suspend fun save(playList: PlayList)
    suspend fun getPlayListByName(name: String): PlayList
    suspend fun delete(playList: PlayList)
    suspend fun getMusics(playListName: String): Array<Music>
}