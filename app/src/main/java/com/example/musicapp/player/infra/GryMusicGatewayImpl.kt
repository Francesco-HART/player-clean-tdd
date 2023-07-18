package com.example.musicapp.v2.player.infra

import com.example.musicapp.api.ApiAlbum
import com.example.musicapp.api.ApiGryt
import com.example.musicapp.api.ApiSong
import com.example.musicapp.v2.player.application.MusicGateway
import com.example.musicapp.v2.player.domain.Music
import java.util.ArrayList


class GryMusicGatewayImpl : MusicGateway {
    override suspend fun getAlbums(): List<ApiAlbum> {
        return ApiGryt.getApi().getAlbums()
    }

    override suspend fun getOneById(id: Int): Music {
        val apiMusic = ApiGryt.getApi().getOneSong(sondId = id)
        return apiMusic?.toMusic() ?: throw MusicNotFoundException("Music not found")
    }

    override suspend fun getAll(): Array<Music> {
        val apiSongs = ApiGryt.getApi().getAllSongs()
        return apiSongs.map { it.toMusic() }.toTypedArray()
    }

    override suspend fun getAllOfAlbum(albumId: String): ArrayList<Music> {
        val apiSongs = ApiGryt.getApi().getSongs(albumId = albumId.toInt())
        return ArrayList(apiSongs.map { it.toMusic() })
    }
}

class MusicNotFoundException(message: String) : Exception(message)

fun ApiSong.toMusic(): Music {
    return Music(
        id = id,
        name = name,
        album = album,
        durationInS = duration,
        file = file
    )
}

