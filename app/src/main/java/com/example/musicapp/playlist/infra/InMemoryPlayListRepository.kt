package com.example.musicapp.v2.playlist.infra

import com.example.musicapp.v2.player.domain.Music
import com.example.musicapp.v2.playlist.application.PlayListRepository
import com.example.musicapp.v2.playlist.domain.PlayList
import com.example.musicapp.v2.playlist.domain.PlayListData

class InMemoryPlayListRepository : PlayListRepository {
    var playLists = arrayListOf<PlayListData>()

    override suspend fun save(playList: PlayList) {
        val newPlayListArray = playLists
        val existingIndex = playLists.indexOfFirst { it.name == playList.name }
        if (existingIndex != -1) {
            newPlayListArray[existingIndex] = playList.data()
        } else {
            newPlayListArray.add(playList.data())
        }
        playLists = newPlayListArray
    }

    fun findMyPlayLists(name: String): PlayList {
        val playlist = playLists.find { it.name == name }!!
        return PlayList.fromData(playlist.copy())
    }

    override suspend fun getPlayListByName(name: String): PlayList {
        return findMyPlayLists(name)
    }

    fun addMany(playLists: ArrayList<PlayList>) {
        this.playLists.addAll(playLists.map { it.data() })
    }

    fun add(
        playList: PlayList
    ) {
        playLists.add(playList.data())
    }

    override suspend fun delete(playList: PlayList) {
        playLists.remove(playList.data())
    }

    override suspend fun getMusics(playListName: String): Array<Music> {
        return findMyPlayLists(playListName).songs.toTypedArray()
    }
}