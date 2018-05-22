package wxm.androidutil.dbUtil;

/**
 * DB row implementation this
 * Created by WangXM on 2016/11/16.
 */
public interface IDBRow<T> {
    /**
     *  获取数据主键
     * @return    主键
     */
    T getID();

    /**
     * 设置数据主键
     * @param mk    主键
     */
    void setID(T mk);

}
