package com.example.musicapp.v2.album.infra

import com.example.musicapp.v2.album.application.AlbumGateway
import com.example.musicapp.v2.album.domain.Album
import com.example.musicapp.v2.album.domain.AlbumData

class InMemoryAlbumGateway : AlbumGateway {
    var albums = arrayListOf<AlbumData>()

    fun findMyAlbums(name: String): Album {
        val album = albums.find { it.name == name }!!
        return Album.fromData(album.copy())
    }

    fun addMany(albums: ArrayList<Album>) {
        this.albums.addAll(albums.map { it.data() })
    }

    fun add(
        album: Album
    ) {
        albums.add(album.data())
    }

    override suspend fun getOneById(id: String): Album {
        val album = albums.find { it.name == id }!!
        return Album.fromData(album.copy())
    }
}