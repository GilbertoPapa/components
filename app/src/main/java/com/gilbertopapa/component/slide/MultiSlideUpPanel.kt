package com.gilbertopapa.component.slide

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.gilbertopapa.component.R
import com.gilbertopapa.component.slide.interfaces.MultiSlideUpPanelListener

open class MultiSlideUpPanel @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {


    private val DEFAULT_OFFSET = 0.3
    private var parallax: Boolean = false
    private var velocity: Float = 400f
    private var fadeColor: Int = 0
    private var decorEnable = true
    private var decorGravity = Gravity.BOTTOM


    private var panels = arrayListOf<SlidePanel>()
    private val dragger = ViewDragHelper.create(this, 0.6f, DragHandler())


    private var panelListener: MultiSlideUpPanelListener? = null


    init {
        val att = context.obtainStyledAttributes(attrs, R.styleable.MultiSlideUpPanel)
        parallax = att.getBoolean(R.styleable.MultiSlideUpPanel_decorParallax, parallax)
        velocity = att.getFloat(R.styleable.MultiSlideUpPanel_decorVelocity, velocity)
        fadeColor = att.getColor(R.styleable.MultiSlideUpPanel_decorFadeColor, fadeColor)
        decorEnable = att.getBoolean(R.styleable.MultiSlideUpPanel_decorEnable, decorEnable)
        decorGravity = Utils.getGravity(att.getInt(R.styleable.MultiSlideUpPanel_decorGravity, 1))
        att.recycle()


        dragger.minVelocity = velocity * context.resources.displayMetrics.density
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // init views in panel array
        if (panels.size < childCount) {
            for (i in 0 until childCount) {
                val params = getChildAt(i).layoutParams as MultiSlideupPanelLayout
                panels.add(
                    SlidePanel(
                        params.enable,
                        params.heightPercent,
                        params.clickable,
                        params.state,
                        getChildAt(i)
                    )
                )
            }
        }

        isFocusableInTouchMode = true
        requestFocus()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        check(widthMode == MeasureSpec.EXACTLY) { "Width must have an exact value or MATCH_PARENT" }


        val layoutHeight = heightSize - paddingTop - paddingBottom


        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams as LayoutParams


            val childWidthSpec: Int
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST)
            } else if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY)
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY)
            }

            val childHeightSpec: Int
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.AT_MOST)
            } else if (lp.height == LayoutParams.MATCH_PARENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.EXACTLY)
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY)
            }
            child.measure(childWidthSpec, childHeightSpec)
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        for (i in 0 until childCount) {
            val child = getChildAt(i)


            val offset: Int = if (panels[i].hper > 0) (measuredHeight * panels[i].hper).toInt() else (measuredHeight * DEFAULT_OFFSET).toInt()


            var childTop: Int
            var childBottom: Int


            if (isBottom()) {
                childTop = measuredHeight - offset
                childBottom = child.measuredHeight + childTop
            } else {
                childTop = -(child.measuredHeight + offset)
                childBottom = offset
            }

            val childLeft = (measuredWidth / 2) - ((child.measuredWidth + child.paddingLeft + child.paddingRight) / 2)
            val childRight = (measuredWidth / 2) + ((child.measuredWidth + child.paddingLeft + child.paddingRight) / 2)

            child.layout(childLeft, childTop, childRight, childBottom)
        }
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is MultiSlideupPanelLayout && super.checkLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MultiSlideupPanelLayout(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MultiSlideupPanelLayout()
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return if (p is MarginLayoutParams)
            MultiSlideupPanelLayout(p)
        else
            MultiSlideupPanelLayout(p)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        super.onInterceptTouchEvent(ev)
        return dragger.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragger.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (dragger != null && dragger.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    inner class DragHandler : ViewDragHelper.Callback() {
        var panel: SlidePanel? = null
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
//            super.onViewPositionChanged(changedView, left, top, dx, dy)
            Log.e("APP", "Position: $left   $top    $dx    $dy")
            invalidate()
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
//            super.onViewCaptured(capturedChild, activePointerId)
            for (pan in panels) {
                if (pan.panel == capturedChild) {
                    panel = pan
                }
            }
            Log.e("APP", "Captured $capturedChild")
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {

            val offset = panelOffset(panel!!, panel!!.panel.top)


            val direction = if (isBottom()) -yvel else yvel

            val target: Int = if (direction > 0) {
                 panelTopPosition(panel!!, 1.0f)
            } else {
                 panelTopPosition(panel!!, 0f)
            }

            dragger.settleCapturedViewAt(panel!!.panel.left, target)
            invalidate()



            Log.e("APP", "Relased $releasedChild")
        }

        override fun onViewDragStateChanged(state: Int) {


            Log.e("APP", "State $state")
        }


        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val collapsedTop = panelTopPosition(panel!!, 0f)
            val expandedTop = panelTopPosition(panel!!, 1.0f)
            invalidate()
            return Math.min(Math.max(top, expandedTop), collapsedTop)
        }
    }


    fun isBottom(): Boolean = decorGravity == Gravity.BOTTOM

    fun getPanelRange(panel: SlidePanel): Int = (measuredHeight * panel.hper).toInt()


    private fun panelTopPosition(vpanel: SlidePanel, slideOffset: Float): Int {
        val slidePixelOffset = (slideOffset * (measuredHeight - getPanelRange(vpanel))).toInt()
        return measuredHeight - paddingBottom - getPanelRange(vpanel) - slidePixelOffset
    }

    private fun panelOffset(panel: SlidePanel, topPosition: Int): Float {
        val topBoundCollapsed = panelTopPosition(panel, 0f)
        return (topBoundCollapsed - topPosition).toFloat() / (measuredHeight - getPanelRange(panel))
    }


    private fun smoothScrollto(panel: SlidePanel, slideOffset: Float) {
        val panelTop = panelTopPosition(panel, slideOffset)
        if (dragger.smoothSlideViewTo(panel.panel, panel.panel.left, panelTop)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }


    fun setListener(listener: MultiSlideUpPanelListener) {
        this.panelListener = listener
    }

}