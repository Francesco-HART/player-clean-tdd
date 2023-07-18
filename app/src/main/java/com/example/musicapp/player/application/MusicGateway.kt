package com.example.musicapp.v2.player.application

import com.example.musicapp.api.ApiAlbum
import com.example.musicapp.v2.player.domain.Music
import java.util.ArrayList

interface MusicGateway {

    suspend fun getAlbums(): List<ApiAlbum>

    suspend fun getOneById(id: Int): Music
    suspend fun getAll(): Array<Music>
    suspend fun getAllOfAlbum(albumId: String): ArrayList<Music>
}