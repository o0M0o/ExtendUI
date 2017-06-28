package wxm.androidutil.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import java.util.ArrayList;

import wxm.androidutil.util.UtilFun;

/**
 * 提供"OK"和"NO"的对话框基类
 * Created by 123 on 2016/11/1.
 */
@SuppressWarnings("unused")
public abstract class DlgOKOrNOBase extends DialogFragment {
    private View mVWDlg;
    private String mTitle;
    private String mOKName;
    private String mNoName;

    /**
     * 对话框选项监听器
     */
    public interface DialogResultListener {
        /**
         * 用户选择积极结果后的回调接口
         * @param dialog  对话框句柄
         */
        void onDialogPositiveResult(DialogFragment dialog);

        /**
         * 用户选择消极结果后的回调接口
         * @param dialog  对话框句柄
         */
        void onDialogNegativeResult(DialogFragment dialog);
    }


    private ArrayList<DialogResultListener> mListener = new ArrayList<>();
    public void addDialogListener(DialogResultListener nl)  {
        mListener.add(nl);
    }


    /**
     * 处理NoticeDialogListener实例
     @Override
     public void onAttach(Activity activity) {
     super.onAttach(activity);
     try {
     mListener = (DialogResultListener) activity;
     } catch (ClassCastException e) {
     throw new ClassCastException(
     activity.toString() + " must implement DialogResultListener");
     }
     }
     */

    /**
     * 在DialogFragment的show方法执行后，系统会调用此方法
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mVWDlg = InitDlgView();

        // 创建dialog并设置button的点击事件
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mVWDlg);

        builder.setMessage(UtilFun.StringIsNullOrEmpty(mTitle) ? "对话框" : mTitle)
                .setPositiveButton(UtilFun.StringIsNullOrEmpty(mOKName) ? "确认" : mOKName,
                        (dialog, id) -> {
                            boolean bcheck = checkBeforeOK();
                            for(DialogResultListener dl : mListener)    {
                                if(bcheck)
                                    dl.onDialogPositiveResult(DlgOKOrNOBase.this);
                                else
                                    dl.onDialogNegativeResult(DlgOKOrNOBase.this);
                            }
                        })
                .setNegativeButton(UtilFun.StringIsNullOrEmpty(mNoName) ? "放弃" : mNoName,
                        (dialog, id) -> {
                            for(DialogResultListener dl : mListener)    {
                                dl.onDialogNegativeResult(DlgOKOrNOBase.this);
                            }
                        });

        return builder.create();
    }

    /**
     * 初始化对话框的视图部分
     * @return  对话框的视图部分
     */
    protected abstract View InitDlgView();

    /**
     * 在确认动作前检查状态
     * @return  正常返回true
     */
    protected boolean checkBeforeOK()   {
        return true;
    }

    /**
     * 获取对话框视图
     * @return  对话框视图
     */
    protected View getDlgView() {
        return mVWDlg;
    }

    /**
     * 初始化对话框辅助字符串
     * @param title         对话框title
     * @param OKName        “OK”选项名
     * @param NoName        "No"选项名
     */
    protected void InitDlgTitle(String title, String OKName, String NoName)     {
        mTitle = title;
        mOKName = OKName;
        mNoName = NoName;
    }
}
