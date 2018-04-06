package wxm.uilib.DistanceMeter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import butterknife.ButterKnife;
import wxm.androidutil.util.UiUtil;
import wxm.androidutil.util.UtilFun;
import wxm.uilib.R;


/**
 * meter rule
 * @author      WangXM
 * @version     create：2017/03/28
 */
public class DistanceMeter extends View {
    public final static String PARA_VAL_MIN = "val_min";
    public final static String PARA_VAL_MAX = "val_max";

    /**
     * translate value to show tag
     */
    public interface TagTranslator {
        /**
         * use value get display tag
         * @param val       value for rule
         * @return          show tag
         */
        String translateTWTag(int val);
    }

    private int mVWWidth;
    private int mVWHeight;


    /**
     * attributes
     */
    private String mAttrSZPostUnit;
    private String mAttrSZPrvUnit;
    private int mAttrMinValue;
    private int mAttrMaxValue;

    private int mAttrTextSize;
    private int mAttrSubScaleHeight;
    private int mAttrMainScaleHeight;
    private int mAttrSubScaleCount;
    private int mAttrMainScaleCount;
    private int mAttrTagWidth;
    private int mAttrTagHeight;

    /**
     * cursors for meter rule
     */
    private ArrayList<DistanceMeterTag>     mALTags = new ArrayList<>();

    /**
     * default translator
     */
    private TagTranslator mTTTranslator = new TagTranslator() {
        @Override
        public String translateTWTag(int val) {
            return mAttrSZPrvUnit + String.valueOf(val) + mAttrSZPostUnit;
        }
    };

    /**
     * const value for self use
     */
    private int TEXT_COLOR_NORMAL;
    private float DISPLAY_DENSITY;
    private float DISPLAY_TEXT_WIDTH;

    class drawHelper {
        private Canvas    mCanvas;

        private int mShortLineWidth = 2;
        private int mLongLineWidth = 4;

        private float mBigWidthUnit;
        private float mSmallWidthUnit;

        private float mBigUnitVal;
        private float mSmallUnitVal;

        private int   PAD_START;
        private int   PAD_END;

        drawHelper(final Canvas dr)    {
            mCanvas = dr;

            PAD_START = getPaddingStart();
            PAD_END   = getPaddingEnd();

            // 获得显示值
            int mTotalValue = mAttrMaxValue - mAttrMinValue;
            mTotalValue = 0 == mTotalValue % mAttrMainScaleCount ?
                            mTotalValue : mTotalValue + mTotalValue % mAttrMainScaleCount;

            mBigWidthUnit = (mVWWidth - PAD_START - PAD_END) / mAttrMainScaleCount;
            mSmallWidthUnit = mBigWidthUnit / mAttrSubScaleCount;

            mBigUnitVal = mTotalValue / mAttrMainScaleCount;
            mSmallUnitVal = mBigUnitVal / mAttrSubScaleCount;
        }

