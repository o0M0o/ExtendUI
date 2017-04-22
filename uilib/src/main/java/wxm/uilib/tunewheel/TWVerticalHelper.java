package wxm.uilib.tunewheel;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.view.MotionEvent;

/**
 * TuneWheel垂直状态时辅助类
 * 辅助实现TuneWheel功能
 * Created by ookoo on 2017/4/22.
 */
class TWVerticalHelper extends TWHelper {
    public TWVerticalHelper(TuneWheel tw)    {
        super(tw);
    }

    public void countVelocityTracker(MotionEvent event)     {
        mTWObj.mVelocityTracker.computeCurrentVelocity(1000);
        float yVelocity = mTWObj.mVelocityTracker.getYVelocity();
        if (Math.abs(yVelocity) > mTWObj.mMinVelocity) {
            mTWObj.mScroller.fling(0, 0, 0, (int) yVelocity, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
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
             * @param s_x        起始x坐标
             * @param e_x        结束x坐标
             */
            private void drawMiddleLine(Canvas canvas, float s_x, float e_x) {
                int indexWidth = 12;

                Paint redPaint = new Paint();
                redPaint.setStrokeWidth(indexWidth);
                redPaint.setColor(mTWObj.LINE_COLOR_CURSOR);
                canvas.drawLine(s_x,  mTWObj.mHeight / 2, e_x, mTWObj.mHeight / 2, redPaint);
            }
        }
        utility helper = new utility();


        canvas.save();

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(mTWObj.TEXT_COLOR_NORMAL);

        TextPaint tp_normal = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp_normal.setTextSize(getDPToPX(mTWObj.mAttrTextSize));

        TextPaint tp_big = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp_big.setTextSize(getDPToPX(mTWObj.mAttrTextSize + 2));
        tp_big.setColor(mTWObj.TEXT_COLOR_HOT);

        int height = mTWObj.mHeight, drawCount = 0;
        float textWidth = Layout.getDesiredWidth("0", tp_normal);
        float textWidth_big = Layout.getDesiredWidth("0", tp_big);

        float w_middle = mTWObj.getWidth() / 2;
        float long_x_dif = getDPToPX(mTWObj.mAttrLongLineHeight /2);
        float short_x_dif = getDPToPX(mTWObj.mAttrShortLineHeight /2);
        float ln_long_x_s = w_middle - long_x_dif;
        float ln_long_x_e = w_middle + long_x_dif;
        float ln_short_x_s = w_middle - short_x_dif;
        float ln_short_x_e = w_middle + short_x_dif;

        for (int i = 0; drawCount <= 4 * height; i++) {
            float yPosition = (height / 2 - mTWObj.mMove) + getDPToPX(i * mTWObj.mAttrLineDivider);
            if (yPosition + mTWObj.getPaddingRight() < height) {
                int cur_v = mTWObj.mAttrCurValue + i;
                String tw_tag = mTWObj.mTTTranslator.translateTWTag(cur_v);
                if (cur_v <= mTWObj.mAttrMaxValue) {
                    if ((cur_v - mTWObj.mAttrMinValue) % (mTWObj.mAttrShortLineCount + 1) == 0) {
                        canvas.drawLine(ln_long_x_s, yPosition,  ln_long_x_e, yPosition, linePaint);

                        canvas.drawText(tw_tag,
                                helper.countLeftStart(tw_tag, ln_long_x_s / 2, textWidth),
                                yPosition + textWidth / 2, tp_normal);
                    } else {
                        canvas.drawLine(ln_short_x_s,  yPosition, ln_short_x_e, yPosition, linePaint);
                    }
                }

                if (mTWObj.mAttrUseCurTag && (0 == i))
                    canvas.drawText(tw_tag,
                            helper.countLeftStart(tw_tag, (ln_long_x_e + mTWObj.mWidth) / 2, textWidth_big),
                            yPosition + textWidth_big / 2, tp_big);
            }

            if (0 != i) {
                yPosition = (height / 2 - mTWObj.mMove) - getDPToPX(i * mTWObj.mAttrLineDivider);
                if (yPosition > mTWObj.getPaddingLeft()) {
                    int cur_v = mTWObj.mAttrCurValue - i;
                    if (cur_v >= mTWObj.mAttrMinValue) {
                        if ((cur_v - mTWObj.mAttrMinValue) % (mTWObj.mAttrShortLineCount + 1) == 0) {
                            String tw_tag = mTWObj.mTTTranslator.translateTWTag(cur_v);
                            canvas.drawLine(ln_long_x_s, yPosition,  ln_long_x_e, yPosition, linePaint);

                            canvas.drawText(tw_tag,
                                    helper.countLeftStart(tw_tag, ln_long_x_s / 2, textWidth),
                                    yPosition + textWidth / 2, tp_normal);
                        } else {
                            canvas.drawLine(ln_short_x_s,  yPosition, ln_short_x_e, yPosition, linePaint);
                        }
                    }
                }
            }

            drawCount += getDPToPX(2 * mTWObj.mAttrLineDivider);
        }

        helper.drawMiddleLine(canvas, ln_long_x_s, ln_long_x_e);
        canvas.restore();
    }
}
