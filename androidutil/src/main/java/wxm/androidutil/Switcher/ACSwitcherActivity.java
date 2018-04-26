package wxm.androidutil.Switcher;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import butterknife.ButterKnife;
import wxm.androidutil.R;

/**
 * activity extend
 * T can be --
 *      android.app.Fragment  or android.support.v4.app.Fragment
 * Created by wxm on 2018/03/30.
 */
public abstract class ACSwitcherActivity<T>
        extends AppCompatActivity       {
    private final static String CHILD_HOT = "child_hot";
    protected String LOG_TAG = "ACSwitcherActivity";

    private int       mDIDBack = R.drawable.ic_back;

    protected ArrayList<T>  mALFrg;
    protected int           mHotFrgIdx  = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_base);

        LOG_TAG = getClass().getSimpleName();
        ButterKnife.bind(this);

        // for left menu(go back)
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getBackIconRID());
        toolbar.setNavigationOnClickListener(v -> leaveActivity());

        // for Fragment
        if (null != mALFrg && savedInstanceState != null) {
            mHotFrgIdx = savedInstanceState.getInt(CHILD_HOT, 0);
        } else  {
            mALFrg = new ArrayList<>();
            mHotFrgIdx = -1;
        }

        setupFragment(savedInstanceState);
        if(null == savedInstanceState)  {
            loadHotFragment(0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHILD_HOT, mHotFrgIdx);
    }

    /**
     * leave activity
     */
    protected void leaveActivity()   {
        finish();
    }

    /**
     * get back icon ID
     * @return      ID
     */
    public int getBackIconRID() {
        return mDIDBack;
    }

    /**
     * set back icon id
     * @param mDIDBack      ID
     */
    public void setBackIconRID(@IdRes int mDIDBack) {
        this.mDIDBack = mDIDBack;
    }


    /**
     * switch in pages
     * will loop switch between all child
     */
    public void switchFragment() {
        if(!(isFinishing() || isDestroyed())) {
            loadHotFragment((mHotFrgIdx + 1) % mALFrg.size());
        }
    }

    /**
     * switch to child fragment
     * @param sb    child fragment want switch to
     */
    public void switchToFragment(T sb)  {
        if(!(isFinishing() || isDestroyed())) {
            for (T frg : mALFrg) {
                if (frg == sb && frg != mALFrg.get(mHotFrgIdx)) {
                    loadHotFragment(mALFrg.indexOf(frg));
                    break;
                }
            }
        }
    }

    /**
     * get current hot fragment
     * @return      current page
     */
    public T getHotFragment()    {
        return mHotFrgIdx >= 0 && mHotFrgIdx < mALFrg.size()
                ? mALFrg.get(mHotFrgIdx) : null;
    }


    protected void removeAllFragment()  {
        for(T i : mALFrg)   {
            if(i instanceof Fragment) {
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.remove((Fragment)i);
                t.commit();
            } else  {
                if(i instanceof android.support.v4.app.Fragment) {
                    android.support.v4.app.FragmentTransaction t =
                            getSupportFragmentManager().beginTransaction();
                    t.remove((android.support.v4.app.Fragment)i);
                    t.commit();
                }
            }
        }

        mALFrg.clear();
        mHotFrgIdx = -1;
    }

    /**
     * add child frg
     * @param child    all child frg
     */
    protected void addFragment(T child)  {
        mALFrg.add(child);

        if(child instanceof Fragment) {
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.add(R.id.fl_holder, (Fragment)child);
            t.hide((Fragment)child);
            t.commit();
        } else  {
            if(child instanceof android.support.v4.app.Fragment) {
                android.support.v4.app.FragmentTransaction t =
                        getSupportFragmentManager().beginTransaction();
                t.add(R.id.fl_holder, (android.support.v4.app.Fragment)child);
                t.hide((android.support.v4.app.Fragment)child);
                t.commit();
            }
        }
    }

    /**
     * invoke this to load fragment
     * @param savedInstanceState    If non-null, this fragment is being re-constructed
     *                              from a previous saved state as given here.
     */
    protected abstract void setupFragment(@Nullable Bundle savedInstanceState);

    /// PRIVATE START
    /**
     * load hot fragment
     */
    private void loadHotFragment(int newIdx) {
        T oldFrg = getHotFragment();
        if(newIdx >= 0  && newIdx < mALFrg.size()) {
            showFragment(oldFrg, false);

            mHotFrgIdx = newIdx;
            showFragment(mALFrg.get(mHotFrgIdx), true);
        }
    }

    /**
     * show/hide fragment
     * @param frg       fragment need show/hide
     * @param bShow     show if true else hide
     */
    private void showFragment(T frg, boolean bShow)    {
        if(null == frg) {
            return;
        }

        if(frg instanceof Fragment) {
            FragmentTransaction t = getFragmentManager().beginTransaction();
            if (bShow) {
                t.show((Fragment) frg);
            } else {
                t.hide((Fragment) frg);
            }
            t.commit();
            return;
        }

        if(frg instanceof android.support.v4.app.Fragment) {
            android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            if (bShow) {
                t.show((android.support.v4.app.Fragment) frg);
            } else {
                t.hide((android.support.v4.app.Fragment) frg);
            }
            t.commit();
        }
    }
    /// PRIVATE END
}
