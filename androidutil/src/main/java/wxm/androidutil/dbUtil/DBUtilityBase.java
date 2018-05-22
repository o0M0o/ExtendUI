package wxm.androidutil.dbUtil;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * base class for db
 * -- D   db data
 * -- T   db data row primary key
 * Created by WangXM on 2016/10/31.
 */
public abstract class DBUtilityBase<D extends IDBRow<T>, T> {
    Timestamp mTSLastModifyData;

    public DBUtilityBase() {
        mTSLastModifyData = new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * 返回数据最后更新时间
     *
     * @return 数据最后更新时间
     */
    public Timestamp getDataLastChangeTime() {
        return mTSLastModifyData;
    }


    /**
     * 添加数据
     *
     * @param nr 待添加数据
     * @return 成功返回true
     */
    public boolean createData(D nr) {
        boolean ret = 1 == getDBHelper().create(nr);
        if (ret) {
            mTSLastModifyData.setTime(Calendar.getInstance(Locale.CHINA).getTimeInMillis());
            onDataCreate(Collections.singletonList(nr.getID()));
        }

        return ret;
    }

    /**
     * 添加数据
     *
     * @param nr 待添加数据
     * @return 成功数量
     */
    public int createDatas(List<D> nr) {
        int ret = 0;
        LinkedList<T> ls_ret = new LinkedList<>();
        for (D d : nr) {
            if (1 == getDBHelper().create(d)) {
                ls_ret.add(d.getID());
                ret++;
            }
        }

        if (0 < ret) {
            mTSLastModifyData.setTime(Calendar.getInstance(Locale.CHINA).getTimeInMillis());
            onDataCreate(ls_ret);
        }

        return ret;
    }

    /**
     * 根据ID获取数据
     *
     * @param id 数据id
     * @return 数据
     */
    public D getData(T id) {
        return getDBHelper().queryForId(id);
    }


    /**
     * 根据ID移除数据
     *
     * @param id 待移除数据id
     * @return 成功返回true
     */
    public boolean removeData(T id) {
        boolean ret = 1 == getDBHelper().deleteById(id);
        if (ret) {
            mTSLastModifyData.setTime(Calendar.getInstance(Locale.CHINA).getTimeInMillis());
            onDataRemove(Collections.singletonList(id));
        }

        return ret;
    }

    /**
     * 根据ID移除数据
     *
     * @param ls_id 待移除数据id
     * @return 成功数量
     */
    public int removeDatas(List<T> ls_id) {
        int ret = 0;
        LinkedList<T> ls_ret = new LinkedList<>();
        for (T d : ls_id) {
            if (1 == getDBHelper().deleteById(d)) {
                ls_ret.add(d);
                ret++;
            }
        }

        if (0 < ret) {
            mTSLastModifyData.setTime(Calendar.getInstance(Locale.CHINA).getTimeInMillis());
            onDataRemove(ls_ret);
        }

        return ret;
    }


    /**
     * 修改数据
     *
     * @param np 待修改数据
     * @return 成功返回true
     */
    public boolean modifyData(D np) {
        boolean ret = 1 == getDBHelper().update(np);
        if (ret) {
            mTSLastModifyData.setTime(Calendar.getInstance(Locale.CHINA).getTimeInMillis());
            onDataModify(Collections.singletonList(np.getID()));
        }

        return ret;
    }

    /**
     * 修改数据
     *
     * @param ls_np 待修改数据
     * @return 成功数量
     */
    public int modifyData(List<D> ls_np) {
        int ret = 0;
        LinkedList<T> ls_ret = new LinkedList<>();
        for (D d : ls_np) {
            if (1 == getDBHelper().update(d)) {
                ls_ret.add(d.getID());
                ret++;
            }
        }

        if (0 < ret) {
            mTSLastModifyData.setTime(Calendar.getInstance(Locale.CHINA).getTimeInMillis());
            onDataModify(ls_ret);
        }

        return ret;
    }


    /**
     * 获取所有数据
     *
     * @return 所有数据
     */
    public List<D> getAllData() {
        return getDBHelper().queryForAll();
    }


    /**
     * 数据有更新时调用
     *
     * @param md 更新数据的主键
     */
    protected abstract void onDataModify(List<T> md);

    /**
     * 新建数据后调用
     *
     * @param cd 新建数据的主键
     */
    protected abstract void onDataCreate(List<T> cd);

    /**
     * 删除数据后调用
     *
     * @param dd 删除数据的主键
     */
    protected abstract void onDataRemove(List<T> dd);

    /**
     * 获取DB辅助类
     *
     * @return DB辅助类
     */
    protected abstract RuntimeExceptionDao<D, T> getDBHelper();
}
