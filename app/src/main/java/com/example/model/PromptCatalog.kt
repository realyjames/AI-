package com.example.model

object PromptCatalog {
    val dimensions: List<PromptDimension> = listOf(
        MorningAfternoonCatalog.morningDimension,
        MorningAfternoonCatalog.afternoonDimension,
        EveningHolidayCatalog.eveningDimension,
        EveningHolidayCatalog.birthdayDimension,
        EveningHolidayCatalog.holidayDimension,
        MediaMusicCatalog.videoDimension,
        MediaMusicCatalog.musicDimension
    )

    fun findFormulaById(id: String): PromptFormula? =
        dimensions.asSequence().flatMap { it.formulas.asSequence() }.firstOrNull { it.id == id }

    fun defaultFormula(): PromptFormula = dimensions.first().formulas.first()
}
