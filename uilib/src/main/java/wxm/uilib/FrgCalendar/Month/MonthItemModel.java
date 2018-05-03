package wxm.uilib.FrgCalendar.Month;

import java.util.Calendar;

import wxm.uilib.FrgCalendar.Base.CalendarUtility;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemModel;
import wxm.uilib.FrgCalendar.CalendarItem.EItemStatus;

/**
 * class for Calendar Item
 * Created by WangXM on 2017/05/02.
 */
@SuppressWarnings("WeakerAccess")
public class MonthItemModel extends BaseItemModel {
    protected boolean isCurrentMonth = false;

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public void setCurrentMonth(boolean isCurrentMonth) {
        this.isCurrentMonth = isCurrentMonth;
    }
}
