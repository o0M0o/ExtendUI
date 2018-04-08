package wxm.uilib.SimpleCalendar;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import wxm.uilib.R;

/**
 * for calendar
 * Created by ookoo on 2017/07/06.
 */
public class CalendarListView extends FrameLayout {
    private static final int WEEK_ITEM_TEXT_SIZE = 12;
    private static final int RED_FF725F = 0xffff725f;

    enum Status {
        // when ListView been push to Top,the status is LIST_OPEN.
        LIST_OPEN,
        // when ListView stay original position ,the status is LIST_CLOSE.
        LIST_CLOSE,
        // when VIEW is dragging.
        DRAGGING,
        //when dragging end,the both CalendarView and ListView will animate to specify position.
        ANIMATING,
    }

    public interface OnCalendarViewItemClickListener {
        /**
         * <p>when item of Calendar View was clicked will be trigger. </p>
         *
         * @param View         the view(Calendar View Item) that was clicked.
         * @param selectedDate the date has been selected is "yyyy-MM-dd" type
         */
        void onDateSelected(View View, String selectedDate);
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

    protected FrameLayout mFLContent;
    protected CalendarView calendarView;
    protected LinearLayout weekBar;
    private GestureDetector gestureDetector;
    private float startY;
    private float downY;
    private float dy;
    private Status status = Status.LIST_CLOSE;

    protected BaseCalendarItemAdapter calendarItemAdapter;

    private OnCalendarViewItemClickListener onCalendarViewItemClickListener;

    public CalendarListView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        CalendarHelper.init(context);
        gestureDetector = new GestureDetector(context, new FlingListener());
        LayoutInflater.from(context).inflate(R.layout.calendar_listview, this);
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.calendar_view_container);
        calendarView = new CalendarView(context);
        frameLayout.addView(calendarView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mFLContent = (FrameLayout) findViewById(R.id.fl_holder);
        initListener();
        initWeekBar();
    }


    /**
     * @param calendarItemAdapter adapter is for CalendarView
     */
    public void setCalendarListViewAdapter(BaseCalendarItemAdapter calendarItemAdapter) {
        this.calendarItemAdapter = calendarItemAdapter;
        this.calendarView.setCalendarItemAdapter(calendarItemAdapter);
    }

    /**
     * get selected date
     * example : 2017-01-28
     *
     * @return date
     */
    public String getSelectedDate() {
        return calendarView.getSelectedDate();
    }

    public void setOnMonthChangedListener(OnMonthChangedListener onMonthChangedListener) {
        calendarView.setOnMonthChangedListener(onMonthChangedListener);
    }


    public void setOnCalendarViewItemClickListener(OnCalendarViewItemClickListener onCalendarViewItemClickListener) {
        this.onCalendarViewItemClickListener = onCalendarViewItemClickListener;
    }

    public void setWeekBar(LinearLayout weekBar) {
        this.weekBar = weekBar;
    }

    public LinearLayout getWeekBar() {
        return weekBar;
    }

    public void changeMonth(String month) {
        String cur_month = calendarView.getCurrentMonth();
        changeMonth(CalendarHelper.getDiffMonthByYearMonth(cur_month, month));
    }

    protected void changeMonth(int diffMonth) {
        String currentDate = calendarView.getCurrentMonth() + "-01";
        if (diffMonth != 0) {
            calendarView.changeMonth(diffMonth, currentDate, status);
        } else {
            if (status == Status.LIST_OPEN) {
                calendarView.animateCalendarViewToDate(currentDate);
            }
            calendarView.animateSelectedViewToDate(currentDate);
        }
    }

