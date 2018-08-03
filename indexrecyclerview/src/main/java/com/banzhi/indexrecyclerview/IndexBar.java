package com.banzhi.indexrecyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.banzhi.indexrecyclerview.bean.BaseIndexBean;
import com.banzhi.indexrecyclerview.utils.IndexDataHelper;

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
    private static final String TAG = "tag";

    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 2;

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
     * 按下时的背景颜色
     */
    private int mPressColor;
    /**
     * 按下时文字的颜色
     */
    private int mTextColor;
    /**
     * 文字的颜色
     */
    private int mPressTextColor;
    /**
     * 字体大小
     */
    private int textSize;

    private int DEFAULT_PRESS_COLOR = Color.GRAY;

    List<String> indexDatas;
    /**
     * 使用数据内容作为索引
     */
    boolean useDatasIndex;
    /**
     * 数据是否有序
     */
    boolean isOrderly;

    /**
     * 临时保存view背景颜色
     */
    private int color = android.R.color.transparent;
    /**
     * indexbar方向
     */
    private int mOrientation;

    RecyclerView.LayoutManager layoutManager;

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
            textSize = typedArray.getDimensionPixelSize(R.styleable.IndexrecyclerviewIndexBar_textSize, DEFAULT_SIZE);
            mPressColor = typedArray.getColor(R.styleable.IndexrecyclerviewIndexBar_pressColor, DEFAULT_PRESS_COLOR);
            mPressTextColor = typedArray.getColor(R.styleable.IndexrecyclerviewIndexBar_pressTextColor, DEFAULT_PRESS_COLOR);
            mTextColor = typedArray.getColor(R.styleable.IndexrecyclerviewIndexBar_textColor, DEFAULT_PRESS_COLOR);
            mOrientation = typedArray.getInt(R.styleable.IndexrecyclerviewIndexBar_orientation, VERTICAL);
        }
        initPaint();
        initDatas();
        setOnIndexPressListener(new OnIndexPressListener() {
            @Override
            public void onIndexChange(int index, String text) {
                textView.setText(text);
                textView.setVisibility(VISIBLE);
                if (layoutManager != null) {
                    int position = getPosByTag(text);
                    if (position > -1) {
                        ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
                    }
                }
            }

            @Override
            public void onMotionEventEnd() {
                textView.setVisibility(GONE);
            }
        });
    }

    TextView textView;

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    RecyclerView mRecyclerView;
    int mHeadCount;

    public void bindRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            layoutManager = mRecyclerView.getLayoutManager();
            RecyclerView.ItemDecoration itemDecoration = mRecyclerView.getItemDecorationAt(0);
            if (itemDecoration instanceof LevitationDecoration) {
                mHeadCount = ((LevitationDecoration) itemDecoration).getHeadCount();
            }
        }
    }

    private void initDatas() {
        if (!useDatasIndex) {
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

    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //手指抬起时背景恢复透明
            Drawable background = getBackground();
            if (background != null) {
                color = ((ColorDrawable) background).getColor();
            }
            setBackgroundColor(mPressColor);
            computePressIndexLocation(event.getX(), event.getY());

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            computePressIndexLocation(event.getX(), event.getY());
        } else {
            //手指抬起时背景恢复透明
            setBackgroundColor(color);
            //重置当前位置
            currentIndex = -1;

            if (mOnIndexPressListener != null) {
                mOnIndexPressListener.onMotionEventEnd();
            }
        }
        return true;
    }

    private int currentIndex = -1;

    /**
     * 计算按下的位置
     */
    private void computePressIndexLocation(float x, float y) {
        if (mOrientation == VERTICAL) {
            // 计算按下的区域位置
            currentIndex = (int) ((y - getPaddingTop()) / indexHeght);
        } else {
            currentIndex = (int) ((x - getPaddingLeft()) / indexHeght);
        }
        if (currentIndex < 0) {
            currentIndex = 0;
        } else if (currentIndex >= indexDatas.size()) {
            currentIndex = indexDatas.size() - 1;
        }
        invalidateMySelft();
        if (mOnIndexPressListener != null) {
            mOnIndexPressListener.onIndexChange(currentIndex, indexDatas.get(currentIndex));
        }

    }

    /**
     * 源数据时候有序
     *
     * @param orderly
     */
    public void setOrderly(boolean orderly) {
        isOrderly = orderly;
    }

    /**
     * 使用源数据作为索引
     */
    public void setUseDatasIndex() {
        this.useDatasIndex = true;
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
        if (mOrientation == VERTICAL) {
            indexHeght = (mHeight - getPaddingTop() - getPaddingBottom()) / indexDatas.size();
        } else {
            indexHeght = (mWidth - getPaddingLeft() - getPaddingRight()) / indexDatas.size();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < indexDatas.size(); i++) {
            String index = indexDatas.get(i);
            Paint.FontMetrics metrics = mPaint.getFontMetrics();
            //计算baseline
            int baseLine = (int) ((indexHeght - metrics.bottom - metrics.top) / 2);
            if (currentIndex == i) {
                mPaint.setColor(mPressTextColor);
            } else {
                mPaint.setColor(mTextColor);
            }
            //绘制文字
            if (mOrientation == VERTICAL) {
                canvas.drawText(index, mWidth / 2 - mPaint.measureText(index) / 2,
                        getPaddingTop() + baseLine + indexHeght * i, mPaint);
            } else {
                canvas.drawText(index, getPaddingLeft() + indexHeght * i + mPaint.measureText(index) / 2, mHeight - getPaddingBottom()
                        , mPaint);
            }
            Log.i(TAG, "onDraw: x=" + (getPaddingLeft() + indexHeght * i) + "  y=" + (mHeight));
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
            if (mOrientation == VERTICAL) {
                measureWidth = Math.max(indexBounds.width() + getPaddingLeft() + getPaddingRight(), measureWidth);
            } else {
                measureWidth = Math.max(indexBounds.width(), measureWidth);
            }
            //循环结束后，得到index的最大高度，然后*size
            if (mOrientation == VERTICAL) {
                measureHeight = Math.max(indexBounds.height(), measureHeight);
            } else {
                measureHeight = Math.max(indexBounds.height() + getPaddingTop() + getPaddingBottom(), measureHeight);
            }
            Log.i(TAG, "onMeasure: index=" + index + "  width=" + indexBounds.width() + "  height=" + indexBounds.height()
                    + "  measureWidth=" + measureWidth + "  measureHeight=" + measureHeight);

        }
        if (mOrientation == VERTICAL) {
            measureHeight *= indexDatas.size();
        } else {
            measureWidth *= indexDatas.size();
        }
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

    OnIndexPressListener mOnIndexPressListener;

    public void setOnIndexPressListener(OnIndexPressListener mOnIndexPressListener) {
        this.mOnIndexPressListener = mOnIndexPressListener;
    }

    public interface OnIndexPressListener {
        /**
         * @param index 当前选中的位置
         * @param text  选中的文字
         */
        void onIndexChange(int index, String text);

        /**
         * 事件结束时回调
         */
        void onMotionEventEnd();
    }

    /**
     * 原始数据
     */
    List<? extends BaseIndexBean> sourceDatas;

    /**
     * 设置原始数据
     *
     * @param sourceDatas
     */
    public void setSourceDatas(List<? extends BaseIndexBean> sourceDatas) {
        this.sourceDatas = sourceDatas;
        initIndexDatas();
        invalidateMySelft();

    }

    private void invalidateMySelft() {
        if (isMainThread()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    /**
     * 初始原始数据 并提取索引
     */
    private void initIndexDatas() {
        if (null == sourceDatas || sourceDatas.isEmpty()) {
            return;
        }
        IndexDataHelper dataHelper = new IndexDataHelper();
        dataHelper.cover(sourceDatas);
        //源数据无序
        if (!isOrderly) {
            dataHelper.sortDatas(sourceDatas);
        }
        if (useDatasIndex) {
            indexDatas = new ArrayList<>();
            dataHelper.getIndex(sourceDatas, indexDatas);
            computeIndexHeight();
        }
    }

    /**
     * 根据传入的tag返回position
     *
     * @param tag
     * @return
     */
    private int getPosByTag(String tag) {
        if (null == sourceDatas || sourceDatas.isEmpty()) {
            return -1;
        }
        if (TextUtils.isEmpty(tag)) {
            return -1;
        }
        for (int i = 0; i < sourceDatas.size(); i++) {
            if (tag.equals(sourceDatas.get(i).getIndexTag())) {
                return i + mHeadCount;
            }
        }
        return -1;
    }
}
