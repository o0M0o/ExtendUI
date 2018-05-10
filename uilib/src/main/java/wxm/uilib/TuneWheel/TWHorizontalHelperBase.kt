package wxm.uilib.TuneWheel

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import android.view.MotionEvent

/**
 * TuneWheel horizontal helper
 * Created by WangXM on 2017/4/22.
 */
internal class TWHorizontalHelperBase(tw: TuneWheel) : TWHelperBase(tw) {
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

    override fun afterLayout()  {
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

        var i = 0
        var drawCount = 0
        while (drawCount <= 4 * mTWObj.width) {
            var xPosition = mTWObj.width / 2 - mTWObj.mMove + getDPToPX((i * mTWObj.mAttrLineDivider).toFloat())
            if (xPosition + mTWObj.paddingRight < mTWObj.width) {
                val cur_v = mTWObj.curValue + i
                if (cur_v <= mTWObj.mAttrMaxValue) {
                    val tw_tag = mTWObj.mTTTranslator.translateTWTag(cur_v)
                    if ((cur_v - mTWObj.mAttrMinValue) % (mTWObj.mAttrShortLineCount + 1) == 0) {
                        canvas.drawLine(xPosition, mLongLineYStart, xPosition, mLongLineYEnd, mLinePaint)

                        canvas.drawText(tw_tag, countLeftStart(xPosition, getTextWidth(mTPNormal, tw_tag)),
                                mTextBottomY, mTPNormal)
                    } else {
                        canvas.drawLine(xPosition, mShortLineYStart, xPosition, mShortLineYEnd, mLinePaint)
                    }

                    if (mTWObj.mAttrUseCurTag && 0 == i)
                        canvas.drawText(tw_tag, countLeftStart(xPosition, getTextWidth(mTPBig, tw_tag)),
                                mTextTopY, mTPBig)
                }
            }

            if (0 != i) {
                xPosition = mTWObj.width / 2 - mTWObj.mMove - getDPToPX((i * mTWObj.mAttrLineDivider).toFloat())
                if (xPosition > mTWObj.paddingLeft) {
                    val cur_v = mTWObj.curValue - i
                    if (cur_v >= mTWObj.mAttrMinValue) {
                        if ((cur_v - mTWObj.mAttrMinValue) % (mTWObj.mAttrShortLineCount + 1) == 0) {
                            val tw_tag = mTWObj.mTTTranslator.translateTWTag(cur_v)
                            canvas.drawLine(xPosition, mShortLineYStart, xPosition,
                                    mShortLineYEnd, mLinePaint)

                            canvas.drawText(tw_tag, countLeftStart(xPosition, getTextWidth(mTPNormal, tw_tag)),
                                    mTextBottomY, mTPNormal)
                        } else {
                            canvas.drawLine(xPosition, mShortLineYStart, xPosition, mShortLineYEnd, mLinePaint)
                        }
                    }
                }
            }

            drawCount += getDPToPX((2 * mTWObj.mAttrLineDivider).toFloat()).toInt()
            i++
        }

        drawMiddleLine(canvas, mShortLineYStart, mShortLineYEnd)
        canvas.restore()
    }

    private fun countCoordinate()   {
        mMiddleHeight = (mTWObj.height / 2).toFloat()
        mLongYDif = getDPToPX((mTWObj.mAttrLongLineHeight / 2).toFloat())
        mShortYDif = getDPToPX((mTWObj.mAttrShortLineHeight / 2).toFloat())

        mLongLineYStart = mMiddleHeight - mLongYDif
        mLongLineYEnd = mMiddleHeight + mLongYDif
        mShortLineYStart = mMiddleHeight - mShortYDif
        mShortLineYEnd = mMiddleHeight + mShortYDif

        mTextTopY = mLongLineYStart / 2 + getTxtHeight(mTPBig) / 2
        mTextBottomY = (mTWObj.height + mLongLineYEnd) / 2 + getTxtHeight(mTPNormal)/ 2
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
     * @param s_y        起始Y坐标
     * @param e_y        结束Y坐标
     */
    private fun drawMiddleLine(canvas: Canvas, s_y: Float, e_y: Float) {
        val indexWidth = 12

        val redPaint = Paint()
        redPaint.strokeWidth = indexWidth.toFloat()
        redPaint.color = mTWObj.LINE_COLOR_CURSOR
        canvas.drawLine((mTWObj.width / 2).toFloat(), s_y, (mTWObj.width / 2).toFloat(), e_y, redPaint)
    }
}
