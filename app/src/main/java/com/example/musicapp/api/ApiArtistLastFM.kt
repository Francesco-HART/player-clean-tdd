package com.example.musicapp.api

import com.example.musicapp.AuthToken
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiArtistLastFM {

    @GET("?method=artist.getinfo&format=json")
    suspend fun getArtistInfos(
        @Query("artist") artist: String,
        @Query("api_key") apiKey: String = "fee589fd755b4eb9ee0fd4f6497e05c2"
    ): ApiArtist

    @GET("?method=album.getinfo&format=json")
    suspend fun getAlbumInfos(
        @Query("artist") artist: String,
        @Query("album") album: String,
        @Query("api_key") apiKey: String = "fee589fd755b4eb9ee0fd4f6497e05c2"
    ): ApiAlbumInfos
}