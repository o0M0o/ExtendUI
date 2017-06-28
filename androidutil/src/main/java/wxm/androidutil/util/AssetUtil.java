package wxm.androidutil.util;

import android.content.res.Resources;

import org.apache.http.util.EncodingUtils;

import java.io.InputStream;

/**
 * util class for asset
 * Created by ookoo on 2017/2/21.
 */
public class AssetUtil {

    /**
     * 从asset中读取文件并转化为字符串
     * @param res       resources类
     * @param fileName  asset中文件名
     * @param encode    asset中文件编码
     * @return          文件内容字符串
     */
    public static String getFromAssets(Resources res, String fileName, String encode){
        String result = "";
        try {
            InputStream in = res.getAssets().open(fileName);
            int lenght = in.available();
            byte[]  buffer = new byte[lenght];

            in.read(buffer);
            result = EncodingUtils.getString(buffer, encode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
