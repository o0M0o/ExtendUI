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

import wxm.androidutil.util.UtilFun;
import wxm.uilib.FrgCalendar.Base.ECalendarMode;
import wxm.uilib.FrgCalendar.Base.ECalendarStatus;
import wxm.uilib.FrgCalendar.Base.CalendarUtility;
import wxm.uilib.FrgCalendar.Base.EDirection;
import wxm.uilib.FrgCalendar.Base.ICalendarListener;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemAdapter;
import wxm.uilib.FrgCalendar.Month.FrgMonth;
import wxm.uilib.FrgCalendar.Month.MothItemAdapter;
import wxm.uilib.FrgCalendar.Week.FrgWeek;
import wxm.uilib.FrgCalendar.Week.WeekItemAdapter;
import wxm.uilib.R;

/**
 * use this in your layout files if you use calendar UI
 *
 * @author WangXM
 * @version create：2018/4/17
 */
public class FrgCalendar extends ConstraintLayout {
    private static final int WEEK_ITEM_TEXT_SIZE = 12;
    private static final int COLOR_RED = 0xffff725f;
    private static final int COLOR_NORMAL_TXT = 0xff000000;

    private class FlingListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            if (getCalendarMode().isWeekMode()) {
                float dif = e1.getX() - e2.getX();
                if (Math.abs(dif) > CalendarUtility.mItemWidth * 2
                        && Math.abs(velocityY) < Math.abs(velocityX)) {
                    Calendar calDay = CalendarUtility.getCalendarByYearMonthDay(mFGWeek.getCurrentDay());
                    calDay.add(Calendar.WEEK_OF_YEAR, dif > 0 ? 1 : -1);

                    mFGWeek.changePage(dif > 0? EDirection.LEFT : EDirection.RIGHT,
                            CalendarUtility.getYearMonthDayStr(calDay));
                    return true;
                }
            } else {
                float dif = e1.getY() - e2.getY();
                if (Math.abs(dif) > CalendarUtility.mItemHeight * 2
                        && Math.abs(velocityY) > Math.abs(velocityX)) {
                    Calendar calDay = CalendarUtility.getCalendarByYearMonthDay(mFGMonth.getCurrentDay());
                    calDay.add(Calendar.MONTH, dif > 0 ? 1 : -1);

                    mFGMonth.changePage(dif > 0? EDirection.UP : EDirection.DOWN,
                            CalendarUtility.getYearMonthDayStr(calDay));
                    return true;
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
    private GestureDetector mGDDetector;
    private FrgMonth mFGMonth;
    private FrgWeek mFGWeek;
    private LinearLayout mLLWeekBar;

    private TextView mTVYear;
    private TextView mTVMonth;
    private ImageView mIVMode;

    // for touch
    private ECalendarStatus status = ECalendarStatus.LIST_CLOSE;

    // other
    private ICalendarListener mDLSelfDateChangeListener = new ICalendarListener() {
        @Override
        public void onDayChanged(String day) {
            if (null != mDLOuterListener) {
                mDLOuterListener.onDayChanged(day);
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onMonthChanged(String yearMonth) {
            mTVYear.setText(yearMonth.substring(0, 4) + "年");

            String monthTag = yearMonth.substring(5, 7) + "月";
            if (monthTag.startsWith("0")) {
                monthTag = monthTag.substring(1);
            }
            mTVMonth.setText(monthTag);

            if (null != mDLOuterListener) {
                mDLOuterListener.onMonthChanged(yearMonth);
            }
        }
    };
    private ICalendarListener mDLOuterListener = null;


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
     *
     * @param year  year, example : "2018"
     * @param month month, range is [0, 11], example : "5"
     * @param day   day in month, range is [1, 31] example : "1"
     */
    public void setCalendarSelectedDay(int year, int month, int day) {
        if (day < 1 || day > 31)
            return;

        int maxDay = CalendarUtility.getMonthDays(year, month);
        if (-1 == maxDay || day > maxDay)
            return;

        Calendar cDay = Calendar.getInstance();
        cDay.set(year, month, day);

        String szDay = CalendarUtility.getYearMonthDayStr(cDay);
        if (getCalendarMode().isWeekMode())
            mFGWeek.setSelectedDay(szDay);
        else
            mFGMonth.setSelectedDay(szDay);
    }

    /**
     * set usr derived adapter in here
     *
     * @param ciMonth usr implementation adapter
     * @param ciWeek  usr implementation adapter
     */
    public void setCalendarItemAdapter(BaseItemAdapter ciMonth, BaseItemAdapter ciWeek) {
        mFGMonth.setCalendarItemAdapter(ciMonth);
        mFGWeek.setCalendarItemAdapter(ciWeek);
    }

    /**
     * set usr date change listener in here
     * when clicked month or day changed, use this listener tell usr
     *
     * @param listener usr listener
     */
    public void setDateChangeListener(ICalendarListener listener) {
        mDLOuterListener = listener;
    }

    /**
     * set calendar mode
     * can save space if use 'week' mode
     *
     * @param mode      mode param
     */
    public void setCalendarMode(ECalendarMode mode) {
        ECalendarMode oldMode = getCalendarMode();
        if(oldMode == mode)
            return;

        mFGMonth.setVisibility(mode.isMonthMode() ? View.VISIBLE : View.GONE);
        mFGWeek.setVisibility(mode.isWeekMode() ? View.VISIBLE : View.GONE);

        String szDay = oldMode.isWeekMode() ? mFGWeek.getCurrentDay() : mFGMonth.getCurrentDay();
        Calendar cDay = UtilFun.StringIsNullOrEmpty(szDay) ? Calendar.getInstance() :
                CalendarUtility.getCalendarByYearMonthDay(szDay);
        setCalendarSelectedDay(cDay.get(Calendar.YEAR), cDay.get(Calendar.MONTH),
                cDay.get(Calendar.DAY_OF_MONTH));

        mIVMode.setImageResource(mode.isMonthMode() ? R.drawable.ic_tag_week : R.drawable.ic_tag_month);
    }

    /**
     * check if in 'shrink mode'
     *
     * @return true if in 'shrink mode'
     */
    public ECalendarMode getCalendarMode() {
        return mFGMonth.getVisibility() == View.VISIBLE ? ECalendarMode.MONTH : ECalendarMode.WEEK;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mGDDetector.onTouchEvent(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                mGDDetector.onTouchEvent(ev);
                status = ECalendarStatus.DRAGGING;
                return true;

            case MotionEvent.ACTION_UP:
                if (status != ECalendarStatus.DRAGGING) {
                    return super.dispatchTouchEvent(ev);
                }

                mGDDetector.onTouchEvent(ev);
                status = ECalendarStatus.ANIMATING;
                return super.dispatchTouchEvent(ev);
        }

        return super.dispatchTouchEvent(ev);
    }


    /// PRIVATE START

    /**
     * derived UI init
     *
     * @param context for UI
     * @param attrs   for UI
     */
    private void initView(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.uilib_frg_calendar, this);
        CalendarUtility.init(context);

        // init UI component
        // first use month mode
        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.cl_holder);
        mFGMonth = (FrgMonth) cl.findViewById(R.id.fg_month);
        mFGWeek = (FrgWeek) cl.findViewById(R.id.fg_week);
        mLLWeekBar = (LinearLayout) findViewById(R.id.week_bar);
        mFGMonth.setVisibility(View.VISIBLE);
        mFGWeek.setVisibility(View.GONE);

        mGDDetector = new GestureDetector(context, new FlingListener());
        mFGMonth.setDayChangeListener(mDLSelfDateChangeListener);
        mFGWeek.setDayChangeListener(mDLSelfDateChangeListener);

        initHeader((ConstraintLayout) findViewById(R.id.cl_header));
        initWeekBar();

        if (isInEditMode()) {
            setCalendarItemAdapter(new MothItemAdapter(context), new WeekItemAdapter(context));

            Calendar cDay = Calendar.getInstance();
            setCalendarSelectedDay(cDay.get(Calendar.YEAR), cDay.get(Calendar.MONTH),
                    cDay.get(Calendar.DAY_OF_MONTH));
        }
    }

    /**
     * init fast select header
     * in this header you can direct jump to year or month
     *
     * @param clHeader layout holder for header
     */
    private void initHeader(ConstraintLayout clHeader) {
        mTVMonth = (TextView) clHeader.findViewById(R.id.tv_month);
        mTVYear = (TextView) clHeader.findViewById(R.id.tv_year);
        ImageView mIVYearLeft = (ImageView) clHeader.findViewById(R.id.iv_year_left);
        ImageView mIVYearRight = (ImageView) clHeader.findViewById(R.id.iv_year_right);
        ImageView mIVMonthLeft = (ImageView) clHeader.findViewById(R.id.iv_month_left);
        ImageView mIVMonthRight = (ImageView) clHeader.findViewById(R.id.iv_month_right);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                int dif = 0;
                Calendar calendar = CalendarUtility.getCalendarByYearMonthDay(
                        getCalendarMode().isWeekMode() ? mFGWeek.getCurrentDay() : mFGMonth.getCurrentDay());
                if (R.id.iv_year_left == id || R.id.iv_year_right == id) {
                    dif = R.id.iv_year_right == id ? 1 : -1;
                    calendar.add(Calendar.YEAR, dif);
                }

                if (R.id.iv_month_left == id || R.id.iv_month_right == id) {
                    dif = R.id.iv_month_right == id ? 1 : -1;
                    calendar.add(Calendar.MONTH, dif);
                }

                if (0 != dif) {
                    if(getCalendarMode().isWeekMode())   {
                        mFGWeek.changePage(dif == 1? EDirection.LEFT : EDirection.RIGHT,
                                CalendarUtility.getYearMonthDayStr(calendar));
                    } else {
                        mFGMonth.changePage(dif == 1? EDirection.UP : EDirection.DOWN,
                                CalendarUtility.getYearMonthDayStr(calendar));
                    }
                }
            }
        };

        mIVYearLeft.setOnClickListener(listener);
        mIVYearRight.setOnClickListener(listener);
        mIVMonthLeft.setOnClickListener(listener);
        mIVMonthRight.setOnClickListener(listener);


        mIVMode = (ImageView) clHeader.findViewById(R.id.iv_mode);
        mIVMode.setImageResource(getCalendarMode().isMonthMode() ? R.drawable.ic_tag_week : R.drawable.ic_tag_month);
        mIVMode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ECalendarMode emOld = getCalendarMode();
                setCalendarMode(emOld.isWeekMode() ? ECalendarMode.MONTH : ECalendarMode.WEEK);
                mIVMode.setImageResource(emOld.isMonthMode() ? R.drawable.ic_tag_month : R.drawable.ic_tag_week);
            }
        });
    }

    /**
     * init week-bar
     */
    private void initWeekBar() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        String[] weeks = getResources().getStringArray(R.array.week);
        for (int i = 0; i < weeks.length; i++) {
            String week = weeks[i];

            TextView textView = new TextView(this.getContext());
            textView.setLayoutParams(lp);
            textView.setText(week);
            textView.setTextSize(WEEK_ITEM_TEXT_SIZE);
            textView.setTextColor(i == weeks.length - 1 || i == weeks.length - 2
                    ? COLOR_RED : COLOR_NORMAL_TXT);
            textView.setGravity(Gravity.CENTER);

            mLLWeekBar.addView(textView);
        }
    }
    /// PRIVATE END
}
