package wxm.uilib.FrgCalendar.Month;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.TreeMap;

import wxm.uilib.FrgCalendar.Base.CalendarStatus;
import wxm.uilib.FrgCalendar.Base.CalendarUtility;
import wxm.uilib.FrgCalendar.FrgCalendar;
import wxm.uilib.R;

/**
 * show calendar day part
 * @author WangXM
 * @version create：2018/4/18
 */
public class FrgMonth extends ConstraintLayout {
    // UI component
    protected GridView  mGVCalendar;
    protected View      mVWFloatingSelected;

    // data
    private String      mSZCurrentMonth;
    private String      mSZSelectedDate;

    private MothAdapter mIAItemAdapter;
    private Class<?>                mECItemModel;

    // listener
    private FrgCalendar.DateChangeListener mDateChangeListener;


    public FrgMonth(Context context) {
        super(context);
        initSelf(context, null);
    }

    public FrgMonth(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSelf(context, attrs);
    }

    public FrgMonth(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSelf(context, attrs);
    }

    public FrgMonth(Context ct, MothAdapter adapter)    {
        super(ct);
        copySelf(ct, adapter);
    }

    /**
     * set date change listener
     * @param listener      listener
     */
    public void setDataSelectedListener(FrgCalendar.DateChangeListener listener) {
        mDateChangeListener = listener;
    }

    /**
     * set adapter for day-ui-component
     * @param ciAdapter     adapter
     */
    public void setCalendarItemAdapter(MothAdapter ciAdapter) {
        mIAItemAdapter = ciAdapter;

        Type tp = ciAdapter.getClass().getGenericSuperclass();
        mECItemModel = tp instanceof ParameterizedType ?
                (Class<?>) ((ParameterizedType)tp).getActualTypeArguments()[0]
                : MonthModel.class;

        mGVCalendar.setAdapter(mIAItemAdapter);
    }

    /**
     * get current selected day
     * @return      selected day
     */
    public String getCurrentDay()   {
        return mSZSelectedDate;
    }

    /**
     * set/clear shrink mode
     * @param flag      true for 'shrink mode', false for 'full mode'
     */
    public void shrink(final boolean flag)    {
        /*
        int hotRow = !flag || null == mIAItemAdapter ? 0
                : mIAItemAdapter.getIndexToTimeMap().indexOf(mSZSelectedDate) / CalendarUtility.COLUMN_COUNT;

        ObjectAnimator objectAnimator2 = ObjectAnimator
                .ofFloat(this, "translationY",
                        -(CalendarUtility.mItemHeight * hotRow));
        objectAnimator2.setTarget(this);
        objectAnimator2.setDuration(300).start();
        */
    }

    /**
     * set calendar selected day without animate
     * @param date      day for selected, example : "2018-05-01"
     */
    public void setSelectedDay(final String date)   {
        // set month view
        Calendar calendar = CalendarUtility.getCalendarByYearMonthDay(date);
        mSZCurrentMonth = CalendarUtility.getYearMonthStr(calendar);
        setDayModel(getCalendarDataList(mSZCurrentMonth));

        // set selected daya
        animateSelectedViewToDate(date, false);
    }

    /**
     * invoke to change month
     * @param offset        offset for month
     * @param date          new month
     * @param status        status for view
     */
    public void changeMonth(int offset, final String date, final CalendarStatus status) {
        offset = offset > 0 ? 1 : -1;

        FrgMonth oldCalendarView = new FrgMonth(getContext(), mIAItemAdapter);
        ConstraintLayout cl = (ConstraintLayout)getParent();
        cl.addView(oldCalendarView);
        oldCalendarView.setTranslationY(getTranslationY());

        Calendar calendar = CalendarUtility.getCalendarByYearMonthDay(date);
        mSZCurrentMonth = CalendarUtility.getYearMonthStr(calendar);
        setDayModel(getCalendarDataList(mSZCurrentMonth));

        setTranslationY(getTranslationY() + offset * this.getHeight());
        animateCalendarToNewMonth(oldCalendarView, date, offset, oldCalendarView.getTranslationY());
    }

