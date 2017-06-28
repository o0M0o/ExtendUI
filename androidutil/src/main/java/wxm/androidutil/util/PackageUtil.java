package wxm.androidutil.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * 包辅助类
 * Created by ookoo on 2017/2/21.
 */
public class PackageUtil {
    private final static String LOG_TAG = "PackageUtil";

    /**
     * 获取包版本号
     * @param context           包上下文
     * @param package_name      包名
     * @return                  包版本号
     */
    public static int getVerCode(Context context, String package_name) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager()
                            .getPackageInfo(package_name, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return verCode;
    }


    /**
     * 获取包版本名
     * @param context           包上下文
     * @param package_name      包名
     * @return                  包版本名
     */
    public static String getVerName(Context context, String package_name) {
        String verName = "";
        try {
            verName = context.getPackageManager()
                            .getPackageInfo(package_name, 0).versionName;
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return verName;
    }
}
