package wxm.extendui.SimpleCalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.extendui.R;
import wxm.uilib.SimpleCalendar.CalendarListView;
import wxm.uilib.TuneWheel.TuneWheel;

/**
 * 展示SimpleCalendar
 */
public class ACSimpleCalendar extends AppCompatActivity {

    @BindView(R.id.calendar_listview)
    CalendarListView mHGVDays;

    // for data
    private CalendarShowItemAdapter mCSIAdapter;

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
        mCSIAdapter = new CalendarShowItemAdapter(this);
        mHGVDays.setCalendarListViewAdapter(mCSIAdapter);

        mHGVDays.setOnMonthChangedListener(yearMonth -> {
            Log.d("SimpleCalendar", "OnMonthChangedListener, yearMonth = " + yearMonth);

        });

        mHGVDays.setOnCalendarViewItemClickListener((View, selectedDate) -> {
            Log.d("SimpleCalendar", "OnCalendarViewItemClick, selectedDate = " + selectedDate);
        });
    }
}
