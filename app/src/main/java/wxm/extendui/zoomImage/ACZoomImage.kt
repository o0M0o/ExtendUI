package wxm.extendui.zoomImage

import android.os.Bundle

import wxm.androidutil.ui.activity.ACSwitcherActivity
import wxm.androidutil.ui.frg.FrgSupportBaseAdv

/**
 * @author WangXM
 * @version create：2018/4/14
 */
class ACZoomImage : ACSwitcherActivity<FrgSupportBaseAdv>() {
    override fun setupFragment(savedInstanceState: Bundle?) {
        addFragment(FrgZoomImage())
    }
}
