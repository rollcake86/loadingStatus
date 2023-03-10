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
    private var drawColor = ResourcesCompat.getColor(resources, R.color.colorPrimary , null)
    private var loadingdrawColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark , null)

    private val circleFillPaint = Paint().apply {
        style = Paint.Style.FILL
        color = ResourcesCompat.getColor(resources, R.color.colorAccent , null)
    }
    private val valueAnimator = ValueAnimator.ofInt(0, 100)
    private lateinit var frame: Rect

    private lateinit var frame2: Rect

    private val paint = Paint().apply {
        color = drawColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    private val paint2 = Paint().apply {
        color = loadingdrawColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        textSize = 70f
        textAlignment = TEXT_ALIGNMENT_CENTER
        isDither = true
        style = Paint.Style.FILL // default: FILL
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when(new){
            ButtonState.Clicked -> {
                drawTextValue = "Download"
            }
            ButtonState.Completed -> {
                drawTextValue = "Download"
                valueAnimator.cancel()
                percentage = 0
                frame2 = Rect(0, 0, (width / 100 * percentage)   , height)
            }
            ButtonState.Loading -> {
                valueAnimator.start()
                drawTextValue = "We are loading"
            }
        }
    }

   var percentage = 0
    init {
        valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(p0: ValueAnimator?) {
                percentage = p0!!.getAnimatedValue() as Int
                Timber.i("animation percetage $percentage")

                frame2 = Rect(0, 0, (width / 100 * percentage)   , height)
                invalidate()
            }
        })
        valueAnimator.duration = 2000
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton,
            0, 0)
        drawColor = typedArray.getColor(R.styleable.LoadingButton_completeColor , context.getColor(R.color.colorPrimary))
        loadingdrawColor = typedArray.getColor(R.styleable.LoadingButton_loadingColor , context.getColor(R.color.colorPrimaryDark))
        typedArray.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        frame = Rect(0, 0, width , height )
        frame2 = Rect(0, 0, (width / 100 * percentage)  , height )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(frame, paint)
        canvas.drawRect(frame2 , paint2)
        canvas.drawText(drawTextValue , frame.width().toFloat() / 2 - 120, frame.height().toFloat() / 2 + 20 , textPaint)
        canvas.drawArc(frame.width().toFloat() - 200,50f,frame.width().toFloat() -100,150f,0f,percentage * 3.6f,true,circleFillPaint)
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