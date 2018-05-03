package wxm.uilib.FrgCalendar.Month;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import wxm.androidutil.ViewHolder.ViewHolder;
import wxm.uilib.FrgCalendar.Base.CalendarUtility;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemAdapter;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemModel;
import wxm.uilib.FrgCalendar.CalendarItem.EItemStatus;
import wxm.uilib.R;

/**
 * adapter for calendar-item-ui
 * you can direct use it or derived it
 * @param <T>       item model type
 *
 * Created by WangXM on 2018/05/02.
 */
@SuppressWarnings("WeakerAccess")
public class MothItemAdapter extends BaseItemAdapter<BaseItemModel> {
    public MothItemAdapter(Context context) {
        super(context);
    }

    protected View getView(String date, BaseItemModel model, View convertView, ViewGroup parent) {
        ViewHolder vhParent = ViewHolder.get(mContext, convertView, R.layout.gi_calendar_item);
        View vwParent = vhParent.getConvertView();

        TextView tvDayNum = vhParent.getView(R.id.tv_day_num);
        tvDayNum.setText(model.getDayNumber());

        vwParent.setBackgroundResource(R.drawable.bg_shape_calendar_item_normal);
        if (model.isToday()) {
            tvDayNum.setTextColor(RED_FF725F);
            tvDayNum.setText(mContext.getResources().getString(R.string.today));
        }

        if (model.isHoliday()) {
            tvDayNum.setTextColor(RED_FF725F);
        }

        if (model.getStatus() == EItemStatus.DISABLE) {
            tvDayNum.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
        }

        if (!model.isCurrentMonth()) {
            tvDayNum.setTextColor(mContext.getResources().getColor(R.color.gray_bbbbbb));
            vwParent.setClickable(false);
        }

        return vwParent;
    }
}
