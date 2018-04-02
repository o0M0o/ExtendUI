package wxm.extendui.ACSwitcher;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import wxm.androidutil.FrgUtility.FrgUtilityBase;
import wxm.androidutil.Switcher.ACSwitcherActivity;
import wxm.extendui.R;


public class ACPageSwitcher extends ACSwitcherActivity<FrgUtilityBase> {
    @Override
    protected void initUi(Bundle savedInstanceState)    {
        super.initUi(savedInstanceState);
        addFragment(new FrgPageOne());
        addFragment(new FrgPageTwo());
    }

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

}
