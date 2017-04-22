package wxm.uilib.tunewheel;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.TextPaint;
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
 * 卷尺控件类。由于时间比较紧，只有下班后有时间，因此只实现了基本功能。<br>
 * 细节问题包括滑动过程中widget边缘的刻度显示问题等<br>
 * <p>
 * 周末有时间会继续更新<br>
 *
 * @author ttdevs
 * @version create：2014年8月26日
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




    private int mLastX, mLastY, mMove;
    private int mWidth, mHeight;

    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private OnValueChangeListener mListener;

    /**
     * 可设置属性
     */
    private String mAttrSZPostUnit;
    private String mAttrSZPrvUnit;
    private int mAttrMinValue;
    private int mAttrMaxValue;
    private int mAttrCurValue;

    private int mAttrTextSize;
    private int mAttrLongLineHeight;
    private int mAttrShortLineHeight;
    private int mAttrShortLineCount;
    private int mAttrLineDivider;

    private int mAttrOrientation;

    private boolean mAttrUseCurTag;

    /**
     * 固定变量
     */
    private int TEXT_COLOR_HOT;
    private int TEXT_COLOR_NORMAL;
    private int LINE_COLOR_CURSOR;
    private float DISPLAY_DENSITY;


    /**
     * 本地辅助类
     */
    private class LocalUtility {
        private void countVelocityTrackerH(MotionEvent event) {
            mVelocityTracker.computeCurrentVelocity(1000);
            float xVelocity = mVelocityTracker.getXVelocity();
            if (Math.abs(xVelocity) > mMinVelocity) {
                mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            }
        }

        private void countVelocityTrackerV(MotionEvent event) {
            mVelocityTracker.computeCurrentVelocity(1000);
            float yVelocity = mVelocityTracker.getYVelocity();
            if (Math.abs(yVelocity) > mMinVelocity) {
                mScroller.fling(0, 0, 0, (int) yVelocity, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            }
        }

        private void changeMoveAndValueH() {
            int tValue = (int) (mMove / getDPToPX(mAttrLineDivider));
            if (Math.abs(tValue) > 0) {
                mAttrCurValue += tValue;
                mMove -= tValue * getDPToPX(mAttrLineDivider);
                if (mAttrCurValue <= mAttrMinValue || mAttrCurValue > mAttrMaxValue) {
                    mAttrCurValue = mAttrCurValue <= mAttrMinValue ? mAttrMinValue : mAttrMaxValue;
                    mMove = 0;
                    mScroller.forceFinished(true);
                }
                notifyValueChange();
            }
            postInvalidate();
        }

        private void changeMoveAndValueV() {
            int tValue = (int) (mMove / getDPToPX(mAttrLineDivider));
            if (Math.abs(tValue) > 0) {
                mAttrCurValue += tValue;
                mMove -= tValue * getDPToPX(mAttrLineDivider);
                if (mAttrCurValue <= mAttrMinValue || mAttrCurValue > mAttrMaxValue) {
                    mAttrCurValue = mAttrCurValue <= mAttrMinValue ? mAttrMinValue : mAttrMaxValue;
                    mMove = 0;
                    mScroller.forceFinished(true);
                }
                notifyValueChange();
            }
            postInvalidate();
        }

        private void countMoveEnd() {
            int roundMove = Math.round(mMove / getDPToPX(mAttrLineDivider));
            mAttrCurValue = mAttrCurValue + roundMove;
            mAttrCurValue = Math.min(Math.max(mAttrMinValue, mAttrCurValue), mAttrMaxValue);

            mLastY = 0;
            mLastX = 0;
            mMove = 0;

            notifyValueChange();
            postInvalidate();
        }

        /**
         * 把dp翻译为px
         * @param dp    val for dp
         * @return      val for px
         */
        private float getDPToPX(float dp) {
            return DISPLAY_DENSITY * dp;
        }
    }


    /**
     * 默认值翻译为tag
     */
    private TagTranslate mTTTranslator = new TagTranslate() {
        @Override
        public String translateTWTag(int val) {
            return mAttrSZPrvUnit + String.valueOf(val) + mAttrSZPostUnit;
        }
    };


    // 辅助类实例
    private LocalUtility    mLUHelper;


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
            String sz_unit = array.getString(R.styleable.TuneWheel_twPostUnit);
            mAttrSZPostUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

            sz_unit = array.getString(R.styleable.TuneWheel_twPrvUnit);
            mAttrSZPrvUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

            mAttrLineDivider = array.getInt(R.styleable.TuneWheel_twLineDivider, 20);
            mAttrShortLineCount = array.getInt(R.styleable.TuneWheel_twShortLineCount, 1);
            mAttrMinValue = array.getInt(R.styleable.TuneWheel_twMinValue, 0);
            mAttrMaxValue = array.getInt(R.styleable.TuneWheel_twMaxValue, 100);
            mAttrCurValue = array.getInt(R.styleable.TuneWheel_twCurValue, 50);

            mAttrTextSize = array.getInt(R.styleable.TuneWheel_twTextSize, 14);
            mAttrLongLineHeight = array.getInt(R.styleable.TuneWheel_twLongLineLength, 24);
            mAttrShortLineHeight = array.getInt(R.styleable.TuneWheel_twShortLineLength, 16);

            mAttrOrientation = array.getInt(R.styleable.TuneWheel_twOrientation, EM_HORIZONTAL);

            mAttrUseCurTag = array.getBoolean(R.styleable.TuneWheel_twUseCurTag, true);
        } finally {
            array.recycle();
        }

        // for helper
        mLUHelper = new LocalUtility();
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

        if(EM_HORIZONTAL == mAttrOrientation)
            drawScaleLineH(canvas);
        else
            drawScaleLineV(canvas);
    }

    /**
     * 垂直模式下绘制刻度线
     *
     * @param canvas context
     */
    private void drawScaleLineV(Canvas canvas) {
        class utility {
            /**
             * 计算显示位置
             * @param tag           tag
             * @param xPosition     起始x坐标
             * @param textWidth     字体宽度
             * @return 偏移坐标
             */
            private float countLeftStart(String tag, float xPosition, float textWidth) {
                return xPosition - ((tag.length() * textWidth) / 2);
            }

            /**
             * 中间的红色指示线
             * @param canvas     画布
             * @param s_x        起始x坐标
             * @param e_x        结束x坐标
             */
            private void drawMiddleLine(Canvas canvas, float s_x, float e_x) {
                int indexWidth = 12;

                Paint redPaint = new Paint();
                redPaint.setStrokeWidth(indexWidth);
                redPaint.setColor(LINE_COLOR_CURSOR);
                canvas.drawLine(s_x,  mHeight / 2, e_x, mHeight / 2, redPaint);
            }
        }
        utility helper = new utility();


        canvas.save();

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(TEXT_COLOR_NORMAL);

        TextPaint tp_normal = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp_normal.setTextSize(mLUHelper.getDPToPX(mAttrTextSize));

        TextPaint tp_big = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp_big.setTextSize(mLUHelper.getDPToPX(mAttrTextSize + 2));
        tp_big.setColor(TEXT_COLOR_HOT);

        int height = mHeight, drawCount = 0;
        float textWidth = Layout.getDesiredWidth("0", tp_normal);
        float textWidth_big = Layout.getDesiredWidth("0", tp_big);

        float w_middle = getWidth() / 2;
        float long_x_dif = mLUHelper.getDPToPX(mAttrLongLineHeight /2);
        float short_x_dif = mLUHelper.getDPToPX(mAttrShortLineHeight /2);
        float ln_long_x_s = w_middle - long_x_dif;
        float ln_long_x_e = w_middle + long_x_dif;
        float ln_short_x_s = w_middle - short_x_dif;
        float ln_short_x_e = w_middle + short_x_dif;

        for (int i = 0; drawCount <= 4 * height; i++) {
            float yPosition = (height / 2 - mMove) + mLUHelper.getDPToPX(i * mAttrLineDivider);
            if (yPosition + getPaddingRight() < height) {
                int cur_v = mAttrCurValue + i;
                String tw_tag = mTTTranslator.translateTWTag(cur_v);
                if (cur_v <= mAttrMaxValue) {
                    if ((cur_v - mAttrMinValue) % (mAttrShortLineCount + 1) == 0) {
                        canvas.drawLine(ln_long_x_s, yPosition,  ln_long_x_e, yPosition, linePaint);

                        canvas.drawText(tw_tag,
                                helper.countLeftStart(tw_tag, ln_long_x_s / 2, textWidth),
                                yPosition + textWidth / 2, tp_normal);
                    } else {
                        canvas.drawLine(ln_short_x_s,  yPosition, ln_short_x_e, yPosition, linePaint);
                    }
                }

                if (mAttrUseCurTag && (0 == i))
                    canvas.drawText(tw_tag,
                            helper.countLeftStart(tw_tag, (ln_long_x_e + mWidth) / 2, textWidth_big),
                            yPosition + textWidth_big / 2, tp_big);
            }

            if (0 != i) {
                yPosition = (height / 2 - mMove) - mLUHelper.getDPToPX(i * mAttrLineDivider);
                if (yPosition > getPaddingLeft()) {
                    int cur_v = mAttrCurValue - i;
                    if (cur_v >= mAttrMinValue) {
                        if ((cur_v - mAttrMinValue) % (mAttrShortLineCount + 1) == 0) {
                            String tw_tag = mTTTranslator.translateTWTag(cur_v);
                            canvas.drawLine(ln_long_x_s, yPosition,  ln_long_x_e, yPosition, linePaint);

                            canvas.drawText(tw_tag,
                                    helper.countLeftStart(tw_tag, ln_long_x_s / 2, textWidth),
                                    yPosition + textWidth / 2, tp_normal);
                        } else {
                            canvas.drawLine(ln_short_x_s,  yPosition, ln_short_x_e, yPosition, linePaint);
                        }
                    }
                }
            }

            drawCount += mLUHelper.getDPToPX(2 * mAttrLineDivider);
        }

        helper.drawMiddleLine(canvas, ln_long_x_s, ln_long_x_e);
        canvas.restore();
    }


    /**
     * 水平模式下绘制刻度线
     *
     * @param canvas context
     */
    private void drawScaleLineH(Canvas canvas) {
        class utility {
            /**
             * 计算显示位置
             * @param tag           tag
             * @param xPosition     起始x坐标
             * @param textWidth     字体宽度
             * @return 偏移坐标
             */
            private float countLeftStart(String tag, float xPosition, float textWidth) {
                return xPosition - ((tag.length() * textWidth) / 2);
            }

            /**
             * 中间的红色指示线
             * @param canvas     画布
             * @param s_y        起始Y坐标
             * @param e_y        结束Y坐标
             */
            private void drawMiddleLine(Canvas canvas, float s_y, float e_y) {
                int indexWidth = 12;

                Paint redPaint = new Paint();
                redPaint.setStrokeWidth(indexWidth);
                redPaint.setColor(LINE_COLOR_CURSOR);
                canvas.drawLine(mWidth / 2, s_y, mWidth / 2, e_y, redPaint);
            }
        }
        utility helper = new utility();


        canvas.save();

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(TEXT_COLOR_NORMAL);

        TextPaint tp_normal = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp_normal.setTextSize(mLUHelper.getDPToPX(mAttrTextSize));

        TextPaint tp_big = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp_big.setTextSize(mLUHelper.getDPToPX(mAttrTextSize + 2));
        tp_big.setColor(TEXT_COLOR_HOT);

        int width = mWidth, drawCount = 0;
        float xPosition;
        float textWidth = Layout.getDesiredWidth("0", tp_normal);
        float textWidth_big = Layout.getDesiredWidth("0", tp_big);

        float h_middle = getHeight() / 2;
        float ln_long_s_y = h_middle - mLUHelper.getDPToPX(mAttrLongLineHeight /2);
        float ln_long_e_y = h_middle + mLUHelper.getDPToPX(mAttrLongLineHeight /2);
        float ln_short_s_y = h_middle - mLUHelper.getDPToPX(mAttrShortLineHeight /2);
        float ln_short_e_y = h_middle + mLUHelper.getDPToPX(mAttrShortLineHeight /2);

        float text_top_pos = ln_long_s_y / 2 + textWidth_big / 2;
        float text_bottom_pos = (getHeight() + ln_long_e_y) / 2 + textWidth / 2;

        for (int i = 0; drawCount <= 4 * width; i++) {
            xPosition = (width / 2 - mMove) + mLUHelper.getDPToPX(i * mAttrLineDivider);
            if (xPosition + getPaddingRight() < mWidth) {
                int cur_v = mAttrCurValue + i;
                if (cur_v <= mAttrMaxValue) {
                    String tw_tag = mTTTranslator.translateTWTag(cur_v);
                    if ((cur_v - mAttrMinValue) % (mAttrShortLineCount + 1) == 0) {
                        canvas.drawLine(xPosition, ln_long_s_y, xPosition, ln_long_e_y, linePaint);

                        canvas.drawText(tw_tag,
                                helper.countLeftStart(tw_tag, xPosition, textWidth),
                                text_bottom_pos, tp_normal);
                    } else {
                        canvas.drawLine(xPosition, ln_short_s_y, xPosition, ln_short_e_y, linePaint);
                    }

                    if (mAttrUseCurTag && (0 == i))
                        canvas.drawText(tw_tag,
                                helper.countLeftStart(tw_tag, xPosition, textWidth),
                                text_top_pos, tp_big);
                }
            }

            if (0 != i) {
                xPosition = (width / 2 - mMove) - mLUHelper.getDPToPX(i * mAttrLineDivider);
                if (xPosition > getPaddingLeft()) {
                    int cur_v = mAttrCurValue - i;
                    if (cur_v >= mAttrMinValue) {
                        if ((cur_v - mAttrMinValue) % (mAttrShortLineCount + 1) == 0) {
                            String tw_tag = mTTTranslator.translateTWTag(cur_v);
                            canvas.drawLine(xPosition, ln_long_s_y, xPosition,
                                    ln_long_e_y, linePaint);

                            canvas.drawText(tw_tag,
                                    helper.countLeftStart(tw_tag, xPosition, textWidth),
                                    text_bottom_pos, tp_normal);
                        } else {
                            canvas.drawLine(xPosition, ln_short_s_y, xPosition, ln_short_e_y, linePaint);
                        }
                    }
                }
            }

            drawCount += mLUHelper.getDPToPX(2 * mAttrLineDivider);
        }

        helper.drawMiddleLine(canvas, ln_long_s_y, ln_long_e_y);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(EM_HORIZONTAL == mAttrOrientation)
            return doTouchH(event);

        return doTouchV(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if(EM_HORIZONTAL == mAttrOrientation)
            doScrollH();
        else
            doScrollV();
    }

    private boolean doTouchH(MotionEvent ev) {
        int action = ev.getAction();
        int xPosition = (int) ev.getX();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);

                mLastX = xPosition;
                mMove = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                mMove += (mLastX - xPosition);
                mLUHelper.changeMoveAndValueH();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLUHelper.countMoveEnd();
                mLUHelper.countVelocityTrackerH(ev);
                return false;

            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    private boolean doTouchV(MotionEvent ev) {
        int action = ev.getAction();
        int yPosition = (int) ev.getY();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);

                mLastY = yPosition;
                mMove = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                mMove += (mLastY - yPosition);
                mLUHelper.changeMoveAndValueV();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLUHelper.countMoveEnd();
                mLUHelper.countVelocityTrackerV(ev);
                return false;

            default:
                break;
        }

        mLastY = yPosition;
        return true;
    }

    private void doScrollH()    {
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
                mLUHelper.countMoveEnd();
            } else {
                int xPosition = mScroller.getCurrX();
                mMove += (mLastX - xPosition);
                mLUHelper.changeMoveAndValueH();
                mLastX = xPosition;
            }
        }
    }

    private void doScrollV()    {
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrY() == mScroller.getFinalY()) { // over
                mLUHelper.countMoveEnd();
            } else {
                int yPosition = mScroller.getCurrY();
                mMove += (mLastY - yPosition);
                mLUHelper.changeMoveAndValueV();
                mLastY = yPosition;
            }
        }
    }

    /**
     * 数据变化后调用监听器
     */
    private void notifyValueChange() {
        if (null != mListener) {
            mListener.onValueChange(mAttrCurValue, mTTTranslator.translateTWTag(mAttrCurValue));
        }
    }
}
