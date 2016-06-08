package com.library.jianghw.circle;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Administrator on 2016/6/4 0004</br>
 * description:</br>
 */
public class RotationCircleBar extends View {
    /**
     * 球对象
     */
    private Ball mOneBall;
    private Ball mTwoBall;
    private static final int DEFAULT_ONE_BALL_COLOR = Color.parseColor("#40df73");
    private static final int DEFAULT_TWO_BALL_COLOR = Color.parseColor("#ffdf3e");
    private static final int DEFAULT_MAX_RADIUS = 15;
    private static final int DEFAULT_MIN_RADIUS = 5;
    private int maxRadius = DEFAULT_MAX_RADIUS;
    private int minRadius = DEFAULT_MIN_RADIUS;
    private float mCenterX;
    private int mCenterY;
    private static final float DEFAULT_DISTANCE = 20;//默认两个小球运行轨迹直径距离
    private float distance = DEFAULT_DISTANCE;
    private static final long DEFAULT_ANIMATOR_DURATION = 1000;
    /**
     * 笔
     */
    private Paint mPaint;
    /**
     * 动画
     */
    private AnimatorSet animatorSet;

    public RotationCircleBar(Context context) {
        this(context, null);
    }

    public RotationCircleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotationCircleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        mOneBall = new Ball();
        mTwoBall = new Ball();
        mOneBall.setColor(DEFAULT_ONE_BALL_COLOR);
        mTwoBall.setColor(DEFAULT_TWO_BALL_COLOR);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        configAnimator();//初始化但不运行
    }

    private void configAnimator() {
        float centerRadius = (maxRadius + minRadius) * 0.5f;
        //半径变化规律：中间大小->最大->中间大小->最小->中间大小
        ObjectAnimator oneRadiusAnimator = ObjectAnimator.ofFloat(
                mOneBall, "radius", centerRadius, maxRadius, centerRadius, minRadius, centerRadius);
        //无限
        oneRadiusAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ValueAnimator oneCenterAnimator = ValueAnimator.ofFloat(-1, 0, 1, 0, -1);
        oneCenterAnimator.setRepeatCount(ValueAnimator.INFINITE);
        oneCenterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float x = mCenterX + (distance) * value;
                mOneBall.setCenterX(x);
                invalidate();
            }
        });

        //半径变化规律：中间大小->最小->中间大小->最大->中间大小
        ObjectAnimator twoRadiusAnimator = ObjectAnimator.ofFloat(
                mOneBall, "radius", centerRadius, minRadius, centerRadius, maxRadius, centerRadius);
        //无限
        twoRadiusAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ValueAnimator twoCenterAnimator = ValueAnimator.ofFloat(1, 0, -1, 0, 1);
        twoCenterAnimator.setRepeatCount(ValueAnimator.INFINITE);
        twoCenterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float x = mCenterX + (distance) * value;
                mTwoBall.setCenterX(x);
                invalidate();
            }
        });

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(oneRadiusAnimator, oneCenterAnimator, twoRadiusAnimator, twoCenterAnimator);
        animatorSet.setDuration(DEFAULT_ANIMATOR_DURATION);//动画一次运行时间
        animatorSet.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() / 2, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() / 4, MeasureSpec.EXACTLY);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() / 2, MeasureSpec.EXACTLY);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() / 4, MeasureSpec.EXACTLY);
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mOneBall.getRadius() > mTwoBall.getRadius()) {
            mPaint.setColor(mTwoBall.getColor());
            canvas.drawCircle(mTwoBall.getCenterX(), mCenterY, mTwoBall.getRadius(), mPaint);
            mPaint.setColor(mOneBall.getColor());
            canvas.drawCircle(mOneBall.getCenterX(), mCenterY, mOneBall.getRadius(), mPaint);
        } else {
            mPaint.setColor(mOneBall.getColor());
            canvas.drawCircle(mOneBall.getCenterX(), mCenterY, mOneBall.getRadius(), mPaint);
            mPaint.setColor(mTwoBall.getColor());
            canvas.drawCircle(mTwoBall.getCenterX(), mCenterY, mTwoBall.getRadius(), mPaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimator();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimator();
    }

    public void startAnimator() {
        if (getVisibility() != VISIBLE) return;
        if (animatorSet != null && animatorSet.isRunning()) return;
        if (animatorSet != null) animatorSet.start();
    }

    private void stopAnimator() {
        if (animatorSet != null) animatorSet.end();
    }
}
