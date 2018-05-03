package wxm.uilib.FrgCalendar.Week;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.TreeMap;

import wxm.uilib.FrgCalendar.Base.CalendarUtility;
import wxm.uilib.FrgCalendar.Base.FrgBaseCalendar;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemAdapter;
import wxm.uilib.R;

/**
 * weekly calendar mode
 * @author WangXM
 * @version create：2018/4/18
 */
public class FrgWeek extends FrgBaseCalendar {
    // UI component
    protected GridView  mGVCalendar;
    protected View      mVWFloatingSelected;

    protected AttributeSet  mASSet;

    // data
    private String      mSZSelectedDate;

    private WeekItemAdapter         mIAItemAdapter;
    private Class<?>                mECItemModel;

    public FrgWeek(Context context) {
        super(context);
    }

    public FrgWeek(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrgWeek(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * set adapter for day-ui-component
     * @param ciAdapter     adapter
     */
    public void setCalendarItemAdapter(BaseItemAdapter ciAdapter) {
        mIAItemAdapter = (WeekItemAdapter)ciAdapter;

        Type tp = ciAdapter.getClass().getGenericSuperclass();
        mECItemModel = tp instanceof ParameterizedType ?
                (Class<?>) ((ParameterizedType)tp).getActualTypeArguments()[0]
                : WeekItemModel.class;

        mGVCalendar.setAdapter(mIAItemAdapter);
    }

    @Override
    public String getCurrentDay()   {
        return mSZSelectedDate;
    }


    @Override
    public void setSelectedDay(final String date)   {
        // set month view
        setDayModel(getCalendarDataList(date));

        // set selected daya
        animateSelectedViewToDate(date, false);
    }

    /**
     * invoke to change week
     * @param offset        offset for month
     * @param date          new date, example : "2018-05-02"
     */
    public void changeWeek(int offset, final String date) {
        offset = offset > 0 ? 1 : -1;

        // for old view
        FrgWeek oldView = copySelf();
        ConstraintLayout cl = (ConstraintLayout)getParent();
        cl.addView(oldView);
        oldView.setTranslationY(getTranslationY());

        // for new view
        setSelectedDay(date);
        setTranslationY(getTranslationY() + offset * this.getHeight() / 2);

        // for animate
        animateToNewWeek(oldView, date, offset, oldView.getTranslationY());
    }

    /// PRIVATE START
    @Override
    protected void initSelf(Context context)  {
        View.inflate(context, R.layout.frg_calendar_days, this);
        CalendarUtility.init(context);

        // init UI component
        mGVCalendar = (GridView)findViewById(R.id.gridview);
        mVWFloatingSelected = findViewById(R.id.selected_view);

        mGVCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                animateSelectedToPos(position, true);
            }
        });

        // upset float selected view
        mVWFloatingSelected.setLayoutParams(
                new LayoutParams(CalendarUtility.mItemWidth, CalendarUtility.mItemHeight));
        mVWFloatingSelected.setVisibility(View.GONE);
    }

    @Override
    protected FrgWeek copySelf() {
        FrgWeek fm = new FrgWeek(getContext(), mASSet);
        try {
            fm.setCalendarItemAdapter(mIAItemAdapter.getClass()
                    .getConstructor(Context.class).newInstance(getContext()));
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        LayoutParams lp = (LayoutParams) getLayoutParams();
        fm.setLayoutParams(new ViewGroup.LayoutParams(lp.width, lp.height));
        fm.setSelectedDay(mSZSelectedDate);
        return fm;
    }

    /**
     * set day-data
     * @param dayModelTreeMap       day-data for calendar day part
     */
    @SuppressWarnings("unchecked")
    private <T extends WeekItemModel> void setDayModel(TreeMap<String, T> dayModelTreeMap) {
        mIAItemAdapter.setDayModel(dayModelTreeMap);
        mIAItemAdapter.notifyDataSetChanged();

        /*
        if(null != mDayChangeListener)  {
            mDayChangeListener.onMonthChanged(mSZCurrentMonth);
        }
        */
    }

    /**
     * animate for move 'selected view' to day
     * @param date          day
     * @param animate       if true use animate
     */
    private void animateSelectedViewToDate(String date, boolean animate) {
        animateSelectedToPos(mIAItemAdapter.getPositionForDay(date), animate);
    }

    /**
     * get days item-model for week
     * @param szDay         "yyyy-MM-dd" for day
     * @return              day-models
     */
    private TreeMap<String, WeekItemModel> getCalendarDataList(String szDay) {
        Calendar calStartDate = CalendarUtility.getCalendarByYearMonthDay(szDay);
        calStartDate.set(Calendar.HOUR_OF_DAY, 0);
        calStartDate.set(Calendar.MINUTE, 0);
        calStartDate.set(Calendar.SECOND, 0);
        int dayOfWeek = calStartDate.get(Calendar.DAY_OF_WEEK);

        Calendar calItem = (Calendar) calStartDate.clone();
        calItem.add(Calendar.DAY_OF_WEEK,
                -(dayOfWeek == Calendar.SUNDAY ? 6 : dayOfWeek - Calendar.SUNDAY - 1));

        Calendar calToday = Calendar.getInstance();
        TreeMap<String, WeekItemModel> dayModelList = new TreeMap<>();
        for (int i = 0; i < CalendarUtility.COLUMN_COUNT; i++) {
            try {
                WeekItemModel dayItem = (WeekItemModel) mECItemModel.newInstance();
                dayItem.initModel(calItem, calToday);

                dayModelList.put(dayItem.getDate(), dayItem);
                calItem.add(Calendar.DAY_OF_MONTH, 1);
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
    private void animateSelectedToPos(final int position, boolean animate) {
        mSZSelectedDate = mIAItemAdapter.getDayInPosition(position);
        final String szDay = mIAItemAdapter.getDayInPosition(position);

        mVWFloatingSelected.setVisibility(View.VISIBLE);
        int left = CalendarUtility.mItemWidth * (position % CalendarUtility.COLUMN_COUNT);
        int top = CalendarUtility.mItemHeight * (position / CalendarUtility.COLUMN_COUNT);
        PropertyValuesHolder pvhX = PropertyValuesHolder
                .ofFloat("X", mVWFloatingSelected.getX(), left);
        PropertyValuesHolder pvhY = PropertyValuesHolder
                .ofFloat("Y", mVWFloatingSelected.getY(), top);
        ObjectAnimator obj = ObjectAnimator.ofPropertyValuesHolder(mVWFloatingSelected, pvhX, pvhY)
                .setDuration(animate ? 200 : 0);
        obj.addListener(new Animator.AnimatorListener()    {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mDayChangeListener != null) {
                    mDayChangeListener.onDayChanged(szDay);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        obj.start();
    }

    /**
     * animate for change calendar month
     * @param oldMonthView           view for old view
     * @param date                      new date for selected
     * @param offset                    offset between new-month to old-month
     * @param translationY              Y position
     */
    private void animateToNewWeek(final FrgWeek oldMonthView, final String date,
                                  int offset, float translationY) {
        ObjectAnimator animator1 = ObjectAnimator
                .ofFloat(this, "translationY", translationY);
        animator1.setTarget(this);
        animator1.setDuration(600);

        final ObjectAnimator animator2 = ObjectAnimator
                .ofFloat(this, "translationY", translationY - offset * this.getHeight());
        animator2.setTarget(oldMonthView);
        animator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ConstraintLayout cl = (ConstraintLayout)getParent();
                cl.removeView(oldMonthView);

                animateSelectedViewToDate(date, true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator2.setDuration(600);

        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animator2.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator1.start();
    }
    /// PRIVATE END
}
