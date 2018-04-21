package wxm.androidutil.ViewHolder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * implement 'view-holder' pattern
 * @author      WangXM
 * @version     create：2017/02/22
 */
public class ViewHolder {
    private Context mContext;
    private View mConvertView;

    // child views
    private SparseArray<View> mSAView;
    // tags
    private SparseArray<Object> mSATag;

    private ViewHolder(Context context, int layoutId, ViewGroup parentView) {
        mSATag = new SparseArray<>();
        mSAView = new SparseArray<>();
        mContext = context;
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parentView, false);
        mConvertView.setTag(this);
    }

    private ViewHolder(Context mContext, int layoutId) {
        mSATag = new SparseArray<>();
        mSAView = new SparseArray<>();
        mConvertView = LayoutInflater.from(mContext).inflate(layoutId, null);
        mConvertView.setTag(this);
    }

    /**
     * get an view-holder
     * @param context     context for root view
     * @param convertView root view(if null, will create it)
     * @param layoutId    layout-id for root view
     * @return view-holder
     */
    public static ViewHolder get(Context context, View convertView, int layoutId) {
        if (convertView == null) {
            return new ViewHolder(context, layoutId);
        }

        return (ViewHolder) convertView.getTag();
    }

    /**
     * get root view
     * @return root-view
     */
    public View getConvertView() {
        return mConvertView;
    }


    /**
     * because use root-view-setTag save self
     * so use this to set tag
     * @param tagId     id for tag
     * @param tag       tag-obj
     */
    public void setSelfTag(int tagId, Object tag)   {
        mSATag.put(tagId, tag);
    }

    /**
     * because use root-view-setTag save self
     * so use this to get tag
     * @param tagId     id for tag
     * @return          tag-obj
     */
    public Object getSelfTag(int tagId) {
        return mSATag.get(tagId);
    }

    /**
     * use id get child-view from root-view
     * @param viewId id for view
     * @param <T>    derived form view
     * @return child view
     */
    public <T extends View> T getView(int viewId) {
        View childView = mSAView.get(viewId);
        //还未绑定列表项中的控件到ViewHolder中
        if (childView == null) {
            childView = mConvertView.findViewById(viewId);
            mSAView.put(viewId, childView);
        }

        return (T) childView;
    }

    /**
     * set text
     * child view must be TextView
     *
     * @param viewId id for child TextView
     * @param text   show text
     * @return self
     */
    public ViewHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);

        return this;
    }


    /**
     * set text color
     * child view must be TextView
     * @param viewId id for child TextView
     * @param color  text color
     * @return self
     */
    @TargetApi(Build.VERSION_CODES.M)
    public ViewHolder setTextColor(int viewId, int color) {
        Resources res = mContext.getResources();
        TextView textView = getView(viewId);
        textView.setTextColor(res.getColor(color, mContext.getTheme()));

        return this;
    }

    /**
     * set image
     * child view must be ImageView
     * @param viewId     id for child ImageView
     * @param drawableId drawable-id for image
     * @return self
     */
    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(drawableId);

        return this;
    }

    /**
     * set image background
     * child view must be ImageView
     * @param viewId     id for child ImageView
     * @param drawableId drawable-id for drawable
     * @return self
     */
    public ViewHolder setImageBackground(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setBackgroundResource(drawableId);
        return this;
    }

    /**
     * set image
     * child view must be ImageView
     * @param viewId id for child ImageView
     * @param bm     bitmap for view
     * @return self
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }
}
