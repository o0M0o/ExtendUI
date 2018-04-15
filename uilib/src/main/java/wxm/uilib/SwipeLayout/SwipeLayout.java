package wxm.uilib.SwipeLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import wxm.uilib.R;

/**
 * @author      chenjiawei
 * @version     create：2015/12/9
 *              modify by WangXM at 2018/04/13
 */
public class SwipeLayout extends LinearLayout {
    private final static int SWIPE_LEFT     = 1;
    private final static int SWIPE_RIGHT    = 2;
    private final static int SWIPE_BOTH     = 3;

    /**
     * listener for slide
     */
    public interface OnSlideListener {
        int SLIDE_STATUS_OFF = 0;
        int SLIDE_STATUS_START_SCROLL = 1;
        int SLIDE_STATUS_ON = 2;

        /**
         * event handler
         * @param view      slide view
         * @param status    slide status
         */
        void onSlide(View view, int status);
    }

    private RelativeLayout mContentView;
    private RelativeLayout mRightView;
    private RelativeLayout mLeftView;

    private Scroller mScroller;
    private OnSlideListener mOnSlideListener;
    private int mHolderWidth = 120;
    private int mLastX = 0;
    private int mLastY = 0;
    private static final int TAN = 2;

    private int mSlideState = OnSlideListener.SLIDE_STATUS_OFF;
    private int mSwipeDirection = SWIPE_RIGHT;

    public SwipeLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    /**
     * set content view
     * content view used for show content
     * @param view   content
     */
    public void setContentView(View view) {
        mContentView.addView(view);
    }

    /**
     * set right view
     * right view used do operation
     * @param v     operation view
     */
    public void setRightView(View v) {
        mRightView.addView(v);
    }

    /**
     * set left view
     * right view used do operation
     * @param v     operation view
     */
    public void setLeftView(View v) {
        mLeftView.addView(v);
    }

    /**
     * set slide listener
     * @param onSlideListener       listener for slide
     */
    public void setOnSlideListener(OnSlideListener onSlideListener) {
        mOnSlideListener = onSlideListener;
    }

    /**
     * close slide status
     */
    public void shrink() {
        if (getScrollX() != 0) {
            this.smoothScrollTo(0, 0);
        }
    }

