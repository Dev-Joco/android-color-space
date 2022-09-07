package com.jojo.android.colorsimilarityapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.jojo.android.colorsimilarityapp.databinding.ActivityMainBinding
import com.jojo.android.colorsimilarityapp.ui.colorSimilarity.ColorSimilarityActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with (binding) {
            btnColorSimilarity.onClickTo(ColorSimilarityActivity::class.java)
        }
    }

    private fun Button.onClickTo(cls: Class<*>) {
        setOnClickListener { startActivity(Intent(this@MainActivity, cls)) }
    }
}