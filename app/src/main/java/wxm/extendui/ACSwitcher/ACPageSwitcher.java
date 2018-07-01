package wxm.extendui.ACSwitcher;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import wxm.androidutil.ui.frg.FrgSupportBaseAdv;
import wxm.androidutil.ui.activity.ACSwitcherActivity;
import wxm.extendui.R;


public class ACPageSwitcher extends ACSwitcherActivity<FrgSupportBaseAdv> {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mu_swither, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_switch: {
                switchFragment();
            }
            break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected List<FrgSupportBaseAdv> setupFragment() {
        ArrayList<FrgSupportBaseAdv> ret = new ArrayList<>();
        ret.add(new FrgPageOne());
        ret.add(new FrgPageTwo());
        ret.add(new FrgPageThree());
        return ret;
    }
}
