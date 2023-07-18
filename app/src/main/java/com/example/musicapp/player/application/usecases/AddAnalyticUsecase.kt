package com.example.musicapp.v2.player.application.usecases

import com.example.musicapp.v2.player.application.DateProvider


data class AddAnalyticCommand(
    val timePlayInMs: Int,
    val user: String,
    val song: String,
)

class AddAnalyticUsecase(
    private val analyticRepository: AnalyticRepository,
    private val dateProvider: DateProvider
) {
    suspend fun execute(dto: AddAnalyticCommand) {
        val analytic = Analytic(
            dto.timePlayInMs,
            dto.user,
            dto.song,
            this.dateProvider.getDateTimeNow()
        )
        analyticRepository.saveAnalytic(analytic)
    }
}