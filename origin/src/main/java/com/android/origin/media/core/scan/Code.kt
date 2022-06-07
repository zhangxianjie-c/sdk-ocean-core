package com.android.origin.media.core.scan

import android.graphics.*
import android.text.TextUtils
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*

/**
Created by Zebra-RD张先杰 on 2022年6月7日11:25:15

Description:
 */
object Code {
    /**
     * 解析二维码图片工具类
     * @param analyzeCallback
     */
    fun analyzeImage(
        path: String?,
        errorResult: (() -> Unit)? = null,
        successResult: ((Bitmap, String) -> Unit),
        ) {
        /**
         * 首先判断图片的大小,若图片过大,则执行图片的裁剪操作,防止OOM
         */
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true // 先获取原大小
        var mBitmap = BitmapFactory.decodeFile(path, options)
        options.inJustDecodeBounds = false // 获取新的大小
        var sampleSize = (options.outHeight / 400.toFloat()).toInt()
        if (sampleSize <= 0) sampleSize = 1
        options.inSampleSize = sampleSize
        mBitmap = BitmapFactory.decodeFile(path, options)
        val multiFormatReader = MultiFormatReader()

        // 解码的参数
        val hints = Hashtable<DecodeHintType, Any?>(2)
        // 可以解析的编码类型
        var decodeFormats = Vector<BarcodeFormat>()
        if (decodeFormats.isEmpty()) {
            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.add(BarcodeFormat.UPC_A)
            decodeFormats.add(BarcodeFormat.UPC_E)
            decodeFormats.add(BarcodeFormat.EAN_13)
            decodeFormats.add(BarcodeFormat.EAN_8)
            decodeFormats.add(BarcodeFormat.CODE_39)
            decodeFormats.add(BarcodeFormat.CODE_93)
            decodeFormats.add(BarcodeFormat.CODE_128)
            decodeFormats.add(BarcodeFormat.ITF)
            decodeFormats.add(BarcodeFormat.QR_CODE)
            decodeFormats.add(BarcodeFormat.DATA_MATRIX)
        }
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        // 设置继续的字符编码格式为UTF8
        // hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 设置解析配置参数
        multiFormatReader.setHints(hints)

        // 开始对图像资源解码
        var rawResult: Result? = null
        try {
            rawResult =
                multiFormatReader.decodeWithState(BinaryBitmap(HybridBinarizer(BitmapLuminanceSource(
                    mBitmap))))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (rawResult != null) {
            successResult.invoke(mBitmap, rawResult.text)
        } else {
            errorResult?.invoke()
        }
    }

    /**
     * 生成二维码图片
     * @param text
     * @param width
     * @param height
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param colorBlack            黑色色块
     * @param colorWhite            白色色块
     * @return BitMap
     */
    fun createImage(
        text: String,
        width: Int,
        height: Int,
        character:String = "utf-8",
        margin: Int = 0,
        colorBlack: Int = Color.parseColor("#000000"),
        colorWhite: Int = Color.parseColor("#FFFFFF"),
    ): Bitmap? {
        if (TextUtils.isEmpty(text)) {
            return null
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null
        }
        return try {
            /** 1.设置二维码相关配置  */
            val hints =
                Hashtable<EncodeHintType, Any?>()
            // 字符转码格式设置
            hints[EncodeHintType.CHARACTER_SET] = character
            // 容错率设置
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            // 空白边距设置
            hints[EncodeHintType.MARGIN] = margin
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象  */
            val bitMatrix =
                QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints)

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值  */
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix[x, y]) {
                        pixels[y * width + x] =
                            colorBlack //黑色色块像素设置，可以通过传入不同的颜色实现彩色二维码，例如Color.argb(1,55,206,141)等设置不同的颜色。
                    } else {
                        pixels[y * width + x] = colorWhite // 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象  */
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    /***
     * 为二维码添加logo
     *
     * @param srcBitmap 二维码图片
     * @param logoBitmap logo图片
     * @param percent logo比例
     * @return 生成的最终的图片
     */
    fun addLogo(srcBitmap: Bitmap?, logoBitmap: Bitmap?, percent: Float = 0.2F): Bitmap? {
        //判断参数是否正确
        if (srcBitmap == null)
            return null
        if (logoBitmap == null)
            return srcBitmap
        //输入logo图片比例错误自动纠正为默认的0.2f
        var logoPercent = percent
        if (percent < 0 || percent > 1)
            logoPercent = 0.2f

        //分别获取bitmap图片的大小
        var sHeight = srcBitmap.height
        var sWidth = srcBitmap.width
        var lHeight = logoBitmap.height
        var lWidth = logoBitmap.width

        //获取缩放比例
        var scareWidth = sHeight * logoPercent / lWidth
        var scareHeight = sWidth * logoPercent / lHeight

        //使用canvas重新绘制bitmap
        var bitmap = Bitmap.createBitmap(sWidth, sHeight, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        canvas.drawBitmap(srcBitmap, 0f, 0f, null)
        canvas.scale(
            scareWidth,
            scareHeight,
            (sWidth / 2).toFloat(),
            (sHeight / 2).toFloat()
        )   //设置缩放中心基点
        canvas.drawBitmap(
            logoBitmap,
            (sWidth / 2 - lWidth / 2).toFloat(),
            (sHeight / 2 - lHeight / 2).toFloat(),
            null
        )
        return bitmap
    }

}