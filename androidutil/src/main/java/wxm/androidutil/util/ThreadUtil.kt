package wxm.androidutil.util

import android.app.Activity
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author      WangXM
 * @version     create：2018/5/23
 */
object ThreadUtil {
    /**
     * in background-thread run [back]
     * after [back] and if no exception happen then run [ui] in ui-thread
     */
    fun runInBackground(h: Activity, back: () -> Unit, ui: () -> Unit) {
        val weakActivity = WeakReference(h)
        Executors.newCachedThreadPool().submit {
            back()
            weakActivity.get()?.let {
                if (!(it.isDestroyed || it.isFinishing)) {
                    it.runOnUiThread(ui)
                }
            }
        }
    }

    /**
     * in background-thread run [back]
     * return callable result or [defRet] if exception happens
     * caller will wait result with timeout parameter [unit] and [timeout]
     */
    fun <T> callInBackground(back: () -> T, defRet: T, unit: TimeUnit, timeout: Long): T {
        val task = Executors.newCachedThreadPool().submit(back)
        try {
            return task.get(timeout, unit)
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }

        return defRet
    }

    /**
     * (after long wait)run [uiRun] in activity thread [wrActivity]
     */
    fun runInUIThread(wrActivity: WeakReference<Activity?>, uiRun: Runnable) {
        wrActivity.get()?.let {
            if (!(it.isDestroyed || it.isFinishing)) {
                it.runOnUiThread(uiRun)
            }
        }
    }
}