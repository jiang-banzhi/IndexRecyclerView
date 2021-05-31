package com.banzhi.indexrecyclerview.widget

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.banzhi.indexrecyclerview.bean.BaseIndexBean
import com.banzhi.indexrecyclerview.decoration.LevitationDecoration
import com.banzhi.indexrecyclerview.interfaces.IDataHelper
import com.banzhi.indexrecyclerview.utils.IndexDataHelper
import java.util.*

/**
 * <pre>
 * @author :
 * @time : 2021/5/31.
 * @desciption : 索引
 * @version :
</pre> *
 */
class IndexBar : View {
    private var mContext: Context? = null

    /**
     * 每个index的高度
     */
    private var indexHeght = 0

    /**
     * view宽度
     */
    private var mWidth = 0

    /**
     * view高度
     */
    private var mHeight = 0

    /**
     * 画笔
     */
    private var mPaint: Paint? = null

    /**
     * 按下时的背景颜色
     */
    private var mPressBackground = 0

    /**
     * 文字颜色
     */
    private var mTextColor = 0

    /**
     * 按下时文字的颜色
     */
    private var mPressTextColor = 0

    /**
     * 文字选中的颜色
     */
    private var mSelectTextColor = 0

    /**
     * 字体大小
     */
    private var textSize = 0
    private val DEFAULT_PRESS_COLOR = Color.GRAY
    private val DEFAULT_BACKGROUND = Color.TRANSPARENT
    private var indexDatas: MutableList<String>? = null

    /**
     * 使用数据内容作为索引
     */
    private var useDatasIndex = false

    /**
     * 数据是否有序
     */
    private var isOrderly = false

    /**
     * 临时保存view背景颜色
     */
    private var color = R.color.transparent

    /**
     * indexbar方向
     */
    private var mOrientation = 0

