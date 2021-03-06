package wxm.androidutil.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

/**
 * 依赖context的辅助类
 * Created by WangXM on 2017/7/4.
 */
public class UiUtil {
    /**
     * dp转换为px
     * @param context       上下文
     * @param dipValue      待转换dp
     * @return              px值
     */
    public static int dip2px(final Context context, final float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    /**
     * px转换为dp
     * @param context       上下文
     * @param pxValue       px值
     * @return              dp值
     */
    public static int px2dip(final Context context, final float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    /**
     * 获取颜色的工具函数
     * @param ct    context
     * @param id    颜色id
     * @return      颜色值
     */
    @SuppressWarnings("deprecation")
    @ColorInt
    public static int getColor(Context ct, @ColorRes int id)    {
        int ret;
        Resources res = ct.getResources();
        ret = res.getColor(id, ct.getTheme());

        return  ret;
    }

    /**
     * 获取Drawable的工具函数
     * @param id    id for drawable
     * @return      drawable
     */
    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context ct, @DrawableRes int id)    {
        Drawable ret;
        Resources res = ct.getResources();
        ret = res.getDrawable(id, ct.getTheme());

        return  ret;
    }
}
