package wxm.uilib.FrgCalendar.CalendarItem;

import java.util.Calendar;
import wxm.uilib.FrgCalendar.Base.CalendarUtility;

/**
 * class for Calendar Item
 * Created by WangXM on 2017/05/02.
 */
@SuppressWarnings("WeakerAccess")
public class BaseItemModel {
    protected long timeMill;

    protected boolean isToday;
    protected boolean isHoliday;
    protected boolean isCurrentMonth = false;

    // day-in-month, range [1, 31]
    protected String szDayNumber;
    // date as string, example : '2018-05-01'
    protected String szDate;

    protected EItemStatus status;

    public BaseItemModel()  {
    }

    /**
     * init model data
     * invoke this before use
     * @param self      for self
     * @param today     today for init
     */
    public void initModel(Calendar self, Calendar today)    {
        timeMill = self.getTimeInMillis();
        szDate = CalendarUtility.getYearMonthDayStr(self);
        szDayNumber = String.valueOf(self.get(Calendar.DAY_OF_MONTH));

        isToday = CalendarUtility.isEqualDays(self, today);
        isHoliday = CalendarUtility.isHoliday(self);
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public void setCurrentMonth(boolean isCurrentMonth) {
        this.isCurrentMonth = isCurrentMonth;
    }

    public String getDayNumber() {
        return szDayNumber;
    }

    public long getTimeMill() {
        return timeMill;
    }

    public String getDate() {
        return szDate;
    }

    public boolean isToday() {
        return isToday;
    }

    public boolean isHoliday() {
        return isHoliday;
    }

    public EItemStatus getStatus() {
        return status;
    }

    public void setStatus(EItemStatus status) {
        this.status = status;
    }
}
