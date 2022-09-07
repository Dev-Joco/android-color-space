package com.jojo.android.colorsimilarityapp.util

enum class ColorType {
    RGB {
        override val ED_MAX = 255f
    },
    HSL {
        override val ED_MAX = 2f
    },
    XYZ {
        override val ED_MAX = 175.77f
    },
    LAB {
        override val ED_MAX = 141.83f
    };

    abstract val ED_MAX: Float

    companion object {
        val values = values()
        val indices = values.indices
        fun valueOf(ordinal: Int): ColorType = values[ordinal]
    }
}