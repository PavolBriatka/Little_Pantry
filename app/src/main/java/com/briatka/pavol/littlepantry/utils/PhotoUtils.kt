package com.briatka.pavol.littlepantry.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.exifinterface.media.ExifInterface
import com.briatka.pavol.littlepantry.BuildConfig
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

object PhotoUtils {

    const val UNSUPPORTED_FORMAT_FLAG = "unsupported_picture_format"

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
        val inSampleSize = min(photoWidth / deviceHeight, photoHeight / deviceWidth)

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

        when (exifInterface?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun rotateRight(bitmap: Bitmap): Bitmap {

        val matrix = Matrix().apply {
            setRotate(90f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun rotateLeft(bitmap: Bitmap): Bitmap {

        val matrix = Matrix().apply {
            setRotate(-90f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun getPathFromUri(context: Context, uri: Uri): String {
        var path = ""

        if (BuildConfig.DEBUG) {
            Log.e(
                "Photo_Utils - File:",
                "Authority: " + uri.authority +
                        ", Fragment: " + uri.fragment +
                        ", Port: " + uri.port +
                        ", Query: " + uri.query +
                        ", Scheme: " + uri.scheme +
                        ", Host: " + uri.host +
                        ", Segments: " + uri.pathSegments.toString()
            )
        }

        when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":")
                        val type = split[0]

                        if ("primary".equals(type, ignoreCase = true)) {
                            path = Environment.getExternalStorageDirectory()
                                .toString() + "/" + split[1]
                        }
                    }
                    isDownloadsDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), docId.toLong()
                        )
                        path = getDataColumn(context, contentUri, null, null)
                    }
                    isMediaDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":")
                        val type = split[0]

                        var contentUri: Uri? = null
                        when (type) {
                            "image" -> {
                                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            }
                            "video" -> {
                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            }
                            "audio" -> {
                                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            }
                        }

                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])
                        path = getDataColumn(context, contentUri!!, selection, selectionArgs)
                    }
                }
            }
            "content".equals(uri.scheme, ignoreCase = true) -> {
                path = if (isGooglePhotosUri(uri)) {
                    uri.lastPathSegment ?: ""
                } else {
                    getDataColumn(context, uri, null, null)
                }
            }
            "file".equals(uri.scheme, ignoreCase = true) -> {
                path = uri.path ?: ""
            }
        }

        if (!path.endsWith(".jpg", true) &&
            !path.endsWith("jpeg", true) &&
            !path.endsWith(".png", true)) {
            path = UNSUPPORTED_FORMAT_FLAG
        }

        return path
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String {
        var cursor: Cursor? = null
        val dataColumnId = "_data"
        val projection = arrayOf(dataColumnId)

        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(dataColumnId)
                return if (columnIndex != -1) cursor.getString(columnIndex) else ""
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e("getDataColumn", e.message)
            }
        } finally {
            cursor?.close()
        }
        return ""
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun deleteFileFromCache(filepath: String) {

        val fileToDelete = File(filepath)
        fileToDelete.delete()
    }
}