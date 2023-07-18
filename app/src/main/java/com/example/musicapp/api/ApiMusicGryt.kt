package com.example.musicapp.api

import com.example.musicapp.AuthToken
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiMusicGryt {
    @POST("api-token-auth/")
    suspend fun authentication(@Body authToken: AuthToken): ApiSession

    @GET("api/genre/")
    suspend fun getGenre(@HeaderMap headerMap: Map<String, String> = ApiGryt.getHeaders()): List<ApiGenre>

    @GET("api/albums/")
    suspend fun getAlbums(@HeaderMap headerMap: Map<String, String> = ApiGryt.getHeaders()): List<ApiAlbum>

    @GET("api/songs/")
    suspend fun getSongs(
        @HeaderMap headerMap: Map<String, String> = ApiGryt.getHeaders(),
        @Query("album__id") albumId: Int
    ): List<ApiSong>

    @Streaming
    @GET
    suspend fun downloadSong(@Url fileUrl: String): Response<ResponseBody>

    @GET("api/songs/")
    suspend fun getAllSongs(
        @HeaderMap headerMap: Map<String, String> = ApiGryt.getHeaders(),
    ): List<ApiSong>

    @GET("api/songs/")
    suspend fun getOneSong(
        @HeaderMap headerMap: Map<String, String> = ApiGryt.getHeaders(),
        @Query("album__id") sondId: Int
    ): ApiSong

}

/*private fun ResponseBody.saveFile() {
    val destinationFile = File(destination_path)
    byteStream().use { inputStream->
        destinationFile.outputStream().use { outputStream->
            inputStream.copyTo(outputStream)
        }
    }
}*/