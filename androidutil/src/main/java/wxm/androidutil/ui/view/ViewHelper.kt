@file:Suppress("unused")
package wxm.androidutil.ui.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.view.View
import android.widget.TextView

/**
 * fast use parent-view's child view
 * @author WangXM
 * @version create：2018/4/11
 */
class ViewHelper(private val parentView: View) {
    /**
     * get root view
     */
    fun getRootView(): View = parentView

    /**
     * get context
     */
    fun getContext(): Context = parentView.context

    /**
     * get child view
     * @param vId       id for child view
     * @return          child view
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> getChildView(@IdRes vId: Int): T? {
        return parentView.findViewById<View>(vId) as? T
    }

    /**
     * set visibility for child view
     * @param vId           id for child view
     * @param visibility    visibility for child view
     */
    fun setVisibility(@IdRes vId: Int, visibility: Int) {
        parentView.findViewById<View>(vId).visibility = visibility
    }

    /**
     * set text [txt] for textView [vId]
     */
    fun setText(@IdRes vId: Int, txt: String): ViewHelper {
        val v = parentView.findViewById<TextView>(vId)
        v.text = txt
        return this
    }

    /**
     * set text with [sId] for textView [vId]
     */
    fun setText(@IdRes vId: Int, @StringRes sId:Int) {
        setText(vId, getContext().getString(sId))
    }

    /**
     * set text with [sId] for textView [vId]
     */
    fun setText(@IdRes vId: Int, @StringRes sId:Int, vararg args:Any) {
        setText(vId, getContext().getString(sId, args))
    }


    /**
     * setup textView text color
     * @param viewId    id for view
     * @param color     color for show
     * @return          self
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun setTextColor(@IdRes viewId: Int, @ColorRes color: Int): ViewHelper {
        val res = parentView.resources
        val textView = parentView.findViewById<TextView>(viewId)
        textView.setTextColor(res.getColor(color, parentView.context.theme))

        return this
    }

    /**
     * set tag for child view
     * @param vId       child view id
     * @param obj       tag object
     * @return          self
     */
    fun setTag(@IdRes vId: Int, obj: Any): ViewHelper {
        getChildView<View>(vId)!!.tag = obj
        return this
    }

    /**
     * get child view tag
     * @param vId       child view id
     * @return          view tag
     */
    fun getTag(@IdRes vId: Int): Any? {
        return getChildView<View>(vId)!!.tag
    }


}
