package vn.eazy.harryle.capture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import java.io.File
import java.io.FileOutputStream

class CachingLayout : FrameLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var ignoreViews: ArrayList<Int> = ArrayList()
    val parentPosition: IntArray by lazy {
        val pos = IntArray(2)
        getLocationInWindow(pos)
        pos
    }
    private var cachingViewObserverListener: CachingViewObserverListener? = null

    fun getCacheBitmap(callback: (bitmap: Bitmap?) -> Any?) {
        if (width <= 0 || height <= 0) {
            cachingViewObserverListener = CachingViewObserverListener(this, callback)
            viewTreeObserver.addOnGlobalLayoutListener(cachingViewObserverListener)
        } else {
            captureViews(callback)
        }
    }

    override fun onDetachedFromWindow() {
        cachingViewObserverListener = null
        super.onDetachedFromWindow()
    }

    fun getInternalBitmap(view: View = this): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        configCanvas(canvas)
        layout(0, 0, width, height)
        view.draw(canvas)
        return bitmap
    }

    fun configCanvas(canvas: Canvas) {
    }

    fun getCacheFile(callback: (file: File?) -> Any?) {
        getCacheBitmap {
            try {
                val file = File.createTempFile("capture", "cache${System.currentTimeMillis()}.png", context.cacheDir)
                file.setWritable(true)
                val fos = FileOutputStream(file)
                it?.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
                it?.recycle()
                callback(file)
            } catch (ex: Exception) {
                ex.printStackTrace()
                callback(null)
            }
        }

    }

    fun captureViews(callback: (bitmap: Bitmap?) -> Any?) {
        if (ignoreViews.isNotEmpty()) {
            if (childCount > 0) {
                val mergedBitmaps = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val mergedCanvas = Canvas(mergedBitmaps)
                configCanvas(mergedCanvas)

                for (i in 0 until childCount) {
                    val view = getChildAt(i)
                    if (!ignoreViews.contains(view.id)) {
                        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(bitmap)
                        configCanvas(canvas)
                        view?.draw(canvas)
                        val pos = view?.getPositionOnScreen()
                        pos?.apply {
                            val deltaPos = IntArray(2)
                            deltaPos[0] = Math.abs(parentPosition[0] - pos[0])
                            deltaPos[1] = Math.abs(parentPosition[1] - pos[1])
                            mergedCanvas.drawBitmap(bitmap, deltaPos[0].toFloat(), deltaPos[1].toFloat(), null)
                            bitmap.recycle()
                        }
                    }
                }
                callback(mergedBitmaps)
            } else {
                callback(getInternalBitmap())
            }
        } else {
            callback(getInternalBitmap())
        }
    }
}

class CachingViewObserverListener(val target: CachingLayout?, val callback: (bitmap: Bitmap?) -> Any?) : ViewTreeObserver.OnGlobalLayoutListener {
    override fun onGlobalLayout() {
        if (target != null && target.width > 0 && target.height > 0) {
            target.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            target.captureViews(callback)
        }
    }


}