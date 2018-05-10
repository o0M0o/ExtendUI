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

        var drawCount = 0
        var i = 0
        while (drawCount <= 4 * mTWObj.height) {
            var yPosition = mTWObj.height / 2 - mTWObj.mMove + getDPToPX((i * mTWObj.mAttrLineDivider).toFloat())
            if (yPosition + mTWObj.paddingRight < mTWObj.height) {
                val cur_v = mTWObj.curValue + i
                val tw_tag = mTWObj.mTTTranslator.translateTWTag(cur_v)

                if (cur_v <= mTWObj.mAttrMaxValue) {
                    if ((cur_v - mTWObj.mAttrMinValue) % (mTWObj.mAttrShortLineCount + 1) == 0) {
                        canvas.drawLine(mLongLineXStart, yPosition, mLongLineXEnd, yPosition, mLinePaint)

                        val tw = getTextWidth(mTPNormal, tw_tag)
                        canvas.drawText(tw_tag, countLeftStart(mLongLineXStart / 2, tw),
                                yPosition + tw/2, mTPNormal)
                    } else {
                        canvas.drawLine(mShortLineXStart, yPosition, mShortLineXEnd, yPosition, mLinePaint)
                    }
                }

                if (mTWObj.mAttrUseCurTag && 0 == i) {
                    val tw = getTextWidth(mTPBig, tw_tag)
                    canvas.drawText(tw_tag,
                            countLeftStart((mLongLineXEnd + mTWObj.width) / 2, tw),
                            yPosition + tw/2, mTPBig)
                }
            }

            if (0 != i) {
                yPosition = mTWObj.height / 2 - mTWObj.mMove - getDPToPX((i * mTWObj.mAttrLineDivider).toFloat())
                if (yPosition > mTWObj.paddingLeft) {
                    val cur_v = mTWObj.curValue - i
                    if (cur_v >= mTWObj.mAttrMinValue) {
                        if ((cur_v - mTWObj.mAttrMinValue) % (mTWObj.mAttrShortLineCount + 1) == 0) {
                            val tw_tag = mTWObj.mTTTranslator.translateTWTag(cur_v)
                            val tw = getTextWidth(mTPNormal, tw_tag)
                            canvas.drawLine(mLongLineXStart, yPosition, mLongLineXEnd, yPosition, mLinePaint)

                            canvas.drawText(tw_tag, countLeftStart(mLongLineXStart / 2, tw),
                                    yPosition + tw/2, mTPNormal)
                        } else {
                            canvas.drawLine(mShortLineXStart, yPosition, mShortLineXEnd, yPosition, mLinePaint)
                        }
                    }
                }
            }

            drawCount += getDPToPX((2 * mTWObj.mAttrLineDivider).toFloat()).toInt()
            i++
        }

        drawMiddleLine(canvas, mLongLineXStart, mLongLineXEnd)
        canvas.restore()
    }

    private fun countCoordinate()   {
        mMiddleWidth = (mTWObj.width / 2).toFloat()
        mLongXDif = getDPToPX((mTWObj.mAttrLongLineHeight / 2).toFloat())
        mShortXDif = getDPToPX((mTWObj.mAttrShortLineHeight / 2).toFloat())

        mLongLineXStart = mMiddleWidth - mLongXDif
        mLongLineXEnd = mMiddleWidth + mLongXDif
        mShortLineXStart = mMiddleWidth - mShortXDif
        mShortLineXEnd = mMiddleWidth + mShortXDif
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
     * @param s_x        起始x坐标
     * @param e_x        结束x坐标
     */
    private fun drawMiddleLine(canvas: Canvas, s_x: Float, e_x: Float) {
        val indexWidth = 12

        val redPaint = Paint()
        redPaint.strokeWidth = indexWidth.toFloat()
        redPaint.color = mTWObj.LINE_COLOR_CURSOR
        canvas.drawLine(s_x, (mTWObj.height / 2).toFloat(), e_x, (mTWObj.height / 2).toFloat(), redPaint)
    }
}
