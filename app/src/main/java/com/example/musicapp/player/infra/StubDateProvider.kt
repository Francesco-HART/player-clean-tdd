package com.example.musicapp.v2.player.infra

import com.example.musicapp.v2.player.application.DateProvider
import java.time.LocalDateTime

class StubDateProvider(date: LocalDateTime) : DateProvider {

    var now: LocalDateTime = date


    override fun getDateTimeNow(): LocalDateTime {
        return now
    }
}