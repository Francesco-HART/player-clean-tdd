package com.example.musicapp.v2.player.infra

import com.example.musicapp.v2.player.application.PlayerProvider
import com.example.musicapp.v2.player.domain.Player
import com.example.musicapp.v2.player.domain.PlayerData
import com.example.musicapp.v2.player.domain.PlayerBuilder
import com.example.musicapp.v2.player.domain.PlayerStatus
import java.time.LocalDateTime

class StubPlayerProvider : PlayerProvider {

    var player: Player = Player.fromData(PlayerBuilder().build().data().copy())

    override suspend fun getCurrentMusic(): String {
        return player.file
    }

    override suspend fun getPlayer(): Player {
        return player
    }

    override suspend fun save(player: Player) {
        this.player = Player.fromData(player.data().copy())
    }
}