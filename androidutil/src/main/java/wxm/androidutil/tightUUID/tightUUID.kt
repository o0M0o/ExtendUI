package wxm.androidutil.tightUUID

import java.math.BigInteger
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/6/9
 */
object tightUUID {
    private const val NEW_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val NEW_CHAR_LEN = NEW_CHAR.length
    private const val PARSE_CHAR_LEN = 6
    private val NEW_POW = BigInteger(NEW_CHAR_LEN.toString(10))

    fun getTUUID(): String  {
        val org = UUID.randomUUID().toString().replace("-", "")
        return translateUUID(org)
    }

    fun translateUUID(org:String): String   {
        var ret = ""

        var startPos = 0
        val endPos = org.length - 1
        while (startPos <= endPos)  {
            val bi = parse(org, startPos, Math.min(endPos, startPos + PARSE_CHAR_LEN))
            ret += toStr(bi)

            startPos += PARSE_CHAR_LEN
        }

        return ret
    }

    private fun parse(org:String, startPos:Int, endPos:Int) : BigInteger    {
        var bi = BigInteger.ZERO

        for(i in startPos until endPos) {
            val pow = Math.pow(16.toDouble(), (i - startPos).toDouble()).toLong()
            bi = bi.add(BigInteger.valueOf(Integer.valueOf(org[i].toString(), 16).toLong() * pow))
        }

        return bi
    }

    private fun toStr(bi:BigInteger): String    {
        var ret = ""

        var newBi = bi
        while (newBi > NEW_POW) {
            ret += NEW_CHAR[newBi.mod(NEW_POW).toInt()]
            newBi = newBi.divide(NEW_POW)
        }

        if(newBi != BigInteger.ZERO)   {
            ret += NEW_CHAR[newBi.mod(NEW_POW).toInt()]
        }

        return ret
    }
}