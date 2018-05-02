package wxm.uilib.FrgCalendar.CalendarItem;

/**
 * class for Calendar Item
 * Created by WangXM on 2017/05/02.
 */
@SuppressWarnings("WeakerAccess")
public class BaseItemModel {
    protected long timeMill;

    protected boolean isCurrentMonth;
    protected boolean isToday;
    protected boolean isHoliday;

    protected String dayNumber;
    protected EItemStatus status;

    public boolean isNotCurrentMonth() {
        return !isCurrentMonth;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        isCurrentMonth = currentMonth;
    }

    public String getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(String dayNumber) {
        this.dayNumber = dayNumber;
    }

    public long getTimeMill() {
        return timeMill;
    }

    public void setTimeMill(long timeMill) {
        this.timeMill = timeMill;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public boolean isHoliday() {
        return isHoliday;
    }

    public void setHoliday(boolean holiday) {
        isHoliday = holiday;
    }

    public EItemStatus getStatus() {
        return status;
    }

    public void setStatus(EItemStatus status) {
        this.status = status;
    }
}
