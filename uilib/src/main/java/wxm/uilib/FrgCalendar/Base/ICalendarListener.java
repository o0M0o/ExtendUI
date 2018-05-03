package wxm.uilib.FrgCalendar.Base;

/**
 * @author WangXM
 * @version create：2018/5/2
 */
public interface ICalendarListener {
    /**
     * @param day           day use "yyyy-MM-dd" format
     */
    void onDayChanged(String day);

    /**
     * when month of calendar view has changed.
     * it include user manually fling CalendarView to change month,
     * also include when user scroll ListView then beyond the current month.
     * it will change month of CalendarView automatically.
     *
     * @param yearMonth     month use "yyyy-MM" format
     */
    void onMonthChanged(String yearMonth);
}
