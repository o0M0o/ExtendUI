package wxm.extendui.zoomImage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import kotterknife.bindView
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.extendui.R
import wxm.uilib.zoomImage.zoomImageView
import android.content.Intent.ACTION_PICK
import wxm.androidutil.improve.let1

/**
 * @author WangXM
 * @version create：2018/4/14
 */
class FrgZoomImage : FrgSupportBaseAdv() {
    private val REQUEST_CODE_PICK_IMAGE = 1

    private val mZIVImage: zoomImageView  by bindView(R.id.ziv_scale)
    private val mBTSetImage: Button  by bindView(R.id.bt_set_image)

    override fun getLayoutID(): Int = R.layout.frg_zoom_image

    override fun initUI(savedInstanceState: Bundle?) {
        mBTSetImage.setOnClickListener{
            Intent(ACTION_PICK).let1 {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            mZIVImage.setImageURI(data!!.data)
        }
    }


    /**
     * show image in [fn] in imageView
    private fun ImageView.setImagePath(fn:String)   {
        this.setImageURI(Uri.fromFile(File(fn)))
    }
     */
}
