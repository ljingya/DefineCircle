package com.example.shuiai.definecircle;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shuiai@dianjia.io
 * @Company 杭州木瓜科技有限公司
 * @date 2017/2/28
 */

public class CircleView<T extends CircleView> extends View {
    private int outSmallCircleRadiousSize;
    private int outSmallCircleSize;
    /**
     * 外层圆的宽度
     */
    private int outCircleSize;
    /**
     * 内层圆的宽度
     */
    private int innerCircleSize;
    /**
     * 内层圆的颜色
     */
    private int innerColor;
    /**
     * 内层圆的画笔
     */
    private Paint mInnerPaint;
    /**
     * 外层圆的画笔
     */
    private Paint mOutArcPaint;
    /**
     * 外层圆弧的矩形
     */
    private RectF mOutRectf;
    /**
     * 外层小圆
     */
    private Paint mOutCirclePaint;
    /**
     * 画线的笔
     */
    private Paint linePaint;
    /**
     * view的宽
     */
    private int width;
    /**
     * View的高
     */
    private int height;
    /**
     * 设置最大值
     */
    private float progressMax;
    /**
     * 设置进度值
     */
    private float progress;
    /**
     * 最大度数
     */
    private float degree;
    /**
     * r
     * 百分比
     */
    private float percentage;
    /**
     * 百分比下的度数
     */
    private float unitDegree;

    /**
     * 初始渐变圆周颜色数组
     */
    private int[] gradientColorArray = new int[]{Color.parseColor("#7DDCFE"), Color.parseColor("#7DDCdd"), Color.parseColor("#7DDCcc"), Color.parseColor("#7DDCbb")};
    /**
     * 另外一种渐变颜色
     */
    private int[] gradientColorArrayother = {Color.parseColor("#fb6222"), Color.parseColor("#fb6233"), Color.parseColor("#fb6244"), Color.parseColor("#fb6245")};
    /**
     * 画刻度线时，判断循环的次数
     */

    /**
     * 属性动画
     */
    private ValueAnimator valueAnimator;
    private float interpolation;
    /**
     * 记录上一次的角度
     */
    private List<Float> lastDegreeList = new ArrayList();

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int arr = a.getIndex(i);
            switch (arr) {
                case R.styleable.CircleView_innerCircleColor:
                    innerColor = a.getColor(arr, Color.BLUE);
                    break;
                case R.styleable.CircleView_innerCircleSize:
                    innerCircleSize = a.getDimensionPixelSize(arr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CircleView_outCircleSize:
                    outCircleSize = a.getDimensionPixelSize(arr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CircleView_smallCircleSize:
                    outSmallCircleSize = a.getDimensionPixelSize(arr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CircleView_smallCircleRadiousSize:
                    outSmallCircleRadiousSize = a.getDimensionPixelSize(arr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()));
                    break;
            }
        }
        a.recycle();
        init();
    }

    private void init() {
        mInnerPaint = new Paint();
        mOutArcPaint = new Paint();
        mOutCirclePaint = new Paint();
        linePaint = new Paint();
        mOutRectf = new RectF();
        lastDegreeList.add((float) 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getPaddingLeft() + getPaddingRight() + (int) mOutRectf.width();
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getPaddingBottom() + getPaddingTop() + (int) mOutRectf.height();
        }
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int center = getWidth() / 2;
        canvas.rotate(-45, center, center);
        LinearGradient linearGradient;
        if (unitDegree + getLastDegree() < 90) {
            linearGradient = new LinearGradient(0, 0, getMeasuredWidth(), getMeasuredHeight(), gradientColorArray, null, Shader.TileMode.CLAMP);
        } else {
            linearGradient = new LinearGradient(0, 0, getMeasuredWidth(), getMeasuredHeight(), gradientColorArrayother, null, Shader.TileMode.CLAMP);

        }
        mInnerPaint.setShader(linearGradient);
        /**
         * 先画内圆
         */
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setStyle(Paint.Style.STROKE);
        mInnerPaint.setStrokeWidth(innerCircleSize);
        int radious = center - outSmallCircleRadiousSize * 2 - innerCircleSize / 2;
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, radious, mInnerPaint);

/**
 * 外圆
 */
        mOutArcPaint.setShader(linearGradient);
        mOutArcPaint.setAntiAlias(true);
        mOutArcPaint.setStrokeWidth(outCircleSize);
        mOutArcPaint.setStyle(Paint.Style.STROKE);
        mOutRectf.left = outSmallCircleRadiousSize;
        mOutRectf.top = outSmallCircleRadiousSize;
        mOutRectf.right = getWidth() - outSmallCircleRadiousSize;
        mOutRectf.bottom = getHeight() - outSmallCircleRadiousSize;
        mOutArcPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(mOutRectf, 180, unitDegree + getLastDegree(), false, mOutArcPaint);
        /**
         * 画刻度线
         */
        linePaint.setColor(Color.WHITE);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(5);
        canvas.drawLine(outSmallCircleRadiousSize * 2, center, outSmallCircleRadiousSize * 2 + (innerCircleSize * 3 / 4), center, linePaint);

