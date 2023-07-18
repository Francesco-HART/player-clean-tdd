package com.example.musicapp.api

import com.example.musicapp.Album
import com.example.musicapp.AlbumInfos
import com.example.musicapp.Artist
import com.example.musicapp.Genre
import com.example.musicapp.Song
import com.example.musicapp.v2.player.domain.Music

fun mapApiAlbumsToAlbums(apiAlbums: List<ApiAlbum>): List<Album> {
    val list = mutableListOf<Album>()
    for (apiAlbum in apiAlbums) {
        list.add(mapApiAlbums(apiAlbum))
    }
    return list
}

fun mapApiSongsToSongs(apiSongs: List<ApiSong>): List<Song> {
    val list = mutableListOf<Song>()
    for (apiSong in apiSongs) {
        list.add(mapApiSongs(apiSong))
    }
    return list
}

fun mapMusicToSong(music: Music): Song {
    return Song(
        id = music.id,
        album = music.album,
        name = music.name,
        file = music.file,
        duration = music.durationInS
    )
}

fun mapApiSongs(apiSong: ApiSong): Song {
    return Song(
        id = apiSong.id,
        album = apiSong.album,
        name = apiSong.name,
        file = apiSong.file,
        duration = apiSong.duration
    )
}

fun filterListByGenre(albums: List<Album>, genre: Genre): List<Album>{
    val list = mutableListOf<Album>()
    for(album in albums){
        if(album.genre == genre){
            list.add(album)
        }
    }
    return list
}

fun filterAlbumsByArtist(albums: List<Album>, artist: String): List<Album>{
    val list = mutableListOf<Album>()
    for(album in albums){
        if(album.artists == artist){
            list.add(album)
        }
    }
    return list
}

fun mapApiGenre(apiGenre: ApiGenre): Genre{
    return Genre(
        id = apiGenre.id,
        name = apiGenre.name
    )
}

fun mapApiAlbums(apiAlbum: ApiAlbum): Album {
    return Album(
        id = apiAlbum.id,
        title = apiAlbum.name,
        artists = apiAlbum.artist_name,
        urlPoster = apiAlbum.album_cover_url,
        genre = mapApiGenre(apiAlbum.genre)
    )
}

fun mapApiAlbumsInfos(apiAlbumInfos: ApiAlbumInfos): AlbumInfos {
    return AlbumInfos(
        name = apiAlbumInfos.album.name,
        artist = apiAlbumInfos.album.artist,
        playcount = apiAlbumInfos.album.playcount,
        listeners = apiAlbumInfos.album.listeners,
        img = apiAlbumInfos.album.image[2].url
    )
}

fun mapApiAlbumsInfosToAlbumsInfos(apiAlbumsInfos: List<ApiAlbumInfos>): List<AlbumInfos> {
    val list = mutableListOf<AlbumInfos>()
    for (apiAlbumInfos in apiAlbumsInfos) {
        list.add(mapApiAlbumsInfos(apiAlbumInfos))
    }
    return list
}

fun mapApiArtist(apiArtist: ApiArtist): Artist {
    return Artist(
        name = apiArtist.artist.name,
        img = apiArtist.artist.image[0].url,
        albums = listOf<Album>(),
        listeners = apiArtist.artist.stats.listeners
    )
}

