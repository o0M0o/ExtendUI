package wxm.uilib.DistanceMeter

import android.support.annotation.ColorInt
import android.support.annotation.IdRes

/**
 * tag for meter rule
 * Created by WangXM on 2017/3/29.
 */
class DistanceMeterTag(var mSZTagName: String) {
    @ColorInt var mCRTagColor: Int = 0

    var mTagVal: Float = 0.toFloat()
    var mTagFontSize: Int = 0
}
