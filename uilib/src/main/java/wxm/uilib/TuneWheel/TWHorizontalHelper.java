package wxm.uilib.TuneWheel;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.view.MotionEvent;

/**
 * TuneWheel水平状态时辅助类
 * 辅助实现TuneWheel功能
 * Created by ookoo on 2017/4/22.
 */
class TWHorizontalHelper extends TWHelper {
    public TWHorizontalHelper(TuneWheel tw)    {
        super(tw);
    }

    public void countVelocityTracker(MotionEvent event)     {
        mTWObj.mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mTWObj.mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mTWObj.mMinVelocity) {
            mTWObj.mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
    }

    public void drawScaleLine(Canvas canvas)    {
        class utility {
            /**
             * 计算显示位置
             * @param tag           tag
             * @param xPosition     起始x坐标
             * @param textWidth     字体宽度
             * @return 偏移坐标
             */
            private float countLeftStart(String tag, float xPosition, float textWidth) {
                return xPosition - ((tag.length() * textWidth) / 2);
            }

            /**
             * 中间的红色指示线
             * @param canvas     画布
             * @param s_y        起始Y坐标
             * @param e_y        结束Y坐标
             */
            private void drawMiddleLine(Canvas canvas, float s_y, float e_y) {
                int indexWidth = 12;

                Paint redPaint = new Paint();
                redPaint.setStrokeWidth(indexWidth);
                redPaint.setColor(mTWObj.LINE_COLOR_CURSOR);
                canvas.drawLine(mTWObj.mWidth / 2, s_y, mTWObj.mWidth / 2, e_y, redPaint);
            }
        }
        utility helper = new utility();


        canvas.save();

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(mTWObj.TEXT_COLOR_NORMAL);

        TextPaint tp_normal = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp_normal.setTextSize(mTWObj.mAttrTextSize);

        TextPaint tp_big = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp_big.setTextSize(mTWObj.mAttrTextSize);
        tp_big.setColor(mTWObj.TEXT_COLOR_HOT);

        int width = mTWObj.mWidth, drawCount = 0;
        float xPosition;
        float textWidth = Layout.getDesiredWidth("0", tp_normal);
        float textWidth_big = Layout.getDesiredWidth("0", tp_big);

        float h_middle = mTWObj.getHeight() / 2;
        float long_y_dif = getDPToPX(mTWObj.mAttrLongLineHeight /2);
        float short_y_dif = getDPToPX(mTWObj.mAttrShortLineHeight /2);
        float ln_long_s_y = h_middle - long_y_dif;
        float ln_long_e_y = h_middle + long_y_dif;
        float ln_short_s_y = h_middle - short_y_dif;
        float ln_short_e_y = h_middle + short_y_dif;

        float text_top_pos = ln_long_s_y / 2 + textWidth_big / 2;
        float text_bottom_pos = (mTWObj.getHeight() + ln_long_e_y) / 2 + textWidth / 2;

        for (int i = 0; drawCount <= 4 * width; i++) {
            xPosition = (width / 2 - mTWObj.mMove) + getDPToPX(i * mTWObj.mAttrLineDivider);
            if (xPosition + mTWObj.getPaddingRight() < mTWObj.mWidth) {
                int cur_v = mTWObj.mAttrCurValue + i;
                if (cur_v <= mTWObj.mAttrMaxValue) {
                    String tw_tag = mTWObj.mTTTranslator.translateTWTag(cur_v);
                    if ((cur_v - mTWObj.mAttrMinValue) % (mTWObj.mAttrShortLineCount + 1) == 0) {
                        canvas.drawLine(xPosition, ln_long_s_y, xPosition, ln_long_e_y, linePaint);

                        canvas.drawText(tw_tag,
                                helper.countLeftStart(tw_tag, xPosition, textWidth),
                                text_bottom_pos, tp_normal);
                    } else {
                        canvas.drawLine(xPosition, ln_short_s_y, xPosition, ln_short_e_y, linePaint);
                    }

                    if (mTWObj.mAttrUseCurTag && (0 == i))
                        canvas.drawText(tw_tag,
                                helper.countLeftStart(tw_tag, xPosition, textWidth),
                                text_top_pos, tp_big);
                }
            }

            if (0 != i) {
                xPosition = (width / 2 - mTWObj.mMove) - getDPToPX(i * mTWObj.mAttrLineDivider);
                if (xPosition > mTWObj.getPaddingLeft()) {
                    int cur_v = mTWObj.mAttrCurValue - i;
                    if (cur_v >= mTWObj.mAttrMinValue) {
                        if ((cur_v - mTWObj.mAttrMinValue) % (mTWObj.mAttrShortLineCount + 1) == 0) {
                            String tw_tag = mTWObj.mTTTranslator.translateTWTag(cur_v);
                            canvas.drawLine(xPosition, ln_long_s_y, xPosition,
                                    ln_long_e_y, linePaint);

                            canvas.drawText(tw_tag,
                                    helper.countLeftStart(tw_tag, xPosition, textWidth),
                                    text_bottom_pos, tp_normal);
                        } else {
                            canvas.drawLine(xPosition, ln_short_s_y, xPosition, ln_short_e_y, linePaint);
                        }
                    }
                }
            }

            drawCount += getDPToPX(2 * mTWObj.mAttrLineDivider);
        }

        helper.drawMiddleLine(canvas, ln_long_s_y, ln_long_e_y);
        canvas.restore();
    }
}
