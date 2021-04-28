package com.jeffbrandon.recipebinder.util

import android.graphics.Point
import android.view.View
import com.jeffbrandon.recipebinder.R

class DragShadowHelper(view: View) : View.DragShadowBuilder(view) {
    override fun onProvideShadowMetrics(outShadowSize: Point, outShadowTouchPoint: Point) {
        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint)
        outShadowTouchPoint.set(view.width - (view.resources.getDimension(R.dimen.drag_target_width).toInt() / 2),
                                view.height / 2)
    }
}
