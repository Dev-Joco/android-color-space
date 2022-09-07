package com.jojo.android.colorsimilarityapp.ui.colorSimilarity.adapter

import com.jojo.android.colorsimilarityapp.util.ColorType
import java.text.DecimalFormat

data class ColorSeekBarStates(
    val rgb: ThreeSeekBar,
    val hsl: ThreeSeekBar,
    val xyz: ThreeSeekBar,
    val lab: ThreeSeekBar,
) {
    operator fun get(index: Int): ThreeSeekBar = when(index) {
        0 -> rgb
        1 -> hsl
        2 -> xyz
        3 -> lab
        else -> throw IllegalArgumentException("index=$index, must be in range(0..3)")
    }
}

data class ThreeSeekBar(
    val type: ColorType,
    val seek0: SeekBarState,
    val seek1: SeekBarState,
    val seek2: SeekBarState,
) {
    operator fun get(index: Int): SeekBarState = when(index) {
        0 -> seek0
        1 -> seek1
        2 -> seek2
        else -> throw IllegalArgumentException("index=$index, must be in range(0..2)")
    }
}

data class SeekBarState(
    val range: IntRange = 0..100,
    val steps: Float = 1f,
    var progress: Int = 0,
) {
    val value: Number
        get() = if (steps == 1f) progress else progress * steps
    val valueStr: String
        get() = if (steps == 1f) value.toString() else numberFormat.format(value)
}

private val numberFormat = DecimalFormat("0.00")