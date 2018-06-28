package vn.eazy.harryle.capture

import android.view.View

fun View.getPositionOnScreen(): IntArray {
    val pos = IntArray(2)
    this.getLocationInWindow(pos)
    return pos
}