        /**
         * 画小圆
         */
        mOutCirclePaint.setShader(linearGradient);
        mOutCirclePaint.setAntiAlias(true);
        mOutCirclePaint.setStyle(Paint.Style.STROKE);
        mOutCirclePaint.setStrokeWidth(10);
        float[] r = calculateBallCenter();
        canvas.drawCircle(r[0], r[1], outSmallCircleRadiousSize, mOutCirclePaint);
        mOutCirclePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(r[0], r[1], outSmallCircleRadiousSize - outSmallCircleSize, mOutCirclePaint);
        float angle = 0;
        while (angle < (unitDegree + getLastDegree())) {
            canvas.drawLine(outSmallCircleRadiousSize * 2 + 10, center, outSmallCircleRadiousSize * 2 + (innerCircleSize * 3 / 4), center, linePaint);
            canvas.rotate(5, center, center);
            angle += 5;
        }
        canvas.drawLine(outSmallCircleRadiousSize * 2, center, outSmallCircleRadiousSize * 2 + (innerCircleSize * 3 / 4), center, linePaint);
    }

    /**
     * 计算外圆圆心位置
     *
     * @return
     */
    private float[] calculateBallCenter() {
        float[] center = new float[2];
        float dy = (float) (Math.sin(Math.toRadians(unitDegree + getLastDegree())) * (getWidth() / 2 - outSmallCircleRadiousSize));
        float dx = (float) (Math.cos(Math.toRadians(unitDegree + getLastDegree())) * (getWidth() / 2 - outSmallCircleRadiousSize));
        center[0] = getWidth() / 2 - dx;
        center[1] = getWidth() / 2 - dy;
        return center;
    }

    /**
     * 设置最大进度的值
     *
     * @param progressMax
     */
    public T setProgressMax(float progressMax) {
        this.progressMax = progressMax;
        return (T) this;
    }

    /**
     * 设置进度数据
     *
     * @param progress
     */
    public T setProgress(float progress) {
        this.progress = progress;
        return (T) this;
    }

    /**
     * 获取上次记录的角度与当前角度的差值
     *
     * @return
     */
    private float getInterpolation() {
        return degree - getLastDegree();
    }

    /**
     * 更新数据和设置动画
     */
    public void update() {
        degree = progress / progressMax * 360;
        lastDegreeList.add(degree);
        setAnimation();
    }

    /**
     * 清除动画
     */
    public void clearAnimator() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    /**
     * 绘制进度
     */
    public void updateProgress() {
        unitDegree = percentage;
        invalidate();
    }

    /**
     * 获取上次记录的进度
     *
     * @return
     */
    private float getLastDegree() {
        float lastDegree = 0;
        int length = lastDegreeList.size();
        if (lastDegreeList != null && length > 1) {
            lastDegree = lastDegreeList.get(length - 2);
        }
        return lastDegree;
    }

    /**
     * 设置另外一个渐变的颜色
     *
     * @param gradientColorArrayother
     */
    public void setColorArray(int[] gradientColorArrayother) {
        this.gradientColorArrayother = gradientColorArrayother;
    }

    private void setAnimation() {
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(5000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                percentage =  getInterpolation() * value;
                if (addUpdateListener != null) {
                    addUpdateListener.onAddUpdateListener(value);
                }
                updateProgress();
            }

        });
        valueAnimator.start();
    }

    private AddUpdateListener addUpdateListener;

    public void setAddUpdateListener(AddUpdateListener addUpdateListener) {
        this.addUpdateListener = addUpdateListener;
    }

    public interface AddUpdateListener {
        void onAddUpdateListener(float percentage);
    }
}
