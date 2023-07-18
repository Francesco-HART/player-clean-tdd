package com.example.musicapp.v2.player.infra

import com.example.musicapp.api.ApiAlbum
import com.example.musicapp.v2.player.application.MusicGateway
import com.example.musicapp.v2.player.domain.Music
import java.util.ArrayList

class InMemoryMusicGateway : MusicGateway {
    private val musicList = mutableListOf<Music>()
    private val albumList = mutableListOf<ApiAlbum>()


    fun addAlbum(album: ApiAlbum) {
        albumList.add(album)
    }

    override suspend fun getAlbums(): List<ApiAlbum> {
        return albumList
    }

    override suspend fun getOneById(id: Int): Music {
        val music = musicList.find {
            it.id == id
        }!!

        return music.copy()
    }

    override suspend fun getAll(): Array<Music> {
        return musicList.toTypedArray()
    }

    override suspend fun getAllOfAlbum(albumId: String): ArrayList<Music> {
        ArrayList(musicList.map { println(it) })
        return ArrayList(musicList.filter { it.album == albumId.toInt() })
    }

    fun addingMusics(music: Array<Music>) {
        musicList.addAll(music)
    }

}