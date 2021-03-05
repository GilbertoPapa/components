package com.gilbertopapa.component.slide.interfaces

import android.view.View

interface MultiSlideUpPanelListener {
    fun onPanelDragging(panel: View,offset:Float,velocity:Float=0f)
    fun onPanelCollapsed(panel:View)
    fun onPanelExpanded(panel:View)
    fun onPanelAnchored(panel:View)
    fun onPanelHidden(panel:View)
}