package wxm.extendui.ListView;

import java.util.ArrayList;
import java.util.List;

import wxm.androidutil.ui.frg.FrgSupportBaseAdv;
import wxm.androidutil.ui.activity.ACSwitcherActivity;

public class ACListView extends ACSwitcherActivity<FrgSupportBaseAdv> {
    @Override
    protected List<FrgSupportBaseAdv> setupFragment() {
        ArrayList<FrgSupportBaseAdv> ret = new ArrayList<>();
        ret.add(new FrgListView());
        return ret;
    }
}
