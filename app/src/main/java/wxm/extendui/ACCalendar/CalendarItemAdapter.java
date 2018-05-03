package wxm.extendui.ACCalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wxm.androidutil.util.UiUtil;
import wxm.extendui.R;
import wxm.uilib.FrgCalendar.CalendarItem.EItemStatus;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemAdapter;
import wxm.uilib.FrgCalendar.Month.MothItemAdapter;

/**
 * 日历节点
 * Created by xiaoming wang on 2017/07/03.
 */
public class CalendarItemAdapter extends BaseItemAdapter<CalendarItemModel> {
    private final int mCLToday;
    private final int mCLHoliday;
    private final int mCLDisable;


    public CalendarItemAdapter(Context context) {
        super(context);

        mCLToday = UiUtil.getColor(context, R.color.red_ff725f);
        mCLHoliday = UiUtil.getColor(context, R.color.red_ff725f);
        mCLDisable = UiUtil.getColor(context, android.R.color.darker_gray);
    }

    @Override
    public View getView(String date, CalendarItemModel model, View convertView, ViewGroup parent) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(mContext)
                            .inflate(R.layout.gi_calendar_item, null);

        TextView dayNum = (TextView) view.findViewById(R.id.tv_day_num);
        dayNum.setText(model.getDayNumber());

        view.setBackgroundResource(model.getRecordCount() > 0 ?
                R.drawable.day_shape : R.drawable.day_empty_shape);

        if (model.isToday()) {
            dayNum.setTextColor(mCLToday);
            dayNum.setText(mContext.getResources().getString(R.string.today));
        }

        if (model.isHoliday()) {
            dayNum.setTextColor(mCLHoliday);
        }

        if (model.getStatus() == EItemStatus.DISABLE) {
            dayNum.setTextColor(mCLDisable);
        }

        if (!model.isCurrentMonth()) {
            dayNum.setTextColor(mContext.getResources().getColor(wxm.uilib.R.color.gray_bbbbbb));
            view.setClickable(true);
        }

        return view;
    }
}
