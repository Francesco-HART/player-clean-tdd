package com.example.musicapp.album


import com.example.musicapp.v2.album.application.AlbumGateway
import com.example.musicapp.v2.album.domain.Album
import com.example.musicapp.v2.album.infra.InMemoryAlbumGateway
import com.example.musicapp.v2.player.application.MusicGateway


interface AlbumFixture {
    fun givenExistingAlbums(albums: ArrayList<Album>)
    val albumGateway: AlbumGateway
}

fun createFixtureAlbum(musicGateway: MusicGateway): AlbumFixture {
    val albumGateway = InMemoryAlbumGateway()

    return object : AlbumFixture {

        override fun givenExistingAlbums(albums: ArrayList<Album>) {
            albumGateway.addMany(albums)
        }

        override val albumGateway: AlbumGateway
            get() = albumGateway
    }
}
