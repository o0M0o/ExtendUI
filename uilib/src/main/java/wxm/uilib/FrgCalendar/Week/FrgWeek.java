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
import wxm.uilib.FrgCalendar.Base.EDirection;
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

    private BaseItemAdapter mIAItemAdapter;

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
        mIAItemAdapter = ciAdapter;
        mGVCalendar.setAdapter(mIAItemAdapter);

        mVWFloatingSelected.setLayoutParams(
                new LayoutParams(mIAItemAdapter.getItemWidth(), mIAItemAdapter.getItemHeight()));
    }

    @Override
    public void setSelectedDay(final String date) {
        doSetSelectedDay(date, false, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void changePage(EDirection direction, final String date) {
        if(CalendarUtility.isInOneWeek(date, getCurrentDay()))  {
            animateSelectedToPos(mIAItemAdapter.getPositionForDay(date), true, true);
            return;
        }

        final ConstraintLayout clHolder = (ConstraintLayout) getParent();
        int offset = direction == EDirection.LEFT ? 1 : -1;
        float translationX = getTranslationX();

        // old view
        final FrgWeek oldView = copySelf();
        clHolder.addView(oldView);
        oldView.setTranslationX(translationX);

        // new view
        mIAItemAdapter.setDayModel(getCalendarDataList(date));
        mIAItemAdapter.notifyDataSetChanged();
        setTranslationX(translationX + offset * this.getWidth());

        // animate
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(this, "translationX", translationX);
        oa1.setTarget(this);
        oa1.setDuration(600);

        ObjectAnimator oa2 = ObjectAnimator.ofFloat(this,
                "translationX", translationX - offset * this.getWidth());
        oa2.setTarget(oldView);
        oa2.setDuration(600);
        oa2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                clHolder.removeView(oldView);
                animateSelectedToPos(mIAItemAdapter.getPositionForDay(date), true, true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        oa1.start();
        oa2.start();
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
                animateSelectedToPos(position, true, true);
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
    /// PRIVATE END
}
