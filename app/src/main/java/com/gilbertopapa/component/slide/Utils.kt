package com.gilbertopapa.component.slide

import android.content.Context
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.Gravity

object Utils {

    fun dp2px(dp: Float,context: Context)= applyDimension(COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()

    fun getState(state: Int): PanelState {
        return when (state) {
            0 -> PanelState.EXPANDED
            1 -> PanelState.COLLAPSED
            2 -> PanelState.ANCHORED
            3 -> PanelState.HIDDEN
            4 -> PanelState.DRAGGING
            else -> PanelState.COLLAPSED
        }
    }

    fun getGravity(intGrav:Int):Int{
        return if(intGrav==0) Gravity.TOP else Gravity.BOTTOM
    }
}