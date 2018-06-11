package wxm.extendui.banner

import android.os.Bundle
import kotterknife.bindView
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.extendui.R
import wxm.extendui.banner.page.BannerAp
import wxm.extendui.banner.page.BannerPara
import wxm.uilib.lbanners.LMBanners
import java.util.*

/**
 * @author WangXM
 * @version create：2018/4/14
 */
class FrgBanner : FrgSupportBaseAdv() {
    private val mLBanners: LMBanners<BannerPara> by bindView(R.id.banners)

    override fun getLayoutID(): Int = R.layout.frg_banner

    override fun initUI(savedInstanceState: Bundle?) {
        mLBanners.setAdapter(BannerAp(activity!!, null),
                ArrayList<BannerPara>().apply {
                    add(BannerPara(R.layout.pg_banner_one))
                    add(BannerPara(R.layout.pg_banner_two))
                })
    }
}
