package com.abner.list

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 *AUTHOR:AbnerMing
 *DATE:2022/11/14
 *INTRODUCE:
 */
class KeyboardView : View {
    private var mOnPositionClickListener: OnPositionClickListener? = null
    private var mOnSingleClickListener: OnSingleClickListener? = null
    private var mRectWidth: Float? = null
    private var mPaint: Paint? = null
    private var mSpacing = 10f//键盘格子默认间隔
    private var mHeight = 100f//键盘格子默认高度
    private var mWordArray =
        arrayOf("A B C", "D E F", "G H I", "J K L", "M N O", "P Q R S", "U V W", "X Y Z")

    //默认背景色
    private var mBackGroundColor = Color.parseColor("#1A1A1A")

    //默认格子背景色
    private var mRectBackGroundColor = Color.parseColor("#646465")

    //默认按下效果背景色
    private var mDownBackGroundColor = mBackGroundColor

    //默认数字颜色
    private var mTextColor = Color.parseColor("#ffffff")

    //默认数字大小
    private var mTextSize = 50f

    //默认的字母文字大小
    private var mLetterTextSize = 20f

    //默认是否显示字母
    private var mIsShowLetter = true

    //存储按下的数字
    private var mNumStringBuffer = StringBuffer()

    //按下的索引
    private var mDownPosition = -1

    //默认按下的数字总长度
    private var mNumberSize = 6

