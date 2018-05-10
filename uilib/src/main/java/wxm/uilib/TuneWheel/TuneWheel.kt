package wxm.uilib.TuneWheel


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller

import wxm.androidutil.util.UiUtil
import wxm.androidutil.util.UtilFun
import wxm.uilib.R

/**
 * use TuneWheel to select value
 * @author  WangXM
 */
@SuppressLint("ClickableViewAccessibility")
class TuneWheel(context: Context, attrs: AttributeSet) : View(context, attrs) {
    internal var mLastX: Int = 0
    internal var mLastY: Int = 0
    internal var mMove: Int = 0

    internal var mMinVelocity: Int = 0
    internal var mScroller: Scroller
    internal var mVelocityTracker: VelocityTracker? = null

    private var mListener: OnValueChangeListener? = null

    /**
     * 可设置属性
     */
    internal var mAttrMinValue: Int = 0
    internal var mAttrMaxValue: Int = 0


    internal var mAttrUseCurTag: Boolean = false
    internal var mAttrTextSize: Int = 0
    internal var mAttrLongLineHeight: Int = 0
    internal var mAttrShortLineHeight: Int = 0
    internal var mAttrShortLineCount: Int = 0
    internal var mAttrLineDivider: Int = 0
    internal var mAttrOrientation: Int = 0

    /**
     * 固定变量
     */
    internal var TEXT_COLOR_HOT: Int = 0
    internal var TEXT_COLOR_NORMAL: Int = 0
    internal var LINE_COLOR_CURSOR: Int = 0


    /**
     * default translate for tag
     */
    internal var mTTTranslator: TagTranslate = object : TagTranslate {
        override fun translateTWTag(tagVal : Int): String {
            return tagVal.toString()
        }
    }

    // helper
    private var mLUHelper: TWHelperBase

    /**
     * get current value
     */
    var curValue: Int = 0
        internal set

    /**
     * get current tag string
     */
    val curValueTag: String
        get() = mTTTranslator.translateTWTag(curValue)

    /**
     * translate tagVal to show string
     */
    interface TagTranslate {
        /**
         * get show tag for [tagVal]
         */
        fun translateTWTag(tagVal: Int): String
    }

    /**
     * listener for value change
     */
    interface OnValueChangeListener {
        /**
         * for value changed to [value] with tag [valTag]
         */
        fun onValueChange(value: Int, valTag: String)
    }

    init {
        // for color
        TEXT_COLOR_NORMAL = Color.BLACK
        TEXT_COLOR_HOT = UiUtil.getColor(context, R.color.firebrick)
        LINE_COLOR_CURSOR = UiUtil.getColor(context, R.color.trans_red)

        // for others
        mScroller = Scroller(getContext())
        mMinVelocity = ViewConfiguration.get(getContext()).scaledMinimumFlingVelocity

        // for parameter
        val array = context.obtainStyledAttributes(attrs, R.styleable.TuneWheel)
        try {
            // for helper
            mAttrOrientation = array.getInt(R.styleable.TuneWheel_twOrientation, EM_HORIZONTAL)

            mAttrLineDivider = array.getInt(R.styleable.TuneWheel_twLineDivider, 20)
            mAttrShortLineCount = array.getInt(R.styleable.TuneWheel_twShortLineCount, 1)
            mAttrMinValue = array.getInt(R.styleable.TuneWheel_twMinValue, 0)
            mAttrMaxValue = array.getInt(R.styleable.TuneWheel_twMaxValue, 100)
            curValue = array.getInt(R.styleable.TuneWheel_twCurValue, 50)

            mAttrTextSize = array.getDimensionPixelSize(R.styleable.TuneWheel_twTextSize,
                    (resources.displayMetrics.scaledDensity * 12).toInt())
            mAttrLongLineHeight = array.getInt(R.styleable.TuneWheel_twLongLineLength, 24)
            mAttrShortLineHeight = array.getInt(R.styleable.TuneWheel_twShortLineLength, 16)

            mAttrUseCurTag = array.getBoolean(R.styleable.TuneWheel_twUseCurTag, true)
        } finally {
            array.recycle()
        }

        mLUHelper = if (EM_HORIZONTAL == mAttrOrientation)
            TWHorizontalHelperBase(this)
        else
            TWVerticalHelperBase(this)
    }

    /**
     * 设置用于接收结果的监听器
     *
     * @param listener 监听器
     */
    fun setValueChangeListener(listener: OnValueChangeListener) {
        mListener = listener
    }


    /**
     * set [tt] as tag translator
     */
    fun setTranslateTag(tt: TagTranslate) {
        mTTTranslator = tt
    }

    /**
     * 调整参数
     * @param m_paras      新参数
     */
    fun adjustPara(m_paras: Map<String, Any>) {
        for (k in m_paras.keys) {
            if (k == PARA_VAL_MIN) {
                mAttrMinValue = m_paras[k] as Int
            } else if (k == PARA_VAL_MAX) {
                mAttrMaxValue = m_paras[k] as Int
            }
        }

        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mLUHelper.afterLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mLUHelper.drawScaleLine(canvas)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val position = if (EM_HORIZONTAL == mAttrOrientation) event.x.toInt() else event.y.toInt()
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mScroller.forceFinished(true)

                if (EM_HORIZONTAL == mAttrOrientation)
                    mLastX = position
                else
                    mLastY = position

                mMove = 0
            }

            MotionEvent.ACTION_MOVE -> {
                mMove += if (EM_HORIZONTAL == mAttrOrientation)
                    mLastX - position
                else
                    mLastY - position

                mLUHelper.changeMoveAndValue()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mLUHelper.countMoveEnd()
                mLUHelper.countVelocityTracker(event)
                return false
            }

            else -> {
            }
        }

        if (EM_HORIZONTAL == mAttrOrientation)
            mLastX = position
        else
            mLastY = position
        return true
    }

    override fun computeScroll() {
        super.computeScroll()

        if (mScroller.computeScrollOffset()) {
            if (EM_HORIZONTAL == mAttrOrientation) {
                if (mScroller.currX == mScroller.finalX) { // over
                    mLUHelper.countMoveEnd()
                } else {
                    val xPosition = mScroller.currX
                    mMove += mLastX - xPosition
                    mLUHelper.changeMoveAndValue()
                    mLastX = xPosition
                }
            } else {
                if (mScroller.currY == mScroller.finalY) { // over
                    mLUHelper.countMoveEnd()
                } else {
                    val yPosition = mScroller.currY
                    mMove += mLastY - yPosition
                    mLUHelper.changeMoveAndValue()
                    mLastY = yPosition
                }
            }
        }
    }

    /**
     * 数据变化后调用监听器
     */
    internal fun notifyValueChange() {
        if (null != mListener) {
            mListener!!.onValueChange(curValue, mTTTranslator.translateTWTag(curValue))
        }
    }

    companion object {
        val PARA_VAL_MIN = "val_min"
        val PARA_VAL_MAX = "val_max"

        val EM_HORIZONTAL = 2
        val EM_VERTICAL = 1
    }
}
