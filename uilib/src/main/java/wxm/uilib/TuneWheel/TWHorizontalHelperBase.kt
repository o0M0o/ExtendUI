package wxm.uilib.TuneWheel

import android.graphics.Canvas
import android.view.MotionEvent

/**
 * TuneWheel horizontal helper
 * Created by WangXM on 2017/4/22.
 */
internal class TWHorizontalHelperBase(tw: TuneWheel) : TWHelperBase(tw) {
    private var mVWWidth: Int = 0
    private var mVWHeight: Int = 0
    private var mMiddleHeight: Float = 0f
    private var mLongYDif: Float = 0f
    private var mShortYDif: Float = 0F

    private var mLongLineYStart: Float = 0f
    private var mLongLineYEnd: Float = 0f
    private var mShortLineYStart: Float = 0f
    private var mShortLineYEnd: Float = 0f

    private var mTextTopY: Float = 0f
    private var mTextBottomY: Float = 0f


    init {
        countCoordinate()
    }

    override fun afterLayout() {
        countCoordinate()
    }

    override fun countVelocityTracker(event: MotionEvent) {
        mTWObj.mVelocityTracker!!.computeCurrentVelocity(1000)
        val xVelocity = mTWObj.mVelocityTracker!!.xVelocity
        if (Math.abs(xVelocity) > mTWObj.mMinVelocity) {
            mTWObj.mScroller.fling(0, 0, xVelocity.toInt(), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0)
        }
    }

    override fun drawScaleLine(canvas: Canvas) {
        canvas.save()
        for (i in 0..mVWWidth / (2 * mTWObj.mAttrLineDivider)) {
            if (0 == i) {
                drawMiddleScale(canvas)
            } else {
                drawLeftScale(canvas, i)
                drawRightScale(canvas, i)
            }
        }

        drawMiddleLine(canvas)
        canvas.restore()
    }

    private fun countCoordinate() {
        mVWWidth = mTWObj.width
        mVWHeight = mTWObj.height

        mMiddleHeight = (mVWHeight / 2).toFloat()
        mLongYDif = ((mTWObj.mAttrLongLineHeight / 2).toFloat())
        mShortYDif = ((mTWObj.mAttrShortLineHeight / 2).toFloat())

        mLongLineYStart = mMiddleHeight - mLongYDif
        mLongLineYEnd = mMiddleHeight + mLongYDif
        mShortLineYStart = mMiddleHeight - mShortYDif
        mShortLineYEnd = mMiddleHeight + mShortYDif

        mTextTopY = mLongLineYStart / 2 + getTxtHeight(mTPBig) / 2
        mTextBottomY = (mVWHeight + mLongLineYEnd) / 2 + getTxtHeight(mTPNormal) / 2
    }


    /**
     * 中间的红色指示线
     * @param canvas     画布
     */
    private fun drawMiddleScale(canvas: Canvas) {
        val xPosition = (mVWWidth / 2 - mTWObj.mMove).toFloat()
        drawScale(canvas, mTWObj.curValue, xPosition)

        if (mTWObj.mAttrUseCurTag) {
            mTWObj.mTTTranslator.translateTWTag(mTWObj.curValue).let {
                canvas.drawText(it, countLeftStart(xPosition, getTextWidth(mTPBig, it)),
                        mTextTopY, mTPBig)
            }
        }
    }

    private fun drawLeftScale(canvas: Canvas, pos: Int) {
        val xPosition = mVWWidth / 2 - mTWObj.mMove - (pos * mTWObj.mAttrLineDivider).toFloat()
        val curVal = mTWObj.curValue - pos
        if (curVal >= mTWObj.mAttrMinValue) {
            drawScale(canvas, curVal, xPosition)
        }
    }

    private fun drawRightScale(canvas: Canvas, pos: Int) {
        val xPosition = mVWWidth / 2 - mTWObj.mMove + (pos * mTWObj.mAttrLineDivider).toFloat()
        val curVal = mTWObj.curValue + pos
        if (curVal <= mTWObj.mAttrMaxValue) {
            drawScale(canvas, curVal, xPosition)
        }
    }

    private fun drawScale(canvas: Canvas, curVal: Int, xPosition: Float)  {
        if ((curVal - mTWObj.mAttrMinValue) % (mTWObj.mAttrShortLineCount + 1) == 0) {
            mTWObj.mTTTranslator.translateTWTag(curVal).let {
                mLinePaint.strokeWidth = LONG_SCALE_WIDTH
                canvas.drawLine(xPosition, mLongLineYStart, xPosition,
                        mLongLineYEnd, mLinePaint)

                canvas.drawText(it, countLeftStart(xPosition, getTextWidth(mTPNormal, it)),
                        mTextBottomY, mTPNormal)
            }
        } else {
            mLinePaint.strokeWidth = SHORT_SCALE_WIDTH
            canvas.drawLine(xPosition, mShortLineYStart, xPosition, mShortLineYEnd, mLinePaint)
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
        ((mTWObj.width) / 2).toFloat().let {
            canvas.drawLine(it, 0f, it, mVWHeight.toFloat(), mMiddleLinePaint)
        }
    }
}
