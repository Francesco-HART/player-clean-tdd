package com.example.musicapp.v2.player.infra

import com.example.musicapp.v2.player.application.usecases.Analytic
import com.example.musicapp.v2.player.application.usecases.AnalyticRepository

class InMemoryAnalytic : AnalyticRepository {
    val analytics = mutableListOf<Analytic>()

    override suspend fun getOneById(id: Int): Analytic {
        return analytics.get(id)
    }

    override suspend fun saveAnalytic(analytic: Analytic) {
        analytics.add(analytic)
    }
}