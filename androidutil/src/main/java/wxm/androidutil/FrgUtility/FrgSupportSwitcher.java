package wxm.androidutil.FrgUtility;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.ButterKnife;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;
import wxm.androidutil.R;

/**
 * base UI for show data
 * Created by wxm on 2016/9/27.
 */
public abstract class FrgSupportSwitcher<T>
        extends FrgUtilitySupportBase {
    private final static String CHILD_HOT = "child_hot";
    protected ArrayList<T>  mFrgArr = new ArrayList<>();
    protected int           mHotFrgIdx  = -1;

    @LayoutRes
    private int mFatherFrg;

    @IdRes
    private int mChildFrg;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mHotFrgIdx = savedInstanceState.getInt(CHILD_HOT, 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHILD_HOT, mHotFrgIdx);
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View v = layoutInflater.inflate(mFatherFrg, viewGroup, false);
        setupFragment(bundle);
        return v;
    }

    @Override
    protected void loadUI(Bundle savedInstanceState) {
        loadHotFrg(0);
    }

    /**
     * switch in pages
     * will loop switch between all child
     */
    public void switchPage() {
        if(null != getView()) {
            loadHotFrg((mHotFrgIdx + 1) % mFrgArr.size());
        }
    }

    /**
     * switch to child page
     * @param sb    child page want switch to
     */
    public void switchToPage(T sb)  {
        if(null != getView()) {
            for (T frg : mFrgArr) {
                if (frg == sb) {
                    if (frg != mFrgArr.get(mHotFrgIdx)) {
                        loadHotFrg(mFrgArr.indexOf(frg));
                    }
                    break;
                }
            }
        }
    }

    /**
     * get current hot page
     * @return      current page
     */
    public T getHotPage()    {
        return mHotFrgIdx >= 0 && mHotFrgIdx < mFrgArr.size() ? mFrgArr.get(mHotFrgIdx) : null;
    }

    /**
     * set child frg
     * @param child    all child frg
     */
    protected void addChildFrg(T child)  {
        if (child instanceof android.support.v4.app.Fragment) {
            mFrgArr.add(child);

            android.support.v4.app.FragmentTransaction t =
                    getChildFragmentManager().beginTransaction();
            t.add(mChildFrg, (android.support.v4.app.Fragment) child);
            t.hide((android.support.v4.app.Fragment) child);
            t.commit();
        }
    }

    /**
     * setup frg id
     * must invoke this before use it
     * @param father        mainly frg
     * @param child         container frg for child
     */
    protected void setupFrgID(@LayoutRes int father, @IdRes int child) {
        mFatherFrg = father;
        mChildFrg = child;
    }

    /**
     * invoke this to load fragment
     * @param savedInstanceState    If non-null, this fragment is being re-constructed
     *                              from a previous saved state as given here.
     */
    protected abstract void setupFragment(Bundle savedInstanceState);

    //// PRIVATE START
    /**
     * load hot fragment
     */
    protected void loadHotFrg(int newIdx) {
        T oldFrg = getHotPage();
        if(null != getView() && newIdx >= 0  && newIdx < mFrgArr.size()) {
            showFragment(oldFrg, false);

            mHotFrgIdx = newIdx;
            showFragment(getHotPage(), true);
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

        if(frg instanceof android.support.v4.app.Fragment) {
            android.support.v4.app.FragmentTransaction t = getChildFragmentManager().beginTransaction();
            if (bShow) {
                t.show((android.support.v4.app.Fragment) frg);
            } else {
                t.hide((android.support.v4.app.Fragment) frg);
            }
            t.commit();
        }
    }
    //// PRIVATE END
}
