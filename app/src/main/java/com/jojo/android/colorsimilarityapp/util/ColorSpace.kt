package com.jojo.android.colorsimilarityapp.util

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import kotlin.math.*

private const val PI = Math.PI
private const val TAN = 2f

class ColorSpace(initColor: Int) {

    var color: Int = initColor
        private set

    var colorToHexString: String = "# ${Integer.toHexString(initColor)}"
        private set

    private val rgb = IntArray(3)
    private val hsl = FloatArray(3)
    private val hsl2 = DoubleArray(3)
    private val xyz = DoubleArray(3)
    private val lab = DoubleArray(3)

    init {
        colorToRGB(initColor, rgb)
        colorToHSL(initColor, hsl)
        ColorUtils.colorToXYZ(initColor, xyz)
        ColorUtils.colorToLAB(initColor, lab)
    }

    operator fun get(type: Int, index: Int): Number = when (type) {
        ColorType.RGB.ordinal -> rgb[index]
        ColorType.HSL.ordinal -> hsl[index]
        ColorType.XYZ.ordinal -> xyz[index]
        ColorType.LAB.ordinal -> lab[index]
        else -> throw IllegalArgumentException("type=$type")
    }

    operator fun set(type: ColorType, index: Int, value: Number) {
        when (type) {
            ColorType.RGB -> {
                rgb[index] = value.toInt()

                color = Color.rgb(rgb[0], rgb[1], rgb[2])

                colorToHSL(color, hsl)
                ColorUtils.colorToXYZ(color, xyz)
                ColorUtils.colorToLAB(color, lab)
            }
            ColorType.HSL -> {
                hsl[index] = value.toFloat()

                updateHSL2(hsl, hsl2)

                color = ColorUtils.HSLToColor(hsl)

                colorToRGB(color, rgb)
                ColorUtils.colorToXYZ(color, xyz)
                ColorUtils.colorToLAB(color, lab)
            }
            ColorType.XYZ -> {
                xyz[index] = value.toDouble()

                color = ColorUtils.XYZToColor(xyz[0], xyz[1], xyz[2])

                colorToRGB(color, rgb)
                colorToHSL(color, hsl)
                ColorUtils.colorToLAB(color, lab)
            }
            ColorType.LAB -> {
                lab[index] = value.toDouble()

                color = ColorUtils.LABToColor(lab[0], lab[1], lab[2])

                colorToRGB(color, rgb)
                colorToHSL(color, hsl)
                ColorUtils.colorToXYZ(color, xyz)
            }
        }
        colorToHexString = "# ${Integer.toHexString(color)}"
    }

    fun distanceTo(o: ColorSpace, outArray: DoubleArray) {
        // RGB
        outArray[0] = sqrt(0.3 * (rgb[0] - o.rgb[0]).pow(2) + 0.59 * (rgb[1] - o.rgb[1]).pow(2) + 0.11 * (rgb[2] - o.rgb[2]).pow(2))
        // HSL
        outArray[1] = sqrt((hsl2[0] - o.hsl2[0]).pow(2) + (hsl2[1] - o.hsl2[1]).pow(2) + (hsl2[2] - o.hsl2[2]).pow(2))
        // XYZ
        outArray[2] = sqrt((xyz[0] - o.xyz[0]).pow(2) + (xyz[1] - o.xyz[1]).pow(2) + (xyz[2] - o.xyz[2]).pow(2))
        // Lab
        outArray[3] = LabUtil.deltaE2000(lab, o.lab)
//        outArray[3] = sqrt((lab[0] - o.lab[0]).pow(2) + (lab[1] - o.lab[1]).pow(2) + (lab[2] - o.lab[2]).pow(2))
    }

    private fun colorToRGB(color: Int, outRgb: IntArray) {
        outRgb[0] = Color.red(color)
        outRgb[1] = Color.green(color)
        outRgb[2] = Color.blue(color)
    }

    private fun colorToHSL(color: Int, hsl: FloatArray) {
        ColorUtils.colorToHSL(color, hsl)
        updateHSL2(hsl, hsl2)
    }

    private fun updateHSL2(hsl: FloatArray, hsl2: DoubleArray) {
        var (h, s, l) = hsl
        val radian = h * PI / 180.0
        l -= 0.5f
        s = Math.min(s, 1 - l.absoluteValue * TAN)

        hsl2[0] = s * cos(radian)
        hsl2[1] = s * sin(radian)
        hsl2[2] = l.toDouble()
    }

    private fun Int.pow(n: Int): Double {
        return this.toDouble().pow(n)
    }

    override fun toString(): String {
        return "ColorSpace[rgb=${rgb.contentToString()}, hsl=${hsl.contentToString()}, xyz=${xyz.contentToString()}, lab=${lab.contentToString()}]"
    }
}