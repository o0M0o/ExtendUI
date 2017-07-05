package wxm.uilib.SimpleCalendar;

import android.content.Context;
import android.util.DisplayMetrics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TreeMap;

/**
 * 日历辅助类
 * Created by kelin on 16-7-20.
 */
public final class CalendarHelper {
    private static final int ROW_ITEM_COUNT = 7;
    private static final int COLUMN_ITEM_COUNT = 6;
    public static final long ONE_DAY_TIME = 24 * 3600 * 1000L;

    public static float density;
    public static int width;
    public static int height;

    public static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy-MM", Locale.CHINA);
    public static final SimpleDateFormat YEAR_MONTH_DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);

    private CalendarHelper() {
    }

    public static void init(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        density = metrics.density;
    }


    public static int dp2px(int dp) {
        return (int) (dp * density);
    }


    public static <T extends BaseCalendarItemModel> TreeMap<String, T> getDefaultCalendarDataListByMonthOffset(String yearMonth, Class<T> clazz) {
        int calendarViewRow = COLUMN_ITEM_COUNT;
        int calendarViewColumn = ROW_ITEM_COUNT;

        Calendar calToday = Calendar.getInstance();
        Calendar calStartDate = Calendar.getInstance();
        calToday.setFirstDayOfWeek(Calendar.SUNDAY);
        calStartDate.setFirstDayOfWeek(Calendar.SUNDAY);
        long time = 0;
        try {
            time = YEAR_MONTH_FORMAT.parse(yearMonth).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calStartDate.setTimeInMillis(time);
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.HOUR_OF_DAY, 0);
        calStartDate.set(Calendar.MINUTE, 0);
        calStartDate.set(Calendar.SECOND, 0);
        Calendar activeCalendar = (Calendar) calStartDate.clone();
        calStartDate.add(Calendar.DAY_OF_WEEK, -(calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY));
        Calendar calEndDate = (Calendar) calStartDate.clone();
        calEndDate.add(Calendar.DAY_OF_MONTH, calendarViewRow * calendarViewColumn - 1);
        Calendar calCalendar = Calendar.getInstance();
        calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());
        TreeMap<String, T> dayModelList = new TreeMap<>();
        if (calEndDate.get(Calendar.DAY_OF_MONTH) >= calendarViewColumn) {
            calendarViewRow--;
        }
        for (int i = 0; i < calendarViewRow * calendarViewColumn; i++) {
            T baseCalendarItemModel = null;
            try {
                baseCalendarItemModel = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            baseCalendarItemModel.setCurrentMonth(areEqualMonth(calCalendar, activeCalendar));
            baseCalendarItemModel.setToday(areEqualDays(calCalendar.getTimeInMillis(), calToday.getTimeInMillis()));
            baseCalendarItemModel.setTimeMill(calCalendar.getTimeInMillis());
            baseCalendarItemModel.setHoliday(Calendar.SUNDAY == calCalendar.get(Calendar.DAY_OF_WEEK) ||
                    Calendar.SATURDAY == calCalendar.get(Calendar.DAY_OF_WEEK));
            baseCalendarItemModel.setDayNumber(String.valueOf(calCalendar.get(Calendar.DAY_OF_MONTH)));
            calCalendar.add(Calendar.DAY_OF_MONTH, 1);
            dayModelList.put(YEAR_MONTH_DAY_FORMAT.format(
                    baseCalendarItemModel.getTimeMill()), (T) baseCalendarItemModel);
        }
        return dayModelList;

    }

    public static Calendar getCalendarByYearMonth(String yearMonth) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(CalendarHelper.YEAR_MONTH_FORMAT.parse(yearMonth).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static Calendar getCalendarByYearMonthDay(String yearMonthDay) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(CalendarHelper.YEAR_MONTH_DAY_FORMAT.parse(yearMonthDay).getTime());
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

        return areEqualMonth(c1, c2)
                    && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean areEqualMonth(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                    && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH));
    }

    public static int getDiffDayByTimeStamp(long startTimeStamp, long endTimeStamp) {
        return Math.round((endTimeStamp - startTimeStamp) * 1.0f / ONE_DAY_TIME);
    }

    public static int getDiffMonthByTime(String startTime, String endTime) {
        Calendar startCalendar = getCalendarByYearMonthDay(startTime);
        Calendar endCalendar = getCalendarByYearMonthDay(endTime);
        return (12 * (endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR))) +
                endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    }

    public static int getDiffMonthByYearMonth(String startTime, String endTime) {
        Calendar startCalendar = getCalendarByYearMonth(startTime);
        Calendar endCalendar = getCalendarByYearMonth(endTime);
        return (12 * (endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR))) +
                endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    }
}
