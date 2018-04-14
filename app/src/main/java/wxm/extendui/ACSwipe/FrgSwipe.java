package wxm.extendui.ACSwipe;

import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.extendui.R;

/**
 * @author WangXM
 * @version create：2018/4/14
 */
public class FrgSwipe extends FrgSupportBaseAdv {
    @Override
    protected int getLayoutID() {
        return R.layout.frg_swipe;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }
}
