package com.ocean.core.framework

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.NinePatchDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.ocean.core.R

/**
 * Created by xianjie on 2022年11月16日16:55:51
 *
 * Description: 优化Toast 注意！！！在Android11.0及以上自定义View的Toast在后台显示时将被屏蔽
 */
@Keep
fun Fragment.toast(text:String){
    requireContext().toast(text)
}

@Keep
fun Context.toast(text:String){
    val currentToast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
    setting(text,currentToast)
    currentToast.show()
}


@Keep
fun Fragment.singleToast(text:String){
    requireContext().singleToast(text)
}
var lastToast:Toast? = null
@Keep
fun Context.singleToast(text:String){
    if (lastToast == null || (lastToast!=null && lastToast!!.view!!.findViewById<TextView>(R.id.toast_text).text != text)){
        lastToast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
        setting(text,lastToast!!)
        lastToast!!.show()
    } else if (lastToast != null){
            lastToast!!.show()
    }

}

fun Context.setting(text:String,toast:Toast){
    val toastLayout: View =
        (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.toast_layout, null)
    val toastTextView = toastLayout.findViewById<TextView>(R.id.toast_text)
    val drawableFrame = resources.getDrawable(R.drawable.toast_frame,null) as NinePatchDrawable
    drawableFrame.setColorFilter(Color.parseColor("#CC000000"), PorterDuff.Mode.SRC_IN)
    toastLayout.background = drawableFrame
    toastTextView.text = text
    toastTextView.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL))
//    toastTextView.textSize = resources.getDimension(R.dimen.sp_10)
    toastTextView.setTextColor(Color.WHITE)
    toast.view = toastLayout
    toast.setGravity(toast.gravity , toast.xOffset, toast.yOffset)
}