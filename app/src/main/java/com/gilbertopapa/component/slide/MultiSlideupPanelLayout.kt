package com.gilbertopapa.component.slide

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.gilbertopapa.component.R

class MultiSlideupPanelLayout : ViewGroup.MarginLayoutParams {

    var enable: Boolean = true
    var clickable = true
    var heightPercent: Float = 0f
    var state = PanelState.COLLAPSED

    constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
        val att = c.obtainStyledAttributes(attrs, R.styleable.MultiSlideUpPanel_Layout)
        enable = att.getBoolean(R.styleable.MultiSlideUpPanel_Layout_panelEnable, enable)
        clickable =
            att.getBoolean(R.styleable.MultiSlideUpPanel_Layout_panelClickable, clickable)
        heightPercent =
            att.getFloat(R.styleable.MultiSlideUpPanel_Layout_panelHeightPercent, heightPercent)
        state = Utils.getState(
            att.getInt(
                R.styleable.MultiSlideUpPanel_Layout_panelState,
                state.value
            )
        )
        att.recycle()
    }

    constructor() : super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    constructor(width: Int, height: Int) : super(width, height)
    constructor(source: ViewGroup.MarginLayoutParams?) : super(source)
    constructor(source: ViewGroup.LayoutParams?) : super(source)


}