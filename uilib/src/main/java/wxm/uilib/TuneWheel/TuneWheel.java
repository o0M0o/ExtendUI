package wxm.uilib.TuneWheel;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.Map;

import wxm.uilib.R;
import wxm.uilib.utility.UtilFun;


/**
 * 卷尺控件类
 * 用户滑动此控件来选择数值
 *
 * @author  wang xiaoming
 */
@SuppressLint("ClickableViewAccessibility")
public class TuneWheel extends View {
    public final static String PARA_VAL_MIN = "val_min";
    public final static String PARA_VAL_MAX = "val_max";

    public final static int    EM_HORIZONTAL    = 2;
    public final static int    EM_VERTICAL      = 1;

    /**
     * 生成TuneWheel的标尺tag
     */
    public interface TagTranslate {
        /**
         * 得到标尺显示tag
         *
         * @param val 标尺值
         * @return 显示tag
         */
        String translateTWTag(int val);
    }

    /**
     * 值变动监听器
     */
    public interface OnValueChangeListener {
        /**
         * 值变动接口
         *
         * @param value  当前数值
         * @param valTag 标尺刻度
         */
        void onValueChange(int value, String valTag);
    }


    int mLastX, mLastY, mMove;
    int mWidth, mHeight;

    int mMinVelocity;
    Scroller mScroller;
    VelocityTracker mVelocityTracker;

    private OnValueChangeListener mListener;

    /**
     * 可设置属性
     */
    String mAttrSZPostUnit;
    String mAttrSZPrvUnit;
    int mAttrMinValue;
    int mAttrMaxValue;
    int mAttrCurValue;

    boolean mAttrUseCurTag;
    int mAttrTextSize;
    int mAttrLongLineHeight;
    int mAttrShortLineHeight;
    int mAttrShortLineCount;
    int mAttrLineDivider;
    int mAttrOrientation;

    /**
     * 固定变量
     */
    int TEXT_COLOR_HOT;
    int TEXT_COLOR_NORMAL;
    int LINE_COLOR_CURSOR;
    float DISPLAY_DENSITY;


    /**
     * 默认值翻译为tag
     */
    TagTranslate mTTTranslator = new TagTranslate() {
        @Override
        public String translateTWTag(int val) {
            return mAttrSZPrvUnit + String.valueOf(val) + mAttrSZPostUnit;
        }
    };

    // 辅助类实例
    private TWHelper    mLUHelper;

    public TuneWheel(Context context, AttributeSet attrs) {
        super(context, attrs);

        // for color
        TEXT_COLOR_NORMAL = Color.BLACK;

        Resources res = context.getResources();
        Resources.Theme te = context.getTheme();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TEXT_COLOR_HOT = res.getColor(R.color.firebrick, te);
            LINE_COLOR_CURSOR = res.getColor(R.color.trans_red, te);
        } else {
            TEXT_COLOR_HOT = res.getColor(R.color.firebrick);
            LINE_COLOR_CURSOR = res.getColor(R.color.trans_red);
        }

        // for others
        mScroller = new Scroller(getContext());
        DISPLAY_DENSITY = getContext().getResources().getDisplayMetrics().density;
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

        // for parameter
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TuneWheel);
        try {
            // for helper
            mAttrOrientation = array.getInt(R.styleable.TuneWheel_twOrientation, EM_HORIZONTAL);
            mLUHelper = EM_HORIZONTAL == mAttrOrientation ? new TWHorizontalHelper(this)
                                : new TWVerticalHelper(this);

            // for prv and post unit tag
            String sz_unit = array.getString(R.styleable.TuneWheel_twPostUnit);
            mAttrSZPostUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

            sz_unit = array.getString(R.styleable.TuneWheel_twPrvUnit);
            mAttrSZPrvUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

            mAttrLineDivider = array.getInt(R.styleable.TuneWheel_twLineDivider, 20);
            mAttrShortLineCount = array.getInt(R.styleable.TuneWheel_twShortLineCount, 1);
            mAttrMinValue = array.getInt(R.styleable.TuneWheel_twMinValue, 0);
            mAttrMaxValue = array.getInt(R.styleable.TuneWheel_twMaxValue, 100);
            mAttrCurValue = array.getInt(R.styleable.TuneWheel_twCurValue, 50);

            mAttrTextSize = array.getDimensionPixelSize(R.styleable.TuneWheel_twTextSize,
                                    (int)mLUHelper.getDPToPX(12));
            mAttrLongLineHeight = array.getInt(R.styleable.TuneWheel_twLongLineLength, 24);
            mAttrShortLineHeight = array.getInt(R.styleable.TuneWheel_twShortLineLength, 16);

            mAttrUseCurTag = array.getBoolean(R.styleable.TuneWheel_twUseCurTag, true);
        } finally {
            array.recycle();
        }
    }

    /**
     * 设置用于接收结果的监听器
     *
     * @param listener 监听器
     */
    public void setValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }


    /**
     * 设置标尺刻度翻译器
     *
     * @param tt 翻译器
     */
    public void setTranslateTag(TagTranslate tt) {
        mTTTranslator = tt;
    }

    /**
     * 获取当前刻度值
     * @return 当前值
     */
    public int getCurValue() {
        return mAttrCurValue;
    }

    /**
     * 获取当前刻度值
     * @return 当前值
     */
    public String getCurValueTag() {
        return mTTTranslator.translateTWTag(mAttrCurValue);
    }

    /**
     * 调整参数
     * @param m_paras      新参数
     */
    public void adjustPara(Map<String, Object> m_paras)  {
        for(String k : m_paras.keySet())     {
            if(k.equals(PARA_VAL_MIN))  {
                mAttrMinValue = (int)m_paras.get(k);
            } else if(k.equals(PARA_VAL_MAX))   {
                mAttrMaxValue = (int)m_paras.get(k);
            }
        }
        
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mWidth = getWidth();
        mHeight = getHeight();
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mLUHelper.drawScaleLine(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int position = EM_HORIZONTAL == mAttrOrientation ? (int)event.getX() : (int)event.getY();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);

                if(EM_HORIZONTAL == mAttrOrientation)
                    mLastX = position;
                else
                    mLastY = position;

                mMove = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                if(EM_HORIZONTAL == mAttrOrientation)
                    mMove += (mLastX - position);
                else
                    mMove += (mLastY - position);

                mLUHelper.changeMoveAndValue();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLUHelper.countMoveEnd();
                mLUHelper.countVelocityTracker(event);
                return false;

            default:
                break;
        }

        if(EM_HORIZONTAL == mAttrOrientation)
            mLastX = position;
        else
            mLastY = position;
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            if (EM_HORIZONTAL == mAttrOrientation) {
                if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
                    mLUHelper.countMoveEnd();
                } else {
                    int xPosition = mScroller.getCurrX();
                    mMove += (mLastX - xPosition);
                    mLUHelper.changeMoveAndValue();
                    mLastX = xPosition;
                }
            } else {
                if (mScroller.getCurrY() == mScroller.getFinalY()) { // over
                    mLUHelper.countMoveEnd();
                } else {
                    int yPosition = mScroller.getCurrY();
                    mMove += (mLastY - yPosition);
                    mLUHelper.changeMoveAndValue();
                    mLastY = yPosition;
                }
            }
        }
    }

    /**
     * 数据变化后调用监听器
     */
    void notifyValueChange() {
        if (null != mListener) {
            mListener.onValueChange(mAttrCurValue, mTTTranslator.translateTWTag(mAttrCurValue));
        }
    }
}
