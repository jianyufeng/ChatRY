package com.sanbafule.sharelock.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sanbafule.sharelock.util.DensityUtil;


/**
 * Created by 简玉锋 on 2016/9/8.
 * 上传群组图像
 */
public class AddIconImageView extends ImageView {
    private Paint mPaint;
    private boolean isPaint =true;

    public AddIconImageView(Context context) {
        this(context, null);
    }

    public AddIconImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddIconImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        scaleType = ScaleType.FIT_XY;
        super.setScaleType(scaleType);

    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        isPaint =false;
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        isPaint =false;
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageResource(int resId) {
        isPaint=false;
        super.setImageResource(resId);
    }

    @Override
    public void setImageURI(Uri uri) {
        isPaint =false;
        super.setImageURI(uri);
    }
    private void init() {
        mPaint = new Paint();
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(Color.DKGRAY);
        mPaint.setStyle(Paint.Style.STROKE);
    }
    public void setPaint(Boolean isPaint){
        this.isPaint  = isPaint;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isPaint){
            PathEffect effect = new DashPathEffect(new float[] { 4, 4, 4, 4},1);
            mPaint.setPathEffect(effect);
            mPaint.setStrokeWidth(1);
            int x=canvas.getWidth()/2;
            int y=canvas.getHeight()/2;
            int mRadius = DensityUtil.dip2px(50);
            int mLineWidth = mRadius/2;
//            canvas.drawCircle(x, y, mRadius, mPaint);
            RectF rectF = new RectF(x-getWidth()/2,y-getHeight()/2,x+getWidth()/2,y+getHeight()/2);
            canvas.drawRoundRect(rectF,10, 10,mPaint);
            mPaint.setStrokeWidth(2);
            mPaint.setPathEffect(null);
            canvas.drawLine(x - mLineWidth, y, x + mLineWidth, y, mPaint);
            canvas.drawLine(x,y-mLineWidth,x,y+mLineWidth,mPaint);
        }
    }


}
