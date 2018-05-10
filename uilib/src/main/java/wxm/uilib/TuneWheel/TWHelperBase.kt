package wxm.uilib.TuneWheel

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import android.view.MotionEvent
import wxm.androidutil.util.UiUtil

/**
 * TuneWheel helper base
 * Created by WangXM on 2017/4/22.
 */
@Suppress("MemberVisibilityCanBePrivate")
internal abstract class TWHelperBase(protected var mTWObj: TuneWheel) {
    protected val mLinePaint: Paint = Paint().apply {
        strokeWidth = 2f
        color = mTWObj.TEXT_COLOR_NORMAL
    }

    protected val mTPNormal: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = mTWObj.mAttrTextSize.toFloat()
    }

    protected val mTPBig: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = mTWObj.mAttrTextSize.toFloat()
        color = mTWObj.TEXT_COLOR_HOT
    }

    /**
     * count tracker velocity for [event]
     */
    abstract fun countVelocityTracker(event: MotionEvent)

    /**
     * use [canvas] draw scale
     */
    abstract fun drawScaleLine(canvas: Canvas)

    abstract fun afterLayout()

    /**
     * get [txt] width for [tp]
     */
    protected fun getTextWidth(tp: TextPaint, txt: String) : Float   {
        return Layout.getDesiredWidth(txt, tp)
    }

    fun getTxtHeight(tp: TextPaint): Float    {
        return tp.fontMetrics.let {
            Math.ceil(it.descent.toDouble() - it.ascent.toDouble())  }.toFloat()
    }

    /**
     * translate dp([dp]) to px
     */
    fun getDPToPX(dp: Float): Float {
        return UiUtil.dip2px(mTWObj.context, dp).toFloat()
    }

    /**
     * translate dp([dp]) to px
     */
    fun getDPToPX(dp: Int): Float {
        return UiUtil.dip2px(mTWObj.context, dp.toFloat()).toFloat()
    }

    /**
     * update move
     */
    fun changeMoveAndValue() {
        val ld = getDPToPX(mTWObj.mAttrLineDivider)
        val tValue = (mTWObj.mMove / ld).toInt()
        if (Math.abs(tValue) > 0) {
            mTWObj.curValue += tValue
            mTWObj.mMove -= (tValue * ld).toInt()

            val min = mTWObj.mAttrMinValue
            val max = mTWObj.mAttrMaxValue
            if (mTWObj.curValue <= min || mTWObj.curValue > max) {
                mTWObj.curValue = if (mTWObj.curValue <= min) min else max
                mTWObj.mMove = 0
                mTWObj.mScroller.forceFinished(true)
            }
            mTWObj.notifyValueChange()
        }
        mTWObj.postInvalidate()
    }

    /**
     * when move end
     */
    fun countMoveEnd() {
        val roundMove = Math.round(mTWObj.mMove / getDPToPX(mTWObj.mAttrLineDivider))
        mTWObj.curValue = mTWObj.curValue + roundMove
        mTWObj.curValue = Math.min(Math.max(mTWObj.mAttrMinValue, mTWObj.curValue),
                mTWObj.mAttrMaxValue)

        mTWObj.mLastY = 0
        mTWObj.mLastX = 0
        mTWObj.mMove = 0

        mTWObj.notifyValueChange()
        mTWObj.postInvalidate()
    }
}
