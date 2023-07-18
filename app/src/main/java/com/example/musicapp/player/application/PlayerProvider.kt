package com.example.musicapp.v2.player.application

import com.example.musicapp.v2.player.domain.Player
import com.example.musicapp.v2.player.domain.PlayerData
import java.time.LocalDateTime

interface PlayerProvider {
    suspend fun getCurrentMusic(): String
    suspend fun getPlayer(): Player
    suspend fun save(player: Player)
}