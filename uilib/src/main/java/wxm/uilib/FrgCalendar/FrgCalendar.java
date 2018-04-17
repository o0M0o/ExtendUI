package wxm.uilib.FrgCalendar;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.util.Calendar;
import java.util.TreeMap;

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
         * @param calendarView  current view
         * @param view          clicked the view(Calendar View Item)
         * @param time          the date has been selected with "yyyy-MM-dd" format
         * @param pos           position in GridView
         */
        void onDateSelected(FrgCalendar calendarView, View view, String time, int pos);
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
                    Calendar calendar = FrgCalendarHelper.getCalendarByYearMonthDay(mSZSelectedDate);
                    calendar.add(Calendar.MONTH, velocityY < 0 ? 1 : -1);
                    changeMonth(velocityY < 0 ? 1 : -1,
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
    protected GridView          mGVCalendar;
    protected View              mVWFloatingSelected;
    private TextView            mTVMonthTips;
    private GestureDetector     mGDDetector;

    private LinearLayout        mLLWeekBar;

    // event listener
    private FrgCalendarItemAdapter  mIAItemAdapter;

    private Class<?>                mECItemModel;
    private OnDateSelectedListener  mOnDateSelectedListener;

    // for touch
    private float mStartY;
    private float mDownY;
    private float mDiffY;
    private CalendarStatus status = CalendarStatus.LIST_CLOSE;

    // other
    private String      mSZCurrentMonth;
    private String      mSZSelectedDate;
    private boolean     mIsMonthChanging = false;

    private TreeMap<String, FrgCalendarItemModel>   mTMDayModel;

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
        mIAItemAdapter = ciAdapter;
        mECItemModel = (Class<?>) ((ParameterizedType) ciAdapter
                            .getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        initCalendarView();
    }

    public String getSelectedDate() {
        return mSZSelectedDate;
    }

    public void setOnSelectedListener(OnDateSelectedListener listener)   {
        mOnDateSelectedListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        SelectedRowColumn selectedRowColumn = getSelectedRowColumn();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDiffY = ev.getY();
                // when listView is close (calendarView is not shrink)
                if (status == CalendarStatus.LIST_CLOSE && mDiffY < mLLWeekBar.getBottom() + getBottom()) {
                    return super.dispatchTouchEvent(ev);
                }
                if (status == CalendarStatus.LIST_OPEN && mDiffY > mLLWeekBar.getBottom() + FrgCalendarHelper.mItemHeight) {
                    return super.dispatchTouchEvent(ev);
                }

                mStartY = ev.getRawY();
                mDownY = ev.getRawY();
                mGDDetector.onTouchEvent(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                float curY = ev.getRawY();
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

                mGDDetector.onTouchEvent(ev);

                setTranslationY(getTranslationY() + selectedRowColumn.row
                        * (curY - mStartY) / ((getHeight() / FrgCalendarHelper.mItemHeight) - 1));
                mStartY = curY;
                status = CalendarStatus.DRAGGING;
                return true;

            case MotionEvent.ACTION_UP:
                curY = ev.getRawY();
                if (status != CalendarStatus.DRAGGING) {
                    return super.dispatchTouchEvent(ev);
                }
                mGDDetector.onTouchEvent(ev);
                if (status == CalendarStatus.ANIMATING) {
                    return super.dispatchTouchEvent(ev);
                }
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
        FrgCalendarHelper.init(context);

        // init UI component
        View.inflate(context, R.layout.frg_calendar, this);
        mGVCalendar = (GridView)findViewById(R.id.gridview);
        mVWFloatingSelected = findViewById(R.id.selected_view);
        mTVMonthTips = (TextView)findViewById(R.id.floating_month_tip);
        mGDDetector = new GestureDetector(context, new FlingListener());

        // hidden month tips
        mTVMonthTips.setVisibility(View.GONE);
        mTVMonthTips.post(new Runnable() {
            @Override
            public void run() {
                LayoutParams lp = (LayoutParams) mTVMonthTips.getLayoutParams();
                lp.topMargin = 2 * FrgCalendarHelper.mItemHeight - mTVMonthTips.getHeight() / 2;
                mTVMonthTips.setLayoutParams(lp);
            }
        });

        // upset gridview
        mGVCalendar.setAdapter(new FrgCalendarItemAdapter(context));
        mGVCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                animateSelectedViewToPos(position);
                mSZSelectedDate = (String) mIAItemAdapter.getIndexToTimeMap().get(position);
                FrgCalendar h = FrgCalendar.this;
                if (mOnDateSelectedListener != null) {
                    mOnDateSelectedListener.onDateSelected(h, view, mSZSelectedDate, position);
                }
            }
        });

        // upset float selected view
        mVWFloatingSelected.setLayoutParams(
                new LayoutParams(FrgCalendarHelper.mItemWidth, FrgCalendarHelper.mItemHeight));

        initWeekBar();
        //if(isInEditMode())  {
            mIAItemAdapter = new FrgCalendarItemAdapter<FrgCalendarItemModel>(context);
            mECItemModel = FrgCalendarItemModel.class;
        //}

        initCalendarView();
    }

    /**
     * init week-bar
     */
    protected void initWeekBar() {
        mLLWeekBar = (LinearLayout) findViewById(R.id.week_bar);
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

    /**
     * animate selected view to calendar position
     * @param position      position in calendar
     */
    private void animateSelectedViewToPos(int position) {
        int left = FrgCalendarHelper.mItemWidth * (position % FrgCalendarHelper.COLUMN_COUNT);
        int top = FrgCalendarHelper.mItemHeight * (position / FrgCalendarHelper.COLUMN_COUNT);
        PropertyValuesHolder pvhX = PropertyValuesHolder
                            .ofFloat("X", mVWFloatingSelected.getX(), left);
        PropertyValuesHolder pvhY = PropertyValuesHolder
                            .ofFloat("Y", mVWFloatingSelected.getY(), top);
        ObjectAnimator.ofPropertyValuesHolder(mVWFloatingSelected, pvhX, pvhY)
                .setDuration(200).start();
    }

    /**
     * invoke to change month
     * @param offset        offset for month
     * @param date          new month
     * @param status        status for view
     */
    private void changeMonth(int offset, final String date, final CalendarStatus status) {
        mSZSelectedDate = date;
        mIsMonthChanging = true;

        FrgCalendar oldCalendarView = new FrgCalendar(getContext());
        oldCalendarView.setCalendarItemAdapter(mIAItemAdapter);
        ConstraintLayout container = (ConstraintLayout) this.getParent();
        container.addView(oldCalendarView);
        oldCalendarView.setTranslationY(getTranslationY());
        Calendar calendar = FrgCalendarHelper.getCalendarByYearMonthDay(mSZSelectedDate);
        mSZCurrentMonth = FrgCalendarHelper.YEAR_MONTH_FORMAT.format(calendar.getTime());
        TreeMap<String, FrgCalendarItemModel> tmItemModel = getCalendarDataList(mSZCurrentMonth);
        setDayModel(tmItemModel);
        setTranslationY(getTranslationY() + offset * this.getHeight());
        animateCalendarToNewMonth(oldCalendarView, offset, oldCalendarView.getTranslationY(), new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(String yearMonth) {
                if (status == CalendarStatus.LIST_OPEN) {
                    animateCalendarToDate(date);
                }
                animateSelectedViewToDate(date);
            }
        });

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

    private void setDayModel(TreeMap<String, FrgCalendarItemModel> dayModelTreeMap) {
        mTMDayModel = dayModelTreeMap;

        initViewStatus();
        mIAItemAdapter.setDayModelList(mTMDayModel);
        mIAItemAdapter.notifyDataSetChanged();
    }

    /**
     * init calendar item UI
     */
    private void initCalendarView() {
        Calendar calendar = Calendar.getInstance();
        long selectedTime = calendar.getTimeInMillis();
        mSZSelectedDate = FrgCalendarHelper.YEAR_MONTH_DAY_FORMAT.format(selectedTime);
        mSZCurrentMonth = FrgCalendarHelper.YEAR_MONTH_FORMAT.format(selectedTime);
        TreeMap<String, FrgCalendarItemModel> tmItemModel = getCalendarDataList(mSZCurrentMonth);

        for (FrgCalendarItemModel model : tmItemModel.values()) {
            if (FrgCalendarHelper.areEqualDays(model.getTimeMill(), selectedTime)) {
                model.setStatus(FrgCalendarItemModel.Status.SELECTED);
                break;
            }
        }

        setDayModel(tmItemModel);
        animateSelectedViewToPos(mIAItemAdapter.getIndexToTimeMap().indexOf(mSZSelectedDate));
    }

    /**
     * init view status
     */
    private void initViewStatus()   {
    }

    /**
     * get selected item row & col
     * @return      row & col for selected item
     */
    private SelectedRowColumn getSelectedRowColumn() {
        Calendar firstItemCalendar = FrgCalendarHelper.getCalendarByYearMonthDay(mTMDayModel.firstKey());
        Calendar selectedItemCalendar = FrgCalendarHelper.getCalendarByYearMonthDay(mSZSelectedDate);
        int diff = FrgCalendarHelper.getDiffDayByTimeStamp(firstItemCalendar.getTimeInMillis(), selectedItemCalendar.getTimeInMillis());
        SelectedRowColumn selectedRowColumn = new SelectedRowColumn();
        selectedRowColumn.column = (diff % FrgCalendarHelper.COLUMN_COUNT);
        selectedRowColumn.row = (diff / FrgCalendarHelper.COLUMN_COUNT);
        return selectedRowColumn;
    }

    /**
     * animate for change calendar month
     * @param oldCalendarView           view for old view
     * @param offset                    offset between new-month to old-month
     * @param translationY              Y position
     * @param monthChangeListener       month change listener
     */
    private void animateCalendarToNewMonth(final FrgCalendar oldCalendarView,
                                           int offset, float translationY, final OnMonthChangedListener monthChangeListener) {
        mTVMonthTips.setText(mSZCurrentMonth);
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(mTVMonthTips, "alpha", 0f, 1f, 0f);
        alpha.setDuration(1500);
        alpha.setInterpolator(new AccelerateInterpolator());
        alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTVMonthTips.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTVMonthTips.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        alpha.start();

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(this, "translationY", translationY);
        objectAnimator1.setTarget(this);
        objectAnimator1.setDuration(800).start();

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(this, "translationY", oldCalendarView.getTranslationY() - offset * this.getHeight());
        objectAnimator2.setTarget(oldCalendarView);
        objectAnimator2.setDuration(800).start();
        objectAnimator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ConstraintLayout container = (ConstraintLayout) FrgCalendar.this.getParent();
                if (null != container) {
                    container.removeView(oldCalendarView);
                }
                mIsMonthChanging = false;
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

    /**
     * animate for change calendar day
     * @param date      new day
     */
    private void animateCalendarToDate(String date) {
        int position = mIAItemAdapter.getIndexToTimeMap().indexOf(date);
        int row = position / FrgCalendarHelper.COLUMN_COUNT;
        ObjectAnimator objectAnimator2 = ObjectAnimator
                .ofFloat(this, "translationY",
                        -(getHeight() * row / ((getHeight() / FrgCalendarHelper.mItemHeight))));
        objectAnimator2.setTarget(this);
        objectAnimator2.setDuration(300).start();
    }

    private void animateSelectedViewToDate(String date) {
        mSZSelectedDate = date;
        int position = mIAItemAdapter.getIndexToTimeMap().indexOf(date);
        animateSelectedViewToPos(position);
    }


    private void animationToTop(SelectedRowColumn selectedRowColumn) {
        status = CalendarStatus.ANIMATING;

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(this, "translationY",
                -(getHeight() * selectedRowColumn.row / ((getHeight() / FrgCalendarHelper.mItemHeight))));
        objectAnimator2.setTarget(this);
        objectAnimator2.setDuration(300).start();

        objectAnimator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                status = CalendarStatus.LIST_OPEN;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private void animationToBottom() {
        status = CalendarStatus.ANIMATING;

        ObjectAnimator objectAnimator2 =
                ObjectAnimator.ofFloat(this, "translationY", 0);
        objectAnimator2.setTarget(0);
        objectAnimator2.setDuration(300).start();

        objectAnimator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                status = CalendarStatus.LIST_CLOSE;
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
