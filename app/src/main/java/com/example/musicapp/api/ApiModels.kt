package com.example.musicapp.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiMoviesWrapper(
    val total_pages: Int,
    val results: List<ApiSong>,
)

@JsonClass(generateAdapter = true)
data class ApiSong(
    val id: Int,
    val album: Int,
    val name: String,
    val file: String,
    val duration: Int
)

@JsonClass(generateAdapter = true)
data class ApiCreditWrapper(
    val cast: List<ApiCast>,
)

@JsonClass(generateAdapter = true)
data class ApiCast(
    val id: Int,
    val name: String,
    val character: String
)

@JsonClass(generateAdapter = true)
data class ApiSession(
    val token: String,
)

@JsonClass(generateAdapter = true)
data class ApiGenre(
    val id:Int,
    val name: String,
)

@JsonClass(generateAdapter = true)
data class ApiAlbum(
    val id:Int,
    val name: String,
    val artist_name: String,
    val genre: ApiGenre,
    val album_cover_url: String
)

@JsonClass(generateAdapter = true)
data class ApiArtist(
    val artist: ApiArtistWrapper
)

@JsonClass(generateAdapter = true)
data class ApiArtistWrapper(
    val name: String,
    val image: List<ApiImage>,
    val stats: ApiArtistStats
)

@JsonClass(generateAdapter = true)
data class ApiArtistStats(
    val listeners: String,
    val playcount: String,
)

@JsonClass(generateAdapter = true)
data class ApiImage(
    @Json(name = "#text")
    val url: String,
    val size: String,
)

@JsonClass(generateAdapter = true)
data class ApiAlbumInfos(
    val album: ApiAlbumChildrenInfos
)

@JsonClass(generateAdapter = true)
data class ApiAlbumChildrenInfos(
    val name: String,
    val artist: String,
    val playcount: String,
    val listeners: String,
    val image: List<ApiImage>
)
