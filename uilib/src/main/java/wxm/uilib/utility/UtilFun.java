package wxm.uilib.utility;

import android.content.Context;

import java.util.List;

/**
 * 辅助函数库
 * Created by ookoo on 2017/4/21.
 */
public class UtilFun {
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

    /**
     * dp转换为px
     * @param context       上下文
     * @param dipValue      待转换dp
     * @return              px值
     */
    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    /**
     * px转换为dp
     * @param context       上下文
     * @param pxValue       px值
     * @return              dp值
     */
    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}
