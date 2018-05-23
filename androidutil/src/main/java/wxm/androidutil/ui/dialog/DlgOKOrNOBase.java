package wxm.androidutil.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

import java.util.ArrayList;

import wxm.androidutil.util.UtilFun;

/**
 * 提供"OK"和"NO"的对话框基类
 * Created by WangXM on 2016/11/1.
 */
@SuppressWarnings("unused")
public abstract class DlgOKOrNOBase extends DialogFragment {
    private View mVWSelfDlg;

    private String mTitle;
    private String mOKName;
    private String mNoName;

    /**
     * 对话框选项监听器
     */
    public interface DialogResultListener {
        /**
         * 用户选择积极结果后的回调接口
         *
         * @param dialog 对话框句柄
         */
        void onDialogPositiveResult(DialogFragment dialog);

        /**
         * 用户选择消极结果后的回调接口
         *
         * @param dialog 对话框句柄
         */
        void onDialogNegativeResult(DialogFragment dialog);
    }


    private ArrayList<DialogResultListener> mListener = new ArrayList<>();

    public void addDialogListener(DialogResultListener nl) {
        mListener.add(nl);
    }

    /**
     * 在DialogFragment的show方法执行后，系统会调用此方法
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mVWSelfDlg = createDlgView(savedInstanceState);

        // 创建dialog并设置button的点击事件
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mVWSelfDlg)
                .setMessage(UtilFun.StringIsNullOrEmpty(mTitle) ? "对话框" : mTitle)
                .setPositiveButton(UtilFun.StringIsNullOrEmpty(mOKName) ? "确认" : mOKName,
                        (dialog, id) -> {
                            boolean bcheck = checkBeforeOK();
                            for (DialogResultListener dl : mListener) {
                                if (bcheck)
                                    dl.onDialogPositiveResult(DlgOKOrNOBase.this);
                                else
                                    dl.onDialogNegativeResult(DlgOKOrNOBase.this);
                            }
                        })
                .setNegativeButton(UtilFun.StringIsNullOrEmpty(mNoName) ? "放弃" : mNoName,
                        (dialog, id) -> {
                            for (DialogResultListener dl : mListener) {
                                dl.onDialogNegativeResult(DlgOKOrNOBase.this);
                            }
                        });

        return builder.create();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDlgView(savedInstanceState);
    }

    /**
     * init ui for dialog
     * @param savedInstanceState    if null, it new created
     */
    protected void initDlgView(@Nullable  Bundle savedInstanceState) {
    }

    /**
     * init dialog view
     * @param savedInstanceState    if null, it fresh created
     * @return                      dialog view
     */
    protected abstract View createDlgView(@Nullable  Bundle savedInstanceState);

    /**
     * 在确认动作前检查状态
     *
     * @return 正常返回true
     */
    protected boolean checkBeforeOK() {
        return true;
    }

    /**
     * 初始化对话框辅助字符串
     *
     * @param title  对话框title
     * @param OKName “OK”选项名
     * @param NoName "No"选项名
     */
    protected void initDlgTitle(String title, String OKName, String NoName) {
        mTitle = title;
        mOKName = OKName;
        mNoName = NoName;
    }

    /**
     * find dialog child view
     * @param vwId      id for child view
     * @param <T>       child view
     * @return          child view or null if not find
     */
    @Nullable
    protected <T extends View> T findDlgChildView(@IdRes int vwId)    {
        return mVWSelfDlg.findViewById(vwId);
    }

    /**
     * get dialog view
     * @return      dialog view
     */
    @NonNull
    protected View getDlgView() {
        return mVWSelfDlg;
    }
}
