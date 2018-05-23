package wxm.androidutil.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wxm.androidutil.type.MySize;

/**
 * 工具类
 * Created by WangXM on 2016/8/17.
 */
public class UtilFun {
    /**
     * 类转换函数
     * 转换失败后返回null, 不抛出异常
     * @param obj   待转换实例
     * @param <T>   需要的结论类型
     * @return      转换成功返回句柄，否则返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj)  {
        return cast_throw(obj, false);
    }

    /**
     * 类转换函数
     * 转换失败后抛出异常
     * @param obj   待转换实例
     * @param <T>   需要的结论类型
     * @return      转换成功返回句柄，否则抛出AssertionError
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast_t(Object obj)  {
        return cast_throw(obj, true);
    }

    /**
     * 类转换函数
     * 转换失败后抛出AssertionError
     * @param obj       待转换实例
     * @param bthrow    如果为true则抛出异常
     * @param <T>       需要的结论类型
     * @return          转换成功返回句柄，否则返回null
     */
    @SuppressWarnings("unchecked")
    private static <T> T cast_throw(Object obj, boolean bthrow) {
        T r = null;
        try {
            r = (T) obj;
        }catch (ClassCastException | NullPointerException e)   {
            if(bthrow)
                throw new AssertionError(
                                String.format(Locale.CHINA, "cast object failure, org type : %s",
                                        obj.getClass()));
        }

        if(bthrow && null == r) {
            throw new AssertionError(
                    String.format(Locale.CHINA, "cast object failure, org type : %s",
                            obj.getClass()));
        }
        return r;
    }


    /**
     * 检查链表是否为NULL或者空
     * @param lst 待检查链表
     * @return 若链表为NULL或者空则返回true,否则返回false
     */
    public static boolean ListIsNullOrEmpty(List lst)    {
        return (null == lst) || lst.isEmpty();
    }


    /**
     * 检查字符串是否空或者null
     * @param cstr  待检查字符串
     * @return   检查结果
     */
    public static boolean StringIsNullOrEmpty(String cstr)      {
        return null == cstr || cstr.isEmpty();
    }
}
