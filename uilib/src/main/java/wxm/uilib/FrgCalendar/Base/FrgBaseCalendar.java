package wxm.uilib.FrgCalendar.Base;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import wxm.androidutil.util.UtilFun;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemAdapter;

/**
 * @author WangXM
 * @version create：2018/5/3
 */
public abstract class FrgBaseCalendar extends ConstraintLayout {
    protected AttributeSet mASSet;
    private String mSZSelectedDate;

    // listener
    protected ICalendarListener mDayChangeListener;

    public FrgBaseCalendar(Context context) {
        super(context);
        mASSet = null;
        initSelf(context);
    }

    public FrgBaseCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mASSet = attrs;
        initSelf(context);
    }

    public FrgBaseCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mASSet = attrs;
        initSelf(context);
    }

    /**
     * set day change listener
     *
     * @param listener listener
     */
    public void setDayChangeListener(ICalendarListener listener) {
        mDayChangeListener = listener;
    }

    /**
     * init self
     *
     * @param context context
     */
    protected abstract void initSelf(Context context);

    /**
     * copy self view for animate
     *
     * @return self copy
     */
    protected abstract FrgBaseCalendar copySelf();

    /**
     * set adapter for day-ui
     *
     * @param ciAdapter adapter
     */
    public abstract void setCalendarItemAdapter(BaseItemAdapter ciAdapter);

    /**
     * set calendar selected day without animate
     *
     * @param date day for selected, example : "2018-05-01"
     */
    public abstract void setSelectedDay(final String date);

    /**
     * set calendar page with selected day & animate
     *
     * @param date day for selected, example : "2018-05-01"
     */
    public abstract void changePage(final String date);

    /**
     * set current selected day
     * @param szDay     current day, example : "2018-05-04"
     */
    protected void setCurrentDay(String szDay)  {
        mSZSelectedDate = szDay;
    }

    /**
     * get current selected day
     *
     * @return selected day
     */
    public String getCurrentDay()  {
        return mSZSelectedDate;
    }

    /**
     * get current month
     * @return      current month, example : "2018-05"
     */
    public String getCurrentMonth() {
        return UtilFun.StringIsNullOrEmpty(mSZSelectedDate) ? null
                : CalendarUtility.getYearMonthStr(mSZSelectedDate);
    }

    /**
     * get current year
     * @return      current year, example : "2018"
     */
    public String getCurrentYear() {
        return UtilFun.StringIsNullOrEmpty(mSZSelectedDate) ? null
                : CalendarUtility.getYearStr(mSZSelectedDate);
    }
}
