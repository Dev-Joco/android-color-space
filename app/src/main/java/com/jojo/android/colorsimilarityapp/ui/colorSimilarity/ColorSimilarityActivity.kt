package com.jojo.android.colorsimilarityapp.ui.colorSimilarity

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.jojo.android.colorsimilarityapp.R
import com.jojo.android.colorsimilarityapp.databinding.ActivityColorSimilarityBinding
import com.jojo.android.colorsimilarityapp.databinding.SimilarityTableBinding
import com.jojo.android.colorsimilarityapp.ui.colorSimilarity.adapter.SeekBarListAdapter
import com.jojo.android.colorsimilarityapp.ui.util.HorizontalNonScrollLinearLayoutManager
import com.jojo.android.colorsimilarityapp.util.ColorType

class ColorSimilarityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityColorSimilarityBinding
    private val tableBinding: SimilarityTableBinding get() = binding.similarityTable

    private val viewModel: ColorSimilarityViewModel by viewModels {
        ColorSimilarityViewModelFactory()
    }

    private lateinit var circleSeekBarAdapter: SeekBarListAdapter
    private lateinit var backgroundSeekBarAdapter: SeekBarListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_color_similarity)

        initView()
    }

    private fun initView() {
        val owner = this

        with (binding) {
            vm = viewModel
            lifecycleOwner = owner

            circleSeekBarAdapter = SeekBarListAdapter(viewModel, owner, CIRCLE_VIEW, ::onProgressChanged)
            circleColorSeekBarList.adapter = circleSeekBarAdapter
            circleColorSeekBarList.layoutManager = HorizontalNonScrollLinearLayoutManager(baseContext)

            backgroundSeekBarAdapter = SeekBarListAdapter(viewModel, owner, BACKGROUND_VIEW, ::onProgressChanged)
            backgroundColorSeekBarList.adapter = backgroundSeekBarAdapter
            backgroundColorSeekBarList.layoutManager = HorizontalNonScrollLinearLayoutManager(baseContext)
        }

        with (tableBinding) {
            vm = viewModel
            lifecycleOwner = owner

            btnSimThresholdPlus.setOnClickListener {
                viewModel.plusSimilarityThresholds()
            }
            btnSimThresholdMinus.setOnClickListener {
                viewModel.minusSimilarityThresholds()
            }
        }
    }

    private fun onProgressChanged(viewType: Int, colorType: ColorType, seekIndex: Int, progress: Int) {
        when (viewType) {
            CIRCLE_VIEW -> viewModel.updateCircleColor(colorType, seekIndex, progress)
            BACKGROUND_VIEW -> viewModel.updateBackgroundColor(colorType, seekIndex, progress)
        }
    }

    companion object {
        const val CIRCLE_VIEW = 0
        const val BACKGROUND_VIEW = 1
    }
}