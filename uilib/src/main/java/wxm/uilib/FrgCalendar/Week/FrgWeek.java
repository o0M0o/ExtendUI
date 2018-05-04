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
import java.util.Calendar;
import java.util.TreeMap;

import wxm.uilib.FrgCalendar.Base.CalendarUtility;
import wxm.uilib.FrgCalendar.Base.FrgBaseCalendar;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemAdapter;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemModel;
import wxm.uilib.R;

/**
 * weekly calendar mode
 *
 * @author WangXM
 * @version create：2018/4/18
 */
public class FrgWeek extends FrgBaseCalendar {
    // UI component
    protected GridView mGVCalendar;
    protected View mVWFloatingSelected;

    protected AttributeSet mASSet;

    // data


    private WeekItemAdapter mIAItemAdapter;

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
     *
     * @param ciAdapter adapter
     */
    public void setCalendarItemAdapter(BaseItemAdapter ciAdapter) {
        mIAItemAdapter = (WeekItemAdapter) ciAdapter;
        mGVCalendar.setAdapter(mIAItemAdapter);
    }

    @Override
    public void setSelectedDay(final String date) {
        // set month view
        setDayModel(getCalendarDataList(date));

        // set selected daya
        animateSelectedViewToDate(date, false);
    }

    /**
     * invoke to change week
     *
     * @param offset offset for month
     * @param date   new date, example : "2018-05-02"
     */
    public void changeWeek(int offset, final String date) {
        offset = offset > 0 ? 1 : -1;

        // for old view
        FrgWeek oldView = copySelf();
        ConstraintLayout cl = (ConstraintLayout) getParent();
        cl.addView(oldView);
        oldView.setTranslationX(getTranslationX());

        // for new view
        setSelectedDay(date);
        setTranslationY(getTranslationX() + offset * this.getWidth());

        // for animate
        animateToNewWeek(oldView, date, offset, oldView.getTranslationX());
    }

    /// PRIVATE START
    @Override
    protected void initSelf(Context context) {
        View.inflate(context, R.layout.frg_calendar_days, this);
        CalendarUtility.init(context);

        // init UI component
        mGVCalendar = (GridView) findViewById(R.id.gridview);
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
        fm.setSelectedDay(getCurrentDay());
        return fm;
    }

    /**
     * set day-data
     *
     * @param dayModelTreeMap day-data for calendar day part
     */
    @SuppressWarnings("unchecked")
    private void setDayModel(TreeMap<String, BaseItemModel> dayModelTreeMap) {
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
     *
     * @param date    day
     * @param animate if true use animate
     */
    private void animateSelectedViewToDate(String date, boolean animate) {
        animateSelectedToPos(mIAItemAdapter.getPositionForDay(date), animate);
    }

    /**
     * get days item-model for week
     *
     * @param szDay "yyyy-MM-dd" for day
     * @return day-models
     */
    private TreeMap<String, BaseItemModel> getCalendarDataList(String szDay) {
        Calendar calStart = CalendarUtility.getCalendarByYearMonthDay(szDay);
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        int dayOfWeek = calStart.get(Calendar.DAY_OF_WEEK);

        Calendar calItem = (Calendar) calStart.clone();
        calItem.add(Calendar.DAY_OF_WEEK,
                -(dayOfWeek == Calendar.SUNDAY ? 6 : dayOfWeek - Calendar.SUNDAY - 1));

        int curMonth = calStart.get(Calendar.MONTH);
        Calendar calToday = Calendar.getInstance();
        TreeMap<String, BaseItemModel> lsDayModel = new TreeMap<>();
        for (int i = 0; i < CalendarUtility.COLUMN_COUNT; i++) {
            BaseItemModel dayItem = mIAItemAdapter.getNewItem();
            dayItem.initModel(calItem, calToday);
            dayItem.setCurrentMonth(curMonth == calItem.get(Calendar.MONTH));

            lsDayModel.put(dayItem.getDate(), dayItem);
            calItem.add(Calendar.DAY_OF_MONTH, 1);
        }

        return lsDayModel;
    }

    /**
     * animate selected view to calendar position
     *
     * @param position position in calendar
     */
    private void animateSelectedToPos(final int position, boolean animate) {
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
        obj.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                boolean bMothChanged = !szDay.substring(0, 7).equals(getCurrentMonth());
                setCurrentDay(szDay);
                if (mDayChangeListener != null) {
                    mDayChangeListener.onDayChanged(getCurrentDay());

                    if(bMothChanged)
                        mDayChangeListener.onMonthChanged(getCurrentMonth());
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
     *
     * @param oldMonthView view for old view
     * @param date         new date for selected
     * @param offset       offset between new-month to old-month
     * @param translationX X position
     */
    private void animateToNewWeek(final FrgWeek oldMonthView, final String date,
                                  int offset, float translationX) {
        ObjectAnimator animator1 = ObjectAnimator
                .ofFloat(this, "translationX", translationX);
        animator1.setTarget(this);
        animator1.setDuration(600);

        final ObjectAnimator animator2 = ObjectAnimator
                .ofFloat(this, "translationX",
                        translationX - offset * this.getWidth());
        animator2.setTarget(oldMonthView);
        animator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ConstraintLayout cl = (ConstraintLayout) getParent();
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
