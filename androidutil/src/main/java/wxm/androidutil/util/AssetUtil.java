package wxm.androidutil.util;

import android.content.res.Resources;

import org.apache.http.util.EncodingUtils;

import java.io.InputStream;

/**
 * util class for asset
 * Created by WangXM on 2017/2/21.
 */
public class AssetUtil {
    /**
     * read file for asset and translate to string
     * @param res       resources object
     * @param fileName  asset file name
     * @param encode    encode for file
     * @return          String for asset file
     */
    public static String getFromAssets(Resources res, String fileName, String encode){
        String result = "";
        try {
            InputStream in = res.getAssets().open(fileName);
            int lenght = in.available();
            byte[]  buffer = new byte[lenght];

            int read = in.read(buffer);
            if(read == lenght) {
                result = EncodingUtils.getString(buffer, encode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
