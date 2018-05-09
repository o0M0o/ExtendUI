package wxm.uilib.DistanceMeter

import android.annotation.SuppressLint
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
    private var mAttrMinValue: Int = 0
    private var mAttrMaxValue: Int = 0

    private var mAttrTextSize: Int = 0
    private var mAttrSubScaleHeight: Int = 0
    private var mAttrMainScaleHeight: Int = 0
    private var mAttrSubScaleCount: Int = 0
    private var mAttrMainScaleCount: Int = 0
    private var mAttrTagWidth: Int = 0
    private var mAttrTagHeight: Int = 0

    private var mTotalValue: Int = 0
    private var mBigUnitWidth: Float = 0f
    private var mSmallUnitWidth: Float = 0f
    private var mBigUnitVal: Float = 0f
    private var mSmallUnitVal: Float = 0f

    /**
     * cursors for meter rule
     */
    private val mALTags = ArrayList<DistanceMeterTag>()

    /**
     * default translator
     */
    private var mTTTranslator: TagTranslator = object : TagTranslator {
        override fun translateTWTag(tagVal: Int): String {
            return tagVal.toString()
        }
    }

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
        private val ONE_DP_PX = UiUtil.dip2px(context, 1f)

        fun drawScales() {
            // for paint
            val subScalePaint = Paint()
            subScalePaint.strokeWidth = SHORT_LINE_WIDTH
            subScalePaint.color = Color.BLACK

            val mainScalePaint = Paint()
            mainScalePaint.strokeWidth = LONG_LINE_WIDTH
            mainScalePaint.color = Color.BLACK

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
                val txtTag = mTTTranslator.translateTWTag(mAttrMinValue + (mBigUnitVal * i).toInt())
                val txtWidth = Layout.getDesiredWidth(txtTag, tpScaleTag)

                // draw main scale
                val xMainScale = rulerValToXCoordinate(i, 0, 0f)
                mCanvas.drawLine(xMainScale, yMainScaleBottom, xMainScale, yMainScaleTop, mainScalePaint)

                val xText: Float = when {
                    0 == i -> paddingStart.toFloat()
                    mAttrMainScaleCount == i -> xMainScale - txtWidth - 4f
                    else -> xMainScale - txtWidth / 2
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
            val yMainScaleTop = yMainScaleBottom - mAttrMainScaleHeight + 4 * ONE_DP_PX
            val yCursorTop = yMainScaleBottom + ONE_DP_PX
            val yCursorBottom = yCursorTop + mAttrTagHeight

            val txtPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
            txtPaint.textSize = mAttrTextSize.toFloat()
            val fHight = txtPaint.fontMetrics
                    .let { Math.ceil(it.descent.toDouble() - it.ascent.toDouble())  }.toFloat()
            val yTextBottom = yCursorBottom + fHight + ONE_DP_PX.toFloat()

            val linePaint = Paint()
            for (mt in mALTags) {
                linePaint.color = mt.mCRTagColor

                val xCursorCenter = meterValueToXCoordinate(mt.mTagVal)
                val xCursorCenterLeft = xCursorCenter - ONE_DP_PX
                val xCursorCenterRight = xCursorCenter + ONE_DP_PX
                val xCursorLeft = xCursorCenter - mAttrTagWidth / 2
                val xCursorRight = xCursorCenter + mAttrTagWidth / 2

                val p = Path().apply {
                    moveTo(xCursorCenterRight, yMainScaleTop)
                    lineTo(xCursorCenterLeft, yMainScaleTop)
                    lineTo(xCursorCenterLeft, yCursorTop)
                    lineTo(xCursorLeft, yCursorBottom)
                    lineTo(xCursorRight, yCursorBottom)
                    lineTo(xCursorCenterRight, yCursorTop)
                    lineTo(xCursorCenterRight, yMainScaleTop)
                }
                mCanvas.drawPath(p, linePaint)

                txtPaint.color = mt.mCRTagColor

                val xCursorTextLeft = xCursorCenter - Layout.getDesiredWidth(mt.mSZTagName, txtPaint) / 2
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
                0 == big -> LONG_LINE_WIDTH.toInt() / 2 + paddingStart
                mAttrMainScaleCount == big -> mVWWidth - LONG_LINE_WIDTH.toInt() / 2 - paddingEnd
                else -> (paddingStart + mBigUnitWidth * big - LONG_LINE_WIDTH.toInt() / 2).toInt()
            }

            val xSmall = mSmallUnitWidth * small
            val xLeft = mSmallUnitWidth * left / mSmallUnitVal
            return xBig + xSmall + xLeft
        }
    }

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.DistanceMeter)
        try {
            mAttrMainScaleCount = array.getInt(R.styleable.DistanceMeter_dmMainScaleCount, 5)
            mAttrSubScaleCount = array.getInt(R.styleable.DistanceMeter_dmSubScaleCount, 2)
            mAttrMinValue = array.getInt(R.styleable.DistanceMeter_dmMinValue, 0)
            mAttrMaxValue = array.getInt(R.styleable.DistanceMeter_dmMaxValue, 100)

            mAttrTextSize = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmTextSize,
                    UiUtil.dip2px(context, 12f))
            mAttrSubScaleHeight = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmSubScaleHeight,
                    UiUtil.dip2px(context, 4f))
            mAttrMainScaleHeight = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmMainScaleHeight,
                    UiUtil.dip2px(context, 8f))

            mAttrTagHeight = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmTagHeight,
                    UiUtil.dip2px(context, 8f))
            mAttrTagWidth = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmTagWidth,
                    UiUtil.dip2px(context, 4f))
        } finally {
            array.recycle()
        }

        adjustDefine()
        if (isInEditMode) {
            val tag1 = DistanceMeterTag("1",
                    (mAttrMinValue + (mAttrMaxValue - mAttrMinValue) / 3).toFloat()).apply {
                mCRTagColor = getContext().getColor(R.color.teal)
            }

            val tag2 = DistanceMeterTag("2",
                (mAttrMinValue + (mAttrMaxValue - mAttrMinValue) / 2).toFloat()).apply {
                mCRTagColor = getContext().getColor(R.color.aquamarine)
                mTagVal = (mAttrMinValue + (mAttrMaxValue - mAttrMinValue) / 2).toFloat()
            }

            val tag3 = DistanceMeterTag("3",
                (mAttrMinValue + (mAttrMaxValue - mAttrMinValue) / 3 * 2).toFloat()).apply {
                mCRTagColor = getContext().getColor(R.color.brown)
            }

            mALTags.addAll(Arrays.asList(tag1, tag2, tag3))
        }
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mVWWidth = width
        mVWHeight = height
        adjustDefine()
        super.onLayout(changed, left, top, right, bottom)
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        DrawHelper(canvas).apply {
            canvas.save()

            drawScales()
            drawCursors()

            canvas.restore()
        }
    }


    /**
     * set [tt] as scale translator,
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
     * adjust attribute by [paras]
     */
    @Suppress("unused")
    fun adjustAttribute(paras: Map<String, Any>) {
        paras.forEach{
            when(it.key)    {
                PARA_VAL_MIN -> mAttrMinValue = it.value as Int
                PARA_VAL_MAX -> mAttrMaxValue = it.value as Int
            }
        }

        adjustDefine()
        invalidate()
    }

   private fun adjustDefine()   {
       mTotalValue = mAttrMaxValue - mAttrMinValue
       if (0 != mTotalValue % mAttrMainScaleCount)  {
           mTotalValue += mTotalValue % mAttrMainScaleCount
       }

       mBigUnitWidth = ((mVWWidth - paddingStart - paddingEnd) / mAttrMainScaleCount).toFloat()
       mSmallUnitWidth = mBigUnitWidth / mAttrSubScaleCount

       mBigUnitVal = (mTotalValue / mAttrMainScaleCount).toFloat()
       mSmallUnitVal = mBigUnitVal / mAttrSubScaleCount
   }

    companion object {
        const val PARA_VAL_MIN = "val_min"
        const val PARA_VAL_MAX = "val_max"

        private const val SHORT_LINE_WIDTH = 2f
        private const val LONG_LINE_WIDTH = 4f
    }
}
