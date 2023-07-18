package com.example.musicapp.album

import com.example.musicapp.v2.album.domain.Album
import com.example.musicapp.v2.album.domain.AlbumData

class AlbumBuilder {

    var album: AlbumData

    init {
        album = AlbumData(
            1,
            "Album 1",
            "Artist 1",
            "url",
        )
    }

    fun withId(id: Int): AlbumBuilder {
        album = album.copy(id = id)
        return this
    }

    fun withName(name: String): AlbumBuilder {
        album = album.copy(name = name)
        return this
    }

    fun withArtist(artist: String): AlbumBuilder {
        album = album.copy(artistName = artist)
        return this
    }

    fun withUrl(url: String): AlbumBuilder {
        album = album.copy(albumCoverUrl = url)
        return this
    }

    fun build(): Album {
        return Album.fromData(album)
    }

}