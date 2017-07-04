package wxm.uilib.IconButton;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import wxm.uilib.R;
import wxm.uilib.utility.UtilFun;


/**
 *  button with icon
 *
 * @author      wxm
 * @version create：2017/03/28
 */
public class IconButton extends ConstraintLayout {
    private final static String LOG_TAG = "IconButton";

    TextView    mTVName;
    ImageView   mIVIcon;

    /**
     * 可设置属性
     */
    // 动作名
    private String  mAttrActName;

    // 动作名尺寸
    private float   mAttrActNameSize;

    // 动作名颜色
    private int     mAttrActNameColor;

    //动作icon的资源id
    private int     mAttrActIconID;

    // 动作icon的宽和高
    private int     mAttrActIconWidth;
    private int     mAttrActIconHeight;

    private final static int VERTICAL       = 1;
    private final static int HORIZONTAL     = 2;

    public IconButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        int orientation = HORIZONTAL;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IconButton);
        try {
            orientation = array.getInt(R.styleable.IconButton_ibOrientation, HORIZONTAL);
        } catch (Exception ex)  {
            Log.e(LOG_TAG, "catch ex : " + ex.toString());
        } finally {
            array.recycle();
        }

        LayoutInflater.from(context)
                .inflate(orientation == HORIZONTAL ?
                            R.layout.vw_icon_button_h : R.layout.vw_icon_button_v
                            ,this);

        mTVName = (TextView)findViewById(R.id.tv_tag);
        mIVIcon = (ImageView)findViewById(R.id.iv_tag);

        initCompent(context, attrs);
    }


    /**
     * 设置动作名
     * @param sz_id    动作名id
     */
    public void setActName(int sz_id)    {
        String sz = getContext().getResources().getString(sz_id);
        setActName(sz);
    }

    /**
     * 设置动作名
     * @param an    动作名
     */
    public void setActName(String an)    {
        mAttrActName = an;
        mTVName.setText(mAttrActName);
    }

    /**
     * 得到动作名
     * @return   动作名
     */
    public String getActName()  {
        return mTVName.getText().toString();
    }

    /**
     * 设置动作icon
     * @param icon_id   id for icon
     */
    public void setActIcon(int icon_id) {
        mAttrActIconID = icon_id;
        mIVIcon.setImageResource(mAttrActIconID);
    }

    /**
     * 初始化自身
     * @param context   上下文
     * @param attrs     配置
     */
    private void initCompent(Context context, AttributeSet attrs)  {
        // for parameter
        boolean b_ok = true;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IconButton);
        try {
            // for icon
            mAttrActIconWidth = array.getDimensionPixelSize(R.styleable.IconButton_ibIconWidth,
                                    UtilFun.dip2px(context, 32));
            mAttrActIconHeight = array.getDimensionPixelSize(R.styleable.IconButton_ibIconHeight,
                                    UtilFun.dip2px(context, 32));

            mAttrActIconID = array.getResourceId(R.styleable.IconButton_ibIcon, R.drawable.ic_look);

            // for name
            int def_color = UtilFun.getColor(context, R.color.text_fit);
            mAttrActNameSize = array.getDimensionPixelSize(R.styleable.IconButton_ibActNameSize,
                                    UtilFun.dip2px(context, 12));
            mAttrActNameColor = array.getColor(R.styleable.IconButton_ibActNameColor, def_color);

            mAttrActName = array.getString(R.styleable.IconButton_ibActName);
            mAttrActName = UtilFun.StringIsNullOrEmpty(mAttrActName) ? "action" : mAttrActName;
        } catch (Exception ex)  {
            b_ok = false;
            Log.e(LOG_TAG, "catch ex : " + ex.toString());
        } finally {
            array.recycle();
        }

        if(b_ok) {
            updateShow();
        }
    }

    /**
     * 更新显示
     */
    private void updateShow()   {
        // for icon
        ViewGroup.LayoutParams lp = mIVIcon.getLayoutParams();
        lp.width = mAttrActIconWidth;
        lp.height = mAttrActIconHeight;
        mIVIcon.setLayoutParams(lp);

        mIVIcon.setImageResource(mAttrActIconID);

        // for name
        mTVName.setText(mAttrActName);
        mTVName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrActNameSize);
        mTVName.setTextColor(mAttrActNameColor);

        invalidate();
        requestLayout();
    }
}
