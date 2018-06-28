package vn.eazy.capture.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //TODO ignoreViews by ids
//        layout_target?.ignoreViews = arrayListOf(R.id.iv_avatar)

        //TODO getBitmap
//        layout_target?.getCacheBitmap { iv_captured?.setImageBitmap(it) }

        //TODO get File
        layout_target?.getCacheFile {
            it?.apply {
                Picasso.get().load(it).into(iv_captured)
            }
        }

    }
}
