package wxm.androidutil.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

/**
 * class name：SIMCardUtil<BR>
 * class description：读取Sim卡信息<BR>
 * PS： 必须在加入各种权限 <BR>
 * Date:2012-3-12<BR>
 *
 * @version 1.00
 * @author CODYY)peijiangping
 */
public class SIMCardUtil {
    /**
     * TelephonyManager提供设备上获取通讯服务信息的入口。 应用程序可以使用这个类方法确定的电信服务商和国家 以及某些类型的用户访问信息。
     * 应用程序也可以注册一个监听器到电话收状态的变化。不需要直接实例化这个类
     * 使用Context.getSystemService(Context.TELEPHONY_SERVICE)来获取这个类的实例。
     */
    private TelephonyManager    mTelephonyManager;
    private Context             mContext;

    public SIMCardUtil(Context context) {
        mContext = context;
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * Role:获取当前设置的电话号码
     * <BR>Date:2012-3-12
     * <BR>@author CODYY)peijiangping
     */
    public String getNativePhoneNumber() {
        return (null == mTelephonyManager || !checkSelfPermission()) ? null
                : mTelephonyManager.getLine1Number();
    }

    /**
     * Role:Telecom service providers获取手机服务商信息 <BR>
     * 需要加入权限<uses-permission
     * android:name="android.permission.READ_PHONE_STATE"/> <BR>
     * Date:2012-3-12 <BR>
     *
     * @author CODYY)peijiangping
     */
    public String getProvidersName() {
        if(!checkSelfPermission())
            return null;

        String ProvidersName = null;
        // 返回唯一的用户ID
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        String IMSI = mTelephonyManager.getSubscriberId();
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            ProvidersName = "中国移动";
        } else if (IMSI.startsWith("46001")) {
            ProvidersName = "中国联通";
        } else if (IMSI.startsWith("46003")) {
            ProvidersName = "中国电信";
        }
        return ProvidersName;
    }

    /**
     * check permission for this util
     * @return
     */
    private boolean checkSelfPermission()   {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED;
    }
}
