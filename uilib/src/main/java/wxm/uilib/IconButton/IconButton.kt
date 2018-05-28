package wxm.uilib.IconButton


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import wxm.androidutil.log.TagLog
import wxm.androidutil.util.UiUtil
import wxm.uilib.R


/**
 * button with icon
 * @author      wxm
 * @version create：2017/03/28
 */
class IconButton(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private var mTVName: TextView
    private var mIVIcon: ImageView

    /**
     * setting able attribute
     */
    // 动作名尺寸
    private var mAttrActNameSize: Float = 0.toFloat()

    // 动作icon的宽和高
    private var mAttrActIconWidth: Int = 0
    private var mAttrActIconHeight: Int = 0

    // for icon
    private var mAttrIconID: Int = 0

    // color for hot or cold
    private var mColorHot: Int = 0
    private var mColorCold: Int = 0

    // cold or hot
    private var mIsHot: Boolean = false

    /**
     * get action name
     */
    val actName: String
        get() = mTVName.text.toString()

    val isHot: Boolean
        get() = mIsHot

    init {
        var orientation: Int = HORIZONTAL
        val array = context.obtainStyledAttributes(attrs, R.styleable.IconButton)
        try {
            orientation = array.getInt(R.styleable.IconButton_ibOrientation, HORIZONTAL)
        } catch (ex: Exception) {
            TagLog.e("init failure", ex)
        } finally {
            array.recycle()
        }

        LayoutInflater.from(context)
                .inflate(if (orientation == HORIZONTAL) R.layout.vw_icon_button_h
                else R.layout.vw_icon_button_v, this)

        mTVName = findViewById(R.id.tv_tag)
        mIVIcon = findViewById(R.id.iv_tag)
        initComponent(context, attrs)
    }


    fun setColdOrHot(isWantHot: Boolean) {
        mIsHot = isWantHot

        (if (mIsHot) mColorHot else mColorCold).let {
            mTVName.setTextColor(it)
            mIVIcon.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_ATOP)
        }

        invalidate()
    }


    /**
     * 设置动作名
     * @param szId    动作名id
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate")
    fun setActName(@StringRes szId: Int) {
        setActName(context.getString(szId))
        setColdOrHot(isHot)
    }

    /**
     * 设置动作名
     * @param an    动作名
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate")
    fun setActName(an: String) {
        mTVName.text = an
        setColdOrHot(isHot)
    }

    /**
     * 设置动作icon
     * @param iconId   id for icon
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate")
    fun setActIcon(@DrawableRes iconId: Int) {
        mIVIcon.setImageResource(iconId)
        setColdOrHot(isHot)
    }

    /**
     * 初始化自身
     * @param context   上下文
     * @param attrs     配置
     */
    private fun initComponent(context: Context, attrs: AttributeSet) {
        // for parameter
        val array = context.obtainStyledAttributes(attrs, R.styleable.IconButton)
        try {
            // for icon
            mAttrActIconWidth = array.getDimensionPixelSize(R.styleable.IconButton_ibIconWidth,
                    UiUtil.dip2px(context, 32f))
            mAttrActIconHeight = array.getDimensionPixelSize(R.styleable.IconButton_ibIconHeight,
                    UiUtil.dip2px(context, 32f))

            mAttrIconID = array.getResourceId(R.styleable.IconButton_ibIcon, R.drawable.ic_look)

            // for name
            mAttrActNameSize = array.getDimensionPixelSize(R.styleable.IconButton_ibActNameSize,
                    UiUtil.dip2px(context, 12f)).toFloat()
            array.getString(R.styleable.IconButton_ibActName).let {
                mTVName.text = if (it.isNullOrEmpty()) {
                    "action"
                } else {
                    it
                }
                Unit
            }

            // for color
            mColorHot = array.getColor(R.styleable.IconButton_ibColorHot, Color.DKGRAY)
            mColorCold = array.getColor(R.styleable.IconButton_ibColorCold, Color.DKGRAY)
        } catch (ex: Exception) {
            TagLog.e("init component failure", ex)
        } finally {
            array.recycle()
        }

        updateShow()
    }

    /**
     * update show
     */
    private fun updateShow() {
        // for icon
        val lp = mIVIcon.layoutParams
        lp.width = mAttrActIconWidth
        lp.height = mAttrActIconHeight
        mIVIcon.layoutParams = lp
        mIVIcon.setImageResource(mAttrIconID)

        // for name
        mTVName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrActNameSize)

        setColdOrHot(false)
        invalidate()
        requestLayout()
    }


    companion object {
        private val VERTICAL = 1
        private val HORIZONTAL = 2
    }
}
