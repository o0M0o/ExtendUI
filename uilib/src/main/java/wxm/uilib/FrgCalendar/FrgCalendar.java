package wxm.uilib.FrgCalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import wxm.uilib.R;

/**
 * @author WangXM
 * @version create：2018/4/17
 */
public class FrgCalendar extends ConstraintLayout {
    private static final int WEEK_ITEM_TEXT_SIZE = 12;
    private static final int RED_FF725F = 0xffff725f;

    enum CalendarStatus {
        // when ListView been push to Top
        LIST_OPEN,
        // when ListView stay original position
        LIST_CLOSE,
        // when VIEW is dragging
        DRAGGING,
        //when dragging end,the both CalendarView and ListView will animate to specify position.
        ANIMATING,
    }

    static class SelectedRowColumn {
        int row;
        int column;
    }

    public interface OnDateSelectedListener {
        /**
         * @param view          clicked the view(Calendar View Item)
         * @param time          the date has been selected with "yyyy-MM-dd" format
         * @param pos           position in GridView
         */
        void onDateSelected(View view, String time, int pos);
    }

    public interface OnMonthChangedListener {
        /**
         * when month of calendar view has changed. it include user manually fling CalendarView to change
         * month,also include when user scroll ListView then beyond the current month.it will change month
         * of CalendarView automatically.
         *
         * @param yearMonth the date has been selected is "yyyy-MM-dd" type
         */
        void onMonthChanged(String yearMonth);
    }


    private class FlingListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (!mIsMonthChanging) {
                if (Math.abs(velocityY) > Math.abs(velocityX)) {
                    Calendar calendar = FrgCalendarHelper.getCalendarByYearMonthDay(mFDDays.getCurrentDay());
                    calendar.add(Calendar.MONTH, velocityY < 0 ? 1 : -1);

                    mIsMonthChanging = true;
                    mFDDays.changeMonth(velocityY < 0 ? 1 : -1,
                            FrgCalendarHelper.YEAR_MONTH_DAY_FORMAT.format(calendar.getTime()),
                            CalendarStatus.LIST_CLOSE);
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    // UI component
    private GestureDetector     mGDDetector;
    private FrgCalendarDays     mFDDays;

    private TextView            mTVYear;
    private TextView            mTVMonth;

    // for touch
    private float mStartY;
    private float mDownY;
    private float mDiffY;
    private CalendarStatus status = CalendarStatus.LIST_CLOSE;

    // other
    private boolean     mIsMonthChanging = false;
    private boolean     mIsShrinkMode = false;

    private OnMonthChangedListener  mOLMonthChange = new OnMonthChangedListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onMonthChanged(String yearMonth) {
            mIsMonthChanging = false;
            mTVYear.setText(yearMonth.substring(0, 4) + "年");
            mTVMonth.setText(yearMonth.substring(5, 7) + "月");

            if(null != mOLOuterMonthChange) {
                mOLOuterMonthChange.onMonthChanged(yearMonth);
            }
        }
    };
    private OnMonthChangedListener  mOLOuterMonthChange = null;


    public FrgCalendar(Context context) {
        super(context);
        initView(context, null);
    }

    public FrgCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public FrgCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public void setCalendarItemAdapter(FrgCalendarItemAdapter ciAdapter) {
        mFDDays.setCalendarItemAdapter(ciAdapter);
    }

    public void setOnSelectedListener(OnDateSelectedListener listener)   {
        mFDDays.setDataSelectedListener(listener);
    }

    public void setOnMonthChangeListener(OnMonthChangedListener listener)   {
        mOLOuterMonthChange = listener;
    }

    public void setShrinkMode(boolean flag) {
        mIsShrinkMode = flag;
        initCalendarDay();
        adjustSelfLayout();
    }

