package wxm.androidutil.ImageUtility

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import wxm.androidutil.type.MySize
import java.io.*

/**
 * 处理图像的辅助类
 * Created by 123 on 2016/8/17.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object ImageUtil {
    /**
     * 加载本地图片
     * @param url  本地图片文件地址
     * @return 结果
     */
    fun getLocalBitmap(url: String): Bitmap? {
        return try {
            BitmapFactory.decodeStream(FileInputStream(url))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }


    /**
     * 加载本地图片
     * @param url  本地图片文件地址
     * @param wsz  想要的bitmap尺寸（可以为null)
     * @return 结果
     */
    fun getRotatedLocalBitmap(url: String, wsz: MySize? = null): Bitmap? {
        return getLocalBitmap(url).let {
            if (null != it) rotateBitmap(it, readPictureDegree(url), wsz)
            else it
        }
    }


    /**
     * 旋转图片，使图片保持正确的方向。
     * @param bitmap 原始图片
     * @param degrees 原始图片的角度
     * @param wantSZ  想要的bitmap尺寸（可以为null)
     * @return Bitmap 旋转后的图片
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Int, wantSZ: MySize?): Bitmap? {
        if (degrees == 0) {
            return bitmap
        }

        return Matrix().let {
            val w = bitmap.width
            val h = bitmap.height
            it.setRotate(degrees.toFloat(), (w / 2).toFloat(), (h / 2).toFloat())

            val parentIt = it
            wantSZ?.let {
                if (w > it.width && h > it.height) {
                    val scaleWidth = it.width.toFloat() / w
                    val scaleHeight = it.height.toFloat() / h
                    (if (scaleWidth > scaleHeight) scaleWidth else scaleHeight).let {
                        parentIt.postScale(it, it)
                    }
                }
            }

            val bmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, it, true)
            bitmap.recycle()

            bmp
        }
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    fun readPictureDegree(path: String): Int {
        return try {
            ExifInterface(path).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL).let {
                when (it) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            0
        }
    }


    /**
     * 转换本地图片到drawable
     * @param res 资源类
     * @param url 本地图片地址
     * @return drawable结果
     */
    fun getLocalDrawable(res: Resources, url: String): Drawable? {
        val bm = getLocalBitmap(url) ?: return null
        return BitmapDrawable(res, bm)
    }


    /**
     * 将bitmap中的某种颜色值替换成新的颜色
     * @param oldBitmap     原bitmap
     * @param oldColor      需替换旧颜色
     * @param newColor      替换的新颜色
     * @return 替换后bitmap
     */
    fun replaceBitmapColor(oldBitmap: Bitmap, oldColor: Int, newColor: Int): Bitmap {
        //相关说明可参考 http://xys289187120.blog.51cto.com/3361352/657590/
        val mBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true)
        //循环获得bitmap所有像素点
        val mBitmapWidth = mBitmap.width
        val mBitmapHeight = mBitmap.height
        for (i in 0 until mBitmapHeight) {
            for (j in 0 until mBitmapWidth) {
                //获得Bitmap 图片中每一个点的color颜色值
                //将需要填充的颜色值如果不是
                //在这说明一下 如果color 是全透明 或者全黑 返回值为 0
                //getPixel()不带透明通道 getPixel32()才带透明部分 所以全透明是0x00000000
                //而不透明黑色是0xFF000000 如果不计算透明部分就都是0了
                val color = mBitmap.getPixel(j, i)
                //Log.i("debug", "j = " + j + ", i = " + i + ", color = " + color);
                //将颜色值存在一个数组中 方便后面修改
                if (color == oldColor) {
                    mBitmap.setPixel(j, i, newColor)  //将白色替换成透明色
                }

            }
        }

        return mBitmap
    }

    /**
     * 从app的assets目录下加载图片，并转换为drawable
     * @param ct        上下文
     * @param ppath     图片文件在assets目录下的文件路径
     * @return          转换成功返回drawable,否则返回null
     */
    fun getAssetsPic(ct: Context, ppath: String): Drawable? {
        return try {
            BitmapFactory.decodeStream(ct.assets.open(ppath)).let {
                BitmapDrawable(ct.resources, it)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    /**
     * save bitmap as jpg file
     * @param bm            bitmap data
     * @param fileDir       jpg file dir
     * @param fileName      jpg filename
     * @return              true if success
     */
    fun saveBitmapToJPGFile(bm: Bitmap, fileDir: String, fileName: String): Boolean {
        var ret = false
        FileOutputStream(File(fileDir, fileName)).let {
            try {
                it.use { f ->
                    ret = bm.compress(Bitmap.CompressFormat.JPEG, 85, f)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }

        return ret
    }
}