    constructor(
        context: Context
    ) : super(context) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        val obt = context.obtainStyledAttributes(attrs, R.styleable.KeyboardView)
        //获取属性中的背景颜色
        mBackGroundColor = obt.getColor(R.styleable.KeyboardView_background_color, mBackGroundColor)
        //获取格子背景色
        mRectBackGroundColor =
            obt.getColor(R.styleable.KeyboardView_rect_background_color, mRectBackGroundColor)
        //获取属性按下的背景颜色
        mDownBackGroundColor =
            obt.getColor(R.styleable.KeyboardView_down_background_color, mDownBackGroundColor)
        //文字颜色
        mTextColor = obt.getColor(R.styleable.KeyboardView_text_color, mTextColor)
        //文字大小
        mTextSize = obt.getDimension(R.styleable.KeyboardView_text_size, mTextSize)
        //字母的文字大小
        mLetterTextSize =
            obt.getDimension(R.styleable.KeyboardView_letter_text_size, mLetterTextSize)
        //格子高度
        mHeight = obt.getDimension(R.styleable.KeyboardView_rect_height, mHeight)
        //格子间距
        mSpacing = obt.getDimension(R.styleable.KeyboardView_rect_spacing, mSpacing)
        //是否显示字母
        mIsShowLetter = obt.getBoolean(R.styleable.KeyboardView_is_rect_letter, mIsShowLetter)
        //设置的按下的数字总长度
        mNumberSize = obt.getInt(R.styleable.KeyboardView_number_size, mNumberSize)
        initView()
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:隐藏字母
     */
    fun hintLetter() {
        mIsShowLetter = false
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:设置整体背景色
     */
    fun setBackGroundColor(backGroundColor: Int) {
        mBackGroundColor = backGroundColor
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:设置数字格子背景色
     */
    fun setRectBackGroundColor(rectBackGroundColor: Int) {
        mRectBackGroundColor = rectBackGroundColor
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:设置文字颜色
     */
    fun setTextColor(textColor: Int) {
        mTextColor = textColor
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:设置按下的数字总长度
     */
    fun setNumberSize(size: Int) {
        mNumberSize = size
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:设置数字大小
     */
    fun setTextSize(textSize: Float) {
        mTextSize = textSize
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:设置数字键盘每格高度
     */
    fun setRectHeight(height: Float) {
        mHeight = height
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:设置数字键盘每格间隔距离
     */
    fun setSpacing(spacing: Float) {
        mSpacing = spacing
    }


    private fun initView() {
        //初始化默认背景
        setBackgroundColor(mBackGroundColor)
        //创建画笔
        mPaint = Paint()
        mPaint!!.color = mRectBackGroundColor
        mPaint!!.strokeWidth = 10f
        mPaint!!.isAntiAlias = true
        mPaint!!.textSize = mTextSize
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mRectWidth = (width - mSpacing * 4) / 3
        mPaint!!.strokeWidth = 10f
        //绘制数字键盘
        for (i in 0..11) {
            //设置方格
            val rect = RectF()
            val iTemp = i / 3
            val rectTop = mHeight * iTemp + mSpacing * (iTemp + 1f)
            rect.top = rectTop
            rect.bottom = rect.top + mHeight
            var leftSpacing = (mSpacing * (i % 3f))
            leftSpacing += mSpacing
            rect.left = mRectWidth!! * (i % 3f) + leftSpacing
            rect.right = rect.left + mRectWidth!!
            //9的位置是空的，跳过不绘制
            if (i == 9) {
                continue
            }
            //11的位置，是删除按钮，直接绘制删除按钮
            if (i == 11) {
                drawDelete(canvas, rect.right, rect.top)
                continue
            }
            mPaint!!.textSize = mTextSize
            mPaint!!.style = Paint.Style.FILL
            //按下的索引 和 方格的 索引一致,改变背景颜色
            if (mDownPosition == (i + 1)) {
                mPaint!!.color = mDownBackGroundColor
            } else {
                mPaint!!.color = mRectBackGroundColor
            }
            //绘制方格
            canvas!!.drawRoundRect(rect, 10f, 10f, mPaint!!)

            //绘制数字
            mPaint!!.color = mTextColor
            var keyWord = "${i + 1}"
            //索引等于 10 从新赋值为 0
            if (i == 10) {
                keyWord = "0"
            }
            val rectWord = Rect()
            mPaint!!.getTextBounds(keyWord, 0, keyWord.length, rectWord)
            val wWord = rectWord.width()
            val htWord = rectWord.height()
            var yWord = rect.bottom - mHeight / 2 + (htWord / 2)
            //上移
            if (i != 0 && i != 10 && mIsShowLetter) {
                yWord -= htWord / 3
            }
            canvas.drawText(
                keyWord,
                rect.right - mRectWidth!! / 2 - (wWord / 2),
                yWord,
                mPaint!!
            )

            //绘制字母
            if ((i in 1..8) && mIsShowLetter) {
                mPaint!!.textSize = mLetterTextSize
                val s = mWordArray[i - 1]
                val rectW = Rect()
                mPaint!!.getTextBounds(s, 0, s.length, rectW)
                val w = rectW.width()
                val h = rectW.height()
                canvas.drawText(
                    s,
                    rect.right - mRectWidth!! / 2 - (w / 2),
                    rect.bottom - mHeight / 2 + h * 2,
                    mPaint!!
                )
            }
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            //当高度为 wrap_content 时 设置一个合适的高度
            setMeasuredDimension(widthSpecSize, (mHeight * 4 + mSpacing * 5 + 10).toInt())
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val upX = event.x
                val upY = event.y
                val touchPosition = getTouch(upX, upY)
                if (touchPosition != 10 && touchPosition != -1 && touchPosition != -2) {
                    //记录按下的方格索引
                    mDownPosition = touchPosition
                    //0的位置  索引 纠正为11
                    if (mDownPosition == 0) {
                        mDownPosition = 11
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                //手指抬起
                val upX = event.x
                val upY = event.y
                val touchPosition = getTouch(upX, upY)
                if (touchPosition != 10 && touchPosition != -2) {
                    if (touchPosition != -1 && mNumStringBuffer.length < mNumberSize) {
                        //小于6添加
                        mNumStringBuffer.append(touchPosition)
                    } else if (touchPosition == -1 && mNumStringBuffer.isNotEmpty()) {
                        mNumStringBuffer.deleteCharAt(mNumStringBuffer.length - 1)
                    }
                    if (touchPosition != -1 && mOnSingleClickListener != null) {
                        mOnSingleClickListener?.single(touchPosition)
                    }

                    if (mOnPositionClickListener != null) {
                        mOnPositionClickListener!!.positionClick(mNumStringBuffer.toString())
                    }
                }

                mDownPosition = -1
                invalidate()
            }
        }
        return true
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:返回触摸的位置
     */
    private fun getTouch(upX: Float, upY: Float): Int {
        var position = -2
        for (i in 0..11) {
            val iTemp = i / 3
            val rectTop = mHeight * iTemp + mSpacing * (iTemp + 1f)
            val top = rectTop
            val bottom = top + mHeight
            var leftSpacing = (mSpacing * (i % 3f))
            leftSpacing += 10f
            val left = mRectWidth!! * (i % 3f) + leftSpacing
            val right = left + mRectWidth!!
            if (upX > left && upX < right && upY > top && upY < bottom) {
                position = i + 1
                //位置11默认为 数字  0
                if (position == 11) {
                    position = 0
                }
                //位置12  数字为 -1 意为删除
                if (position == 12) {
                    position = -1
                }
            }
        }
        return position
    }


    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:点击回调
     */
    fun setOnNumClickListener(action: (String) -> Unit) {
        mOnPositionClickListener = object : OnPositionClickListener {
            override fun positionClick(num: String) {
                action(num)
            }
        }
    }

    interface OnPositionClickListener {
        //-1为点击了删除按键
        fun positionClick(num: String)
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:单个点击
     */
    fun setOnSingleClickListener(action: (Int) -> Unit) {
        mOnSingleClickListener = object : OnSingleClickListener {
            override fun single(num: Int) {
                action(num)
            }
        }
    }

    interface OnSingleClickListener {
        fun single(num: Int)
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:绘制删除按键，直接canvas自绘，不使用图片
     */
    private fun drawDelete(canvas: Canvas?, right: Float, top: Float) {
        val rWidth = 15
        val lineWidth = 35
        val x = right - mRectWidth!! / 2 - (rWidth + lineWidth) / 4
        val y = top + mHeight / 2
        val path = Path()
        path.moveTo(x - rWidth, y)
        path.lineTo(x, y - rWidth)
        path.lineTo(x + lineWidth, y - rWidth)
        path.lineTo(x + lineWidth, y + rWidth)
        path.lineTo(x, y + rWidth)
        path.lineTo(x - rWidth, y)
        path.close()
        mPaint!!.strokeWidth = 2f
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.color = mTextColor
        canvas!!.drawPath(path, mPaint!!)

        //绘制小×号
        mPaint!!.style = Paint.Style.FILL
        mPaint!!.textSize = 30f
        val content = "×"
        val rectWord = Rect()
        mPaint!!.getTextBounds(content, 0, content.length, rectWord)
        val wWord = rectWord.width()
        val htWord = rectWord.height()
        canvas.drawText(
            content,
            right - mRectWidth!! / 2 - wWord / 2 + 3,
            y + htWord / 3 * 2 + 2,
            mPaint!!
        )

    }
}