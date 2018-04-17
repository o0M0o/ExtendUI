package wxm.extendui.ACCalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.extendui.R;
import wxm.uilib.FrgCalendar.FrgCalendar;

/**
 * 展示SimpleCalendar
 */
public class ACCalendar extends AppCompatActivity {

    @BindView(R.id.frgCalendar)
    FrgCalendar mHGVDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_frg_calendar);

        ButterKnife.bind(this);
        initUI();
    }

    /**
     * 初始化UI控件
     */
    private void initUI()   {
        CalendarShowItemAdapter mCSIAdapter = new CalendarShowItemAdapter(this);
        mHGVDays.setCalendarItemAdapter(mCSIAdapter);

        mHGVDays.setOnSelectedListener((calendarView, view, time, pos) ->
                Toast.makeText(getApplicationContext(),
                "selected : " + time,
                Toast.LENGTH_SHORT).show());
    }
}
