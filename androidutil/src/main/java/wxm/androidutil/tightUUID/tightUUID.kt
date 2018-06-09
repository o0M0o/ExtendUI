package wxm.androidutil.tightUUID

import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/6/9
 */
object tightUUID {
    private const val NEW_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val NEW_CHAR_LEN = NEW_CHAR.length
    private val NEW_POW_ARR = Array(12,
            { Math.pow(NEW_CHAR_LEN.toDouble(), it.toDouble()).toLong() })

    fun getTUUID(): String  {
        val org = UUID.randomUUID().toString().replace("-", "")
        return translateUUID(org)
    }

    fun translateUUID(org:String): String   {
        var ret = ""
        org.split("-").let {
            it.forEach {
                val sz = dataToNewStr(it.toLong(16))
                var tag = ""
                for(i in 0 until it.length - 1) {
                    if('0' == it[i])    {
                        tag += "0"
                    } else  {
                        break
                    }
                }

                ret += (if(tag.isNotEmpty())  "$tag$sz"
                        else sz) + "-"
            }
        }

        return ret.removeSuffix("-")
    }

    fun translateTUUID(org:String): String   {
        var ret = ""
        org.split("-").let {
            it.forEach {
                var tag = ""
                for(i in 0  until it.length -1) {
                    if('0' == it[i])    {
                        tag += "0"
                    } else  {
                        break
                    }
                }
                val sz = strToData(it).toString(16)


                ret += (if(tag.isNotEmpty())  "$tag$sz"
                        else sz) + "-"
            }
        }

        return ret.removeSuffix("-")
    }

    private fun dataToNewStr(data:Long): String     {
        var ret = ""
        var vd = data
        while (vd >= NEW_CHAR_LEN)  {
            ret = NEW_CHAR[(vd % NEW_CHAR_LEN).toInt()] + ret
            vd /= NEW_CHAR_LEN
        }

        if(vd.toInt() != 0) {
            ret = NEW_CHAR[(vd % NEW_CHAR_LEN).toInt()] + ret
        }

        return ret
    }

    private fun strToData(sz:String): Long  {
        var ret = 0L
        val maxPos = sz.length - 1
        for(i in maxPos downTo 0)    {
            ret += NEW_CHAR.indexOf(sz[i]) * NEW_POW_ARR[maxPos - i]
        }

        return ret
    }
}