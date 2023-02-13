package com.ocean.core.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.*
import java.util.*


/**
Created by xianjie on 2022年11月2日16:07:47

Description:主要是对图片的处理 向外暴露的方法尽量不要用扩展方法 那会失去FileManage的意义
 */
object FileManage {
    fun  String.isHttps()=
        substring(0,6) == "https:"
    fun dataTransfer(fis: InputStream, fos: OutputStream) {
        try {
            var len: Int
            val bt = ByteArray(1024)
            while (fis.read(bt).also { len = it } != -1) {
                fos.write(bt, 0, len)
            }
            fos.flush()
            fis.close()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

     fun  getNumLargeSmallLetter(size:Int):String{
        val buffer =  StringBuilder()
        val random =  Random()
         for(i in 0..size){
            if(random.nextInt(2) % 2 == 0)
                    (if(random.nextInt(2) % 2 == 0) buffer.append((random.nextInt(26) + 'A'.code).toChar())
                    else buffer.append((random.nextInt(26) + 'a'.code).toChar()))
            else buffer.append(random.nextInt(10))
        }
        return buffer.toString()
    }

    fun getFileExtensionFromUrl(uri:String?):String{
        return if (uri!=null){
            val mimeExtension = MimeTypeMap.getFileExtensionFromUrl(uri)
            return mimeExtension.ifBlank {
                val list = uri.split(".")
                if (list.isNotEmpty()) list[list.size - 1] else ""
            }
        }else ""
    }

    /**
     * 版本适配过的Uri获取
     */
    fun getProviderUri(context: Context, uri: String): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            FileProvider.getUriForFile(
                context,
                "com.pcoinsight.brdeinsectiz.fileprovider",
                File(uri)
            )
        else Uri.fromFile(File(uri))
    }

    fun getProviderUri(context: Context, uri: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) FileProvider.getUriForFile(
            context,
            "com.pcoinsight.brdeinsectiz.fileprovider",
            uri
        )
        else Uri.fromFile(uri)
    }

    /**
     * 拍照存放路径
     */
    fun getTakePhotoUri(context: Context): Uri {
        val fileDirectory = File(context.getExternalFilesDir(null), "/TakePhone")
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs()
        }
        val baseFile =
            File(
                context.getExternalFilesDir("TakePhone"),
                File.separator + "${System.currentTimeMillis()}.jpg"
            )
        try {
            if (baseFile.exists()) {
                baseFile.delete()
            }
            baseFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return getProviderUri(context, baseFile)
    }

    private fun Context.getCopyFile(fileName: String?): File {
        val fileDirectory = File(getExternalFilesDir(null), "/Copy")
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs()
        }
        val baseFile =
            File(getExternalFilesDir("Copy"), File.separator + fileName)
        try {
            if (baseFile.exists()) {
                baseFile.delete()
            }
            baseFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return baseFile
    }

    /**
     * 压缩图片文件
     */
    private fun Context.getCompressFile(originUri: String): File {
        val fileDirectory = File(getExternalFilesDir(null), "/")
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs()
        }
        val baseFile =
            File(
                getExternalFilesDir("Compress"),
                File.separator + "${System.currentTimeMillis()}.${
                    getFileExtensionFromUrl(originUri)
                }"
            )
        try {
            if (baseFile.exists()) {
                baseFile.delete()
            }
            baseFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return baseFile
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param imageUri
     */
     fun getImageAbsolutePath(context: Context, imageUri: Uri): String? {
        if (DocumentsContract.isDocumentUri(context, imageUri)
        ) {
            if (isExternalStorageDocument(imageUri)) {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return context.getExternalFilesDir(null).toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(imageUri)) {
                val id = DocumentsContract.getDocumentId(imageUri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(imageUri)) {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } // MediaStore (and general)
        else if ("content".equals(imageUri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(imageUri)) imageUri.lastPathSegment else context.getFilePathFromURI(
                imageUri
            )
        } else if ("file".equals(imageUri.scheme, ignoreCase = true)) {
            return imageUri.path
        }
        return null
    }

    private fun Context.getFilePathFromURI(contentUri: Uri?): String? {
        //copy file and send new file path
        val fileName = getFileName(contentUri)
        if (!TextUtils.isEmpty(fileName)) {
            val copyFile: File = getCopyFile(fileName)
            copy(contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    private fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path!!.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    private fun Context.copy(srcUri: Uri?, dstFile: File?) {
        try {
            val inputStream = contentResolver.openInputStream(srcUri!!)
                ?: return
            val outputStream: OutputStream = FileOutputStream(dstFile)
            var read: Int
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable = inputStream.available()
            //int bufferSize = 1024;
            val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
//            IOUtils.copyStream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Deprecated("java.lang.IllegalArgumentException: column '_data' does not exist. Available columns: []")
    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * 图片压缩
     */
    fun compressImage(
        context: Context,
        originUri: Uri,
        newUri: ((Uri) -> Unit)
    ) {
        val path = getImageAbsolutePath(context, originUri)
        //若gif 不压缩
        if (getFileExtensionFromUrl(path) == "gif") {
            newUri(getProviderUri(context, File(path!!)))
            return
        }
        //压缩过的新文件
        val baseFile = context.getCompressFile(path ?: return)
        var bitmap = BitmapFactory.decodeFile(path).copy(Bitmap.Config.RGB_565, true)
        val degree = readPictureDegree(path)
        var optimizeBitmap = rotateBitmap(bitmap, degree.toFloat())
        val quality = 20
        val baos = ByteArrayOutputStream()
        // 把压缩后的数据存放到baos中
        optimizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        try {
            val fos = FileOutputStream(baseFile)
            fos.write(baos.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        newUri(getProviderUri(context, baseFile))
    }

    /**
     * 获取图片旋转角度
     * @param srcPath
     * @return
     */
    private fun readPictureDegree(srcPath: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(srcPath)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            );
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270;
            }
        } catch (e: IOException) {
            e.printStackTrace();
        }
        return degree
    }

    //处理图片旋转
    private fun rotateBitmap(bitmap: Bitmap, rotate: Float): Bitmap {
        val w = bitmap.getWidth()
        val h = bitmap.getHeight()

        // Setting post rotate to 90
        val mtx = Matrix()
        mtx.postRotate(rotate)
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }


    fun format(size: Long): String {
        val kb: Long = 1024
        val mb = kb * 1024
        val gb = mb * 1024
        return if (size >= gb) {
            String.format("%.1f GB", size.toFloat() / gb)
        } else if (size >= mb) {
            val f = size.toFloat() / mb
            String.format(if (f > 100) "%.0f MB" else "%.1f MB", f)
        } else if (size > kb) {
            val f = size.toFloat() / kb
            String.format(if (f > 100) "%.0f KB" else "%.1f KB", f)
        } else {
            String.format("%d B", size)
        }
    }

    fun getFileSize(context: Context, uri: Uri): Long =
        when (uri.scheme) {
            ContentResolver.SCHEME_FILE -> File(uri.path).length()
            ContentResolver.SCHEME_CONTENT -> try {
                context.contentResolver.openFileDescriptor(uri, "r")?.statSize ?: 0
            } catch (e: Exception) {
                0L
            }
            else -> 0L
        }
}

