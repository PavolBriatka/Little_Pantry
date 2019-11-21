package com.briatka.pavol.littlepantry.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import android.view.WindowManager
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
}