package com.example.musicapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Session(
    val username: String,
    val token: String,
    val playlists: List<Playlist> = listOf()
): Parcelable

data class AuthToken(
    val username: String,
    val password: String,
)

@Parcelize
data class Song(
    val id: Int,
    val album: Int,
    val name: String,
    val file: String,
    val duration: Int
): Parcelable

@Parcelize
data class Album(
    val id: Int,
    val title: String,
    val artists: String,
    val urlPoster: String,
    val genre: Genre
) : Parcelable

@Parcelize
data class AlbumInfos(
    val name: String,
    val artist: String,
    val playcount: String,
    val listeners: String,
    val img: String
) : Parcelable

@Parcelize
data class Genre(
    val id: Int,
    val name: String,
) : Parcelable

@Parcelize
data class Artist(
    val name: String,
    val img: String,
    val albums: List<Album>,
    val listeners: String
): Parcelable

@Parcelize
data class  Playlist(
    val id: Int,
    val name: String,
    val songs: List<Song>,
): Parcelable