    public boolean isShrinkMode()   {
        return mIsShrinkMode;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDiffY = ev.getY();
                /*
                // when listView is close (calendarView is not shrink)
                if (status == CalendarStatus.LIST_CLOSE && mDiffY < mLLWeekBar.getBottom() + getBottom()) {
                    return super.dispatchTouchEvent(ev);
                }
                if (status == CalendarStatus.LIST_OPEN && mDiffY > mLLWeekBar.getBottom() + FrgCalendarHelper.mItemHeight) {
                    return super.dispatchTouchEvent(ev);
                }
                */

                mStartY = ev.getRawY();
                mDownY = ev.getRawY();
                mGDDetector.onTouchEvent(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                float curY = ev.getRawY();
                /*
                if (status == CalendarStatus.LIST_CLOSE && curY > mStartY) {
                    return super.dispatchTouchEvent(ev);
                }

                if (status == CalendarStatus.LIST_OPEN && curY < mStartY) {
                    if (mDiffY > mLLWeekBar.getBottom() + FrgCalendarHelper.mItemHeight) {
                        return super.dispatchTouchEvent(ev);
                    } else {
                        return true;
                    }
                }

                if (status == CalendarStatus.LIST_CLOSE && mDiffY < mLLWeekBar.getBottom() + getBottom()) {
                    return super.dispatchTouchEvent(ev);
                }
                if (status == CalendarStatus.LIST_OPEN && mDiffY > mLLWeekBar.getBottom() + FrgCalendarHelper.mItemHeight) {
                    return super.dispatchTouchEvent(ev);
                }
                */
                mGDDetector.onTouchEvent(ev);
                /*
                setTranslationY(getTranslationY() + selectedRowColumn.row
                        * (curY - mStartY) / ((getHeight() / FrgCalendarHelper.mItemHeight) - 1));
                */
                mStartY = curY;
                status = FrgCalendar.CalendarStatus.DRAGGING;
                return true;

            case MotionEvent.ACTION_UP:
                //curY = ev.getRawY();
                if (status != FrgCalendar.CalendarStatus.DRAGGING) {
                    return super.dispatchTouchEvent(ev);
                }

                mGDDetector.onTouchEvent(ev);
                if (status == FrgCalendar.CalendarStatus.ANIMATING) {
                    return super.dispatchTouchEvent(ev);
                }

                /*
                if (curY < mDownY) {
                    if (Math.abs((curY - mDownY)) > getHeight() / 2) {
                        animationToTop(selectedRowColumn);
                    } else {
                        animationToBottom();
                    }
                } else {
                    if (Math.abs((curY - mDownY)) < getHeight() / 2) {
                        animationToTop(selectedRowColumn);
                    } else {
                        animationToBottom();
                    }
                }
                */
        }

        return super.dispatchTouchEvent(ev);
    }


    /// PRIVATE START
    /**
     * derived UI init
     * @param context   for UI
     * @param attrs     for UI
     */
    private void initView(Context context, AttributeSet attrs)    {
        View.inflate(context, R.layout.frg_calendar, this);
        FrgCalendarHelper.init(context);

        // init UI component
        mFDDays = (FrgCalendarDays)findViewById(R.id.fd_days);

        mGDDetector = new GestureDetector(context, new FlingListener());
        mFDDays.setMonthChangeListener(mOLMonthChange);

        initFastSelected((ConstraintLayout)findViewById(R.id.cl_header));
        initCalendarDay();
        adjustSelfLayout();
    }

    private void initCalendarDay()  {
        mFDDays.shrink(mIsShrinkMode);
    }

    private void adjustSelfLayout()    {
        /*
        int h = ((LayoutParams)findViewById(R.id.cl_header).getLayoutParams()).height;

        int newH = mIsShrinkMode ?
                        h + FrgCalendarHelper.mItemHeight
                        : h + FrgCalendarHelper.mItemHeight * FrgCalendarHelper.ROW_COUNT;
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, newH));
        */
    }


    private void initFastSelected(ConstraintLayout clHeader)    {
        mTVMonth = (TextView)clHeader.findViewById(R.id.tv_month);
        mTVYear = (TextView)clHeader.findViewById(R.id.tv_year);
        ImageView mIVYearLeft = (ImageView) clHeader.findViewById(R.id.iv_year_left);
        ImageView mIVYearRight = (ImageView) clHeader.findViewById(R.id.iv_year_right);
        ImageView mIVMonthLeft = (ImageView) clHeader.findViewById(R.id.iv_month_left);
        ImageView mIVMonthRight = (ImageView) clHeader.findViewById(R.id.iv_month_right);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                int dif = 0;
                Calendar calendar = FrgCalendarHelper.getCalendarByYearMonthDay(mFDDays.getCurrentDay());
                if(R.id.iv_year_left == id || R.id.iv_year_right == id) {
                    dif = R.id.iv_year_right == id ? 1 : -1;
                    calendar.add(Calendar.YEAR, dif);
                }

                if(R.id.iv_month_left == id || R.id.iv_month_right == id)   {
                    dif = R.id.iv_month_right == id ? 1 : -1;
                    calendar.add(Calendar.MONTH, dif);
                }

                if(0 != dif) {
                    mIsMonthChanging = true;
                    mFDDays.changeMonth(dif,
                            FrgCalendarHelper.YEAR_MONTH_DAY_FORMAT.format(calendar.getTime()),
                            CalendarStatus.LIST_CLOSE);
                }
            }
        };

        mIVYearLeft.setOnClickListener(listener);
        mIVYearRight.setOnClickListener(listener);
        mIVMonthLeft.setOnClickListener(listener);
        mIVMonthRight.setOnClickListener(listener);
    }
    /// PRIVATE END
}
