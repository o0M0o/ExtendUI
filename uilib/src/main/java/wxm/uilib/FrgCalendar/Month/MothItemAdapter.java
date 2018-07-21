package wxm.uilib.FrgCalendar.Month;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wxm.androidutil.ui.view.ViewHolder;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemAdapter;
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemModel;
import wxm.uilib.FrgCalendar.CalendarItem.EItemStatus;
import wxm.uilib.R;

/**
 * adapter for calendar-item-ui
 * you can direct use it or derived it
 *
 * Created by WangXM on 2018/05/02.
 */
@SuppressWarnings("WeakerAccess")
public class MothItemAdapter extends BaseItemAdapter<BaseItemModel> {
    private int mGrayTxtColor;
    private int mRedTxtColor;
    private int mNormalTxtColor;

    private String mSZToday;

    public MothItemAdapter(Context context) {
        super(context);

        mGrayTxtColor = mContext.getColor(R.color.gray_bbbbbb);
        mRedTxtColor = RED_FF725F;
        mNormalTxtColor = mContext.getColor(R.color.text_fit);

        mSZToday = mContext.getString(R.string.today);
    }

    protected View getView(String date, BaseItemModel model, View convertView, ViewGroup parent) {
        ViewHolder vhParent = ViewHolder.get(mContext, convertView, R.layout.uilib_calendar_item);
        View vwParent = vhParent.getConvertView();
        TextView tvDayNum = vhParent.getView(R.id.tv_day_num);

        tvDayNum.setText(model.getDayNumber());
        vwParent.setBackgroundResource(R.drawable.bg_shape_calendar_item_normal);

        if (!model.isCurrentMonth()) {
            tvDayNum.setTextColor(mGrayTxtColor);
            vwParent.setClickable(true);
        }   else {
            tvDayNum.setTextColor(mNormalTxtColor);
            vwParent.setClickable(false);

            if (model.isToday()) {
                tvDayNum.setTextColor(mRedTxtColor);
                tvDayNum.setText(mSZToday);
            }

            if (model.isHoliday()) {
                tvDayNum.setTextColor(mRedTxtColor);
            }

            if (model.getStatus() == EItemStatus.DISABLE) {
                tvDayNum.setTextColor(mGrayTxtColor);
            }
        }

        return vwParent;
    }
}
