package com.example.minilivescore.utils

import android.graphics.Color
import android.widget.TextView
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.core.content.res.ResourcesCompat
import com.example.minilivescore.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object Preferences {
    private val clubBackgrounds = mapOf(
        "TOT" to R.drawable.tottenham,
        "WHU" to R.drawable.westham,
        "FUL" to R.drawable.fulham,
        "AVL" to R.drawable.astonvila,
        "IPS" to R.drawable.ipswich,
        "EVE" to R.drawable.eveton,
        "MUN" to R.drawable.manutd,
        "BRE" to R.drawable.brenfort,
        "NEW" to R.drawable.newcastle,
        "BHA" to R.drawable.brighton,
        "SOU" to R.drawable.southamton,
        "LEI" to R.drawable.leicester,
        "BOU" to R.drawable.bounermount,
        "ARS" to R.drawable.arsenal,
        "WOL" to R.drawable.wolves,
        "MCI" to R.drawable.mancity,
        "LIV" to R.drawable.liverpool,
        "CHE" to R.drawable.chelsea,
        "NOT" to R.drawable.nottingham,
        "CRY" to R.drawable.crystal,

        // Thêm các đội khác tương tự
    )
    fun applyBackground(team:String,view: View){
        val backGround = clubBackgrounds[team]?:R.drawable.inplay
        view.setBackgroundResource(backGround)
    }
    fun setFontStyle(tv:TextView){
        val typeFace = ResourcesCompat.getFont(tv.context, R.font.premierleague_bold)
        tv.typeface = typeFace
    }

    fun formatDate(dateTime:String):String{
        val date = ZonedDateTime.parse(dateTime)
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val dateFormatter = date.format(formatter)
        return dateFormatter
    }
    private fun getAverageColorFromGradient(gradientDrawable: GradientDrawable): Int {
        // Lấy danh sách màu từ GradientDrawable (nếu có)
        val gradientColors = gradientDrawable.colors
        return if (gradientColors != null) {
            // Tính toán màu trung bình từ các màu trong gradient
            var red = 0
            var green = 0
            var blue = 0
            for (color in gradientColors) {
                red += Color.red(color)
                green += Color.green(color)
                blue += Color.blue(color)
            }
            val count = gradientColors.size
            Color.rgb(red / count, green / count, blue / count)
        } else {
            // Nếu không lấy được mảng màu, trả về màu mặc định
            Color.TRANSPARENT
        }
    }

    fun updateTextColorBasedOnGradient(layout: View, textView: TextView) {
        val background = layout.background
        if (background is GradientDrawable) {
            val averageColor = getAverageColorFromGradient(background)
            updateTextColorBasedOnBrightness(textView, averageColor)
        }
    }

    private fun updateTextColorBasedOnBrightness(textView: TextView, backgroundColor: Int) {
        if (isColorDark(backgroundColor)) {
            textView.setTextColor(Color.WHITE) // Màu nền tối, chữ trắng
        } else {
            textView.setTextColor(Color.BLACK) // Màu nền sáng, chữ đen
        }
    }

    private fun isColorDark(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

}