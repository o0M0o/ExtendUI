package wxm.uilib.TwoStateButton;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.TextView;

import wxm.androidutil.util.UiUtil;
import wxm.androidutil.util.UtilFun;
import wxm.uilib.R;


/**
 *  双状态button
 *  按下按键后，按键的外观会切换到另一种状态
 *  适合表达“on-off”切换按键
 * @author      wxm
 * @version create：2017/04/24
 */
public class TwoStateButton extends ConstraintLayout {
    private final static String LOG_TAG = "SmallButton";

    TextView    mTVTag;

    /**
     * 可设置属性
     */
    private String mAttrTextOn;
    private String mAttrTextOff;

    private Drawable     mAttrBackGroundOn;
    private Drawable     mAttrBackGroundOff;

    private boolean mAttrIsOn;

    public TwoStateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.vw_two_state_button, this);
        mTVTag = (TextView)findViewById(R.id.tv_tag);
        this.setClickable(true);

        initCompent(context, attrs);
    }

    /**
     * 是否处于"on"
     * @return  is on ?
     */
    public boolean isInOk() {
        return mAttrIsOn;
    }

    /**
     * 得到当前的标签
     * @return  当前标签
     */
    public String getCurTxt()   {
        return mTVTag.getText().toString();
    }

    /**
     * 初始化自身
     * @param context   上下文
     * @param attrs     配置
     */
    private void initCompent(Context context, AttributeSet attrs)  {
        // for parameter
        int text_color;
        int text_size = 12;
        int text_color_def;
        Resources res = context.getResources();

        /*  固定变量    */
        float DISPLAY_DENSITY = res.getDisplayMetrics().density;
        text_color_def = UiUtil.getColor(context, R.color.text_fit);
        text_color = text_color_def;

        boolean b_ok = true;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TwoStateButton);
        try {
            text_size = array.getDimensionPixelSize(R.styleable.TwoStateButton_sbTextSize,
                                        (int) DISPLAY_DENSITY * 12);
            text_color = array.getColor(R.styleable.TwoStateButton_sbTextColor, text_color_def);

            mAttrTextOn = array.getString(R.styleable.TwoStateButton_sbTextOn);
            mAttrTextOn = UtilFun.StringIsNullOrEmpty(mAttrTextOn) ? "on" : mAttrTextOn;

            mAttrTextOff = array.getString(R.styleable.TwoStateButton_sbTextOff);
            mAttrTextOff = UtilFun.StringIsNullOrEmpty(mAttrTextOff) ? "off" : mAttrTextOff;

            mAttrIsOn = array.getBoolean(R.styleable.TwoStateButton_sbIsOn, false);

            mAttrBackGroundOn = array.getDrawable(R.styleable.TwoStateButton_sbBackGroundOn);
            mAttrBackGroundOff = array.getDrawable(R.styleable.TwoStateButton_sbBackGroundOff);
        } catch (Exception ex)  {
            b_ok = false;
            Log.e(LOG_TAG, "catch ex : " + ex.toString());
        } finally {
            array.recycle();
        }

        if(b_ok) {
            if (null == mAttrBackGroundOn)
                mAttrBackGroundOn = UiUtil.getDrawable(context, R.drawable.ts_button_on);

            if (null == mAttrBackGroundOff)
                mAttrBackGroundOff = UiUtil.getDrawable(context, R.drawable.ts_button_off);

            mTVTag.setTextColor(text_color);
            mTVTag.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_size);
            mTVTag.setText(mAttrIsOn ? mAttrTextOn : mAttrTextOff);
            setBackground(mAttrIsOn ? mAttrBackGroundOn : mAttrBackGroundOff);
        }
    }

    @Override
    public boolean performClick()  {
        mAttrIsOn = !mAttrIsOn;

        setBackground(mAttrIsOn ? mAttrBackGroundOn : mAttrBackGroundOff);
        mTVTag.setText(mAttrIsOn ? mAttrTextOn : mAttrTextOff);
        return super.performClick();
    }
}