        void drawScales() {
            // for paint
            Paint subScalePaint = new Paint();
            subScalePaint.setStrokeWidth(mShortLineWidth);
            subScalePaint.setColor(TEXT_COLOR_NORMAL);

            Paint mainScalePaint = new Paint();
            mainScalePaint.setStrokeWidth(mLongLineWidth);
            mainScalePaint.setColor(TEXT_COLOR_NORMAL);

            TextPaint tpScaleTag = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            tpScaleTag.setColor(UiUtil.getColor(getContext(), R.color.text_fit));
            tpScaleTag.setTextSize(mAttrTextSize);

            // for axis
            float yMainScaleBottom = mVWHeight - (mVWHeight - mAttrMainScaleHeight) / 2;
            float yMainScaleTop = yMainScaleBottom - mAttrMainScaleHeight;
            float ySubScaleBottom = mVWHeight - (mVWHeight - mAttrSubScaleHeight) / 2;
            float ySubScaleTop = ySubScaleBottom - mAttrSubScaleHeight;
            float yTextBottom = yMainScaleTop - 8;

            for(int i = 0; i <= mAttrMainScaleCount; i++) {
                // draw main scale
                float xMainScale = rulerValToXCoordinate(i, 0, 0);
                mCanvas.drawLine(xMainScale, yMainScaleBottom, xMainScale, yMainScaleTop, mainScalePaint);

                String txtTag = mTTTranslator.translateTWTag(mAttrMinValue
                        + (int)(mBigUnitVal * i));
                float xText;
                if(0 == i)  {
                    xText = PAD_START;
                } else if (mAttrMainScaleCount == i)   {
                    xText = xMainScale -  txtTag.length() * DISPLAY_TEXT_WIDTH - 4;
                } else  {
                    xText = xMainScale -  txtTag.length() * DISPLAY_TEXT_WIDTH / 2;
                }
                mCanvas.drawText(txtTag, xText, yTextBottom, tpScaleTag);

                // draw sub scale
                for(int j = 1; j < mAttrSubScaleCount; j++)  {
                    float xSubScale = rulerValToXCoordinate(i, j, 0);
                    mCanvas.drawLine(xSubScale, ySubScaleBottom, xSubScale, ySubScaleTop, subScalePaint);
                }
            }
        }

        void drawCursors() {
            Paint linePaint = new Paint();

            float yMainScaleBottom = mVWHeight - (mVWHeight - mAttrMainScaleHeight) / 2;
            float yCursorTop = yMainScaleBottom + DPToPixel(1);
            float yCursorBottom = yCursorTop + mAttrTagHeight;
            float yTextBottom = yCursorBottom + DISPLAY_TEXT_WIDTH + DPToPixel(4);

            TextPaint tpCursor = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            for (DistanceMeterTag mt : mALTags) {
                linePaint.setColor(mt.mCRTagColor);

                float xCursorCenter = meterValueToXCoordinate(mt.mTagVal);
                float xCursorLeft = xCursorCenter - mAttrTagWidth / 2;
                float xCursorRight = xCursorCenter + mAttrTagWidth / 2;

                Path p = new Path();
                p.moveTo(xCursorCenter, yCursorTop);
                p.lineTo(xCursorLeft, yCursorBottom);
                p.lineTo(xCursorRight, yCursorBottom);
                p.lineTo(xCursorCenter, yCursorTop);
                mCanvas.drawPath(p, linePaint);

                tpCursor.setColor(mt.mCRTagColor);
                tpCursor.setTextSize(mAttrTextSize);

                float xCursorTextLeft = xCursorCenter - mt.mSZTagName.length() * DISPLAY_TEXT_WIDTH / 2;
                mCanvas.drawText(mt.mSZTagName, xCursorTextLeft, yTextBottom, tpCursor);
            }
        }

        /**
         * translate meter value to x coordinate
         * @param val       meter value
         * @return          X coordinate in view
         */
        private float meterValueToXCoordinate(float val)    {
            val -= mAttrMinValue;
            int big = (int)(val / mBigUnitVal);

            float l_v = val % mBigUnitVal;
            int small = (int)(l_v / mSmallUnitVal);
            float left = (l_v % mSmallUnitVal);

            return rulerValToXCoordinate(big, small, left);
        }

