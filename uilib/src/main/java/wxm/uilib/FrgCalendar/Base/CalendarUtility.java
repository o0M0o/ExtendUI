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
    private static boolean isInit = false;

    // calendar is 7 column * 6 row
    public static final int ROW_COUNT     = 6;
    public static final int COLUMN_COUNT  = 7;

    // pixel size for frg
    public static int width;
    public static int height;

    // pixel size for calendar item
    public static int mItemWidth;
    public static int mItemHeight;
    public static int mItemBigHeight;


    private static final SimpleDateFormat YEAR_MONTH =
            new SimpleDateFormat("yyyy-MM", Locale.CHINA);
    private static final SimpleDateFormat YEAR_MONTH_DAY =
            new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    /**
     * invoke this before use this utility
     * @param context       for activity or fragment
     */
    public static void init(Context context) {
        if(!isInit) {
            isInit = true;

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            width = metrics.widthPixels;
            height = metrics.heightPixels;

            mItemWidth = width / COLUMN_COUNT;
            mItemHeight = mItemWidth * 3 / 4;
            mItemBigHeight = mItemWidth;
        }
    }

    /**
     * get year-month part from year-month[-day] string
     * @param szDay         example : "2018-05-01"
     * @return              example : "2018-05"
     */
    public static String getYearMonthStr(String szDay)  {
        return szDay.substring(0, 7);
    }

    /**
     * get year part from year[-month-day] string
     * @param szDay         example : "2018-05-01"
     * @return              example : "2018"
     */
    public static String getYearStr(String szDay)  {
        return szDay.substring(0, 4);
    }

    /**
     * get string from calendar
     * @param cDay      calendar for string
     * @return          example : "2018-05-01"
     */
    public static String getYearMonthDayStr(Calendar cDay)  {
        return YEAR_MONTH_DAY.format(cDay.getTimeInMillis());
    }

    /**
     * get string from timestamp value
     * @param cDay      timestamp value for string
     * @return          example : "2018-05-01"
     */
    public static String getYearMonthDayStr(long cDay)  {
        return YEAR_MONTH_DAY.format(cDay);
    }

    /**
     * get string from calendar
     * @param cDay      calendar for string
     * @return          example : "2018-05"
     */
    public static String getYearMonthStr(Calendar cDay)  {
        return YEAR_MONTH.format(cDay.getTime());
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

    /**
     * use year-moth string get calendar
     * @param yearMonth     example: "2018-05"
     * @return              calendar
     */
    public static Calendar getCalendarByYearMonth(String yearMonth) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(YEAR_MONTH.parse(yearMonth).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * use year-year-day string get calendar
     * @param yearMonthDay      example: "2018-05-04"
     * @return                  calendar
     */
    public static Calendar getCalendarByYearMonthDay(String yearMonthDay) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(CalendarUtility.YEAR_MONTH_DAY.parse(yearMonthDay).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * test in one day
     * @param time1     time 1
     * @param time2     time 2
     * @return          true if in one day
     */
    public static boolean isEqualDays(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        c2.setTimeInMillis(time2);

        return isEqualDays(c1, c2);
    }

    /**
     * test in one day
     * @param c1        time 1
     * @param c2        time 2
     * @return          true if in one day
     */
    public static boolean isEqualDays(Calendar c1, Calendar c2)  {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * test in one month
     * @param c1        time 1
     * @param c2        time 2
     * @return          true if in one month
     */
    public static boolean isEqualMonth(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                    && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH));
    }

    /**
     * test in one week
     * day1 & day2 example : "2018-05-05"
     * @param day1      day 1
     * @param day2      day 2
     * @return          true if in one week
     */
    public static boolean isInOneWeek(String day1, String day2) {
        if(null == day1 || null == day2)
            return false;

        Calendar c1 = getCalendarByYearMonthDay(day1);
        Calendar c2 = getCalendarByYearMonthDay(day2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR);
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
