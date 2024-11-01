package com.jeffbrandon.recipebinder.fragments

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

object EdgeToEdgeUtil {
    fun padInsets(view: View, updateLayoutParams: Boolean = false) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v: View, windowInsets: WindowInsetsCompat ->
            val insets =
                windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            if (updateLayoutParams) {
                v.updateLayoutParams<MarginLayoutParams> {
                    leftMargin = insets.left
                    topMargin = insets.top
                    rightMargin = insets.right
                    bottomMargin = insets.bottom
                }
            } else {
                v.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            }
            WindowInsetsCompat.CONSUMED
        }
    }
}
