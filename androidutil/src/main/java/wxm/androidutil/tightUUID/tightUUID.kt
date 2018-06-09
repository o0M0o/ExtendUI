package wxm.androidutil.tightUUID

import android.support.annotation.IntegerRes
import java.math.BigInteger
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/6/9
 */
object tightUUID {
    private const val NEW_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val NEW_CHAR_LEN = NEW_CHAR.length
    private val NEW_POW_ARR = Array(5,
            { Math.pow(16.toDouble(), it.toDouble()).toInt() })

    fun getTUUID(): String  {
        val org = UUID.randomUUID().toString().replace("-", "")
        return translateUUID(org)
    }

    fun translateUUID(org:String): String   {
        var ret = ""
        var lastPos = 0
        var lastLeft = 0
        for(i in 0 until org.length)    {
            val curStr = org[i].toString()
            if(curStr == "-") {
                ret += NEW_CHAR[lastLeft % NEW_CHAR_LEN]
                ret += "-"

                lastPos = i + 1
                lastLeft = 0
            } else {
                val totalVal = (curStr.toInt(16) * NEW_POW_ARR[i - lastPos] + lastLeft)
                if (totalVal >= NEW_CHAR_LEN) {
                    ret += NEW_CHAR[totalVal % NEW_CHAR_LEN]

                    lastPos = i + 1
                    lastLeft = (totalVal / NEW_CHAR_LEN)
                } else {
                    lastLeft = totalVal
                }
            }
        }

        if(lastLeft != 0)   {
            ret += NEW_CHAR[lastLeft % NEW_CHAR_LEN]
        }

        return ret
    }
}