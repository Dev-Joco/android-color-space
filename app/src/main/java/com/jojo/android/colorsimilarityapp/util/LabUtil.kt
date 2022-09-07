package com.jojo.android.colorsimilarityapp.util

import kotlin.math.*

/*
 * https://en.wikipedia.org/wiki/CIE_1931_color_space
 * https://ko.wikipedia.org/wiki/CIELAB_%EC%83%89_%EA%B3%B5%EA%B0%84
 * https://en.wikipedia.org/wiki/Color_difference#cite_note-20
 * https://m.blog.naver.com/atago59/222092830734
 * */
object LabUtil {

    private const val PI = Math.PI
    private const val DEGREES_TO_RADIANS = 0.017453292519943295
    private const val RADIANS_TO_DEGREES = 57.29577951308232

    private val POW_25_7 = (25.0).pow(7.0)

    fun deltaE2000(Lab1: DoubleArray, Lab2: DoubleArray): Double {
        val (L1, a1, b1) = Lab1
        val (L2, a2, b2) = Lab2

        // ΔL'
        val dLp = L2 - L1

        // ΔC'
        val C1 = sqrt(a1.pow(2) + b1.pow(2))
        val C2 = sqrt(a2.pow(2) + b2.pow(2))
        val Cb = (C1 + C2) / 2
        val Cb_7 = Cb.pow(7)
        val G = sqrt(Cb_7 / (Cb_7 + POW_25_7))
        val ap1 = a1 + a1 / 2 * (1 - G)
        val ap2 = a2 + a2 / 2 * (1 - G)
        val Cp1 = sqrt(ap1.pow(2) + b1.pow(2))
        val Cp2 = sqrt(ap2.pow(2) + b2.pow(2))
        val dCp = Cp2 - Cp1

        // ΔH'
        val hp1 = atan2(b1, ap1).toDegrees() % 360
        val hp2 = atan2(b2, ap2).toDegrees() % 360
        val hp_diff = (hp1 - hp2).absoluteValue
        val hp_sum = hp1 + hp2
        val d_hp = if (hp_diff <= 180) {
            hp2 - hp1
        } else if (hp2 <= hp1) {
            hp2 - hp1 + 360
        } else {
            hp2 - hp1 - 360
        }
        val dHp = 2 * sqrt(Cp1 * Cp2) * sin((d_hp / 2).toRadians())

        // SL
        val Lb = (L1 + L2) / 2
        val SL = 1 + 0.015 * (Lb - 50).pow(2) / sqrt(20 + (Lb - 50).pow(2))

        // SC
        val Cpb = (Cp1 + Cp2) / 2
        val SC = 1 + 0.045 * Cpb

        // SH
        val Hpb = if (hp_diff <= 180) {
            hp_sum / 2
        } else if (hp_sum < 360) {
            (hp_sum + 360) / 2
        } else {
            (hp_sum - 360) / 2
        }
        val T = 1 - 0.17 * cos((Hpb - 30).toRadians())
                  + 0.24 * cos((2 * Hpb).toRadians())
                  + 0.32 * cos((3 * Hpb + 6).toRadians())
                  - 0.20 * cos((4 * Hpb - 63).toRadians())
        val SH = 1 + 0.015 * Cpb * T

        // RT
        val Cpb_7 = Cpb.pow(7)
        val RT = -2 * sqrt(Cpb_7 / (Cpb_7 + POW_25_7)) * sin((60 * exp(-((Hpb - 275) / 25).pow(2))).toRadians())

        return sqrt((dLp / SL).pow(2) + (dCp / SC).pow(2) + (dHp / SH).pow(2) + RT * (dCp / SC) * (dHp / SH))
    }

    private fun Double.toRadians(): Double = this * DEGREES_TO_RADIANS
    private fun Double.toDegrees(): Double = this * RADIANS_TO_DEGREES
}