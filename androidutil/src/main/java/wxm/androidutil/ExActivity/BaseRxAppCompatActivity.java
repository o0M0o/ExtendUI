package wxm.androidutil.ExActivity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import wxm.androidutil.R;

/**
 * for activity base
 * Created by wxm on 2016/12/1.
 */
public abstract class BaseRxAppCompatActivity
        extends RxAppCompatActivity {
    protected String LOG_TAG = "BaseAppCompatActivity";

    private int       mDIDBack = R.drawable.ic_back;

    // 下面两个成员必须有一个有效
    protected Fragment mFGHolder;
    protected android.support.v4.app.Fragment  mFGSupportHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_base);

        ButterKnife.bind(this);
        initUi(savedInstanceState);
    }

    /**
     * 需要在调用此函数前赋值view holder和LOG_TAG
     * 优先使用当前view holder，然后再使用兼容view holder
     * @param savedInstanceState 视图参数
     */
    protected void initUi(Bundle savedInstanceState) {
        initFrgHolder();

        /*
        if(null == mFGHolder && null == mFGSupportHolder)   {
            Log.e(LOG_TAG, "需要先赋值View Holder");
            return;
        }
        */

        // for left menu(go back)
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getBackIconRID());
        toolbar.setNavigationOnClickListener(v -> leaveActivity());

        if(null == savedInstanceState)  {
            if(null != mFGHolder) {
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.add(R.id.fl_holder, mFGHolder);
                t.commit();
            } else  {
                if(null != mFGSupportHolder)    {
                    android.support.v4.app.FragmentTransaction t =
                                            getSupportFragmentManager().beginTransaction();
                    t.add(R.id.fl_holder, mFGSupportHolder);
                    t.commit();
                }
            }
        }
    }


    /**
     * 离开当前activity
     */
    protected abstract void leaveActivity();

    /**
     * 初始化fragement holder
     */
    protected abstract void initFrgHolder();

    /**
     * 得到后退ICON资源ID
     * @return   资源ID
     */
    public int getBackIconRID() {
        return mDIDBack;
    }

    /**
     * 设置后退ICON资源ID
     * @param mDIDBack      待设置资源ID
     */
    public void setBackIconRID(int mDIDBack) {
        this.mDIDBack = mDIDBack;
    }
}