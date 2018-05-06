package wxm.extendui.ACCalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.extendui.R;
import wxm.uilib.FrgCalendar.Base.ECalendarMode;
import wxm.uilib.FrgCalendar.Base.ICalendarListener;
import wxm.uilib.FrgCalendar.FrgCalendar;
import wxm.uilib.FrgCalendar.Month.MothItemAdapter;
import wxm.uilib.FrgCalendar.Week.WeekItemAdapter;

/**
 * 展示SimpleCalendar
 */
public class ACCalendar extends AppCompatActivity {
    @BindView(R.id.frgCalendar)
    FrgCalendar mHGVDays;

    @BindView(R.id.but_shrink)
    Button mBTShrink;

    private final static String LOG_TAG = "ACCalendar";

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
        mHGVDays.setCalendarItemAdapter(new MothItemAdapter(this),
                new WeekItemAdapter(this));
        Calendar cDay = Calendar.getInstance();
        mHGVDays.setCalendarSelectedDay(cDay.get(Calendar.YEAR), cDay.get(Calendar.MONTH),
                cDay.get(Calendar.DAY_OF_MONTH));

        mBTShrink.setText(mHGVDays.getCalendarMode().isWeekMode() ? "WEEK-MODE" : "MONTH-MODE");

        Toast tt = Toast.makeText(getApplicationContext(), "selected : ", Toast.LENGTH_SHORT);
        mHGVDays.setDateChangeListener(new ICalendarListener() {
            @Override
            public void onDayChanged(String day) {
                String szLog = (mHGVDays.getCalendarMode().isWeekMode() ? "week-mode" : "moth-mode")
                                + " selected : " + day;
                Log.i(LOG_TAG, szLog);

                tt.setText(szLog);
                tt.setDuration(Toast.LENGTH_SHORT);
                tt.show();
            }

            @Override
            public void onMonthChanged(String yearMonth) {
                String szLog = (mHGVDays.getCalendarMode().isWeekMode() ? "week-mode" : "moth-mode")
                                + " monthChanged : " + yearMonth;
                Log.i(LOG_TAG, szLog);

                tt.setText(szLog);
                tt.setDuration(Toast.LENGTH_SHORT);
                tt.show();
            }
        });

        mBTShrink.setOnClickListener(v -> {
            mBTShrink.setText(mHGVDays.getCalendarMode().isWeekMode() ? "WEEK-MODE" : "MONTH-MODE");
            mHGVDays.setCalendarMode(mHGVDays.getCalendarMode().isWeekMode()
                    ? ECalendarMode.MONTH : ECalendarMode.WEEK);
        });
    }
}
