package com.trueconf.toggle

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.SwitchCompat


class CustomSwitchCompat @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SwitchCompat(context, attrs) {

    init {
        background = null
        trackDrawable = TrackDrawable()
        thumbDrawable = ThumbDrawable()
    }

    private val trackLabelPaint = Paint().apply {
        isAntiAlias = true
        textSize = LABEL_SIZE
        color = LABEL_COLOR
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        (trackDrawable as GradientDrawable).setSize(w, h)
        (thumbDrawable as GradientDrawable).setSize(w / 2, h)
    }

    inner class TrackDrawable : GradientDrawable() {

        private val textOffBounds = Rect()
        private val textOnBounds = Rect()

        init {
            setStroke(TRACK_STROKE_WIDTH, TRACK_STROKE_COLOR)
        }

        override fun onBoundsChange(r: Rect) {
            super.onBoundsChange(r)

            cornerRadius = 8f.dp2Px

            textOffBounds.set(r)
            textOffBounds.right = r.width() / 2

            textOnBounds.set(textOffBounds)
            textOnBounds.offset(textOffBounds.width(), 0)
        }

        override fun draw(canvas: Canvas) {
            super.draw(canvas)

            drawLabel(canvas, textOffBounds, trackLabelPaint, textOff)
            drawLabel(canvas, textOnBounds, trackLabelPaint, textOn)
        }
    }

    inner class ThumbDrawable : GradientDrawable() {

        private val thumbLabelBounds = Rect()

        init {
            //Set color for selected items background
            setColor(THUMB_COLOR)
        }

        override fun onBoundsChange(r: Rect) {
            super.onBoundsChange(r)

            setupPadding(r)
            cornerRadius = 8f.dp2Px
            thumbLabelBounds.set(r)
        }

        fun setupPadding(r: Rect) {
            val padding = 4f.dp2Px.toInt()
            r.top += padding
            r.right -= padding
            r.bottom -= padding
            r.left += padding
        }

        override fun draw(canvas: Canvas) {
            super.draw(canvas)
            invalidate()
            requestLayout()
        }
    }

    companion object {

        val TRACK_STROKE_WIDTH = 1f.dp2Px.toInt()
        val TRACK_STROKE_COLOR = Color.parseColor("#E6F5F7")
        val THUMB_COLOR = Color.parseColor("#33FFFFFF")
        val LABEL_COLOR = Color.WHITE
        val LABEL_SIZE = 14f.sp2Px

        fun drawLabel(canvas: Canvas, bounds: Rect, paint: Paint, text: CharSequence?) {
            text ?: return

            val width = bounds.width()
            if (width <= 0) {
                return
            }

            val textPaint = TextPaint(paint)
            val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0f, 1.0f)
                .setIncludePad(false)
                .build()

            val textHeight = staticLayout.height
            val textWidth = staticLayout.width

            // Вычисляем координаты для центрирования текста в bounds
            val x = bounds.left + (bounds.width() - textWidth) / 2f
            val y = bounds.top + (bounds.height() - textHeight) / 2f

            canvas.save()
            canvas.translate(x, y)
            staticLayout.draw(canvas)
            canvas.restore()
        }

        private inline val Float.sp2Px
            get() = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                this,
                Resources.getSystem().displayMetrics
            )

        private inline val Float.dp2Px
            get() = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this,
                Resources.getSystem().displayMetrics
            )
    }
}