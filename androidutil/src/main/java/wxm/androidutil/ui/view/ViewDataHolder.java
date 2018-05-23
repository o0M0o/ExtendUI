package wxm.androidutil.ui.view;

/**
 * use tag get it's translate data
 * can use this in ListView, can translate data to view-info only when load view
 * @author WangXM
 * @version create：2018/4/20
 */
@SuppressWarnings("WeakerAccess")
public abstract class ViewDataHolder<T, D> {
    private T   mTag;
    private D   mData;

    public ViewDataHolder() {
        mTag    = null;
        mData   = null;
    }

    public ViewDataHolder(T tag) {
        setTag(tag);
    }

    /**
     * set holder tag
     * invoke this can cause data reload when get it
     * @param tag       new data tag
     */
    public void setTag(T tag)    {
        mTag = tag;
        mData = null;
    }

    /**
     * get holder tag
     * @return          holder tag
     */
    public T getTag()    {
        return mTag;
    }

    /**
     * get data
     * @return      load & return data
     */
    public D getData()  {
        if(null == mTag)
            return null;

        if(null == mData)   {
            mData = getDataByTag(mTag);
        }

        return mData;
    }

    /**
     * use tag load data
     * @param tag       data tag
     * @return          data
     */
    protected abstract D getDataByTag(T tag);
}