    /**
     * 数据转换帮助类
     */
    private var mDataHelper: IDataHelper? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    constructor(context: Context) : super(context) {
        init(context, null, -1)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, -1)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        mContext = context
        //默认的TextSize
        val DEFAULT_SIZE =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, resources.displayMetrics)
                .toInt()
        val typedArray = context.obtainStyledAttributes(
            attrs,
            com.banzhi.indexrecyclerview.R.styleable.IndexrecyclerviewIndexBar
        )
        if (typedArray != null) {
            textSize = typedArray.getDimensionPixelSize(
                com.banzhi.indexrecyclerview.R.styleable.IndexrecyclerviewIndexBar_indexTextSize,
                DEFAULT_SIZE
            )
            mPressBackground = typedArray.getColor(
                com.banzhi.indexrecyclerview.R.styleable.IndexrecyclerviewIndexBar_pressBackground,
                DEFAULT_BACKGROUND
            )
            mTextColor = typedArray.getColor(
                com.banzhi.indexrecyclerview.R.styleable.IndexrecyclerviewIndexBar_indexTextColor,
                DEFAULT_PRESS_COLOR
            )
            mPressTextColor = typedArray.getColor(
                com.banzhi.indexrecyclerview.R.styleable.IndexrecyclerviewIndexBar_pressTextColor,
                mTextColor
            )
            mSelectTextColor = typedArray.getColor(
                com.banzhi.indexrecyclerview.R.styleable.IndexrecyclerviewIndexBar_selectTextColor,
                mTextColor
            )
            mOrientation = typedArray.getInt(
                com.banzhi.indexrecyclerview.R.styleable.IndexrecyclerviewIndexBar_orientation,
                VERTICAL
            )
        }
        initPaint()
        initDatas()
        setOnIndexPressListener(object : OnIndexPressListener {
            override fun onIndexChange(index: Int, text: String) {
                if (textView != null) {
                    textView!!.text = text
                    textView!!.visibility = VISIBLE
                }
                if (layoutManager != null) {
                    val position = getPosByTag(text)
                    Log.i(TAG, "onIndexChange: position===>$position")
                    if (position > -1) {
                        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                            position,
                            0
                        )
                    }
                }
            }

            override fun onMotionEventEnd() {
                if (textView != null) {
                    textView!!.visibility = GONE
                }
            }
        })
    }

    private var textView: TextView? = null
    fun setTextView(textView: TextView?) {
        this.textView = textView
    }

    var mRecyclerView: RecyclerView? = null
    var mHeadCount = 0
    fun bindRecyclerView(recyclerView: RecyclerView?) {
        mRecyclerView = recyclerView
        if (mRecyclerView != null) {
            layoutManager = mRecyclerView!!.layoutManager
            val itemDecoration = mRecyclerView!!.getItemDecorationAt(0)
            if (itemDecoration is LevitationDecoration) {
                mHeadCount = itemDecoration.headCount
            }
        }
    }

    private fun initDatas() {
        indexDatas = if (!useDatasIndex) {
            Arrays.asList(*DEFAULT_INDEX)
        } else {
            ArrayList()
        }
    }

    private fun initPaint() {
        mPaint = Paint()
        mPaint!!.textSize = textSize.toFloat()
        mPaint!!.isAntiAlias = true
    }

    var isPress = false

    @SuppressLint("ResourceAsColor")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            isPress = true
            val background = background
            if (background != null) {
                color = (background as ColorDrawable).color
            }
            setBackgroundColor(mPressBackground)
            computePressIndexLocation(event.x, event.y)
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            computePressIndexLocation(event.x, event.y)
        } else {
            isPress = false
            //手指抬起时背景恢复透明
            setBackgroundColor(color)
            //重置当前位置
            currentIndex = -1
            if (mOnIndexPressListener != null) {
                mOnIndexPressListener!!.onMotionEventEnd()
            }
        }
        return true
    }

    private var currentIndex = -1

    /**
     * 计算按下的位置
     */
    private fun computePressIndexLocation(x: Float, y: Float) {
        currentIndex = if (mOrientation == VERTICAL) {
            // 计算按下的区域位置
            ((y - paddingTop) / indexHeght).toInt()
        } else {
            ((x - paddingLeft) / indexHeght).toInt()
        }
        if (currentIndex < 0) {
            currentIndex = 0
        } else if (currentIndex >= indexDatas!!.size) {
            currentIndex = indexDatas!!.size - 1
        }
        invalidateMySelft()
        if (mOnIndexPressListener != null) {
            mOnIndexPressListener!!.onIndexChange(currentIndex, indexDatas!![currentIndex])
        }
    }

    /**
     * 源数据时候有序
     *
     * @param orderly
     */
    fun setOrderly(orderly: Boolean) {
        isOrderly = orderly
    }

    /**
     * 使用源数据作为索引
     */
    fun setUseDatasIndex() {
        useDatasIndex = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        computeIndexHeight()
    }

    /**
     * 计算单个index高度
     */
    private fun computeIndexHeight() {
        indexHeght = if (mOrientation == VERTICAL) {
            (mHeight - paddingTop - paddingBottom) / indexDatas!!.size
        } else {
            (mWidth - paddingLeft - paddingRight) / indexDatas!!.size
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in indexDatas!!.indices) {
            val index = indexDatas!![i]
            val metrics = mPaint!!.fontMetrics
            //计算baseline
            val baseLine = ((indexHeght - metrics.bottom - metrics.top) / 2).toInt()
            if (currentIndex == i) {
                mPaint!!.color = mSelectTextColor
            } else {
                mPaint!!.color = if (isPress) mPressTextColor else mTextColor
            }
            //绘制文字
            if (mOrientation == VERTICAL) {
                canvas.drawText(
                    index, mWidth / 2 - mPaint!!.measureText(index) / 2, (
                            paddingTop + baseLine + indexHeght * i).toFloat(), mPaint
                )
            } else {
                canvas.drawText(
                    index,
                    paddingLeft + indexHeght * i + mPaint!!.measureText(index) / 2,
                    (mHeight - paddingBottom
                            ).toFloat(),
                    mPaint
                )
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //取出宽高的MeasureSpec  Mode 和Size
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)
        //最终测量出来的宽高
        var measureWidth = 0
        var measureHeight = 0

        //得到合适宽度：
        //存放每个绘制的index的Rect区域
        val indexBounds = Rect()
        var index: String //每个要绘制的index内容
        for (i in indexDatas!!.indices) {
            index = indexDatas!![i]
            //测量计算文字所在矩形，可以得到宽高
            mPaint!!.getTextBounds(index, 0, index.length, indexBounds)
            //循环结束后，得到index的最大宽度
            measureWidth = if (mOrientation == VERTICAL) {
                Math.max(indexBounds.width() + paddingLeft + paddingRight, measureWidth)
            } else {
                Math.max(indexBounds.width(), measureWidth)
            }
            //循环结束后，得到index的最大高度，然后*size
            measureHeight = if (mOrientation == VERTICAL) {
                Math.max(indexBounds.height(), measureHeight)
            } else {
                Math.max(indexBounds.height() + paddingTop + paddingBottom, measureHeight)
            }
        }
        if (mOrientation == VERTICAL) {
            measureHeight *= indexDatas!!.size
        } else {
            measureWidth *= indexDatas!!.size
        }
        if (wMode == MeasureSpec.EXACTLY) {
            measureWidth = wSize
        } else if (wMode == MeasureSpec.AT_MOST) {
            //wSize此时是父控件能给子View分配的最大空间
            measureWidth = Math.min(measureWidth, wSize)
        } else if (wMode == MeasureSpec.UNSPECIFIED) {
        }
        //得到合适的高度：
        if (hMode == MeasureSpec.EXACTLY) {
            measureHeight = hSize
        } else if (hMode == MeasureSpec.AT_MOST) {
            //wSize此时是父控件能给子View分配的最大空间
            measureHeight = Math.min(measureHeight, hSize)
        } else if (hMode == MeasureSpec.UNSPECIFIED) {
        }
        setMeasuredDimension(measureWidth, measureHeight)
    }

    var mOnIndexPressListener: OnIndexPressListener? = null
    fun setOnIndexPressListener(mOnIndexPressListener: OnIndexPressListener?) {
        this.mOnIndexPressListener = mOnIndexPressListener
    }

    interface OnIndexPressListener {
        /**
         * @param index 当前选中的位置
         * @param text  选中的文字
         */
        fun onIndexChange(index: Int, text: String)

        /**
         * 事件结束时回调
         */
        fun onMotionEventEnd()
    }

    /**
     * 原始数据
     */
    private var sourceDatas: MutableList<out BaseIndexBean>? = null

    /**
     * 设置原始数据
     *
     * @param sourceDatas
     */
    fun setSourceDatas(sourceDatas: MutableList<out BaseIndexBean>?) {
        this.sourceDatas = sourceDatas
        initIndexDatas()
        invalidateMySelft()
    }

    private fun invalidateMySelft() {
        if (isMainThread) {
            invalidate()
        } else {
            postInvalidate()
        }
    }

    val isMainThread: Boolean
        get() = Thread.currentThread() === Looper.getMainLooper().thread

    /**
     * 初始原始数据 并提取索引
     */
    private fun initIndexDatas() {
        if (sourceDatas.isNullOrEmpty()) {
            return
        }
        if (mDataHelper == null) {
            mDataHelper = IndexDataHelper()
        }
        mDataHelper!!.cover(sourceDatas!!)
        //源数据无序
        if (!isOrderly) {
            mDataHelper!!.sortDatas(sourceDatas!!)
        }
        if (useDatasIndex) {
            indexDatas = mutableListOf()
            mDataHelper!!.getIndex(sourceDatas!!, indexDatas!!)
            computeIndexHeight()
        }
    }

    /**
     * 根据传入的tag返回position
     *
     * @param tag
     * @return
     */
    private fun getPosByTag(tag: String): Int {
        if (null == sourceDatas || sourceDatas!!.isEmpty()) {
            return -1
        }
        if (TextUtils.isEmpty(tag)) {
            return -1
        }
        for (i in sourceDatas!!.indices) {
            if (tag == sourceDatas!![i]!!.getIndexTag()) {
                return i + mHeadCount
            }
        }
        return -1
    }

    companion object {
        private const val TAG = "tag"
        const val VERTICAL = 1
        const val HORIZONTAL = 2

        /**
         * 默认索引
         */
        private val DEFAULT_INDEX = arrayOf(
            "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"
        )
    }
}