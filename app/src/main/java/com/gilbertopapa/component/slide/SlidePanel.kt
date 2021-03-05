package com.gilbertopapa.component.slide

import android.view.View

data class SlidePanel(
    var enable: Boolean = true,
    var hper: Float = -1f,
    var clickable: Boolean = true,
    var state: PanelState = PanelState.COLLAPSED,
    var panel: View
)