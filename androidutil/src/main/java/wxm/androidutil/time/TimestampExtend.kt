package wxm.androidutil.time

import java.sql.Timestamp
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/5/25
 */

/**
 * translator to calendar
 */
fun Timestamp.toCalendar(): Calendar {
    return this.let { Calendar.getInstance().apply { timeInMillis = it.time } }
}