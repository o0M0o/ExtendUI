package wxm.androidutil.log

import android.util.Log

/**
 * use android logcat write log with tag
 * @author      WangXM
 * @version     create：2018/5/19
 */
object TagLog {
    private val mSelfClassName = TagLog.javaClass.name

    /**
     * translate [msg] to string
     */
    private fun anyToStr(msg: Any?): String {
        if (null == msg)
            return "null"

        return when (msg) {
            is String -> msg
            else -> msg.toString()
        }
    }

    /**
     * find caller stack
     */
    private fun findCallerStack(): StackTraceElement? {
        val arrSE = Thread.currentThread().stackTrace!!
        for (i in 0 until arrSE.size) {
            if (arrSE[i].className == mSelfClassName) {
                for (j in i until arrSE.size) {
                    if (arrSE[j].className != mSelfClassName) {
                        return arrSE[j]
                    }
                }
            }
        }

        return null
    }

    /**
     * write [msg] and [ta] to log
     * use [log1] if ta is null else use [log2]
     */
    private fun doLog(log1: (t: String, m: String) -> Int,
                      log2: (t: String, m: String, ta: Throwable?) -> Int,
                      msg: Any?, ta: Throwable? = null): Int {
        val tag = findCallerStack().let {
            if (null == it) {
                ""
            } else {
                val cn = it.className.let { it.substring(it.lastIndexOf(".") + 1)}
                val mn = it.methodName!!.let {
                    val fi = it.indexOf("$")
                    if(0 < fi)    {
                        it.substring(0, fi)
                    } else it
                }
                "$cn@$mn"
            }
        }

        return if (null == ta) {
            log1(tag, anyToStr(msg))
        } else {
            log2(tag, anyToStr(msg), ta)
        }
    }

    /**
     * verbose level
     */
    fun v(msg: Any?, ta: Throwable? = null): Int {
        return doLog({t, m -> Log.v(t, m)},
                {t, m , tr-> Log.v(t, m, tr)},
                msg, ta)
    }

    /**
     * info level
     */
    fun i(msg: Any?, ta: Throwable? = null): Int {
        return doLog({t, m -> Log.i(t, m)},
                {t, m , tr-> Log.i(t, m, tr)},
                msg, ta)
    }

    /**
     * debug level
     */
    fun d(msg: Any?, ta: Throwable? = null): Int {
        return doLog({t, m -> Log.d(t, m)},
                {t, m , tr-> Log.d(t, m, tr)},
                msg, ta)
    }

    /**
     * error level
     */
    fun e(msg: Any?, ta: Throwable? = null): Int {
        return doLog({t, m -> Log.e(t, m)},
                {t, m , tr-> Log.e(t, m, tr)},
                msg, ta)
    }
}