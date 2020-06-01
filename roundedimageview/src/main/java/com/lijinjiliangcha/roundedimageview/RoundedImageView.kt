package com.lijinjiliangcha.roundedimageview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

//圆弧ImageView
class RoundedImageView : AppCompatImageView {

    private var path: Path? = null
    private var radius: Float = 0f

    private var roundedStyle: Int = 0

    private var rect: Rect? = null
    private var rectF: RectF? = null

    private var clipBitmap: Bitmap? = null
    private val clipPaint: Paint by lazy {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLUE
//        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))// 设置转换模式（显示Scr与Dst交接的区域）
        paint
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        //取消硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView)
        radius = typedArray.getDimension(R.styleable.RoundedImageView_radius, 0f)
        roundedStyle = typedArray.getInt(R.styleable.RoundedImageView_roundedStyle, RoundedStyle.ARC.value)
        scaleType = ScaleType.CENTER_CROP
        typedArray.recycle()

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //当控件大小改变时重新获取path
        initClipBitmap(w, h)
    }

    private fun initClipBitmap(w: Int, h: Int) {
        when (roundedStyle) {
            RoundedStyle.ARC.value -> onArc(w, h)
            RoundedStyle.CIRCULAR.value -> onCircular(w, h)
            RoundedStyle.OVAL.value -> onOval(w, h)
        }
    }

//    private fun onArc(w: Int, h: Int) {
//        if (radius == 0f) {
//            path = null
//            return
//        }
//        path = Path();
//        //四个圆角
//        path?.moveTo(radius, 0f)
//        path?.lineTo(w - radius, 0f)
//        //右上角圆弧
//        path?.quadTo(w.toFloat(), 0f, w.toFloat(), radius)
//        path?.lineTo(w.toFloat(), h - radius)
//        //右下角圆弧
//        path?.quadTo(w.toFloat(), h.toFloat(), w - radius, h.toFloat())
//        path?.lineTo(radius, h.toFloat())
//        //左下角圆弧
//        path?.quadTo(0f, h.toFloat(), 0f, h - radius)
//        path?.lineTo(0f, radius)
//        //左上角圆弧
//        path?.quadTo(0f, 0f, radius, 0f)
//        createClipBitmap(w, h)
//    }

    private fun onArc(w: Int, h: Int) {
        if (radius == 0f) {
            clipBitmap = null
            return
        }
        val path = Path()
        path.let {
            val r = 2 * radius
            it.moveTo(0f, radius)
            //左上角
            val ltF = RectF(0f, 0f, r, r)
            it.arcTo(ltF, 180f, 90f, false)
            //右上角
            val rtF = RectF(w.toFloat() - r, 0f, w.toFloat(), r)
            it.arcTo(rtF, 270f, 90f, false)
            //右下角
            val rbF = RectF(w.toFloat() - r, h.toFloat() - r, w.toFloat(), h.toFloat())
            it.arcTo(rbF, 0f, 90f, false)
            //左下角
            val lbF = RectF(0f, h.toFloat() - r, r, h.toFloat())
            it.arcTo(lbF, 90f, 90f, false)
            createClipBitmap(w, h)
        }
    }

    private fun onCircular(w: Int, h: Int) {
        path = Path()
        val cx = w.toFloat() / 2
        val cy = h.toFloat() / 2
        path?.addCircle(cx, cy, if (cx > cy) cy else cx, Path.Direction.CW)
        createClipBitmap(w, h)
    }

    private fun onOval(w: Int, h: Int) {
        path = Path()
        path?.addOval(0f, 0f, w.toFloat(), h.toFloat(), Path.Direction.CW)
        createClipBitmap(w, h)
    }

    private fun createClipBitmap(w: Int, h: Int) {
        rect = Rect(0, 0, w, h)
        rectF = RectF(0f, 0f, w.toFloat(), h.toFloat())
        clipBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(clipBitmap!!)
        // 画布绘制Bitmap时搞锯齿
        canvas.setDrawFilter(PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        canvas.drawPath(path!!, clipPaint)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        clipBitmap?.let {
            clipPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_IN))
            canvas?.drawBitmap(it, rect, rectF!!, clipPaint)
            // 清除Xfermode
            clipPaint.setXfermode(null)
        }
    }

    /**
     * 设定圆弧弧度
     * @param dp 弧度单位为dp
     */
    fun setRadiusToDp(dp: Int) {
        if (dp < 0)
            return
        radius = dip2px(dp)
        reset()
    }

    /**
     * 设定圆弧弧度
     * @param px 弧度单位为px
     */
    fun setRadiusToPx(px: Float) {
        if (px < 0)
            return
        this.radius = px
        reset()
    }

    /**
     * 设置圆弧风格
     * @param style 圆弧风格枚举类
     */
    fun setRoundedStyle(style: RoundedStyle) {
        this.roundedStyle = style.value
        reset()
    }

    private fun reset() {
        initClipBitmap(width, height)
        invalidate()
    }

    private fun dip2px(dpValue: Int): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }

    enum class RoundedStyle(val value: Int) {
        ARC(0),//圆弧
        CIRCULAR(1),//圆形
        OVAL(2)//椭圆
    }

}