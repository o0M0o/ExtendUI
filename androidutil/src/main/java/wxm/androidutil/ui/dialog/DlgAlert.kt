package wxm.androidutil.ui.dialog

import android.app.AlertDialog
import android.content.Context

/**
 * for easy use alert dialog
 * @author      WangXM
 * @version     create：2018/5/14
 */
object DlgAlert {
    /**
     * get text presentation for [obj] in [ct]
     */
    private fun anyToCharSequence(ct:Context, obj: Any): CharSequence   {
        return when(obj)   {
            is CharSequence -> obj
            is Int -> ct.getString(obj)
            else -> obj.toString()
        }
    }

    /**
     * get alert dialog instance with [title] and [msg]
     * do [oper] for builder before create & show it
     */
    fun showAlert(home: Context, title: Any, msg: Any,
                  oper: (dlg: AlertDialog.Builder) -> Unit = {}) {
        return AlertDialog.Builder(home)
                .setTitle(anyToCharSequence(home, title))
                .setMessage(anyToCharSequence(home, msg))
                .apply { oper(this) }
                .create().show()
    }
}