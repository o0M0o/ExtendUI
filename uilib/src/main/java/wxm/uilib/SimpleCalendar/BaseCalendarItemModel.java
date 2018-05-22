package wxm.uilib.SimpleCalendar;

/**
 * class for Calendar Item
 * Created by WangXM on 2017/07/07
 */
public class BaseCalendarItemModel {
    /**
     * if is not the current month
     * this item's background should be gloomy，because it is not belong to active month.
     */
    private boolean isCurrentMonth;

    // calendar item would show this number to show what date it is.
    private String dayNumber;
    private long timeMill;
    private boolean isToday;
    private boolean isHoliday;
    private Status status;

    public enum Status {
        NONE,
        DISABLE,
        SELECTED,
    }

    public boolean isNotCurrentMonth() {
        return !isCurrentMonth;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
