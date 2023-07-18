package com.example.musicapp.v2.album.domain


data class AlbumData(
    val id: Int,
    val name: String,
    val artistName: String,
    val albumCoverUrl: String
) {
}

class Album(
    val id: Int,
    val name: String,
    val artistName: String,
    val albumCoverUrl: String
) {
    fun data(
    ): AlbumData {
        return AlbumData(
            this.id,
            this.name,
            this.artistName,
            this.albumCoverUrl
        )
    }

    companion object {
        fun fromData(
            data: AlbumData
        ): Album {
            return Album(
                data.id,
                data.name,
                data.artistName,
                data.albumCoverUrl
            )
        }
    }
}