package wxm.uilib.FrgCalendar.Base;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import wxm.uilib.FrgCalendar.CalendarItem.BaseItemAdapter;

/**
 * @author WangXM
 * @version create：2018/5/3
 */
public abstract class FrgBaseCalendar extends ConstraintLayout {
    protected AttributeSet mASSet;

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
     * get current selected day
     *
     * @return selected day
     */
    public abstract String getCurrentDay();
}