    protected void initListener() {
        calendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(CalendarView calendarView, View view, String time, int pos) {
                if (onCalendarViewItemClickListener != null) {
                    onCalendarViewItemClickListener.onDateSelected(view, time);
                }
            }
        });
    }

    protected void initWeekBar() {
        weekBar = (LinearLayout) findViewById(R.id.week_bar);
        int txt_black = getResources().getColor(android.R.color.black);

        String[] weeks = getResources().getStringArray(R.array.week);
        for (int i = 0; i < weeks.length; i++) {
            String week = weeks[i];
            TextView textView = new TextView(this.getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            textView.setText(week);
            textView.setTextSize(WEEK_ITEM_TEXT_SIZE);
            textView.setTextColor(i == weeks.length - 1 || i == weeks.length - 2
                    ?  RED_FF725F : txt_black);
            textView.setGravity(Gravity.CENTER);

            weekBar.addView(textView);
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        CalendarView.SelectedRowColumn selectedRowColumn = calendarView.getSelectedRowColumn();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                dy = ev.getY();
                // when listView is close (calendarView is not shrink)
                if (status == Status.LIST_CLOSE && dy < weekBar.getBottom() + calendarView.getBottom()) {
                    return super.dispatchTouchEvent(ev);
                }
                if (status == Status.LIST_OPEN && dy > weekBar.getBottom() + CalendarView.mItemHeight) {
                    return super.dispatchTouchEvent(ev);
                }

                startY = ev.getRawY();
                downY = ev.getRawY();
                gestureDetector.onTouchEvent(ev);

                break;

            case MotionEvent.ACTION_MOVE:
                float curY = ev.getRawY();
                if (status == Status.LIST_CLOSE && curY > startY) {
                    return super.dispatchTouchEvent(ev);
                }

                if (status == Status.LIST_OPEN && curY < startY) {
                    if (dy > weekBar.getBottom() + CalendarView.mItemHeight) {
                        return super.dispatchTouchEvent(ev);
                    } else {
                        return true;
                    }
                }

                if (status == Status.LIST_CLOSE && dy < weekBar.getBottom() + calendarView.getBottom()) {
                    return super.dispatchTouchEvent(ev);
                }
                if (status == Status.LIST_OPEN && dy > weekBar.getBottom() + CalendarView.mItemHeight) {
                    return super.dispatchTouchEvent(ev);
                }

                gestureDetector.onTouchEvent(ev);

                calendarView.setTranslationY(calendarView.getTranslationY() + selectedRowColumn.row
                        * (curY - startY) / ((calendarView.getHeight() / CalendarView.mItemHeight) - 1));
                startY = curY;
                status = Status.DRAGGING;
                return true;

            case MotionEvent.ACTION_UP:
                curY = ev.getRawY();
                if (status != Status.DRAGGING) {
                    return super.dispatchTouchEvent(ev);
                }
                gestureDetector.onTouchEvent(ev);
                if (status == Status.ANIMATING) {
                    return super.dispatchTouchEvent(ev);
                }
                if (curY < downY) {
                    if (Math.abs((curY - downY)) > calendarView.getHeight() / 2) {
                        animationToTop(selectedRowColumn);
                    } else {
                        animationToBottom();
                    }
                } else {
                    if (Math.abs((curY - downY)) < calendarView.getHeight() / 2) {
                        animationToTop(selectedRowColumn);
                    } else {
                        animationToBottom();
                    }

                }
        }

        return super.dispatchTouchEvent(ev);
    }

    private class FlingListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (status != Status.ANIMATING) {
                if (velocityY < 0) {
                    animationToTop(calendarView.getSelectedRowColumn());
                } else {
                    animationToBottom();
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private void animationToTop(CalendarView.SelectedRowColumn selectedRowColumn) {
        status = Status.ANIMATING;

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(this, "translationY", -(calendarView.getHeight() * selectedRowColumn.row / ((calendarView.getHeight() / CalendarView.mItemHeight))));
        objectAnimator2.setTarget(calendarView);
        objectAnimator2.setDuration(300).start();

        objectAnimator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                status = Status.LIST_OPEN;
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
        status = Status.ANIMATING;

        ObjectAnimator objectAnimator2 =
                ObjectAnimator.ofFloat(this, "translationY", 0);
        objectAnimator2.setTarget(calendarView);
        objectAnimator2.setDuration(300).start();

        objectAnimator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                status = Status.LIST_CLOSE;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }


}
