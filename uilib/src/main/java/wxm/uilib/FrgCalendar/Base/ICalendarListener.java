package wxm.uilib.FrgCalendar.Base;

import android.view.View;

/**
 * @author WangXM
 * @version create：2018/5/2
 */
public interface ICalendarListener {
    /**
     * @param view      clicked the view(Calendar View Item)
     * @param day       the date has been selected with "yyyy-MM-dd" format
     */
    void onDayChanged(View view, String day);

    /**
     * when month of calendar view has changed. it include user manually fling CalendarView to change
     * month,also include when user scroll ListView then beyond the current month.it will change month
     * of CalendarView automatically.
     *
     * @param yearMonth the date has been selected is "yyyy-MM-dd" type
     */
    void onMonthChanged(String yearMonth);
}
