package wxm.androidutil.FrgUtility;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 工具Fragment基础类（兼容V4)
 * Created by ookoo on 2016/11/16.
 */
public abstract class FrgUtilitySupportBase extends Fragment {
    protected String LOG_TAG = "FrgUtilitySupportBase";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)     {
        super.onActivityCreated(savedInstanceState);
        enterActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflaterView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        if (null != view) {
            initUiComponent(view);
        }
    }

    @Override
    public void onResume()  {
        super.onResume();
        loadUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        leaveActivity();
    }

    /**
     * 刷新UI
     */
    public void refreshUI()    {
        View v = getView();
        if(null != v) {
            initUiComponent(v);
            loadUI();
        }
    }

    /**
     * 加载视图
     * 在initView前调用
     * @param inflater                  para
     * @param container                 para
     * @param savedInstanceState        para
     * @return   inflated view
     */
    protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState);


    /**
     * 初始化视图
     * 在inflatView后调用
     * @param v                 init for view
     */
    protected abstract void initUiComponent(View v);

    /**
     * 初始化视图UI内容
     */
    protected abstract void loadUI();

    /**
     * 和activity附着
     */
    @SuppressWarnings("EmptyMethod")
    protected void enterActivity()  {
    }

     /**
     * 在结束前清理工作
     */
    @SuppressWarnings("EmptyMethod")
    protected void leaveActivity()  {
    }
}

