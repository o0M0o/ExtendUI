package wxm.androidutil.FrgUtility;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * derived class for fragment(support V4)
 * Created by ookoo on 2016/11/16.
 */
public abstract class FrgUtilitySupportBase extends Fragment {
    protected String LOG_TAG;

    public FrgUtilitySupportBase()  {
        super();
        LOG_TAG = getClass().getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflaterView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, v);
        initUI(savedInstanceState);
        return v;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        if (null != view) {
            loadUI(savedInstanceState);
        }
    }

    /*
    @Override
    public void onResume()  {
        super.onResume();
        loadUI(null);
    }
    */

    /**
     * refresh UI
     */
    public final void refreshUI()    {
        if(isVisible()) {
            initUI(null);
            loadUI(null);
        }
    }

    /**
     * reload UI
     */
    public final void reloadUI()    {
        if(isVisible()) {
            loadUI(null);
        }
    }

    /**
     * realize this in derived class
     * @param inflater                  inflater for view
     * @param container                 view holder
     * @param savedInstanceState        If non-null, this fragment is being re-constructed
     *                                  from a previous saved state as given here.
     * @return                          inflated view
     */
    protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container,
                                         Bundle savedInstanceState);

    /**
     * load ui
     * @param savedInstanceState        If non-null, this fragment is being re-constructed
     *                                  from a previous saved state as given here.
     */
    protected void loadUI(Bundle savedInstanceState)    {
    }

    /**
     * init ui
     * @param savedInstanceState        If non-null, this fragment is being re-constructed
     *                                  from a previous saved state as given here.
     */
    protected void initUI(Bundle savedInstanceState)    {
    }
}

