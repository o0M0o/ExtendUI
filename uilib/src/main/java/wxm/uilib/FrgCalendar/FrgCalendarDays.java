package wxm.uilib.FrgCalendar;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Calendar;
import java.util.TreeMap;

import wxm.uilib.R;

/**
 * @author WangXM
 * @version create：2018/4/18
 */
public class FrgCalendarDays extends ConstraintLayout {
    // UI component
    protected GridView  mGVCalendar;
    protected View      mVWFloatingSelected;

    // data
    private String      mSZCurrentMonth;
    private String      mSZSelectedDate;

    private FrgCalendarItemAdapter  mIAItemAdapter;
    private Class<?>                mECItemModel;

    // listener
    private FrgCalendar.OnDateSelectedListener mOnDateSelectedListener;
    private FrgCalendar.OnMonthChangedListener mOnMonthChangeListener;


    public FrgCalendarDays(Context context) {
        super(context);
        initSelf(context, null);
    }

    public FrgCalendarDays(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSelf(context, attrs);
    }

    public FrgCalendarDays(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSelf(context, attrs);
    }

    public void setDataSelectedListener(FrgCalendar.OnDateSelectedListener listener) {
        mOnDateSelectedListener = listener;
    }

    public void setMonthChangeListener(FrgCalendar.OnMonthChangedListener listener) {
        mOnMonthChangeListener = listener;
    }


    public void setCalendarItemAdapter(FrgCalendarItemAdapter ciAdapter) {
        mIAItemAdapter = ciAdapter;

        Type tp = ciAdapter.getClass().getGenericSuperclass();
        mECItemModel = tp instanceof ParameterizedType ?
                (Class<?>) ((ParameterizedType)tp).getActualTypeArguments()[0]
                : FrgCalendarItemModel.class;

        mGVCalendar.setAdapter(mIAItemAdapter);
        initCalendarView();
    }


    public String getCurrentDay()   {
        return mSZSelectedDate;
    }

    /**
     * invoke to change month
     * @param offset        offset for month
     * @param date          new month
     * @param status        status for view
     */
    public void changeMonth(int offset, final String date, final FrgCalendar.CalendarStatus status) {
        FrgCalendar oldCalendarView = new FrgCalendar(getContext());
        oldCalendarView.setCalendarItemAdapter(mIAItemAdapter);
        oldCalendarView.findViewById(R.id.week_bar).setVisibility(GONE);
        ConstraintLayout cl = (ConstraintLayout)getParent();
        cl.addView(oldCalendarView);
        oldCalendarView.setTranslationY(getTranslationY());

        Calendar calendar = FrgCalendarHelper.getCalendarByYearMonthDay(date);
        mSZCurrentMonth = FrgCalendarHelper.YEAR_MONTH_FORMAT.format(calendar.getTime());
        TreeMap<String, FrgCalendarItemModel> tmItemModel = getCalendarDataList(mSZCurrentMonth);
        setDayModel(tmItemModel);

        //setTranslationY(getTranslationY() + offset * this.getHeight());
        animateCalendarToNewMonth(oldCalendarView, offset, oldCalendarView.getTranslationY(), new FrgCalendar.OnMonthChangedListener() {
            @Override
            public void onMonthChanged(String yearMonth) {
                animateSelectedViewToDate(date);
            }
        });
    }



    /// PRIVATE START
    private void initSelf(Context context, AttributeSet attrs)  {
        View.inflate(context, R.layout.frg_calendar_days, this);
        FrgCalendarHelper.init(context);

        // init UI component
        mGVCalendar = (GridView)findViewById(R.id.gridview);
        mVWFloatingSelected = findViewById(R.id.selected_view);

        // upset gridview
        int newH = FrgCalendarHelper.ROW_COUNT * FrgCalendarHelper.mItemHeight;
        LayoutParams lpGV = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, newH);
        mGVCalendar.setLayoutParams(lpGV);

        mGVCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                animateSelectedViewToPos(position);
            }
        });

        // upset float selected view
        mVWFloatingSelected.setLayoutParams(
                new LayoutParams(FrgCalendarHelper.mItemWidth, FrgCalendarHelper.mItemHeight));
        mVWFloatingSelected.setVisibility(View.GONE);

        if(isInEditMode())  {
            setCalendarItemAdapter(new FrgCalendarItemAdapter(context));
        }
    }

    /**
     * init calendar item UI
     */
    private void initCalendarView() {
        Calendar calendar = Calendar.getInstance();
        long selectedTime = calendar.getTimeInMillis();
        String selectedDate = FrgCalendarHelper.YEAR_MONTH_DAY_FORMAT.format(selectedTime);
        mSZCurrentMonth = FrgCalendarHelper.YEAR_MONTH_FORMAT.format(selectedTime);
        TreeMap<String, FrgCalendarItemModel> tmItemModel = getCalendarDataList(mSZCurrentMonth);

        for (FrgCalendarItemModel model : tmItemModel.values()) {
            if (FrgCalendarHelper.areEqualDays(model.getTimeMill(), selectedTime)) {
                model.setStatus(FrgCalendarItemModel.Status.SELECTED);
                break;
            }
        }

        setDayModel(tmItemModel);
        animateSelectedViewToPos(mIAItemAdapter.getIndexToTimeMap().indexOf(selectedDate));
    }

    private void setDayModel(TreeMap<String, FrgCalendarItemModel> dayModelTreeMap) {
        mIAItemAdapter.setDayModelList(dayModelTreeMap);
        mIAItemAdapter.notifyDataSetChanged();

        if(null != mOnMonthChangeListener)  {
            mOnMonthChangeListener.onMonthChanged(mSZCurrentMonth);
        }
    }

    private void animateSelectedViewToDate(String date) {
        int position = mIAItemAdapter.getIndexToTimeMap().indexOf(date);
        animateSelectedViewToPos(position);
    }

    /**
     * get days item-model for month
     * @param yearMonth     year and month for days
     * @return              item-models
     */
    private TreeMap<String, FrgCalendarItemModel> getCalendarDataList(String yearMonth) {
        int totalDays = FrgCalendarHelper.ROW_COUNT * FrgCalendarHelper.COLUMN_COUNT;

        Calendar calStartDate = Calendar.getInstance();
        long time = 0;
        try {
            time = FrgCalendarHelper.YEAR_MONTH_FORMAT.parse(yearMonth).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calStartDate.setTimeInMillis(time);
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
        TreeMap<String, FrgCalendarItemModel> dayModelList = new TreeMap<>();
        for (int i = 0; i < totalDays; i++) {
            try {
                FrgCalendarItemModel dayItem = null == mECItemModel ?
                        new FrgCalendarItemModel() : (FrgCalendarItemModel) mECItemModel.newInstance();

                dayItem.setCurrentMonth(curMonth == calItem.get(Calendar.MONTH));
                dayItem.setToday(FrgCalendarHelper.areEqualDays(calItem, calToday));
                dayItem.setTimeMill(calItem.getTimeInMillis());
                dayItem.setHoliday(Calendar.SUNDAY == calItem.get(Calendar.DAY_OF_WEEK) ||
                        Calendar.SATURDAY == calItem.get(Calendar.DAY_OF_WEEK));
                dayItem.setDayNumber(String.valueOf(calItem.get(Calendar.DAY_OF_MONTH)));
                calItem.add(Calendar.DAY_OF_MONTH, 1);

                dayModelList.put(FrgCalendarHelper.YEAR_MONTH_DAY_FORMAT.format(dayItem.getTimeMill()),
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
    private void animateSelectedViewToPos(int position) {
        mSZSelectedDate = (String) mIAItemAdapter.getIndexToTimeMap().get(position);

        mVWFloatingSelected.setVisibility(View.VISIBLE);
        int left = FrgCalendarHelper.mItemWidth * (position % FrgCalendarHelper.COLUMN_COUNT);
        int top = FrgCalendarHelper.mItemHeight * (position / FrgCalendarHelper.COLUMN_COUNT);
        PropertyValuesHolder pvhX = PropertyValuesHolder
                .ofFloat("X", mVWFloatingSelected.getX(), left);
        PropertyValuesHolder pvhY = PropertyValuesHolder
                .ofFloat("Y", mVWFloatingSelected.getY(), top);
        ObjectAnimator.ofPropertyValuesHolder(mVWFloatingSelected, pvhX, pvhY)
                .setDuration(200).start();

        if (mOnDateSelectedListener != null) {
            mOnDateSelectedListener.onDateSelected(mGVCalendar.getChildAt(position), mSZSelectedDate, position);
        }
    }

    /**
     * animate for change calendar month
     * @param oldCalendarView           view for old view
     * @param offset                    offset between new-month to old-month
     * @param translationY              Y position
     * @param monthChangeListener       month change listener
     */
    private void animateCalendarToNewMonth(final FrgCalendar oldCalendarView,
                                           int offset, float translationY, final FrgCalendar.OnMonthChangedListener monthChangeListener) {
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(this, "translationY", translationY);
        objectAnimator1.setTarget(this);
        objectAnimator1.setDuration(800).start();

        //ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(this, "translationY", oldCalendarView.getTranslationY() - offset * this.getHeight());
        ObjectAnimator objectAnimator2 = ObjectAnimator
                .ofFloat(this, "translationY",
                        offset > 0 ? oldCalendarView.getTranslationY() - this.getHeight()
                                : 0 - this.getHeight());
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

                if (monthChangeListener != null) {
                    monthChangeListener.onMonthChanged(mSZCurrentMonth);
                }
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
