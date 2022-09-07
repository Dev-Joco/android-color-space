package com.jojo.android.colorsimilarityapp.ui.colorSimilarity

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jojo.android.colorsimilarityapp.ui.colorSimilarity.adapter.ColorSeekBarStates
import com.jojo.android.colorsimilarityapp.ui.colorSimilarity.adapter.SeekBarState
import com.jojo.android.colorsimilarityapp.ui.colorSimilarity.adapter.ThreeSeekBar
import com.jojo.android.colorsimilarityapp.util.ColorSpace
import com.jojo.android.colorsimilarityapp.util.ColorType

class ColorSimilarityViewModel : ViewModel() {

    val circleColor: LiveData<ColorSpace>
        get() = _circleColor

    val backgroundColor: LiveData<ColorSpace>
        get() = _backgroundColor

    val similarityTable: LiveData<ColorSimilarityTable>
        get() = _similarityTable

    val circleSeekBarStates: LiveData<ColorSeekBarStates>
        get() = _circleSeekBarStates

    val backgroundSeekBarStates: LiveData<ColorSeekBarStates>
        get() = _backgroundSeekBarStates

    private val _circleColor = MutableLiveData(ColorSpace(CIRCLE_INIT_COLOR))
    private val _backgroundColor = MutableLiveData(ColorSpace(BACKGROUND_INIT_COLOR))

    private val _similarityTable = MutableLiveData(ColorSimilarityTable(_backgroundColor.value!!, _circleColor.value!!))

    private val _circleSeekBarStates = MutableLiveData(createColorSeekBarStates(circleColor.value!!))
    private val _backgroundSeekBarStates = MutableLiveData(createColorSeekBarStates(backgroundColor.value!!))

    fun updateCircleColor(colorType: ColorType, index: Int, progress: Int) {
        _circleColor.value?.let { colorSpace ->
            val steps = _circleSeekBarStates.value!![colorType.ordinal][index].steps

            colorSpace[colorType, index] = progress * steps

            updateSimilarityTable()
            updateCircleColorSeekBarStates()

            _circleColor.value = colorSpace
        }
    }

    fun updateBackgroundColor(colorType: ColorType, index: Int, progress: Int) {
        _backgroundColor.value?.let { colorSpace ->
            val steps = _backgroundSeekBarStates.value!![colorType.ordinal][index].steps

            colorSpace[colorType, index] = progress * steps

            updateSimilarityTable()
            updateBackgroundColorSeekBarStates()

            _backgroundColor.value = colorSpace
        }
    }

    fun plusSimilarityThresholds() {
        _similarityTable.value?.let {
            it.plusSimilarityThresholds()
            _similarityTable.value = it
        }
    }

    fun minusSimilarityThresholds() {
        _similarityTable.value?.let {
            it.minusSimilarityThresholds()
            _similarityTable.value = it
        }
    }

    private fun updateSimilarityTable() {
        val table = _similarityTable.value ?: return
        val circleColor = _circleColor.value ?: return
        val backgroundColor = _backgroundColor.value ?: return

        table.updateEuclideanDistances(backgroundColor, circleColor)
        table.updateSimilarities()

        _similarityTable.value = table
    }

    private fun updateCircleColorSeekBarStates() {
        _circleSeekBarStates.value?.let {
            for (colorType in 0..3) for (index in 0..2) {
                val state = it[colorType][index]
                val value = _circleColor.value!![colorType, index]

                state.progress = (value.toDouble() / state.steps).toInt()
            }

            _circleSeekBarStates.value = it
        }
    }

    private fun updateBackgroundColorSeekBarStates() {
        _backgroundSeekBarStates.value?.let {
            for (colorType in 0..3) for (index in 0..2) {
                val seekBarState = it[colorType][index]
                val value = _backgroundColor.value!![colorType, index]

                seekBarState.progress = (value.toDouble() / seekBarState.steps).toInt()
            }

            _backgroundSeekBarStates.value = it
        }
    }

    private fun createColorSeekBarStates(color: ColorSpace): ColorSeekBarStates = ColorSeekBarStates(
        rgb = ThreeSeekBar(
            type = ColorType.RGB,
            seek0 = SeekBarState(range = 0..255, progress = color[ColorType.RGB.ordinal, 0].toInt()),
            seek1 = SeekBarState(range = 0..255, progress = color[ColorType.RGB.ordinal, 1].toInt()),
            seek2 = SeekBarState(range = 0..255, progress = color[ColorType.RGB.ordinal, 2].toInt()),
        ),
        hsl = ThreeSeekBar(
            type = ColorType.HSL,
            seek0 = SeekBarState(range = 0..360, progress = color[ColorType.HSL.ordinal, 0].toInt()),
            seek1 = SeekBarState(range = 0..100, progress = color[ColorType.HSL.ordinal, 1].toInt(), steps = 0.01f),
            seek2 = SeekBarState(range = 0..100, progress = color[ColorType.HSL.ordinal, 2].toInt(), steps = 0.01f),
        ),
        xyz = ThreeSeekBar(
            type = ColorType.XYZ,
            seek0 = SeekBarState(range = 0..950, progress = color[ColorType.XYZ.ordinal, 0].toInt() * 10, steps = 0.1f),
            seek1 = SeekBarState(range = 0..1000, progress = color[ColorType.XYZ.ordinal, 1].toInt() * 10, steps = 0.1f),
            seek2 = SeekBarState(range = 0..1080, progress = color[ColorType.XYZ.ordinal, 2].toInt() * 10, steps = 0.1f),
        ),
        lab = ThreeSeekBar(
            type = ColorType.LAB,
            seek0 = SeekBarState(range = 0..1000, progress = color[ColorType.LAB.ordinal, 0].toInt() * 10, steps = 0.1f),
            seek1 = SeekBarState(range = -1280..1270, progress = color[ColorType.LAB.ordinal, 1].toInt() * 10, steps = 0.1f),
            seek2 = SeekBarState(range = -1280..1270, progress = color[ColorType.LAB.ordinal, 2].toInt() * 10, steps = 0.1f),
        ),
    )

    companion object {
        private const val TAG = "ViewModel"
        private val CIRCLE_INIT_COLOR = Color.BLACK
        private val BACKGROUND_INIT_COLOR = Color.WHITE
    }
}

class ColorSimilarityViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ColorSimilarityViewModel() as T
    }
}