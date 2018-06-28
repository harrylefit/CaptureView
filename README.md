# CaptureView

1. Add ignore views (just support for 1 nested level)
    ```kotlin
    cachingLayout?.ignoreViews = arrayListOf(R.id.iv_avatar)
    ```

2. Get cacheBitmap
    ```kotlin
    cachingLayoiut?.getCacheBitmap { iv_captured?.setImageBitmap(it) }
    ```

3. Get cacheFile
    ```kotlin
     layout_target?.getCacheFile {
            it?.apply {
                Picasso.get().load(it).into(iv_captured)
            }
        }
    ```
4. Override functions
    - it's using canvas to draw bitmap so you can override `configCanvas(canvas)` to config for it.