package wxm.uilib.FrgCalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import wxm.uilib.R;
import wxm.uilib.SimpleCalendar.BaseCalendarItemModel;
import wxm.uilib.SimpleCalendar.CalendarView;


/**
 * 日历节点适配器
 *
 * @param <T> 日历节点数据
 */
public class FrgCalendarItemAdapter<T extends FrgCalendarItemModel> extends BaseAdapter {
    private static final int RED_FF725F = 0xffff725f;

    protected Context mContext;
    //key:date("yyyy-MM-dddd"),value: you custom CalendarItemModel must extend BaseCalendarItemModel
    private TreeMap<String, T> dayModelList = new TreeMap<>();
    //list to keep dayModelList's key that convenient for get key by index.
    private List<String> indexToTimeMap = new ArrayList<>();

    public FrgCalendarItemAdapter(Context context) {
        this.mContext = context;
    }

    public TreeMap<String, T> getDayModelList() {
        return dayModelList;
    }

    public void setDayModelList(TreeMap<String, T> dayModelList) {
        this.dayModelList = dayModelList;
        indexToTimeMap.clear();
        indexToTimeMap.addAll(this.dayModelList.keySet());
    }

    public List<String> getIndexToTimeMap() {
        return indexToTimeMap;
    }

    @Override
    public int getCount() {
        return dayModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * default calendar item view
     * override this function to custom your View items.
     *
     * @param date        date for item
     * @param model       data
     * @param convertView param
     * @param parent      param
     * @return param for origin function
     */
    public View getView(String date, T model, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.gi_calendar_item, null);
        }
        TextView dayNum = (TextView) view.findViewById(R.id.tv_day_num);
        dayNum.setText(model.getDayNumber());

        view.setBackgroundResource(R.drawable.bg_shape_calendar_item_normal);
        if (model.isToday()) {
            dayNum.setTextColor(RED_FF725F);
            dayNum.setText(mContext.getResources().getString(R.string.today));
        }

        if (model.isHoliday()) {
            dayNum.setTextColor(RED_FF725F);
        }

        if (model.getStatus() == FrgCalendarItemModel.Status.DISABLE) {
            dayNum.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
        }

        if (model.isNotCurrentMonth()) {
            dayNum.setTextColor(mContext.getResources().getColor(R.color.gray_bbbbbb));
            view.setClickable(true);
        }

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String date = indexToTimeMap.get(position);
        View view = getView(date, dayModelList.get(date), convertView, parent);
        GridView.LayoutParams layoutParams = new GridView.LayoutParams(CalendarView.mItemWidth, CalendarView.mItemHeight);
        view.setLayoutParams(layoutParams);
        return view;
    }
}
