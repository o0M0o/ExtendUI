package wxm.androidutil.util;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 使用弱引用避免内存泄漏的消息处理器
 * Created by WangXM on 2016/11/16.
 */
public abstract class WRMsgHandler<T>
                        extends Handler {
    protected String TAG = "WRMsgHandler";
    private WeakReference<T> mWRHome;

    protected WRMsgHandler(T ac) {
        super();
        mWRHome = new WeakReference<>(ac);
    }

    @Override
    public void handleMessage(Message msg) {
        T home = mWRHome.get();
        if(null == home)
            return;

        processMsg(msg, home);
    }

    /**
     * 处理消息
     * @param m         待处理消息
     * @param home      处理器宿主
     */
    protected abstract void processMsg(Message m, T home);
}
