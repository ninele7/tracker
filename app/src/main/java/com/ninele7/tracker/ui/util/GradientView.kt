package com.ninele7.tracker.ui.util

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class GradientView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    private val colors = intArrayOf(
        Color.HSVToColor(floatArrayOf(0f, 1f, 1f)),
        Color.HSVToColor(floatArrayOf(60f, 1f, 1f)),
        Color.HSVToColor(floatArrayOf(120f, 1f, 1f)),
        Color.HSVToColor(floatArrayOf(180f, 1f, 1f)),
        Color.HSVToColor(floatArrayOf(240f, 1f, 1f)),
        Color.HSVToColor(floatArrayOf(300f, 1f, 1f)),
        Color.HSVToColor(floatArrayOf(360f, 1f, 1f))
    )

    override fun onDraw(canvas: Canvas?) {
        val shader: Shader = LinearGradient(
            left.toFloat(),
            0f,
            right.toFloat(),
            0f,
            colors,
            null,
            Shader.TileMode.CLAMP
        )
        paint.shader = shader
        canvas?.drawPaint(paint)
        super.onDraw(canvas)
    }
}
