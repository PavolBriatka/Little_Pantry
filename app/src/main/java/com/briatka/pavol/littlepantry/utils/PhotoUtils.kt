package com.briatka.pavol.littlepantry.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

object PhotoUtils {

    @Throws(IOException::class)
    fun createTempFile(context: Context): File {

        val fileName = SimpleDateFormat("E-dd-MM-yyyy", Locale.getDefault())
            .format(Date())

        val storageDir = context.externalCacheDir

        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    fun resampleImage(filepath: String, context: Context): Bitmap {

        val metrics = DisplayMetrics()
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.defaultDisplay.getMetrics(metrics)

        val deviceWidth = metrics.widthPixels
        val deviceHeight = metrics.heightPixels

        //get photo size without allocating memory
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filepath, options)

        val photoWidth = options.outWidth
        val photoHeight = options.outHeight

        //calculate inSampleSize
        val inSampleSize = min(photoWidth/deviceHeight, photoHeight/deviceWidth)

        //decode resized picture
        options.inSampleSize = inSampleSize
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filepath, options)
    }

    fun fixRotation(bitmap: Bitmap, filepath: String): Bitmap {

        var exifInterface: ExifInterface? = null
        val matrix = Matrix()

        try {
            exifInterface = ExifInterface(filepath)
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

        when (exifInterface?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
        }

        return Bitmap.createBitmap(bitmap,0,0,bitmap.width, bitmap.height,matrix,true)
    }

    fun getPathFromUri(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        var path: String?
        path = if (cursor == null) {
            uri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(index)
        }
        cursor?.close()

        if (!path.endsWith(".jpg", true) &&
            !path.endsWith("jpeg", true) &&
            !path.endsWith(".png", true)) {
            path = null
        }

        return path
    }

    fun deleteFileFromCache(filepath: String) {

        val fileToDelete = File(filepath)
        fileToDelete.delete()
    }
}