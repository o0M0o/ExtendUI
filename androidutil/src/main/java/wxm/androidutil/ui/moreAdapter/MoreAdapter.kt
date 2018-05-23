package wxm.androidutil.ui.moreAdapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.ImageView
import android.widget.SimpleAdapter
import android.widget.TextView

import wxm.androidutil.ui.view.ViewHolder

/**
 * fix some issues when use SimpleAdapter
 * @author WangXM
 * @version create：2018/5/8
 */
abstract class MoreAdapter(protected val context: Context, data: List<Map<String, *>>,
                           @Suppress("MemberVisibilityCanBePrivate") @param:LayoutRes @field:LayoutRes
                           protected val mLRSelfDef: Int,
                           private val mFromKey: Array<String?> = arrayOfNulls(0),
                           private val mToId: IntArray = IntArray(0))
    : SimpleAdapter(context, data, mLRSelfDef, mFromKey, mToId) {
    private val mVWChild : Array<View?> = arrayOfNulls(data.size)

    /**
     * default view binder
     */
    private val mDefaultViewBinder = ViewBinder { v, d, text ->
        when (v) {
            is Checkable -> {
                when {
                    d is Boolean -> {
                        (v as Checkable).isChecked = d
                    }
                    v is TextView -> {
                        setViewText(v as TextView, text)
                    }
                    else -> {
                        throw IllegalStateException("${v.javaClass.name} should be bound to a Boolean, " +
                                "not a ${d?.javaClass ?: "<unknown type>"}")
                    }
                }
            }

            is TextView -> {
                // Note: keep the instanceof TextView check at the bottom of these
                // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                setViewText(v, text)
            }

            is ImageView -> {
                if (d is Int) {
                    setViewImage(v, d)
                } else {
                    setViewImage(v, text)
                }
            }

            else -> {
                throw IllegalStateException("${v.javaClass.name} should be bound to a Boolean, " +
                        "not a ${d?.javaClass ?: "<unknown type>"}")
            }
        }

        true
    }

    override fun getViewTypeCount(): Int {
        return count.let { if (it < 1) 1 else it }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return ViewHolder.get(context, convertView, getChildViewLayout(position)).let {
            loadView(position, it)
            mVWChild[position] = it.convertView
            selfBindView(position, it.convertView)

            it.convertView
        }
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        mVWChild.fill(null)
    }

    override fun notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated()
        mVWChild.fill(null)
    }

    /**
     * load childView at [pos] with holder [vhHolder]
     */
    protected abstract fun loadView(pos: Int, vhHolder: ViewHolder)

    /**
     * get childView layout-id at [pos]
     */
    @Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")
    @LayoutRes
    protected open fun getChildViewLayout(pos: Int): Int {
        return mLRSelfDef
    }

    /**
     * self view bind
     */
    @Suppress("UNCHECKED_CAST")
    private fun selfBindView(position: Int, view: View): Boolean {
        val bind = viewBinder
        val map = getItem(position) as Map<String, *>
        for(i in 0 until mToId.size)    {
            view.findViewById<View>(mToId[i])?.let {
                val data = map[mFromKey[i]]
                val txt = data?.toString() ?: ""

                var bound = false
                if(null != bind) {
                    bound = bind.setViewValue(it, data, txt)
                }

                if(!bound)  {
                    mDefaultViewBinder.setViewValue(it, data, txt)
                }

                Unit
            }
        }

        return true
    }

    /**
     * do [funOperator] for each childView until it return false
     */
    @Suppress("unused")
    protected fun forEachChildView(funOperator: (view:View, pos:Int) -> Boolean)    {
        mVWChild.filterNotNull().forEach{
            if(!funOperator(it, mVWChild.indexOf(it)))
                return@forEach
        }
    }
}
