package com.example.musicapp.v2.player.domain

import com.example.musicapp.v2.player.domain.Music

class MusicBuilder {
    var music: Music

    init {
        music = Music(1, "Music 1", "Artist 1", 1, 1)
    }

    fun withId(id: Int): MusicBuilder {
        music = music.copy(id = id)
        return this
    }

    fun withName(name: String): MusicBuilder {
        music = music.copy(name = name)
        return this
    }

    fun withFile(file: String): MusicBuilder {
        music = music.copy(file = file)
        return this
    }

    fun withAlbum(album: Int): MusicBuilder {
        music = music.copy(album = album)
        return this
    }

    fun withDurationInS(duration: Int): MusicBuilder {
        music = music.copy(durationInS = duration)
        return this
    }

    fun build(): Music {
        return music
    }

}