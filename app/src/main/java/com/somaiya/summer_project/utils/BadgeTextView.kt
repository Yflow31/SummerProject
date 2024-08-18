package com.somaiya.summer_project.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.somaiya.summer_project.R

/**
 * @author Utsav Devadiga
 */
class BadgeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        // Apply the custom styles initially
        applyCustomStyles()
    }

    private fun applyCustomStyles() {
        // Set custom font (you can adjust this if needed)
        typeface = Typeface.createFromAsset(context.assets, "font/lexend_regular.ttf")

        // Set default padding
        setPadding(16, 8, 16, 8)

        // Set default background and text color (can be overridden via parameters)
        background = context.getDrawable(R.drawable.default_background)
        setTextColor(context.resources.getColor(R.color.primary))
    }

    // Custom function to set the background color programmatically
    fun setCustomBackgroundColor(color: Int) {
        background.setTint(color)
    }

    // Custom function to set the text color programmatically
    fun setCustomTextColor(color: Int) {
        setTextColor(color)
    }
}