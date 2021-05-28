package com.banzhi.indexrecyclerview.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;


import com.banzhi.indexrecyclerview.interfaces.ISupperInterface;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/8/2.
 * @desciption : recyclerview悬停
 * @version :
 * </pre>
 */

public class LevitationDecoration extends RecyclerView.ItemDecoration {

    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * title背景
     */
    private int mTitleColor;
    /**
     * title文字颜色
     */
    private int mTextColor;
    /**
     * title文字尺寸
     */
    private int mTextSize;
    /**
     * 左边距
     */
    private int mTextLeftPadding;
    /**
     * title高度
     */
    private int mTitleHeight;
    /**
     * 上下文
     */
    Context mContext;
    /**
     * recyclerview头部view数量
     */
    int mHeadCount;
    /**
     * 绘制内容
     */
    List<? extends ISupperInterface> mDatas;

    /**
     * 滑动效果
     */
    public static final int MODE_TRANSLATE = 1;
    /**
     * 重叠效果
     */
    public static final int MODE_OVERLAP = 2;

    @IntDef({MODE_TRANSLATE, MODE_OVERLAP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MODE {

    }

    int mode = MODE_TRANSLATE;

    public LevitationDecoration(Context context) {
        mContext = context;
        mTitleColor = Color.GRAY;
        mTextColor = Color.BLACK;
        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, mContext.getResources().getDisplayMetrics());
        //默认设置title左边距为字体大小，避免itemview的paddingleft为0时title紧靠屏幕
        mTextLeftPadding = mTextSize;
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, mContext.getResources().getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
    }

    public void setTextLeftPadding(int textLeftPadding) {
        this.mTextLeftPadding = textLeftPadding;
    }

    public void setDatas(List<? extends ISupperInterface> datas) {
        this.mDatas = datas;
    }

    public void setMode(@MODE int mode) {
        this.mode = mode;
    }

    public int getHeadCount() {
        return mHeadCount;
    }

    public void setHeadCount(int headCount) {
        this.mHeadCount = headCount;
    }

    public void setTitleColor(@ColorInt int color) {
        this.mTitleColor = color;
    }

    public void setTitleColorResource(@ColorRes int color) {
        this.mTitleColor = mContext.getResources().getColor(color);
    }


    public void setTextColor(@ColorInt int color) {
        this.mTextColor = color;
    }

    public void setTextColorResource(@ColorRes int color) {
        this.mTextColor = mContext.getResources().getColor(color);
    }


    public void setTextSize(float textSize) {
        this.mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                textSize, mContext.getResources().getDisplayMetrics());
    }

    public void setTitleHeight(float height) {
        this.mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                height, mContext.getResources().getDisplayMetrics());
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            //获取child的position
            int position = params.getViewLayoutPosition();
            if (mDatas == null || mDatas.isEmpty() || position > mDatas.size() - 1 || position < 0) {
                return;
            }
            //计算真实位置
            position -= mHeadCount;
            if (position > -1) {
                if (position == 0) {
                    drawTitleArea(c, left, right, child, params, position);
                } else {
                    if (null != mDatas.get(position).getIndexTag()
                            && !mDatas.get(position).getIndexTag().equals(mDatas.get(position - 1).getIndexTag())) {
                        drawTitleArea(c, left, right, child, params, position);

                    }

                }
            }
        }
    }

    /**
     * 绘制title区域
     *
     * @param c
     * @param left
     * @param right
     * @param child
     * @param params
     * @param position
     */

    private void drawTitleArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {
        mPaint.setColor(mTitleColor);
        c.drawRect(left, child.getTop() - params.topMargin - mTitleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(mTextColor);
        String text = mDatas.get(position).getIndexTag();
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);
        c.drawText(text, child.getPaddingLeft() + mTextLeftPadding, child.getTop() - params.topMargin - (mTitleHeight / 2 - rect.height() / 2), mPaint);
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        position -= mHeadCount;

        if (mDatas == null || mDatas.isEmpty() || position > mDatas.size() - 1 || position < 0) {
            return;
        }
        View child = parent.findViewHolderForLayoutPosition(position + mHeadCount).itemView;
        //定义一个flag，Canvas是否位移过的标志
        String text = mDatas.get(position).getIndexTag();
        boolean flag = false;
        if ((position + 1) < mDatas.size()) {
            String nextText = mDatas.get(position + 1).getIndexTag();
            //当前第一个可见的Item的tag，不等于其后一个item的tag，说明悬浮的View要切换了
            if (null != text && !text.equals(nextText)) {
                //当第一个可见的item在屏幕中还剩的高度小于title区域的高度时，我们也该开始做悬浮Title的“交换动画”
                if (child.getHeight() + child.getTop() < mTitleHeight) {
                    c.save();//每次绘制前 保存当前Canvas状态，
                    flag = true;

                    if (mode == MODE_OVERLAP) {
                        //头部折叠起来的视效
                        //可与123行 c.drawRect 比较，只有bottom参数不一样，由于 child.getHeight() + child.getTop() < mTitleHeight，所以绘制区域是在不断的减小，有种折叠起来的感觉
                        c.clipRect(parent.getPaddingLeft() + mTextSize, parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + child.getHeight() + child.getTop());
                    } else {
                        //上滑时，将canvas上移 （y为负数
                        c.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
                    }

                }
            }
        }
        mPaint.setColor(mTitleColor);
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(),
                parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mTitleHeight, mPaint);
        mPaint.setColor(mTextColor);
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);
        c.drawText(text, child.getPaddingLeft() + mTextLeftPadding,
                parent.getPaddingTop() + mTitleHeight - (mTitleHeight / 2 - rect.height() / 2),
                mPaint);
        if (flag) {
            c.restore();//恢复画布到之前保存的状态
        }

    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        position -= mHeadCount;
        if (mDatas == null || mDatas.isEmpty() || position > mDatas.size() - 1) {
            return;
        }
        if (position > -1) {
            if (position == 0) {
                outRect.set(0, mTitleHeight, 0, 0);
            } else {//其他的通过判断
                String text = mDatas.get(position).getIndexTag();
                String lastText = mDatas.get(position - 1).getIndexTag();
                if (null != text && !text.equals(lastText)) {
                    //不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                    outRect.set(0, mTitleHeight, 0, 0);
                }
            }
        }
    }


}
