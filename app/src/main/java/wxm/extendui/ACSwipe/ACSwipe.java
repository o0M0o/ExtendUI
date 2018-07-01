package wxm.extendui.ACSwipe;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import wxm.androidutil.ui.frg.FrgSupportBaseAdv;
import wxm.androidutil.ui.activity.ACSwitcherActivity;

/**
 * @author WangXM
 * @version create：2018/4/14
 */
public class ACSwipe extends ACSwitcherActivity<FrgSupportBaseAdv> {
    @Override
    protected List<FrgSupportBaseAdv> setupFragment() {
        ArrayList<FrgSupportBaseAdv> ret = new ArrayList<>();
        ret.add(new FrgSwipe());
        return ret;
    }
}
