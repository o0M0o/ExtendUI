package wxm.uilib.FrgCalendar.Month;

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
import wxm.uilib.FrgCalendar.Base.EDirection;
import wxm.uilib.FrgCalendar.Base.FrgBaseCalendar;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemAdapter;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemModel;
import wxm.uilib.R;

/**
 * monthly calendar mode
 *
 * @author WangXM
 * @version create：2018/4/18
 */
public class FrgMonth extends FrgBaseCalendar {
    // UI component
    protected GridView mGVCalendar;
    protected View mVWFloatingSelected;

    // data
    private BaseItemAdapter mIAItemAdapter;

    public FrgMonth(Context context) {
        super(context);
    }

    public FrgMonth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrgMonth(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setCalendarItemAdapter(BaseItemAdapter ciAdapter) {
        mIAItemAdapter = ciAdapter;
        mGVCalendar.setAdapter(mIAItemAdapter);
    }

    @Override
    public void setSelectedDay(final String date) {
        doSetSelectedDay(date, false, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void changePage(EDirection direction, final String date) {
        if(0 == CalendarUtility.getYearMonthStr(date).compareTo(getCurrentMonth())) {
            animateSelectedToPos(mIAItemAdapter.getPositionForDay(date), true, true);
        } else {
            int offset = direction == EDirection.UP ? 1 : -1;

            // for old view
            FrgMonth oldCalendarView = copySelf();
            ConstraintLayout cl = (ConstraintLayout) getParent();
            cl.addView(oldCalendarView);
            oldCalendarView.setTranslationY(getTranslationY());

            // for new view
            mIAItemAdapter.setDayModel(getCalendarDataList(date));
            mIAItemAdapter.notifyDataSetChanged();
            setTranslationY(getTranslationY() + offset * this.getHeight());

            // for animate
            animateToNewMonth(oldCalendarView, date, direction);
        }
    }

    /// PRIVATE START
    @Override
    protected void initSelf(Context context) {
        View.inflate(context, R.layout.uilib_frg_calendar_days, this);
        CalendarUtility.init(context);

        // init UI component
        mGVCalendar = (GridView) findViewById(R.id.gridview);
        mVWFloatingSelected = findViewById(R.id.selected_view);

        mGVCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                animateSelectedToPos(position, true, true);
            }
        });

        // upset float selected view
        mVWFloatingSelected.setLayoutParams(
                new LayoutParams(CalendarUtility.mItemWidth, CalendarUtility.mItemHeight));
        mVWFloatingSelected.setVisibility(View.GONE);
    }

    @Override
    protected FrgMonth copySelf() {
        FrgMonth fm = new FrgMonth(getContext(), mASSet);
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
     * set selected day
     * @param date              new selected day
     * @param animate           if true use animate with selected-view
     * @param callListener       if true will invoke day-change-listener
     */
    @SuppressWarnings("unchecked")
    private void doSetSelectedDay(final String date, final boolean animate, final boolean callListener) {
        mIAItemAdapter.setDayModel(getCalendarDataList(date));
        mIAItemAdapter.notifyDataSetChanged();

        animateSelectedToPos(mIAItemAdapter.getPositionForDay(date), animate, callListener);
    }


    /**
     * get days item-model for month
     *
     * @param yearMonth year and month for days
     * @return item-models
     */
    private TreeMap<String, BaseItemModel> getCalendarDataList(String yearMonth) {
        Calendar calStartDate = CalendarUtility.getCalendarByYearMonth(yearMonth);
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.HOUR_OF_DAY, 0);
        calStartDate.set(Calendar.MINUTE, 0);
        calStartDate.set(Calendar.SECOND, 0);
        int dayOfWeek = calStartDate.get(Calendar.DAY_OF_WEEK);

        Calendar calItem = (Calendar) calStartDate.clone();
        calItem.add(Calendar.DAY_OF_WEEK,
                -(dayOfWeek == Calendar.SUNDAY ? 6 : dayOfWeek - Calendar.SUNDAY - 1));

        int curMonth = calStartDate.get(Calendar.MONTH);
        Calendar calToday = Calendar.getInstance();
        TreeMap<String, BaseItemModel> dayModelList = new TreeMap<>();
        for (int i = 0; i < CalendarUtility.ROW_COUNT * CalendarUtility.COLUMN_COUNT; i++) {
            BaseItemModel dayItem = mIAItemAdapter.getNewItem();
            dayItem.initModel(calItem, calToday);
            dayItem.setCurrentMonth(curMonth == calItem.get(Calendar.MONTH));

            dayModelList.put(dayItem.getDate(), dayItem);
            calItem.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dayModelList;
    }

    /**
     * animate selected view to calendar position
     *
     * @param position          position in calendar
     * @param animate           if true use animate with selected-view
     * @param callListener      if true will invoke day-change-listener
     */
    private void animateSelectedToPos(final int position, final boolean animate, final boolean callListener) {
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
                boolean bMothChanged = !CalendarUtility.getYearMonthStr(szDay).equals(getCurrentMonth());
                setCurrentDay(szDay);
                if (callListener && mDayChangeListener != null) {
                    if(bMothChanged)
                        mDayChangeListener.onMonthChanged(getCurrentMonth());

                    mDayChangeListener.onDayChanged(getCurrentDay());
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
     */
    private void animateToNewMonth(final FrgMonth oldMonthView, final String date, EDirection direction) {
        float oldTY = oldMonthView.getTranslationY();
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(this, "translationY", oldTY);
        oa1.setTarget(this);
        oa1.setDuration(600);

        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(this, "translationY",
                        oldTY - (direction == EDirection.UP ? 1 : -1) * this.getHeight());
        oa2.setTarget(oldMonthView);
        oa2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ConstraintLayout cl = (ConstraintLayout) getParent();
                cl.removeView(oldMonthView);

                animateSelectedToPos(mIAItemAdapter.getPositionForDay(date), true, true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        oa2.setDuration(600);

        oa1.start();
        oa2.start();
    }
    /// PRIVATE END
}
