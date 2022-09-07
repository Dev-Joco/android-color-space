package com.jojo.android.colorsimilarityapp.ui.colorSimilarity.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.jojo.android.colorsimilarityapp.databinding.ColorSeekBarBinding
import com.jojo.android.colorsimilarityapp.ui.colorSimilarity.ColorSimilarityActivity.Companion.BACKGROUND_VIEW
import com.jojo.android.colorsimilarityapp.ui.colorSimilarity.ColorSimilarityActivity.Companion.CIRCLE_VIEW
import com.jojo.android.colorsimilarityapp.ui.colorSimilarity.ColorSimilarityViewModel
import com.jojo.android.colorsimilarityapp.util.ColorType

class SeekBarListAdapter(
    private val viewModel: ColorSimilarityViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val viewType: Int,
    private val onProgressChanged: (viewType: Int, colorType: ColorType, seekIndex: Int, progress: Int) -> Unit
) : RecyclerView.Adapter<SeekBarViewHolder>() {

    override fun getItemCount(): Int = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeekBarViewHolder {
        return createViewHolder(parent, viewModel, lifecycleOwner, this@SeekBarListAdapter.viewType, onProgressChanged)
    }

    override fun onBindViewHolder(holder: SeekBarViewHolder, position: Int) {
        val states = when (viewType) {
            CIRCLE_VIEW -> viewModel.circleSeekBarStates.value?.get(position)
            BACKGROUND_VIEW -> viewModel.backgroundSeekBarStates.value?.get(position)
            else -> return
        } ?: return

        holder.bind(states)
    }
}

class SeekBarViewHolder(
    private val binding: ColorSeekBarBinding,
    private val viewModel: ColorSimilarityViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val viewType: Int,
    private val onProgressChanged: (viewType: Int, colorType: ColorType, seekIndex: Int, progress: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root), SeekBar.OnSeekBarChangeListener {

    init {
        binding.seek0.setOnSeekBarChangeListener(this)
        binding.seek1.setOnSeekBarChangeListener(this)
        binding.seek2.setOnSeekBarChangeListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(states: ThreeSeekBar) {
        with (binding) {
            vm = viewModel
            lifecycleOwner = this@SeekBarViewHolder.lifecycleOwner
            colorType = states.type.ordinal
            viewType = this@SeekBarViewHolder.viewType

            val colorSpaceName = states.type.name

            binding.colorSpaceName.text = "[$colorSpaceName]"

            states.seek0.let {
                label0.text = "${colorSpaceName[0]}: "
                value0.text = it.valueStr
                seek0.min = it.range.first
                seek0.max = it.range.last
                seek0.progress = it.progress
            }

            states.seek1.let {
                label1.text = "${colorSpaceName[1]}: "
                value1.text = it.valueStr
                seek1.min = it.range.first
                seek1.max = it.range.last
                seek1.progress = it.progress
            }

            states.seek2.let {
                label2.text = "${colorSpaceName[2]}: "
                value2.text = it.valueStr
                seek2.min = it.range.first
                seek2.max = it.range.last
                seek2.progress = it.progress
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (seekBar == null || !fromUser)
            return

        val colorType = ColorType.valueOf(adapterPosition)
        val seekIndex = (seekBar.tag as String).toInt()

        onProgressChanged.invoke(viewType, colorType, seekIndex, progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}

private fun createViewHolder(
    parent: ViewGroup,
    viewModel: ColorSimilarityViewModel,
    lifecycleOwner: LifecycleOwner,
    viewType: Int,
    onProgressChanged: (viewType: Int, colorType: ColorType, seekIndex: Int, progress: Int) -> Unit
): SeekBarViewHolder {
    val binding = ColorSeekBarBinding.inflate(LayoutInflater.from(parent.context), parent, false).also {
        it.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            width = parent.width / 4 - marginStart - marginEnd
        }
    }
    return SeekBarViewHolder(
        binding,
        viewModel,
        lifecycleOwner,
        viewType,
        onProgressChanged
    )
}