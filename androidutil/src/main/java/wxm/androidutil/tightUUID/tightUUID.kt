package wxm.androidutil.tightUUID

import java.util.*
import kotlin.collections.HashMap

/**
 * @author      WangXM
 * @version     create：2018/6/9
 */
object tightUUID {
    private const val NEW_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val NEW_CHAR_LEN = NEW_CHAR.length
    private val NEW_POW_ARR = Array(12,
            { Math.pow(NEW_CHAR_LEN.toDouble(), it.toDouble()).toLong() })
    private val IDX_ARR = Array(260, { NEW_CHAR.indexOf(it.toChar())})
    private const val DELIMITER_CHAR = '-'

    /**
     * get tightUUID can revert to UUID
     * example : '0f04803e-502a-4f90-8012-db98b6efccb0' -> '0h3axw-5l0-5iw-8wO-16yQl101G'
     */
    fun getTUUID(): String {
        return toTUUID(UUID.randomUUID().toString())
    }

    /**
     * get fined tightUUID
     * fined tightUUID with smaller length but can not revert to UUID
     * example : '0f04803e-502a-4f90-8012-db98b6efccb0' -> 'h3axw5l05iw8wO16yQl101G'
     */
    fun getFineTUUID(): String {
        val sb = StringBuilder()
        UUID.randomUUID().toString().forEach {
            if (DELIMITER_CHAR != it) {
                sb.append(it)
            }
        }

        return toTUUID(sb.toString())
    }

    /**
     * translate UUID [org] to TUUID
     * when [fined] false
     *      '0f04803e-502a-4f90-8012-db98b6efccb0' -> '0h3axw-5l0-5iw-8wO-16yQl101G'
     * when [fined] true
     *      '0f04803e-502a-4f90-8012-db98b6efccb0' -> 'h3axw5l05iw8wO16yQl101G'
     */
    fun toTUUID(org: String, fined: Boolean = false): String {
        val ret = StringBuilder()
        org.split(DELIMITER_CHAR).let {
            it.forEach {
                val sz = dataToNewStr(it.toLong(16))
                ret.append(if (fined) {
                    sz
                } else {
                    val tag = getZeroTag(it)
                    (if (tag.isNotEmpty()) "$tag$sz" else sz) + DELIMITER_CHAR
                })
            }
        }

        return if (fined) ret.toString() else ret.removeSuffix(DELIMITER_CHAR.toString()).toString()
    }

    /**
     * translate TUUID [org] to UUID
     * [org] must get from [toTUUID] with 'fined' is false
     */
    fun toUUID(org: String): String {
        val ret = StringBuilder()
        org.split(DELIMITER_CHAR).let {
            it.forEach {
                strToData(it).toString(16).let { sz ->
                    getZeroTag(it).let { tag ->
                        (if (tag.isNotEmpty()) "$tag$sz" else sz) + DELIMITER_CHAR
                    }.let {
                        ret.append(it)
                    }
                }
            }
        }

        return ret.removeSuffix(DELIMITER_CHAR.toString()).toString()
    }

    /**
     * use [NEW_CHAR] get str from [data]
     */
    private fun dataToNewStr(data: Long): String {
        val ret = StringBuilder()
        val toSZ = {idx:Int ->
            ret.insert(0, NEW_CHAR[idx])
        }

        var vd = data
        while (vd >= NEW_CHAR_LEN) {
            toSZ((vd % NEW_CHAR_LEN).toInt())
            vd /= NEW_CHAR_LEN
        }

        if (vd.toInt() != 0) {
            toSZ((vd % NEW_CHAR_LEN).toInt())
        } else {
            if(ret.isEmpty())   {
                toSZ((vd % NEW_CHAR_LEN).toInt())
            }
        }

        return ret.toString()
    }

    /**
     * translate str from [NEW_CHAR] to Long
     */
    private fun strToData(sz: String): Long {
        var ret = 0L
        val maxPos = sz.length - 1
        for (i in maxPos downTo 0) {
            ret += IDX_ARR[sz[i].toInt()] * NEW_POW_ARR[maxPos - i]
        }

        return ret
    }

    /**
     * get '0' tag in HEX str
     */
    private fun getZeroTag(org: String): String {
        val tag = StringBuilder()
        // 避免'0000'字符串情况，不检查org最后一位
        for (i in 0 until org.length - 1) {
            if ('0' == org[i]) {
                tag.append("0")
            } else {
                return tag.toString()
            }
        }

        return tag.toString()
    }
}