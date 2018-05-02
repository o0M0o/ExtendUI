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

import wxm.uilib.FrgCalendar.Base.CalendarStatus;
import wxm.uilib.FrgCalendar.Base.CalendarUtility;
import wxm.uilib.FrgCalendar.Month.MothAdapter;
import wxm.uilib.FrgCalendar.Month.FrgMonth;
import wxm.uilib.R;

/**
 * use this in your layout files if you use calendar UI
 * @author WangXM
 * @version create：2018/4/17
 */
public class FrgCalendar extends ConstraintLayout {
    private static final int WEEK_ITEM_TEXT_SIZE = 12;
    private static final int RED_FF725F = 0xffff725f;




    public interface DateChangeListener {
        /**
         * @param view          clicked the view(Calendar View Item)
         * @param time          the date has been selected with "yyyy-MM-dd" format
         * @param pos           position in GridView
         */
        void onDayChanged(View view, String time, int pos);

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
                    Calendar calendar = CalendarUtility.getCalendarByYearMonthDay(mFDDays.getCurrentDay());
                    calendar.add(Calendar.MONTH, velocityY < 0 ? 1 : -1);

                    mIsMonthChanging = true;
                    mFDDays.changeMonth(velocityY < 0 ? 1 : -1,
                            CalendarUtility.getYearMonthDayStr(calendar),
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
    private FrgMonth mFDDays;
    private LinearLayout        mLLWeekBar;

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

    private DateChangeListener mDLSelfDateChangeListener = new DateChangeListener() {
        @Override
        public void onDayChanged(View view, String time, int pos) {
            if(null != mDLOuterListener) {
                mDLOuterListener.onDayChanged(view, time, pos);
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onMonthChanged(String yearMonth) {
            mIsMonthChanging = false;
            mTVYear.setText(yearMonth.substring(0, 4) + "年");

            String monthTag = yearMonth.substring(5, 7) + "月";
            if(monthTag.startsWith("0"))    {
                monthTag = monthTag.substring(1);
            }
            mTVMonth.setText(monthTag);

            if(null != mDLOuterListener) {
                mDLOuterListener.onMonthChanged(yearMonth);
            }
        }
    };
    private DateChangeListener  mDLOuterListener = null;


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

    /**
     * set calendar selected day
     * @param year      year, example : "2018"
     * @param month     month, range is [0, 11], example : "5"
     * @param day       day in month, range is [1, 31] example : "1"
     */
    public void setCalendarSelectedDay(int year, int month, int day)    {
        if(day < 1 || day > 31)
            return;

        int maxDay = CalendarUtility.getMonthDays(year, month);
        if(-1 == maxDay || day > maxDay)
            return;

        Calendar cDay = Calendar.getInstance();
        cDay.set(year, month, day);
        mFDDays.setSelectedDay(CalendarUtility.getYearMonthDayStr(cDay));
    }

    /**
     * set usr derived adapter in here
     * @param ciAdapter     usr implementation adapter
     */
    public void setCalendarItemAdapter(MothAdapter ciAdapter) {
        mFDDays.setCalendarItemAdapter(ciAdapter);
    }

    /**
     * set usr date change listener in here
     * when clicked month or day changed, use this listener tell usr
     * @param listener      usr listener
     */
    public void setDateChangeListener(DateChangeListener listener)   {
        mDLOuterListener = listener;
    }

    /**
     * set shrink mode
     * can save space if use shrink mode
     * but not implementation this mode now
     * @param flag          true for 'shrink mode', false for 'full mode'
     */
    public void setShrinkMode(boolean flag) {
        mIsShrinkMode = flag;
        initCalendarDay();
        adjustSelfLayout();
    }

    /**
     * check if in 'shrink mode'
     * @return          true if in 'shrink mode'
     */
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
                if (status == CalendarStatus.LIST_OPEN && mDiffY > mLLWeekBar.getBottom() + CalendarUtility.mItemHeight) {
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
                    if (mDiffY > mLLWeekBar.getBottom() + CalendarUtility.mItemHeight) {
                        return super.dispatchTouchEvent(ev);
                    } else {
                        return true;
                    }
                }

                if (status == CalendarStatus.LIST_CLOSE && mDiffY < mLLWeekBar.getBottom() + getBottom()) {
                    return super.dispatchTouchEvent(ev);
                }
                if (status == CalendarStatus.LIST_OPEN && mDiffY > mLLWeekBar.getBottom() + CalendarUtility.mItemHeight) {
                    return super.dispatchTouchEvent(ev);
                }
                */
                mGDDetector.onTouchEvent(ev);
                /*
                setTranslationY(getTranslationY() + selectedRowColumn.row
                        * (curY - mStartY) / ((getHeight() / CalendarUtility.mItemHeight) - 1));
                */
                mStartY = curY;
                status = CalendarStatus.DRAGGING;
                return true;

            case MotionEvent.ACTION_UP:
                //curY = ev.getRawY();
                if (status != CalendarStatus.DRAGGING) {
                    return super.dispatchTouchEvent(ev);
                }

                mGDDetector.onTouchEvent(ev);
                if (status == CalendarStatus.ANIMATING) {
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
        CalendarUtility.init(context);

        // init UI component
        ConstraintLayout cl = (ConstraintLayout)findViewById(R.id.cl_holder);
        mFDDays = (FrgMonth)cl.findViewById(R.id.fd_days);
        mLLWeekBar = (LinearLayout) findViewById(R.id.week_bar);

        mGDDetector = new GestureDetector(context, new FlingListener());
        mFDDays.setDataSelectedListener(mDLSelfDateChangeListener);

        initFastSelected((ConstraintLayout)findViewById(R.id.cl_header));
        initWeekBar();
        initCalendarDay();
        adjustSelfLayout();

        if(isInEditMode())  {
            setCalendarItemAdapter(new MothAdapter(context));

            Calendar cDay = Calendar.getInstance();
            setCalendarSelectedDay(cDay.get(Calendar.YEAR), cDay.get(Calendar.MONTH),
                    cDay.get(Calendar.DAY_OF_MONTH));
        }
    }

    /**
     * init calendar day part
     */
    private void initCalendarDay()  {
        mFDDays.shrink(mIsShrinkMode);
    }

    /**
     * adjust self layout
     * NOT IMPLEMENTATION NOW!
     */
    private void adjustSelfLayout()    {
        /*
        int h = ((LayoutParams)findViewById(R.id.cl_header).getLayoutParams()).height;

        int newH = mIsShrinkMode ?
                        h + CalendarUtility.mItemHeight
                        : h + CalendarUtility.mItemHeight * CalendarUtility.ROW_COUNT;
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, newH));
        */
    }

    /**
     * init fast select header
     * in this header you can direct jump to year or month
     * @param clHeader      layout holder for header
     */
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
                Calendar calendar = CalendarUtility.getCalendarByYearMonthDay(mFDDays.getCurrentDay());
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
                            CalendarUtility.getYearMonthDayStr(calendar),
                            CalendarStatus.LIST_CLOSE);
                }
            }
        };

        mIVYearLeft.setOnClickListener(listener);
        mIVYearRight.setOnClickListener(listener);
        mIVMonthLeft.setOnClickListener(listener);
        mIVMonthRight.setOnClickListener(listener);
    }

    /**
     * init week-bar
     */
    private void initWeekBar() {
        int txt_black = getResources().getColor(android.R.color.black);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        String[] weeks = getResources().getStringArray(R.array.week);
        for (int i = 0; i < weeks.length; i++) {
            String week = weeks[i];

            TextView textView = new TextView(this.getContext());
            textView.setLayoutParams(lp);
            textView.setText(week);
            textView.setTextSize(WEEK_ITEM_TEXT_SIZE);
            textView.setTextColor(i == weeks.length - 1 || i == weeks.length - 2
                    ?  RED_FF725F : txt_black);
            textView.setGravity(Gravity.CENTER);

            mLLWeekBar.addView(textView);
        }
    }
    /// PRIVATE END
}
