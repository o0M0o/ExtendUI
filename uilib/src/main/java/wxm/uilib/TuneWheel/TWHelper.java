package wxm.uilib.TuneWheel;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * TuneWheel辅助类
 * 辅助实现TuneWheel功能
 * Created by ookoo on 2017/4/22.
 */
abstract class TWHelper {
    protected TuneWheel mTWObj;
    private float DISPLAY_DENSITY;

    public TWHelper(TuneWheel tw)    {
        mTWObj = tw;
        DISPLAY_DENSITY = tw.getContext().getResources().getDisplayMetrics().density;
    }

    /**
     * 计算手势追踪
     * @param event     事件
     */
    public abstract void countVelocityTracker(MotionEvent event);

    /**
     * 绘制刻度
     * @param canvas
     */
    public abstract void drawScaleLine(Canvas canvas);


    /**
     * 把dp翻译为px
     * @param dp    val for dp
     * @return      val for px
     */
    public float getDPToPX(float dp) {
        return DISPLAY_DENSITY * dp;
    }

    /**
     * 更新移动和值
     */
    public void changeMoveAndValue()    {
        float ld = getDPToPX(mTWObj.mAttrLineDivider);
        int tValue = (int) (mTWObj.mMove / ld);
        if (Math.abs(tValue) > 0) {
            mTWObj.mAttrCurValue += tValue;
            mTWObj.mMove -= tValue * ld;

            int min = mTWObj.mAttrMinValue;
            int max = mTWObj.mAttrMaxValue;
            if (mTWObj.mAttrCurValue <= min || mTWObj.mAttrCurValue > max) {
                mTWObj.mAttrCurValue = mTWObj.mAttrCurValue <= min ? min : max;
                mTWObj.mMove = 0;
                mTWObj.mScroller.forceFinished(true);
            }
            mTWObj.notifyValueChange();
        }
        mTWObj.postInvalidate();
    }

    /**
     * 手势移动终止
     */
    public void countMoveEnd() {
        int roundMove = Math.round(mTWObj.mMove / getDPToPX(mTWObj.mAttrLineDivider));
        mTWObj.mAttrCurValue = mTWObj.mAttrCurValue + roundMove;
        mTWObj.mAttrCurValue = Math.min(Math.max(mTWObj.mAttrMinValue, mTWObj.mAttrCurValue),
                                    mTWObj.mAttrMaxValue);

        mTWObj.mLastY = 0;
        mTWObj.mLastX = 0;
        mTWObj.mMove = 0;

        mTWObj.notifyValueChange();
        mTWObj.postInvalidate();
    }
}
