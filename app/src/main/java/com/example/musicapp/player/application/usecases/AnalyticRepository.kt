package com.example.musicapp.v2.player.application.usecases

import java.time.LocalDateTime


data class Analytic(
    val timePlayInMs: Int,
    val user: String,
    val song: String,
    val createdAt: LocalDateTime
)

interface AnalyticRepository {
    suspend fun getOneById(id: Int): Analytic
    suspend fun saveAnalytic(analytic: Analytic)
}