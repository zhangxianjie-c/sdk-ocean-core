package com.android.origin.media.core.scan

import android.content.Context
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.view.TextureView
import com.android.origin.Constant
import com.android.origin.media.camera.CameraHelper
import com.android.origin.media.camera.CameraUtils
import com.android.origin.media.camera.SizeFilter
import com.android.origin.media.camera.impl.PictureSizeFilter
import com.android.origin.media.core.Orientation
import com.android.origin.media.core.Transform
import com.android.origin.media.core.h264.AvcTransform
import com.android.origin.media.core.h264.ClipAvcTransform
import com.android.origin.media.widget.CameraView
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import java.util.*

/**
Created by Zebra-RD张先杰 on 2022年6月2日13:49:11

Description:
 */
class Scan : TextureView.SurfaceTextureListener, DecoderHandler.DecoderCallback {
    var context: Context? = null

    var helper: CameraHelper? = null

    //camera最佳筛选
    var filter: SizeFilter? = null

    //预览的缩放相关参数
    var matrix: Matrix? = null

    //扫描解析处理
    var handler: DecoderHandler? = null

    var cameraView: CameraView? = null

    fun init(ct: Context, cV: CameraView) {
        context = ct
        helper = CameraHelper()
        filter = PictureSizeFilter()
        cameraView = cV
        helper!!.setPreviewCallback { data, camera ->
            if (handler != null) {
                handler!!.push(data)
            }
        }
        initView()
    }

    private fun initView() {
        cameraView!!.setOnCameraGestureListener(object : CameraView.OnCameraGestureListener {
            override fun onHandleZoom(zoomScale: Float) {
                helper!!.handleZoom(zoomScale)
            }

            override fun onHandleFocus(x: Float, y: Float, w: Int, h: Int) {
                helper!!.handleFocus(CameraUtils.reverseRotate(x, y, w, h, matrix))
            }
        })
    }

    override fun onSurfaceTextureAvailable(p0: SurfaceTexture, width: Int, height: Int) {
        startCamera()
    }

    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
        return true
    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {}

    override fun handleResult(result: com.google.zxing.Result) {
        scanResult?.invoke(result)
    }

    fun startCamera() {
        cameraView!!.let {
            if (it.isAvailable) {
                matrix = helper!!.openScanCamera(it.surfaceTexture,
                    it.width,
                    it.height,
                    filter,
                    Constant.SCAN_MIN_FPS,
                    Constant.SCAN_MAX_FPS)
                if (matrix != null) {
                    it.setTransform(matrix)
                }
                it.setMaxScale(helper!!.maxZoomScale)
                val w = helper!!.preSize.width
                val h = helper!!.preSize.height
                val matrixSize: Point = CameraUtils.matrixSize(w, h, matrix)
                val transform: Transform = if (matrixSize.equals(w, h)) AvcTransform(w,
                    h,
                    0,
                    Orientation.ROTATE90) else ClipAvcTransform(matrixSize.x,
                    matrixSize.y,
                    w,
                    h,
                    0,
                    Orientation.ROTATE90)
                //Camera旋转90, 270时, w, h调换
                handler = DecoderHandler(context, matrixSize.y,
                    matrixSize.x,
                    FormatDecoder(EnumSet.of(BarcodeFormat.QR_CODE), null
                    ) { point ->

                    },
                    transform,
                    false,
                    this)
            } else {
                it.surfaceTextureListener = this
            }
        }
    }

    //释放相机
    fun stopCamera() {
        helper!!.closeCamera()
        if (handler != null) {
            handler!!.stop()
            handler = null
        }
        matrix = null
    }

    var scanResult: ((com.google.zxing.Result) -> Unit)? = null
}