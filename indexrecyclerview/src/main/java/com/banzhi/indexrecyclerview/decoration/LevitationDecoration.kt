package com.banzhi.indexrecyclerview.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.IntDef
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.banzhi.indexrecyclerview.interfaces.ISupperInterface
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * <pre>
 * @author : No.1
 * @time : 2018/8/2.
 * @desciption : recyclerview悬停
 * @version :
</pre> *
 */
class LevitationDecoration(
    /**
     * 上下文
     */
    var mContext: Context
) : ItemDecoration() {
    /**
     * 画笔
     */
    private val mPaint: Paint

    /**
     * title背景
     */
    private var mTitleColor: Int

    /**
     * title文字颜色
     */
    private var mTextColor: Int

    /**
     * title文字尺寸
     */
    private var mTextSize: Int

    /**
     * 左边距
     */
    private var mTextLeftPadding: Int

    /**
     * title高度
     */
    private var mTitleHeight: Int

    /**
     * recyclerview头部view数量
     */
    var headCount = 0

    /**
     * 绘制内容
     */
    private  var mDatas: List<ISupperInterface?>? = null

    @IntDef(MODE_TRANSLATE, MODE_OVERLAP)
    @Retention(RetentionPolicy.SOURCE)
    annotation class MODE

    private var mode = MODE_TRANSLATE
    fun setTextLeftPadding(textLeftPadding: Int) {
        mTextLeftPadding = textLeftPadding
    }

    fun setDatas(datas: List<ISupperInterface?>?) {
        mDatas = datas
    }

    fun setMode(@MODE mode: Int) {
        this.mode = mode
    }

    fun setTitleColor(@ColorInt color: Int) {
        mTitleColor = color
    }

    fun setTitleColorResource(@ColorRes color: Int) {
        mTitleColor = mContext.resources.getColor(color)
    }

    fun setTextColor(@ColorInt color: Int) {
        mTextColor = color
    }

    fun setTextColorResource(@ColorRes color: Int) {
        mTextColor = mContext.resources.getColor(color)
    }

    fun setTextSize(textSize: Float) {
        mTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            textSize, mContext.resources.displayMetrics
        ).toInt()
    }

    fun setTitleHeight(height: Float) {
        mTitleHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            height, mContext.resources.displayMetrics
        ).toInt()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            //获取child的position
            var position = params.viewLayoutPosition
            if (mDatas == null || mDatas!!.isEmpty() || position > mDatas!!.size - 1 || position < 0) {
                return
            }
            //计算真实位置
            position -= headCount
            if (position > -1) {
                if (position == 0) {
                    drawTitleArea(c, left, right, child, params, position)
                } else {
                    if (null != mDatas!![position]!!.getIndexTag()
                        && mDatas!![position]!!.getIndexTag() != mDatas!![position - 1]!!.getIndexTag()
                    ) {
                        drawTitleArea(c, left, right, child, params, position)
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
    private fun drawTitleArea(
        c: Canvas,
        left: Int,
        right: Int,
        child: View,
        params: RecyclerView.LayoutParams,
        position: Int
    ) {
        mPaint.color = mTitleColor
        c.drawRect(
            left.toFloat(),
            (child.top - params.topMargin - mTitleHeight).toFloat(),
            right.toFloat(),
            (child.top - params.topMargin).toFloat(),
            mPaint
        )
        mPaint.color = mTextColor
        val text = mDatas!![position]!!.getIndexTag()
        val rect = Rect()
        mPaint.getTextBounds(text, 0, text.length, rect)
        c.drawText(
            text,
            (child.paddingLeft + mTextLeftPadding).toFloat(),
            (child.top - params.topMargin - (mTitleHeight / 2 - rect.height() / 2)).toFloat(),
            mPaint
        )
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        var position =
            (parent.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
        position -= headCount
        if (mDatas == null || mDatas!!.isEmpty() || position > mDatas!!.size - 1 || position < 0) {
            return
        }
        val child = parent.findViewHolderForLayoutPosition(position + headCount)!!.itemView
        //定义一个flag，Canvas是否位移过的标志
        val text = mDatas!![position]!!.getIndexTag()
        var flag = false
        if (position + 1 < mDatas!!.size) {
            val nextText = mDatas!![position + 1]!!.getIndexTag()
            //当前第一个可见的Item的tag，不等于其后一个item的tag，说明悬浮的View要切换了
            if (null != text && text != nextText) {
                //当第一个可见的item在屏幕中还剩的高度小于title区域的高度时，我们也该开始做悬浮Title的“交换动画”
                if (child.height + child.top < mTitleHeight) {
                    c.save() //每次绘制前 保存当前Canvas状态，
                    flag = true
                    if (mode == MODE_OVERLAP) {
                        //头部折叠起来的视效
                        //可与123行 c.drawRect 比较，只有bottom参数不一样，由于 child.getHeight() + child.getTop() < mTitleHeight，所以绘制区域是在不断的减小，有种折叠起来的感觉
                        c.clipRect(
                            parent.paddingLeft + mTextSize,
                            parent.paddingTop,
                            parent.right - parent.paddingRight,
                            parent.paddingTop + child.height + child.top
                        )
                    } else {
                        //上滑时，将canvas上移 （y为负数
                        c.translate(0f, (child.height + child.top - mTitleHeight).toFloat())
                    }
                }
            }
        }
        mPaint.color = mTitleColor
        c.drawRect(
            parent.paddingLeft.toFloat(),
            parent.paddingTop.toFloat(),
            (
                    parent.right - parent.paddingRight).toFloat(),
            (parent.paddingTop + mTitleHeight).toFloat(),
            mPaint
        )
        mPaint.color = mTextColor
        val rect = Rect()
        mPaint.getTextBounds(text, 0, text.length, rect)
        c.drawText(
            text, (child.paddingLeft + mTextLeftPadding).toFloat(), (
                    parent.paddingTop + mTitleHeight - (mTitleHeight / 2 - rect.height() / 2)).toFloat(),
            mPaint
        )
        if (flag) {
            c.restore() //恢复画布到之前保存的状态
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        var position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        position -= headCount
        if (mDatas == null || mDatas!!.isEmpty() || position > mDatas!!.size - 1) {
            return
        }
        if (position > -1) {
            if (position == 0) {
                outRect[0, mTitleHeight, 0] = 0
            } else { //其他的通过判断
                val text = mDatas!![position]!!.getIndexTag()
                val lastText = mDatas!![position - 1]!!.getIndexTag()
                if (null != text && text != lastText) {
                    //不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                    outRect[0, mTitleHeight, 0] = 0
                }
            }
        }
    }

    companion object {
        /**
         * 滑动效果
         */
        const val MODE_TRANSLATE = 1

        /**
         * 重叠效果
         */
        const val MODE_OVERLAP = 2
    }

    init {
        mTitleColor = Color.GRAY
        mTextColor = Color.BLACK
        mTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            16f,
            mContext.resources.displayMetrics
        ).toInt()
        //默认设置title左边距为字体大小，避免itemview的paddingleft为0时title紧靠屏幕
        mTextLeftPadding = mTextSize
        mTitleHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            30f,
            mContext.resources.displayMetrics
        ).toInt()
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.textSize = mTextSize.toFloat()
    }
}