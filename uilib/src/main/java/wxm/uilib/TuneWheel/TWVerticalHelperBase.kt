package wxm.uilib.TuneWheel

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import android.view.MotionEvent

/**
 * TuneWheel vertical helper
 * Created by WangXM on 2017/4/22.
 */
internal class TWVerticalHelperBase(tw: TuneWheel) : TWHelperBase(tw) {
    private var mVWHeight: Int = 0
    private var mMiddleWidth: Float = 0f
    private var mLongXDif: Float = 0f
    private var mShortXDif: Float = 0f

    private var mLongLineXStart: Float = 0f
    private var mLongLineXEnd: Float = 0f
    private var mShortLineXStart: Float = 0f
    private var mShortLineXEnd: Float = 0f

    init {
        countCoordinate()
    }

    override fun afterLayout()  {
        countCoordinate()
    }

    override fun countVelocityTracker(event: MotionEvent) {
        mTWObj.mVelocityTracker!!.computeCurrentVelocity(1000)
        val yVelocity = mTWObj.mVelocityTracker!!.yVelocity
        if (Math.abs(yVelocity) > mTWObj.mMinVelocity) {
            mTWObj.mScroller.fling(0, 0, 0, yVelocity.toInt(), Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0)
        }
    }

    override fun drawScaleLine(canvas: Canvas) {
        canvas.save()

        for (i in 0 .. mVWHeight / (2 * mTWObj.mAttrLineDivider)) {
            if (0 == i) {
                drawMiddleScale(canvas)
            } else {
                drawUpScale(canvas, i)
                drawDownScale(canvas, i)
            }
        }

        drawMiddleLine(canvas)
        canvas.restore()
    }

    private fun countCoordinate()   {
        mVWHeight = mTWObj.height
        mMiddleWidth = (mTWObj.width / 2).toFloat()
        mLongXDif =  (mTWObj.mAttrLongLineHeight / 2).toFloat()
        mShortXDif = (mTWObj.mAttrShortLineHeight / 2).toFloat()

        mLongLineXStart = mMiddleWidth - mLongXDif
        mLongLineXEnd = mMiddleWidth + mLongXDif
        mShortLineXStart = mMiddleWidth - mShortXDif
        mShortLineXEnd = mMiddleWidth + mShortXDif
    }


    /**
     * 中间的红色指示线
     * @param canvas     画布
     */
    private fun drawMiddleScale(canvas: Canvas) {
        val yPosition = (mVWHeight / 2 - mTWObj.mMove).toFloat()
        drawScale(canvas, mTWObj.curValue, yPosition)

        if (mTWObj.mAttrUseCurTag) {
            mTWObj.mTTTranslator.translateTWTag(mTWObj.curValue).let {
                val tw = getTextWidth(mTPBig, it)
                canvas.drawText(it, (mLongLineXEnd + mTWObj.width) / 2,
                        yPosition + tw/2, mTPBig)
            }
        }
    }

    private fun drawUpScale(canvas: Canvas, pos: Int) {
        val yPosition = mVWHeight / 2 - mTWObj.mMove - (pos * mTWObj.mAttrLineDivider).toFloat()
        val curVal = mTWObj.curValue - pos
        if (curVal >= mTWObj.mAttrMinValue) {
            drawScale(canvas, curVal, yPosition)
        }
    }

    private fun drawDownScale(canvas: Canvas, pos: Int) {
        val yPosition = mVWHeight / 2 - mTWObj.mMove + (pos * mTWObj.mAttrLineDivider).toFloat()
        val curVal = mTWObj.curValue + pos
        if (curVal <= mTWObj.mAttrMaxValue) {
            drawScale(canvas, curVal, yPosition)
        }
    }

    private fun drawScale(canvas: Canvas, curVal: Int, yPosition: Float)  {
        if ((curVal - mTWObj.mAttrMinValue) % (mTWObj.mAttrShortLineCount + 1) == 0) {
            mTWObj.mTTTranslator.translateTWTag(curVal).let {
                canvas.drawLine(mLongLineXStart, yPosition, mLongLineXEnd,
                        yPosition, mLinePaint)

                val tw = getTextWidth(mTPNormal, it)
                canvas.drawText(it, countLeftStart(mLongLineXStart / 2, tw),
                        yPosition + tw/2, mTPNormal)
            }
        } else {
            canvas.drawLine(mShortLineXStart, yPosition, mShortLineXEnd, yPosition, mLinePaint)
        }
    }


    /**
     * 计算显示位置
     * @param xPosition     起始x坐标
     * @param textWidth     字体宽度
     * @return 偏移坐标
     */
    private fun countLeftStart(xPosition: Float, textWidth: Float): Float {
        return xPosition - textWidth / 2
    }

    /**
     * 中间的红色指示线
     * @param canvas     画布
     */
    private fun drawMiddleLine(canvas: Canvas) {
        val yPosition = (mVWHeight / 2).toFloat()
        Paint().let {
            it.strokeWidth = getDPToPX(8)
            it.color = mTWObj.LINE_COLOR_CURSOR
            canvas.drawLine(mLongLineXStart, yPosition, mLongLineXEnd, yPosition, it)
        }
    }
}
