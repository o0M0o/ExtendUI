package wxm.androidutil.DBHelper;

/**
 * DB行数据接口
 * Created by ookoo on 2016/11/16.
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
