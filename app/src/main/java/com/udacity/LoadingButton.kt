package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import timber.log.Timber
import kotlin.properties.Delegates

private const val STROKE_WIDTH = 12f // has to be float

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var drawTextValue = "Download"
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPrimary , null)

    private val circleFillPaint = Paint().apply {
        style = Paint.Style.FILL
        color = ResourcesCompat.getColor(resources, R.color.colorAccent , null)
    }
    private val valueAnimator = ValueAnimator.ofInt(0, 100)
    private lateinit var frame: Rect

    private lateinit var frame2: Rect

    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.FILL // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    private val paint2 = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark , null)
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.FILL // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        textSize = 70f
        textAlignment = TEXT_ALIGNMENT_CENTER
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.FILL // default: FILL
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when(new){
            ButtonState.Clicked -> {
                valueAnimator.start()
                drawTextValue = "Download"
            }
            ButtonState.Completed -> {
                drawTextValue = "Download"
            }
            ButtonState.Loading -> {
                drawTextValue = "Downloading"
            }
        }
    }

//    val _buttonState = MutableLiveData<ButtonState>()
//    val buttonState : LiveData<ButtonState>
//        get() = _buttonState
   var percentage = 0
    init {
        valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(p0: ValueAnimator?) {
                percentage = p0!!.getAnimatedValue() as Int
                Timber.i("animation percetage $percentage")
                val inset = 5
                frame2 = Rect(inset, inset, (width / 100 * percentage) - inset  , height - inset)
                invalidate()
            }
        })
        valueAnimator.duration = 2000
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val inset = 5
        frame = Rect(inset, inset, width - inset, height - inset)
        frame2 = Rect(inset, inset, (width / 100 * percentage) - inset  , height - inset)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(frame, paint)
        canvas.drawRect(frame2 , paint2)
        canvas.drawText(drawTextValue , frame.width().toFloat() / 2 - 120, frame.height().toFloat() / 2 + 20 , textPaint)
        canvas.drawCircle(50f, 50f, 50f, circleFillPaint);
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}