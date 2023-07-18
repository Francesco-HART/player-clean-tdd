package com.example.musicapp.v2.player.domain

class MusicNotFoundError : Throwable() {}

data class Music(val id: Int, val name: String, val file: String, val album: Int, val durationInS: Int)
