package com.example.musicapp.v2.player.infra

import com.example.musicapp.v2.player.application.DateProvider
import java.time.LocalDateTime

class DateNowProvider : DateProvider {
    override fun getDateTimeNow(): LocalDateTime {
        return LocalDateTime.now()
    }
}