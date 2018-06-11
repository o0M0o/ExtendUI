package wxm.extendui.ACBanner

import android.os.Bundle

import wxm.androidutil.ui.activity.ACSwitcherActivity
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.extendui.ACSwipe.FrgSwipe

/**
 * @author WangXM
 * @version create：2018/4/14
 */
class ACBanner : ACSwitcherActivity<FrgSupportBaseAdv>() {
    override fun setupFragment(savedInstanceState: Bundle?) {
        addFragment(FrgBanner())
    }
}
