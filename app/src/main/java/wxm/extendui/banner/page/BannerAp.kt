package wxm.extendui.banner.page

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import wxm.extendui.R
import wxm.extendui.banner.page.BannerPara
import wxm.uilib.lbanners.LMBanners
import wxm.uilib.lbanners.adapter.LBaseAdapter

/**
 * for frg
 * Created by WangXM on 16/12/15.
 */
class BannerAp(private val mContext: Context, private val mVGGroup: ViewGroup?) : LBaseAdapter<BannerPara> {
    override fun getView(lBanners: LMBanners<*>, context: Context, position: Int, data: BannerPara): View? {
        return when (data.mLayoutId) {
            R.layout.pg_banner_one -> {
                LayoutInflater.from(mContext).inflate(R.layout.pg_banner_one, mVGGroup)
            }

            R.layout.pg_banner_two -> {
                LayoutInflater.from(mContext).inflate(R.layout.pg_banner_two, mVGGroup)
            }

            else -> null
        }
    }
}
