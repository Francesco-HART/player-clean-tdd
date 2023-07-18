package com.example.musicapp.v2.player.application

import java.time.LocalDateTime

interface DateProvider {
    fun getDateTimeNow(): LocalDateTime
}