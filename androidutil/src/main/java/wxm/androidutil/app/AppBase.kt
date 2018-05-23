package wxm.androidutil.app

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.annotation.*
import android.support.v4.content.ContextCompat
import wxm.androidutil.log.TagLog

/**
 * for easy get&use app context
 * Created by WangXM on 2016/5/7.
 */
@Suppress("unused")
abstract class AppBase : Application() {
    /**
     * handler for app crash
     */
    private object UncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread?, e: Throwable?) {
            TagLog.e("uncaught exception ${if(t==null) "" else "at thread $t"}", e)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler)
        instance = this
    }

    @Suppress("MemberVisibilityCanBePrivate", "unused")
    companion object {
        private lateinit var instance: AppBase

        fun appContext(): AppBase = instance

        fun <T> getSystemService(service: String): T? {
            @Suppress("UNCHECKED_CAST")
            return instance.getSystemService(service) as T
        }

        fun checkPermission(permission: String): Boolean {
            return ContextCompat.checkSelfPermission(instance, permission) ==
                    PackageManager.PERMISSION_GRANTED
        }

        /**
         * get version number for package
         * @param context       for package
         * @return              version number
         */
        fun getVerCode(context: Context): Int {
            var verCode = -1
            try {
                verCode = context.packageManager.getPackageInfo(instance.packageName, 0).versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                TagLog.e("", e)
            }

            return verCode
        }


        /**
         * get version paraName for package
         * @param context       context for package
         * @return              version paraName
         */
        fun getVerName(context: Context): String {
            var verName = ""
            try {
                verName = context.packageManager.getPackageInfo(instance.packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                TagLog.e("", e)
            }

            return verName
        }

        /**
         * get app resources
         */
        fun getResources(): Resources = instance.resources

        /**
         * get res string
         */
        fun getString(@StringRes resId: Int, vararg args:Any): String {
            return instance.getString(resId, args)
        }

        fun getString(@StringRes resId: Int): String {
            return instance.getString(resId)
        }

        /**
         * get res color
         */
        @ColorInt
        fun getColor(@ColorRes resId: Int): Int {
            return instance.getColor(resId)
        }

        /**
         * get res drawable
         */
        fun getDrawable(@DrawableRes resId: Int): Drawable {
            return instance.getDrawable(resId)
        }

        /**
         * get dimension by pixel
         */
        fun getDimension(@DimenRes resId: Int): Float {
            return getResources().getDimension(resId)
        }
    }
}
