package wxm.extendui.ACCalendar;


import wxm.uilib.FrgCalendar.CalendarItem.BaseItemModel;
import wxm.uilib.FrgCalendar.Month.MonthItemModel;

/**
 * for extended
 * Created by kelin on 16-7-20.
 */
public class CalendarItemModel extends MonthItemModel {
    private int mRecordCount;

    public int getRecordCount() {
                              return mRecordCount;
                                                  }
    public void setRecordCount(int mRecordCount) {
                                               this.mRecordCount = mRecordCount;
                                                                                }
}

