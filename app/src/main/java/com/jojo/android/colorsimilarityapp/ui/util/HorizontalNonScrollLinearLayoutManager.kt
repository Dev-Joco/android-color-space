package com.jojo.android.colorsimilarityapp.ui.util

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HorizontalNonScrollLinearLayoutManager(
    context: Context
) : LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {

    override fun canScrollHorizontally(): Boolean = false
    override fun canScrollVertically(): Boolean = false
}