    // 如果其子View存在消耗点击事件的View，那么SwipeLayout的onTouchEvent不会被执行，
    // 因为在ACTION_MOVE的时候返回true，执行其onTouchEvent方法
    // 返回值为true表示本次触摸事件由自己执行，即执行SwipeLayout的onTouchEvent方法
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlide(this,
                            OnSlideListener.SLIDE_STATUS_START_SCROLL);
                    mSlideState = OnSlideListener.SLIDE_STATUS_START_SCROLL;
                }
                //这里需要记录mLastX，mLastY的值，不然当SwipeLayout已经处于开启状态时，
                // 用于再次滑动SwipeLayout时，会先立即复原到关闭状态，用户体验不太好
                mLastX = (int) ev.getX();
                mLastY = (int) ev.getY();
                return false;

            case MotionEvent.ACTION_MOVE:
                // 比较X、Y轴的滑动距离
                // 如果X轴的滑动距离小于两倍的Y轴滑动距离，则不执行SwipeLayout的滑动事件
                return Math.abs(x - mLastX) >= Math.abs(y - mLastY) * TAN;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean performClick()   {
        return  super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int scrollX = getScrollX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                //如果SwipeLayout是在譬如ScrollView、ListView这种可以上下滑动的View中
                //那么当用户的手指滑出SwipeLayout的边界，那么将会触发器ACTION_CANCEL事件
                //如果此情形发生，那么SwipeLayout将会处于停止状态，无法复原。
                //增加下面这句代码，就是告诉父控件，不要cancel我的事件，我的事件我继续处理。
                getParent().requestDisallowInterceptTouchEvent(true);
                int deltaX = x - mLastX;
                if (deltaX != 0) {
                    int newScrollX = scrollX - deltaX;
                    switch (mSwipeDirection)    {
                        // swipe range is [0, mHolderWidth]
                        case SWIPE_RIGHT :
                        case SWIPE_LEFT:
                            newScrollX = Math.min(mHolderWidth, Math.max(0, newScrollX));
                            break;

                        // swipe range is [0,
                        // 2 * mHolderWidth]
                        case SWIPE_BOTH:
                            newScrollX = Math.min(2 * mHolderWidth, Math.max(0, newScrollX));
                            break;
                    }
                    this.scrollTo(newScrollX, 0);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                //如果已滑动的距离满足下面条件，则SwipeLayout直接滑动到最大距离，不然滑动到最小距离0
                int newScrollX = 0;
                switch (mSwipeDirection)    {
                    case SWIPE_RIGHT :
                    case SWIPE_LEFT:
                        newScrollX = scrollX > mHolderWidth*0.75 ? mHolderWidth : 0;
                        break;

                    case SWIPE_BOTH:
                        newScrollX = scrollX > mHolderWidth*0.75 ?
                                        scrollX > mHolderWidth*1.75 ? 2 * mHolderWidth : mHolderWidth
                                        : 0;
                        break;
                }

                this.smoothScrollTo(newScrollX, 0);
                if (mOnSlideListener != null) {
                    switch (mSwipeDirection)    {
                        case SWIPE_RIGHT :
                            mOnSlideListener.onSlide(this,
                                    newScrollX == 0 ?
                                            OnSlideListener.SLIDE_STATUS_OFF
                                            : OnSlideListener.SLIDE_STATUS_ON);

                            mSlideState = newScrollX == 0 ?
                                            OnSlideListener.SLIDE_STATUS_OFF
                                            : OnSlideListener.SLIDE_STATUS_ON;
                            break;

                        case SWIPE_LEFT:
                            mOnSlideListener.onSlide(this,
                                    newScrollX != 0 ?
                                            OnSlideListener.SLIDE_STATUS_OFF
                                            : OnSlideListener.SLIDE_STATUS_ON);

                            mSlideState = newScrollX != 0 ?
                                    OnSlideListener.SLIDE_STATUS_OFF
                                    : OnSlideListener.SLIDE_STATUS_ON;
                            break;

                        case SWIPE_BOTH:
                            mOnSlideListener.onSlide(this,
                                    newScrollX != mHolderWidth ?
                                            OnSlideListener.SLIDE_STATUS_ON
                                            : OnSlideListener.SLIDE_STATUS_OFF);

                            mSlideState = newScrollX != mHolderWidth ?
                                    OnSlideListener.SLIDE_STATUS_ON
                                    : OnSlideListener.SLIDE_STATUS_OFF;
                            break;
                    }
                }
                getParent().requestDisallowInterceptTouchEvent(false);
            }

            default:
                break;
        }

        mLastX = x;
        mLastY = y;
        return true;
    }

    /**
     * get slide status
     * @return      slide status
     */
    public int getSlideState() {
        return mSlideState;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setLayoutWidth(mContentView, r - l);
    }

    /// PRIVATE START
    /**
     * scroll self
     * @param destX     destination x
     * @param destY     destination y
     */
    private void smoothScrollTo(int destX, int destY) {
        // 缓慢滚动到指定位置
        int scrollX = getScrollX();
        int delta = 0;
        switch (mSwipeDirection)    {
            case SWIPE_RIGHT :
                delta = destX - scrollX;
                break;

            case SWIPE_LEFT:
                delta = destX - scrollX;
                break;

            case SWIPE_BOTH:
                delta = destX - scrollX;
                break;
        }

        mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
        invalidate();
    }

    /**
     * init self
     */
    private void initView(Context context, AttributeSet attrs) {
        mScroller = new Scroller(context);
        setOrientation(LinearLayout.HORIZONTAL);
        View.inflate(context, R.layout.container_swipelayout, this);
        mContentView = (RelativeLayout)findViewById(R.id.view_content);
        mRightView = (RelativeLayout)findViewById(R.id.view_right);
        mLeftView = (RelativeLayout)findViewById(R.id.view_left);

        int defPXWidth = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 120,
                getResources().getDisplayMetrics()));

        @LayoutRes int idContent = R.layout.def_content;
        @LayoutRes int idRight = R.layout.def_sub;
        if(null != attrs) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout);
            try {
                mHolderWidth = array.getDimensionPixelSize(R.styleable.SwipeLayout_dmRightWidth, defPXWidth);
                idContent = array.getResourceId(R.styleable.SwipeLayout_idContentView, R.layout.def_content);
                idRight = array.getResourceId(R.styleable.SwipeLayout_idRightView, R.layout.def_sub);

                mSwipeDirection = array.getInt(R.styleable.SwipeLayout_fgDirection, SWIPE_RIGHT);
            } finally {
                array.recycle();
            }
        }   else    {
            mHolderWidth = defPXWidth;
        }

        switch (mSwipeDirection)    {
            case SWIPE_LEFT :   {
                mRightView.setVisibility(View.GONE);

                setLeftView(LayoutInflater.from(context).inflate(idRight, null));
                setLayoutWidth(mLeftView, mHolderWidth);
            }
            break;

            case SWIPE_RIGHT : {
                mLeftView.setVisibility(View.GONE);

                setRightView(LayoutInflater.from(context).inflate(idRight, null));
                setLayoutWidth(mRightView, mHolderWidth);
            }
            break;

            case SWIPE_BOTH :   {
                setLeftView(LayoutInflater.from(context).inflate(idRight, null));
                setRightView(LayoutInflater.from(context).inflate(idRight, null));
                setLayoutWidth(mLeftView, mHolderWidth);
                setLayoutWidth(mRightView, mHolderWidth);
            }
            break;
        }
        setContentView(LayoutInflater.from(context).inflate(idContent, null));

        if(isInEditMode())  {
            this.scrollTo(SWIPE_RIGHT == mSwipeDirection ? mHolderWidth : 0, 0);
        } else  {
            this.scrollTo(SWIPE_RIGHT != mSwipeDirection ? mHolderWidth : 0, 0);
        }
    }

    /**
     * set layout width
     * @param layout    layout object
     * @param width     layout width
     */
    private void setLayoutWidth(RelativeLayout layout, int width) {
        ViewGroup.LayoutParams param = layout.getLayoutParams();
        param.width = width;
        layout.setLayoutParams(param);
    }
    /// PRIVATE END
}
