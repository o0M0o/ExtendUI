package wxm.extendui.SimpleCalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.extendui.R;
import wxm.uilib.SimpleCalendar.CalendarListView;

/**
 * 展示SimpleCalendar
 */
public class ACSimpleCalendar extends AppCompatActivity {

    @BindView(R.id.calendar_listview)
    CalendarListView mHGVDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_simple_calendar);

        ButterKnife.bind(this);
        initUI();
    }

    /**
     * 初始化UI控件
     */
    private void initUI()   {
        CalendarShowItemAdapter mCSIAdapter = new CalendarShowItemAdapter(this);
        mHGVDays.setCalendarListViewAdapter(mCSIAdapter);

        mHGVDays.setOnMonthChangedListener(yearMonth -> {
            Log.d("SimpleCalendar",
                    "OnMonthChangedListener, yearMonth = " + yearMonth
                            + " selectedDate = " + mHGVDays.getSelectedDate());

        });

        mHGVDays.setOnCalendarViewItemClickListener((View, selectedDate) -> {
            Log.d("SimpleCalendar",
                    "OnCalendarViewItemClick, para-selectedDate = " + selectedDate
                        + " selectedDate = " + mHGVDays.getSelectedDate());
        });
    }
}