        /**
         * translate ruler value to x coordinate
         * @param big       big unit value
         * @param small     small unit value
         * @param left      left value
         * @return          x coordinate in view
         */
        private float rulerValToXCoordinate(int big, int small, float left) {
            float x_big = 0 == big ?
                    mLongLineWidth / 2 + PAD_START
                    : (mAttrMainScaleCount == big ?
                    mVWWidth - mLongLineWidth / 2 - PAD_END
                    : PAD_START + mBigWidthUnit * big - mLongLineWidth / 2 );

            float x_small = mSmallWidthUnit * small;
            float x_left = mSmallWidthUnit * left / mSmallUnitVal;
            return x_big + x_small + x_left;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public DistanceMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        ButterKnife.bind(this);

        // for normal setting
        Resources res = context.getResources();
        DISPLAY_DENSITY = res.getDisplayMetrics().density;
        TEXT_COLOR_NORMAL = Color.BLACK;

        // for parameter
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DistanceMeter);
        try {
            String sz_unit = array.getString(R.styleable.DistanceMeter_dmPostUnit);
            mAttrSZPostUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

            sz_unit = array.getString(R.styleable.DistanceMeter_dmPrvUnit);
            mAttrSZPrvUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

            mAttrMainScaleCount = array.getInt(R.styleable.DistanceMeter_dmMainScaleCount, 5);
            mAttrSubScaleCount = array.getInt(R.styleable.DistanceMeter_dmSubScaleCount, 2);
            mAttrMinValue = array.getInt(R.styleable.DistanceMeter_dmMinValue, 0);
            mAttrMaxValue = array.getInt(R.styleable.DistanceMeter_dmMaxValue, 100);

            mAttrTextSize = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmTextSize,
                                    (int)(DISPLAY_DENSITY * 12));
            mAttrSubScaleHeight = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmSubScaleHeight, DPToPixel(4));
            mAttrMainScaleHeight = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmMainScaleHeight, DPToPixel(8));

            mAttrTagHeight = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmTagHeight, DPToPixel(8));
            mAttrTagWidth = array.getDimensionPixelSize(R.styleable.DistanceMeter_dmTagWidth, DPToPixel(4));

            // get other
            TextPaint tp_normal = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            tp_normal.setTextSize(mAttrTextSize);
            DISPLAY_TEXT_WIDTH = Layout.getDesiredWidth("0", tp_normal);
        } finally {
            array.recycle();
        }

        if(isInEditMode())  {
            DistanceMeterTag mt_f = new DistanceMeterTag();
            mt_f.mSZTagName = "1";
            mt_f.mCRTagColor = getContext().getColor(R.color.teal);
            mt_f.mTagVal = mAttrMinValue + (mAttrMaxValue - mAttrMinValue) / 3;

            DistanceMeterTag mt_od = new DistanceMeterTag();
            mt_od.mSZTagName = "2";
            mt_od.mCRTagColor = getContext().getColor(R.color.aquamarine);
            mt_od.mTagVal = mAttrMinValue + (mAttrMaxValue - mAttrMinValue) / 2;

            DistanceMeterTag mt_b = new DistanceMeterTag();
            mt_b.mSZTagName = "3";
            mt_b.mCRTagColor = getContext().getColor(R.color.brown);
            mt_b.mTagVal = mAttrMinValue + (mAttrMaxValue - mAttrMinValue) / 3 * 2;

            mALTags.addAll(Arrays.asList(mt_f, mt_od, mt_b));
        }
    }

    /**
     * set scale translator
     * @param tt    translator
     */
    public void setScaleTranslator(TagTranslator tt) {
        mTTTranslator = tt;
    }


    /**
     * clear cursor
     */
    public void clearCursor()     {
        mALTags.clear();
        invalidate();
        requestLayout();
    }

    /**
     * add cursor
     * @param vt    cursors
     */
    public void addCursor(DistanceMeterTag ... vt)    {
        mALTags.addAll(Arrays.asList(vt));
        invalidate();
        requestLayout();
    }

    /**
     * adjust attribute
     * @param m_paras      new attributes
     */
    public void adjustAttribute(Map<String, Object> m_paras)  {
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
        mVWWidth = getWidth();
        mVWHeight = getHeight();
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScaleLine(canvas);
    }

    /**
     * draw scale lines
     * @param canvas    drawer
     */
    private void drawScaleLine(final Canvas canvas) {
        drawHelper helper = new drawHelper(canvas);

        canvas.save();
        helper.drawScales();
        helper.drawCursors();
        canvas.restore();
    }

    private int DPToPixel(float dp)   {
        return (int)(DISPLAY_DENSITY * dp);
    }
}
