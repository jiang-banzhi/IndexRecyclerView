package com.banzhi.indexrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/7/26.
 * @desciption : 索引
 * @version :
 * </pre>
 */

public class IndexBar extends View {
    private Context mContext;
    /**
     * 默认索引
     */
    private static final String[] DEFAULT_INDEX = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    /**
     * 每个index的高度
     */
    private int indexHeght;
    /**
     * view宽度
     */
    private int mWidth;
    /**
     * view高度
     */
    private int mHeight;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 按下时的颜色
     */
    private int pressColor;
    /**
     * 字体大小
     */
    private int textSize;

    private int DEFAULT_PRESS_COLOR = Color.GRAY;

    List<String> indexDatas;

    boolean useDatasIndex = true;


    public IndexBar(Context context) {
        super(context);
        init(context, null, -1);

    }

    public IndexBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1);
    }

    public IndexBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.mContext = context;
        //默认的TextSize
        int DEFAULT_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IndexrecyclerviewIndexBar);
        if (typedArray != null) {
            textSize = typedArray.getDimensionPixelSize(R.styleable.IndexrecyclerviewIndexBar_Indexrecyclerview_textSize, DEFAULT_SIZE);
            pressColor = typedArray.getColor(R.styleable.IndexrecyclerviewIndexBar_Indexrecyclerview_pressColor, DEFAULT_PRESS_COLOR);

        }
        initPaint();
        initDatas();
    }

    private void initDatas() {
        if (useDatasIndex) {
            indexDatas = Arrays.asList(DEFAULT_INDEX);
        } else {
            indexDatas = new ArrayList<>();
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setTextSize(textSize);
        mPaint.setAntiAlias(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //手指抬起时背景恢复透明
            setBackgroundColor(pressColor);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

        } else  {
            //手指抬起时背景恢复透明
            setBackgroundResource(android.R.color.transparent);
        }

        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        computeIndexHeight();
    }

    /**
     * 计算单个index高度
     */
    private void computeIndexHeight() {
        indexHeght = (mHeight - getPaddingTop() - getPaddingBottom()) / indexDatas.size();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < indexDatas.size(); i++) {
            String index = indexDatas.get(i);
            Paint.FontMetrics metrics = mPaint.getFontMetrics();
            //计算baseline
            int baseLine = (int) ((indexHeght - metrics.bottom - metrics.top) / 2);
            //绘制文字
            canvas.drawText(index, mWidth / 2 - mPaint.measureText(index) / 2, getPaddingTop() + baseLine + indexHeght * i, mPaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //取出宽高的MeasureSpec  Mode 和Size
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        //最终测量出来的宽高
        int measureWidth = 0, measureHeight = 0;

        //得到合适宽度：
        //存放每个绘制的index的Rect区域
        Rect indexBounds = new Rect();
        String index;//每个要绘制的index内容
        for (int i = 0; i < indexDatas.size(); i++) {
            index = indexDatas.get(i);
            //测量计算文字所在矩形，可以得到宽高
            mPaint.getTextBounds(index, 0, index.length(), indexBounds);
            //循环结束后，得到index的最大宽度
            measureWidth = Math.max(indexBounds.width(), measureWidth);
            //循环结束后，得到index的最大高度，然后*size
            measureHeight = Math.max(indexBounds.height(), measureHeight);
        }
        measureHeight *= indexDatas.size();
        if (wMode == MeasureSpec.EXACTLY) {
            measureWidth = wSize;
        } else if (wMode == MeasureSpec.AT_MOST) {
            //wSize此时是父控件能给子View分配的最大空间
            measureWidth = Math.min(measureWidth, wSize);
        } else if (wMode == MeasureSpec.UNSPECIFIED) {

        }
        //得到合适的高度：
        if (hMode == MeasureSpec.EXACTLY) {
            measureHeight = hSize;
        } else if (hMode == MeasureSpec.AT_MOST) {
            //wSize此时是父控件能给子View分配的最大空间
            measureHeight = Math.min(measureHeight, hSize);
        } else if (hMode == MeasureSpec.UNSPECIFIED) {

        }
        setMeasuredDimension(measureWidth, measureHeight);
    }
}
