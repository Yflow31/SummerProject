package com.somaiya.summer_project.utils

import com.github.mikephil.charting.formatter.ValueFormatter

/**
 * @author Utsav Devadiga
 */
class PercentageValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()}%"
    }
}