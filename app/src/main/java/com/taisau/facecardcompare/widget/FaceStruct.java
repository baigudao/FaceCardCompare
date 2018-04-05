package com.taisau.facecardcompare.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.GFace;
import com.taisau.facecardcompare.util.DetectInfo;


/**
 * Created by Administrator on 2016/11/16 0016.
 */

public class FaceStruct extends View {
    private boolean isDraw=false;
    private Rect mBound;
    private Paint mPaint;
    private FrameLayout frameLayout;
    private int pic_width=0;
    private int pic_height=0;
    private DetectInfo.FacePos facePos;

    public FaceStruct(Context context) {

        this(context, null);
    }

    public FaceStruct(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceStruct(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mBound = new Rect();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FaceStruct(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, 0);
    }

    public int getPic_height() {
        return pic_height;
    }

    public void setPic_height(int pic_height) {
        this.pic_height = pic_height;
    }

    public int getPic_width() {
        return pic_width;
    }

    public void setPic_width(int pic_width) {
        this.pic_width = pic_width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = 0;
        int height = 0;
        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode)
        {
            case MeasureSpec.EXACTLY:// 明确指定了
                width = getPaddingLeft() + getPaddingRight() + specSize;
                break;
            case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
                width = getPaddingLeft() + getPaddingRight() + mBound.width();
                break;
        }
        /**
         * 设置高度
         */
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode)
        {

            case MeasureSpec.EXACTLY:// 明确指定了
                height = getPaddingTop() + getPaddingBottom() + specSize;
                break;
            case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
                height = getPaddingTop() + getPaddingBottom() + mBound.height();
                break;
        }
        setMeasuredDimension(width, height);
    }

    //绘制点的方法
    public void drawView(DetectInfo.FacePos facePos, FrameLayout frameLayout, int pic_width, int pic_height){
        this.frameLayout=frameLayout;
        this.pic_width=pic_width;
        this.pic_height=pic_height;
        if (facePos!=null) {
            this.facePos = facePos;
            createFrame(this.frameLayout);
            invalidate();
        }
        else
        {
            clearPoint(this.frameLayout);
        }
    }

    public void drawView(GFace.FacePointInfo info, FrameLayout frameLayout, int pic_width, int pic_height){
        this.frameLayout=frameLayout;
        this.pic_width=pic_width;
        this.pic_height=pic_height;
        if ( info!=null) {
            this.facePos=new DetectInfo.FacePos(info.ptEyeLeft.x,info.ptEyeLeft.y,info.ptEyeRight.x,info.ptEyeRight.y,
                                                    info.ptNose.x,info.ptNose.y,info.ptMouthLeft.x,info.ptMouthLeft.y,
                                                    info.ptMouthRight.x,info.ptMouthRight.y);
            createFrame(this.frameLayout);
            invalidate();
        }
        else
        {
            clearPoint(this.frameLayout);
        }
    }



    //绘制人脸关键点
    @Override
    protected void onDraw(Canvas canvas)
    {
        mPaint.setColor(Color.BLUE);
        if(frameLayout!=null){
            float x_bi=frameLayout.getWidth()/(float)pic_width;
            float y_bi=frameLayout.getHeight()/(float)pic_height;
            canvas.drawCircle((pic_width-facePos.getEye_left_x())*x_bi,facePos.getEye_left_y()*y_bi,3,mPaint);
            canvas.drawCircle((pic_width-facePos.getEye_right_x())*x_bi,facePos.getEye_right_y()*y_bi,3,mPaint);
            canvas.drawCircle((pic_width-facePos.getNose_x())*x_bi,facePos.getNose_y()*y_bi,3,mPaint);
            canvas.drawCircle((pic_width-facePos.getMouth_left_x())*x_bi,facePos.getMouth_left_y()*y_bi,3,mPaint);
            canvas.drawCircle((pic_width-facePos.getMouth_right_x())*x_bi,facePos.getMouth_right_y()*y_bi,3,mPaint);
        }
    }



    //绘制人脸五点为全屏控件
    public void createFrame(FrameLayout faceFrame){
        int  frameWidth=faceFrame.getWidth();
        int frameHeight=faceFrame.getHeight();
        if (getParent()==null)
            faceFrame.addView(this);
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)getLayoutParams();
        params.height=frameHeight;
        params.width=frameWidth;
        setLayoutParams(params);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    //清除脸五点为全屏控件
    public void clearPoint(FrameLayout faceFrame)
    {
        if (getParent()==null)
            faceFrame.addView(this);
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)getLayoutParams();
        params.height=0;
        params.width=0;
        setLayoutParams(params);
    }

}
