package wxm.androidutil.FrgUtility;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * derived class for fragment(support V4)
 * Created by ookoo on 2016/11/16.
 */
public abstract class FrgUtilitySupportBase extends Fragment {
    protected String LOG_TAG = "FrgUtilityBase";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)     {
        super.onActivityCreated(savedInstanceState);
        LOG_TAG = getClass().getSimpleName();

        enterActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflaterView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        if (null != view) {
            loadUI(savedInstanceState);
        }
    }

    @Override
    public void onResume()  {
        super.onResume();
        loadUI(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        leaveActivity();
    }

    /**
     * refresh UI
     */
    public void refreshUI()    {
        View v = getView();
        if(null != v) {
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
    protected abstract void loadUI(Bundle savedInstanceState);

    /**
     * invoked after enter activity
     */
    @SuppressWarnings("EmptyMethod")
    protected void enterActivity()  {
    }

    /**
     * invoked after leave activity
     */
    @SuppressWarnings("EmptyMethod")
    protected void leaveActivity()  {
    }
}

