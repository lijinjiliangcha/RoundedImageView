package com.lijinjiliangcha.roundedimageview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.ImageView
import android.graphics.Paint


//圆弧ImageView
class RoundedImageView : ImageView {

    private var path: Path? = null
    private var radius: Float = 0f

    private val paint: Paint = Paint()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView)
        radius = typedArray.getDimension(R.styleable.RoundedImageView_radius, 0f)
        scaleType = ScaleType.CENTER_CROP
        typedArray.recycle()

        paint.color = 0xFFFFFFFF.toInt()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        LogUtils.i("测试", "onSizeChanged : w = $w ，h = $h，radius = $radius")
        if (radius == 0f) {
            path = null
            return
        }
        path = Path();
        //四个圆角
        path?.moveTo(radius, 0f)
        path?.lineTo(w - radius, 0f)
        //右上角圆弧
        path?.quadTo(w.toFloat(), 0f, w.toFloat(), radius)
        path?.lineTo(w.toFloat(), h - radius)
        //右下角圆弧
        path?.quadTo(w.toFloat(), h.toFloat(), w - radius, h.toFloat())
        path?.lineTo(radius, h.toFloat())
        //左下角圆弧
        path?.quadTo(0f, h.toFloat(), 0f, h - radius)
        path?.lineTo(0f, radius)
        //左上角圆弧
        path?.quadTo(0f, 0f, radius, 0f)

    }

    override fun onDraw(canvas: Canvas?) {
        if (path != null) {
            canvas?.clipPath(path!!)
        }
        super.onDraw(canvas)
    }

    fun setRadius(dp: Int) {
        if (dp < 0)
            return
        radius = dip2px(dp)
        invalidate()
    }

    fun setRadius(px: Float) {
        if (px < 0)
            return
        this.radius = px
    }

    fun setRoundedStyle(style: RoundedStyle) {

    }

    private fun dip2px(dpValue: Int): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }

    enum class RoundedStyle(value: Int) {
        ARC(0),
        CIRCULAR(1)
    }
}