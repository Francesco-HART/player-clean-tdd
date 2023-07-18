package com.example.musicapp.v2.album.application

import com.example.musicapp.v2.album.domain.Album

interface AlbumGateway {
    suspend fun getOneById(id: String): Album
}