package wxm.uilib.FrgCalendar;

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
final class FrgCalendarHelper {
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

    static final SimpleDateFormat YEAR_MONTH_FORMAT =
            new SimpleDateFormat("yyyy-MM", Locale.CHINA);
    static final SimpleDateFormat YEAR_MONTH_DAY_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    private FrgCalendarHelper() {
    }

    static void init(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width   = metrics.widthPixels;
        height  = metrics.heightPixels;

        mItemWidth  = width / COLUMN_COUNT;
        mItemHeight = mItemWidth * 3 / 4;
    }

    private static Calendar getCalendarByYearMonth(String yearMonth) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(FrgCalendarHelper.YEAR_MONTH_FORMAT.parse(yearMonth).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    static Calendar getCalendarByYearMonthDay(String yearMonthDay) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(FrgCalendarHelper.YEAR_MONTH_DAY_FORMAT.parse(yearMonthDay).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    static boolean areEqualDays(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        c2.setTimeInMillis(time2);

        return areEqualDays(c1, c2);
    }


    static boolean areEqualDays(Calendar c1, Calendar c2)  {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    static boolean areEqualMonth(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                    && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH));
    }

    static int getDiffDayByTimeStamp(long startTimeStamp, long endTimeStamp) {
        return Math.round((endTimeStamp - startTimeStamp) * 1.0f / ONE_DAY_TIME);
    }

    static int getDiffMonthByYearMonth(String startTime, String endTime) {
        Calendar startCalendar = getCalendarByYearMonth(startTime);
        Calendar endCalendar = getCalendarByYearMonth(endTime);
        return (12 * (endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR))) +
                endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    }
}
