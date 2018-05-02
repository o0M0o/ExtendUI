package wxm.extendui.ACCalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.extendui.R;
import wxm.uilib.FrgCalendar.Base.ICalendarListener;
import wxm.uilib.FrgCalendar.FrgCalendar;

/**
 * 展示SimpleCalendar
 */
public class ACCalendar extends AppCompatActivity {
    @BindView(R.id.frgCalendar)
    FrgCalendar mHGVDays;

    @BindView(R.id.but_shrink)
    Button mBTShrink;

    private boolean mIsShrink;

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
        CalendarItemAdapter mCSIAdapter = new CalendarItemAdapter(this);
        mHGVDays.setCalendarItemAdapter(mCSIAdapter);
        Calendar cDay = Calendar.getInstance();
        mHGVDays.setCalendarSelectedDay(cDay.get(Calendar.YEAR), cDay.get(Calendar.MONTH),
                cDay.get(Calendar.DAY_OF_MONTH));

        Toast tt = Toast.makeText(getApplicationContext(), "selected : ", Toast.LENGTH_SHORT);
        mHGVDays.setDateChangeListener(new ICalendarListener() {
            @Override
            public void onDayChanged(View view, String day) {
                tt.setText("selected : " + day);
                tt.setDuration(Toast.LENGTH_SHORT);
                tt.show();
            }

            @Override
            public void onMonthChanged(String yearMonth) {
                tt.setText(yearMonth);
                tt.setDuration(Toast.LENGTH_SHORT);
                tt.show();
            }
        });

        mBTShrink.setOnClickListener(v -> mHGVDays.setShrinkMode(!mHGVDays.isShrinkMode()));
    }
}