    /// PRIVATE START
    /**
     * init self
     * @param context       layout param
     * @param attrs         layout param
     */
    private void initSelf(Context context, AttributeSet attrs)  {
        View.inflate(context, R.layout.frg_calendar_days, this);
        //CalendarUtility.init(context);

        // init UI component
        mGVCalendar = (GridView)findViewById(R.id.gridview);
        mVWFloatingSelected = findViewById(R.id.selected_view);

        mGVCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                animateSelectedViewToPos(position, true);
            }
        });

        // upset float selected view
        mVWFloatingSelected.setLayoutParams(
                new LayoutParams(CalendarUtility.mItemWidth, CalendarUtility.mItemHeight));
        mVWFloatingSelected.setVisibility(View.GONE);
    }

    private void copySelf(Context ct, MothAdapter adapter) {
        View.inflate(ct, R.layout.frg_calendar_days, this);

        mGVCalendar = (GridView)findViewById(R.id.gridview);
        mVWFloatingSelected = findViewById(R.id.selected_view);

        mVWFloatingSelected.setVisibility(View.GONE);
    }

    /**
     * set day-data
     * @param dayModelTreeMap       day-data for calendar day part
     */
    private void setDayModel(TreeMap<String, MonthModel> dayModelTreeMap) {
        mIAItemAdapter.setDayModelList(dayModelTreeMap);
        mIAItemAdapter.notifyDataSetChanged();

        if(null != mDateChangeListener)  {
            mDateChangeListener.onMonthChanged(mSZCurrentMonth);
        }
    }

    /**
     * animate for move 'selected view' to day
     * @param date          day
     * @param animate       if true use animate
     */
    private void animateSelectedViewToDate(String date, boolean animate) {
        int position = mIAItemAdapter.getIndexToTimeMap().indexOf(date);
        animateSelectedViewToPos(position, animate);
    }

    /**
     * get days item-model for month
     * @param yearMonth     year and month for days
     * @return              item-models
     */
    private TreeMap<String, MonthModel> getCalendarDataList(String yearMonth) {
        int totalDays = CalendarUtility.ROW_COUNT * CalendarUtility.COLUMN_COUNT;

        Calendar calStartDate = Calendar.getInstance();
        calStartDate.setTimeInMillis(CalendarUtility.parseYearMonthStr(yearMonth));
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.HOUR_OF_DAY, 0);
        calStartDate.set(Calendar.MINUTE, 0);
        calStartDate.set(Calendar.SECOND, 0);

        int curMonth = calStartDate.get(Calendar.MONTH);
        int dayOfWeek = calStartDate.get(Calendar.DAY_OF_WEEK);
        Calendar calToday = Calendar.getInstance();
        Calendar calItem = (Calendar) calStartDate.clone();
        calItem.add(Calendar.DAY_OF_WEEK,
                -(dayOfWeek == Calendar.SUNDAY ? 6 : dayOfWeek - Calendar.SUNDAY - 1));
        TreeMap<String, MonthModel> dayModelList = new TreeMap<>();
        for (int i = 0; i < totalDays; i++) {
            try {
                MonthModel dayItem = null == mECItemModel ?
                        new MonthModel() : (MonthModel) mECItemModel.newInstance();

                dayItem.setCurrentMonth(curMonth == calItem.get(Calendar.MONTH));
                dayItem.setToday(CalendarUtility.areEqualDays(calItem, calToday));
                dayItem.setTimeMill(calItem.getTimeInMillis());
                dayItem.setHoliday(Calendar.SUNDAY == calItem.get(Calendar.DAY_OF_WEEK) ||
                        Calendar.SATURDAY == calItem.get(Calendar.DAY_OF_WEEK));
                dayItem.setDayNumber(String.valueOf(calItem.get(Calendar.DAY_OF_MONTH)));
                calItem.add(Calendar.DAY_OF_MONTH, 1);

                dayModelList.put(CalendarUtility.getYearMonthDayStr(dayItem.getTimeMill()),
                        dayItem);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return dayModelList;
    }

    /**
     * animate selected view to calendar position
     * @param position      position in calendar
     */
    private void animateSelectedViewToPos(int position, boolean animate) {
        mSZSelectedDate = (String) mIAItemAdapter.getIndexToTimeMap().get(position);

        mVWFloatingSelected.setVisibility(View.VISIBLE);
        int left = CalendarUtility.mItemWidth * (position % CalendarUtility.COLUMN_COUNT);
        int top = CalendarUtility.mItemHeight * (position / CalendarUtility.COLUMN_COUNT);
        PropertyValuesHolder pvhX = PropertyValuesHolder
                .ofFloat("X", mVWFloatingSelected.getX(), left);
        PropertyValuesHolder pvhY = PropertyValuesHolder
                .ofFloat("Y", mVWFloatingSelected.getY(), top);
        ObjectAnimator.ofPropertyValuesHolder(mVWFloatingSelected, pvhX, pvhY)
                .setDuration(animate ? 200 : 0).start();

        if (mDateChangeListener != null) {
            mDateChangeListener.onDayChanged(mGVCalendar.getChildAt(position), mSZSelectedDate, position);
        }
    }

    /**
     * animate for change calendar month
     * @param oldCalendarView           view for old view
     * @param date                      new date for selected
     * @param offset                    offset between new-month to old-month
     * @param translationY              Y position
     */
    private void animateCalendarToNewMonth(final FrgMonth oldCalendarView, final String date,
                                           int offset, float translationY) {
        ObjectAnimator objectAnimator1 = ObjectAnimator
                .ofFloat(this, "translationY", translationY);
        objectAnimator1.setTarget(this);
        objectAnimator1.setDuration(800).start();

        ObjectAnimator objectAnimator2 = ObjectAnimator
                .ofFloat(this, "translationY",
                        oldCalendarView.getTranslationY() - offset * this.getHeight());
        objectAnimator2.setTarget(oldCalendarView);
        objectAnimator2.setDuration(800).start();
        objectAnimator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ConstraintLayout cl = (ConstraintLayout)getParent();
                cl.removeView(oldCalendarView);

                animateSelectedViewToDate(date, true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
    /// PRIVATE END
}
