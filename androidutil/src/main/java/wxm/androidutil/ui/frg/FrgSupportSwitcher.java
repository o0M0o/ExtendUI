package wxm.androidutil.ui.frg;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import java.util.ArrayList;


/**
 * base UI for show data
 * Created by wxm on 2016/9/27.
 */
public abstract class FrgSupportSwitcher<T>
        extends FrgSupportBaseAdv {
    private final static String CHILD_HOT = "child_hot";
    protected ArrayList<T>  mFrgArr = new ArrayList<>();
    protected int           mHotFrgIdx  = -1;

    @LayoutRes
    private int mFatherFrg;

    @IdRes
    private int mChildFrg;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }


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
    protected int getLayoutID()    {
        return mFatherFrg;
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {
        if(null == savedInstanceState)  {
            setupFragment(null);
        }

        loadUI(savedInstanceState);
    }

    @Override
    protected void loadUI(Bundle savedInstanceState) {
        loadHotFrg(isHaveHotPage() ? mHotFrgIdx : 0);
    }

    /**
     * switch in pages
     * will loop switch between all child
     */
    public void switchPage() {
        loadHotFrg((mHotFrgIdx + 1) % mFrgArr.size());
    }

    /**
     * switch to child page
     * @param sb    child page want switch to
     */
    public void switchToPage(T sb)  {
        for (T frg : mFrgArr) {
            if (frg == sb) {
                if (frg != getHotPage()) {
                    loadHotFrg(mFrgArr.indexOf(frg));
                }
                break;
            }
        }
    }

    /**
     * get current hot page
     * @return      current page
     */
    public T getHotPage()    {
        return isHaveHotPage() ? mFrgArr.get(mHotFrgIdx) : null;
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
    protected abstract void setupFragment(@Nullable Bundle savedInstanceState);

    //// PRIVATE START
    /**
     * load hot fragment
     */
    protected void loadHotFrg(int newIdx) {
        T oldFrg = getHotPage();
        if(null != getView() && newIdx >= 0  && newIdx < mFrgArr.size()) {
            if(mHotFrgIdx == newIdx)    {
                showFragment(getHotPage(), true);
                return;
            }

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

    private boolean isHaveHotPage() {
        return mHotFrgIdx >= 0 && mHotFrgIdx < mFrgArr.size();
    }
    //// PRIVATE END
}
