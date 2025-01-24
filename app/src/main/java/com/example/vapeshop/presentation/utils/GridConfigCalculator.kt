package com.example.vapeshop.presentation.utils

import kotlin.math.roundToInt

private const val MIN_CARD_WIDTH_DP = 150
private const val MAX_CARD_WIDTH_DP = 300
private const val MAX_SPAN_COUNT = 6

class GridConfigCalculator(private val screenWidth: Int, private val density: Float) {

    fun calculateSpanCount(spacing: Int): Int {
        // Вычисляем минимальную и максимальную ширину карточки в пикселях
        val minCardWidth = (MIN_CARD_WIDTH_DP * density).roundToInt()
        val maxCardWidth = (MAX_CARD_WIDTH_DP * density).roundToInt()

        // Создаем диапазон для ширины карточки
        val range = minCardWidth..maxCardWidth

        var spanCount = 1
        var i = 2
        // Ищем оптимальное количество столбцов, пока не найдем подходящее значение или не достигнем максимального количества столбцов
        while (i <= MAX_SPAN_COUNT) {
            // Вычисляем ширину карточки для текущего количества столбцов
            val cardWidth =
                (screenWidth / i - spacing * (i + 1))
            // Если ширина карточки попадает в заданный диапазон, то устанавливаем количество столбцов и выходим из цикла
            if (cardWidth in range) {
                spanCount = i
                break
            }
            i++
        }
        return spanCount
    }

    fun calculateCardWidth(spanCount: Int, spacing: Int): Int {
        val totalSpacing = (spanCount + 1) * spacing
        return ((screenWidth - totalSpacing) / spanCount.toFloat()).roundToInt()
    }
}