package wxm.extendui.ACSwipe;

import android.os.Bundle;

import wxm.androidutil.frgUtil.FrgSupportBaseAdv;
import wxm.androidutil.switcher.ACSwitcherActivity;

/**
 * @author WangXM
 * @version create：2018/4/14
 */
public class ACSwipe extends ACSwitcherActivity<FrgSupportBaseAdv> {
    @Override
    protected void setupFragment(Bundle savedInstanceState) {
        addFragment(new FrgSwipe());
    }
}
