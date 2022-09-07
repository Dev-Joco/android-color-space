package com.jojo.android.colorsimilarityapp.ui.colorSimilarity

import com.jojo.android.colorsimilarityapp.util.ColorSpace
import com.jojo.android.colorsimilarityapp.util.ColorType
import java.text.DecimalFormat


class ColorSimilarityTable(color1: ColorSpace, color2: ColorSpace) {

    private val similarities: FloatArray = FloatArray(4)
    private val distances: DoubleArray = DoubleArray(4)
    private val similarityThresholds: FloatArray = floatArrayOf(95f, 95f, 95f, 95f)
    private val distanceThresholds: DoubleArray = DoubleArray(4)

    private val formatter = DecimalFormat("#.##")

    init {
        updateEuclideanDistances(color1, color2)
        updateSimilarities()
        updateDistanceThresholds()
    }

    fun updateEuclideanDistances(color1: ColorSpace, color2: ColorSpace) {
        color1.distanceTo(color2, distances)
    }

    fun updateSimilarities() {
        for (type in ColorType.values) {
            similarities[type.ordinal] = (1 - (distances[type.ordinal] / type.ED_MAX)).toFloat() * 100
        }
    }

    fun plusSimilarityThresholds() {
        for (i in 0..3) {
            similarityThresholds[i] = (similarityThresholds[i] + 1).coerceAtMost(100f)
        }
        updateDistanceThresholds()
    }

    fun minusSimilarityThresholds() {
        for (i in 0..3) {
            similarityThresholds[i] = (similarityThresholds[i] - 1).coerceAtLeast(0f)
        }
        updateDistanceThresholds()
    }

    fun getSimilarity(colorType: ColorType): String {
        return "${formatter.format(similarities[colorType.ordinal])} %"
    }
    fun getEuclideanDistance(colorType: ColorType): String {
        return formatter.format(distances[colorType.ordinal])
    }
    fun getSimilarityThreshold(colorType: ColorType): String {
        return "${formatter.format(similarityThresholds[colorType.ordinal])} %"
    }
    fun getEuclideanDistanceThreshold(colorType: ColorType): String {
        return formatter.format(distanceThresholds[colorType.ordinal])
    }
    fun isCrossThreshold(colorType: ColorType): Boolean {
        return similarities[colorType.ordinal] >= similarityThresholds[colorType.ordinal]
    }

    private fun updateDistanceThresholds() {
        for (type in ColorType.values) {
            distanceThresholds[type.ordinal] = type.ED_MAX * (1 - similarityThresholds[type.ordinal] / 100.0)
        }
    }
}