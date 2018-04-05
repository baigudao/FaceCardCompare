package com.taisau.facecardcompare.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/8/10 0010.
 */

@SuppressLint("AppCompatCustomView")
public class TextWithTips extends TextView {
    boolean hasNew = false;


    public TextWithTips(Context context) {
        super(context);
    }

    public TextWithTips(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextWithTips(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextWithTips(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean isHasNew() {
        return hasNew;
    }

    public void setHasNew(boolean hasNew) {
        this.hasNew = hasNew;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hasNew) {
            Paint paint = new Paint();
            //画圆角矩形
            paint.setStyle(Paint.Style.FILL);//充满
           /* paint.setColor(Color.RED);
            paint.setAntiAlias(true);// 设置画笔的锯齿效果
            RectF oval3 = new RectF(0, 0, 20, 20);// 设置个新的长方形
            canvas.drawRoundRect(oval3, 20, 15, paint);//第二个参数是x半径，第三个参数是y半径*/
            paint.setColor(Color.WHITE);
            canvas.drawText("有新的版本:", 0, 20, paint);

        }
    }


}
