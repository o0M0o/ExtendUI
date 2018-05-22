package wxm.uilib.FrgCalendar.CalendarItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.TreeMap;

import wxm.uilib.FrgCalendar.Base.CalendarUtility;

/**
 * adapter for calendar-item-ui
 * you can direct use it or derived it
 * @param <T>       item model type
 *
 * Created by WangXM on 2018/05/02.
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseItemAdapter<T extends BaseItemModel> extends BaseAdapter {
    protected static final int RED_FF725F = 0xffff725f;
    protected Context mContext;

    protected Class<?>   mItemType;

    protected int mItemWidth;
    protected int mItemHeight;

    /**
     * key      : date("yyyy-MM-dd")
     * value    : you ItemModel extended from BaseItemModel
     */
    protected TreeMap<String, T> mTMItemModel = new TreeMap<>();
    protected ArrayList<String> mSZDayArr = new ArrayList<>();

    public BaseItemAdapter(Context context) {
        this.mContext = context;

        Type tp = getClass().getGenericSuperclass();
        mItemType = tp instanceof ParameterizedType ?
                (Class<?>) ((ParameterizedType) tp).getActualTypeArguments()[0]
                : BaseItemModel.class;

        setItemHeight(CalendarUtility.mItemHeight);
        setItemWidth(CalendarUtility.mItemWidth);
    }

    @Override
    public int getCount() {
        return mTMItemModel.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String date = mSZDayArr.get(position);
        View view = getView(date, mTMItemModel.get(date), convertView, parent);
        view.setLayoutParams(new GridView.LayoutParams(mItemWidth, mItemHeight));
        return view;
    }

    public int getItemHeight()  {
        return mItemHeight;
    }

    public void setItemHeight(int val)  {
        mItemHeight = val;
    }

    public int getItemWidth()  {
        return mItemWidth;
    }

    public void setItemWidth(int val)  {
        mItemWidth = val;
    }

    /**
     * set day-model
     * @param dayModel      new day model
     */
    public void setDayModel(TreeMap<String, T> dayModel) {
        mTMItemModel.clear();
        mTMItemModel.putAll(dayModel);

        mSZDayArr.clear();
        mSZDayArr.addAll(mTMItemModel.keySet());
    }

    public int getPositionForDay(String day) {
        return mSZDayArr.indexOf(day);
    }

    public String getDayInPosition(int pos) {
        return mSZDayArr.get(pos);
    }

    @SuppressWarnings("unchecked")
    public T getNewItem()   {
        try {
            return (T)mItemType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
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
    protected abstract View getView(String date, T model, View convertView, ViewGroup parent);
}
