package wxm.uilib.FrgCalendar.Base;

import android.content.Context;
import android.util.DisplayMetrics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * helper class for calender
 * Created by kelin on 16-7-20.
 */
public final class CalendarUtility {
    // calendar is 7 column * 6 row
    public static final int ROW_COUNT     = 6;
    public static final int COLUMN_COUNT  = 7;

    // pixel size for frg
    public static int width;
    public static int height;

    // pixel size for calendar item
    public static int mItemWidth;
    public static int mItemHeight;

    private static final long ONE_DAY_TIME = 24 * 3600 * 1000L;

    private static final SimpleDateFormat YEAR_MONTH_FORMAT =
            new SimpleDateFormat("yyyy-MM", Locale.CHINA);
    private static final SimpleDateFormat YEAR_MONTH_DAY_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    public static void init(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width   = metrics.widthPixels;
        height  = metrics.heightPixels;

        mItemWidth  = width / COLUMN_COUNT;
        mItemHeight = mItemWidth * 3 / 4;
    }

    /**
     * get string from calendar
     * @param cDay      calendar for string
     * @return          example : "2018-05-01"
     */
    public static String getYearMonthDayStr(Calendar cDay)  {
        return YEAR_MONTH_DAY_FORMAT.format(cDay.getTimeInMillis());
    }

    /**
     * get string from timestamp value
     * @param cDay      timestamp value for string
     * @return          example : "2018-05-01"
     */
    public static String getYearMonthDayStr(long cDay)  {
        return YEAR_MONTH_DAY_FORMAT.format(cDay);
    }

    /**
     * get string from calendar
     * @param cDay      calendar for string
     * @return          example : "2018-05"
     */
    public static String getYearMonthStr(Calendar cDay)  {
        return YEAR_MONTH_FORMAT.format(cDay.getTime());
    }

    /**
     * check whether in holiday
     * @param cDay      calendar for day
     * @return          true if holiday
     */
    public static boolean isHoliday(Calendar cDay)  {
        int day = cDay.get(Calendar.DAY_OF_WEEK);
        return Calendar.SUNDAY == day || Calendar.SATURDAY == day;
    }

    public static Calendar getCalendarByYearMonth(String yearMonth) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(YEAR_MONTH_FORMAT.parse(yearMonth).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static Calendar getCalendarByYearMonthDay(String yearMonthDay) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(CalendarUtility.YEAR_MONTH_DAY_FORMAT.parse(yearMonthDay).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static boolean areEqualDays(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        c2.setTimeInMillis(time2);

        return areEqualDays(c1, c2);
    }


    public static boolean areEqualDays(Calendar c1, Calendar c2)  {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean areEqualMonth(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                    && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH));
    }

    public static int getDiffDayByTimeStamp(long startTimeStamp, long endTimeStamp) {
        return Math.round((endTimeStamp - startTimeStamp) * 1.0f / ONE_DAY_TIME);
    }

    public static int getDiffMonthByYearMonth(String startTime, String endTime) {
        Calendar startCalendar = getCalendarByYearMonth(startTime);
        Calendar endCalendar = getCalendarByYearMonth(endTime);
        return (12 * (endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR))) +
                endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    }

    /**
     * get day count in year-month
     * @param year      for year
     * @param month     for month, range : [0-11]
     * @return          day count, range is [28-31] or -1 for error
     */
    public static int getMonthDays(int year, int month) {
        month++;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }
}
