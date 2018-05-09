package wxm.uilib.DistanceMeter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.Layout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import wxm.androidutil.util.UiUtil
import wxm.androidutil.util.UtilFun
import wxm.uilib.R
import java.util.*

/**
 * meter rule
 * @author      WangXM
 * @version     create：2017/03/28
 */
@RequiresApi(api = Build.VERSION_CODES.M)
class DistanceMeter constructor(context: Context, attrs: AttributeSet)
    : View(context, attrs) {
    private var mVWWidth: Int = 0
    private var mVWHeight: Int = 0

    /**
     * attributes
     */
    private var mAttrSZPostUnit: String? = null
    private var mAttrSZPrvUnit: String? = null
    private var mAttrMinValue: Int = 0
    private var mAttrMaxValue: Int = 0

    private var mAttrTextSize: Int = 0
    private var mAttrSubScaleHeight: Int = 0
    private var mAttrMainScaleHeight: Int = 0
    private var mAttrSubScaleCount: Int = 0
    private var mAttrMainScaleCount: Int = 0
    private var mAttrTagWidth: Int = 0
    private var mAttrTagHeight: Int = 0

    /**
     * cursors for meter rule
     */
    private val mALTags = ArrayList<DistanceMeterTag>()

    /**
     * default translator
     */
    private var mTTTranslator: TagTranslator = object : TagTranslator {
        override fun translateTWTag(tagVal: Int): String {
            return mAttrSZPrvUnit + tagVal.toString() + mAttrSZPostUnit
        }
    }

    /**
     * const value for self use
     */
    private val TEXT_COLOR_NORMAL: Int = Color.BLACK
    private val DISPLAY_DENSITY: Float
    private var DISPLAY_TEXT_WIDTH: Float = 0.toFloat()

    /**
     * translate value to show tag
     */
    interface TagTranslator {
        /**
         * get show tag from [tagVal]
         */
        fun translateTWTag(tagVal: Int): String
    }

    internal inner class DrawHelper(private val mCanvas: Canvas) {
        private val mShortLineWidth = 2
        private val mLongLineWidth = 4

        private val mBigWidthUnit: Float
        private val mSmallWidthUnit: Float

        private val mBigUnitVal: Float
        private val mSmallUnitVal: Float


        init {
            // 获得显示值
            var mTotalValue = mAttrMaxValue - mAttrMinValue
            mTotalValue = if (0 == mTotalValue % mAttrMainScaleCount)
                mTotalValue
            else
                mTotalValue + mTotalValue % mAttrMainScaleCount

            mBigWidthUnit = ((mVWWidth - paddingStart - paddingEnd) / mAttrMainScaleCount).toFloat()
            mSmallWidthUnit = mBigWidthUnit / mAttrSubScaleCount

            mBigUnitVal = (mTotalValue / mAttrMainScaleCount).toFloat()
            mSmallUnitVal = mBigUnitVal / mAttrSubScaleCount
        }

        fun drawScales() {
            // for paint
            val subScalePaint = Paint()
            subScalePaint.strokeWidth = mShortLineWidth.toFloat()
            subScalePaint.color = TEXT_COLOR_NORMAL

            val mainScalePaint = Paint()
            mainScalePaint.strokeWidth = mLongLineWidth.toFloat()
            mainScalePaint.color = TEXT_COLOR_NORMAL

            val tpScaleTag = TextPaint(Paint.ANTI_ALIAS_FLAG)
            tpScaleTag.color = UiUtil.getColor(context, R.color.text_fit)
            tpScaleTag.textSize = mAttrTextSize.toFloat()

            // for axis
            val yMainScaleBottom = (mVWHeight - (mVWHeight - mAttrMainScaleHeight) / 2).toFloat()
            val yMainScaleTop = yMainScaleBottom - mAttrMainScaleHeight
            val ySubScaleBottom = (mVWHeight - (mVWHeight - mAttrSubScaleHeight) / 2).toFloat()
            val ySubScaleTop = ySubScaleBottom - mAttrSubScaleHeight
            val yTextBottom = yMainScaleTop - 8

            for (i in 0..mAttrMainScaleCount) {
                // draw main scale
                val xMainScale = rulerValToXCoordinate(i, 0, 0f)
                mCanvas.drawLine(xMainScale, yMainScaleBottom, xMainScale, yMainScaleTop, mainScalePaint)

                val txtTag = mTTTranslator.translateTWTag(mAttrMinValue + (mBigUnitVal * i).toInt())
                val xText: Float = when {
                    0 == i -> paddingStart.toFloat()
                    mAttrMainScaleCount == i -> xMainScale - txtTag.length * DISPLAY_TEXT_WIDTH - 4f
                    else -> xMainScale - txtTag.length * DISPLAY_TEXT_WIDTH / 2
                }
                mCanvas.drawText(txtTag, xText, yTextBottom, tpScaleTag)

                // draw sub scale
                for (j in 1 until mAttrSubScaleCount) {
                    val xSubScale = rulerValToXCoordinate(i, j, 0f)
                    mCanvas.drawLine(xSubScale, ySubScaleBottom, xSubScale, ySubScaleTop, subScalePaint)
                }
            }
        }

        fun drawCursors() {
            val yMainScaleBottom = (mVWHeight - (mVWHeight - mAttrMainScaleHeight) / 2).toFloat()
            val yCursorTop = yMainScaleBottom + UiUtil.dip2px(context, 1f)
            val yCursorBottom = yCursorTop + mAttrTagHeight
            val yTextBottom = yCursorBottom + DISPLAY_TEXT_WIDTH + UiUtil.dip2px(context, 4f).toFloat()

            val linePaint = Paint()
            val txtPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
            for (mt in mALTags) {
                linePaint.color = mt.mCRTagColor

                val xCursorCenter = meterValueToXCoordinate(mt.mTagVal)
                val xCursorLeft = xCursorCenter - mAttrTagWidth / 2
                val xCursorRight = xCursorCenter + mAttrTagWidth / 2

                val p = Path().apply {
                    moveTo(xCursorCenter, yCursorTop)
                    lineTo(xCursorLeft, yCursorBottom)
                    lineTo(xCursorRight, yCursorBottom)
                    lineTo(xCursorCenter, yCursorTop)
                }
                mCanvas.drawPath(p, linePaint)

                txtPaint.color = mt.mCRTagColor
                txtPaint.textSize = mAttrTextSize.toFloat()

                val xCursorTextLeft = xCursorCenter - mt.mSZTagName.length * DISPLAY_TEXT_WIDTH / 2
                mCanvas.drawText(mt.mSZTagName, xCursorTextLeft, yTextBottom, txtPaint)
            }
        }

        /**
         * translate meter value [tagVal] to x coordinate
         */
        private fun meterValueToXCoordinate(tagVal: Float): Float {
            val newVal = tagVal - mAttrMinValue.toFloat()
            val lv = newVal % mBigUnitVal
            return rulerValToXCoordinate((newVal / mBigUnitVal).toInt(),
                    (lv / mSmallUnitVal).toInt(), lv % mSmallUnitVal)
        }

        /**
         * translate ruler value to x coordinate
         * @param big       big unit value
         * @param small     small unit value
         * @param left      left value
         * @return          x coordinate in view
         */
        private fun rulerValToXCoordinate(big: Int, small: Int, left: Float): Float {
            val xBig: Int = when {
                0 == big -> mLongLineWidth / 2 + paddingStart
                mAttrMainScaleCount == big -> mVWWidth - mLongLineWidth / 2 - paddingEnd
                else -> (paddingStart + mBigWidthUnit * big - mLongLineWidth / 2).toInt()
            }

            val xSmall = mSmallWidthUnit * small
            val xLeft = mSmallWidthUnit * left / mSmallUnitVal
            return xBig + xSmall + xLeft
        }
    }

    init {
        // for normal setting
        val res = context.resources
        DISPLAY_DENSITY = res.displayMetrics.density

        // for parameter
        val array = context.obtainStyledAttributes(attrs, R.styleable.DistanceMeter)
        try {
            var szUnit = array.getString(R.styleable.DistanceMeter_dmPostUnit)
            mAttrSZPostUnit = if (UtilFun.StringIsNullOrEmpty(szUnit)) "" else szUnit

            szUnit = array.getString(R.styleable.DistanceMeter_dmPrvUnit)
            mAttrSZPrvUnit = if (UtilFun.StringIsNullOrEmpty(szUnit)) "" else szUnit

            mAttrMainScaleCount = array.getInt(R.styleable.DistanceMeter_dmMainScaleCount, 5)
            mAttrSubScaleCount = array.getInt(R.styleable.DistanceMeter_dmSubScaleCount, 2)
            mAttrMinValue = array.getInt(R.styleable.DistanceMeter_dmMinValue, 0)
            mAttrMaxValue = array.getInt(R.styleable.DistanceMeter_dmMaxValue, 100)

            mAttrTextSize = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmTextSize,
                    (DISPLAY_DENSITY * 12).toInt())
            mAttrSubScaleHeight = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmSubScaleHeight,
                    UiUtil.dip2px(context, 4f))
            mAttrMainScaleHeight = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmMainScaleHeight,
                    UiUtil.dip2px(context, 8f))

            mAttrTagHeight = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmTagHeight,
                    UiUtil.dip2px(context, 8f))
            mAttrTagWidth = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmTagWidth,
                    UiUtil.dip2px(context, 4f))

            // get other
            TextPaint(Paint.ANTI_ALIAS_FLAG).let {
                it.textSize = mAttrTextSize.toFloat()
                DISPLAY_TEXT_WIDTH = Layout.getDesiredWidth("0", it)
            }
        } finally {
            array.recycle()
        }

        if (isInEditMode) {
            val tag1 = DistanceMeterTag("1").apply {
                mCRTagColor = getContext().getColor(R.color.teal)
                mTagVal = (mAttrMinValue + (mAttrMaxValue - mAttrMinValue) / 3).toFloat()
            }

            val tag2 = DistanceMeterTag("2").apply {
                mCRTagColor = getContext().getColor(R.color.aquamarine)
                mTagVal = (mAttrMinValue + (mAttrMaxValue - mAttrMinValue) / 2).toFloat()
            }

            val tag3 = DistanceMeterTag("3").apply {
                mCRTagColor = getContext().getColor(R.color.brown)
                mTagVal = (mAttrMinValue + (mAttrMaxValue - mAttrMinValue) / 3 * 2).toFloat()
            }

            mALTags.addAll(Arrays.asList(tag1, tag2, tag3))
        }
    }

    /**
     * set scale translator
     * @param tt    translator
     */
    fun setScaleTranslator(tt: TagTranslator) {
        mTTTranslator = tt
    }

    /**
     * clear cursor
     */
    fun clearCursor() {
        mALTags.clear()

        invalidate()
        requestLayout()
    }

    /**
     * add [vt] as cursor
     */
    fun addCursor(vararg vt: DistanceMeterTag) {
        mALTags.clear()
        mALTags.addAll(Arrays.asList(*vt))

        invalidate()
        requestLayout()
    }

    /**
     * adjust attribute
     * @param paras      new attributes
     */
    fun adjustAttribute(paras: Map<String, Any>) {
        paras.forEach{
            when(it.key)    {
                PARA_VAL_MIN -> mAttrMinValue = it.value as Int
                PARA_VAL_MAX -> mAttrMaxValue = it.value as Int
            }
        }

        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mVWWidth = width
        mVWHeight = height
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawScaleLine(canvas)
    }

    /**
     * draw scale lines
     * @param canvas    drawer
     */
    private fun drawScaleLine(canvas: Canvas) {
        canvas.save()
        DrawHelper(canvas).apply {
            drawScales()
            drawCursors()
        }
        canvas.restore()
    }

    companion object {
        const val PARA_VAL_MIN = "val_min"
        const val PARA_VAL_MAX = "val_max"
    }
}
