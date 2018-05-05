package wxm.uilib.FrgCalendar.Base;

/**
 * @author WangXM
 * @version create：2018/5/5
 */
public enum ECalendarMode {
    WEEK,
    MONTH;

    public boolean isWeekMode() {
        return this == WEEK;
    }

    public boolean isMonthMode() {
        return this == MONTH;
    }
}
