package wxm.androidutil.dialog;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Locale;

import wxm.androidutil.R;
import wxm.androidutil.util.UtilFun;

/**
 * 日期选择对话框
 * Created by WangXM on 2016/11/1.
 */
@SuppressWarnings("deprecation")
public class DlgDatePicker extends DlgOKOrNOBase {
    private String mInitDate;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    public void setInitDate(String initDate)    {
        mInitDate = initDate;
    }

    public String getCurDate()  {
        if(null == mDatePicker || null == mTimePicker)
            return "";

        int h, m;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            h = mTimePicker.getHour();
            m = mTimePicker.getMinute();
        } else {
            h = mTimePicker.getCurrentHour();
            m = mTimePicker.getCurrentMinute();
        }

        return String.format(Locale.CHINA, "%d-%02d-%02d %02d:%02d:00",
                mDatePicker.getYear(),
                mDatePicker.getMonth() + 1,
                mDatePicker.getDayOfMonth(),
                h, m);
    }


    @Override
    final protected View createDlgView(Bundle bundle) {
        initDlgTitle("选择日期与时间", "接受", "放弃");

        if(UtilFun.StringIsNullOrEmpty(mInitDate))
            return null;

        View vw = View.inflate(getActivity(), R.layout.dlg_date, null);
        mDatePicker = UtilFun.cast_t(vw.findViewById(R.id.date_picker));
        mTimePicker = UtilFun.cast_t(vw.findViewById(R.id.time_picker));
        mTimePicker.setIs24HourView(true);

        mDatePicker.init(Integer.valueOf(mInitDate.substring(0, 4)),
                Integer.valueOf(mInitDate.substring(5, 7)) - 1,
                Integer.valueOf(mInitDate.substring(8, 10)),
                null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(Integer.valueOf(mInitDate.substring(11, 13)));
            mTimePicker.setMinute(Integer.valueOf(mInitDate.substring(14, 16)));
        } else {
            mTimePicker.setCurrentHour(Integer.valueOf(mInitDate.substring(11, 13)));
            mTimePicker.setCurrentMinute(Integer.valueOf(mInitDate.substring(14, 16)));
        }
        return vw;
    }
}
