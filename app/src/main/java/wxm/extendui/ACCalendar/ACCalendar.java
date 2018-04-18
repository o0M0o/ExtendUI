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

        Toast tt = Toast.makeText(getApplicationContext(), "selected : ", Toast.LENGTH_SHORT);
        mHGVDays.setOnSelectedListener((view, time, pos) ->   {
            //tt.cancel();
            tt.setText("selected : " + time);
            tt.setDuration(Toast.LENGTH_SHORT);
            tt.show();
        });

        Toast tt1 = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        mHGVDays.setOnMonthChangeListener(yearMonth -> {
            tt1.setText(yearMonth);
            tt1.setDuration(Toast.LENGTH_SHORT);
            tt1.show();
        });
    }
}
