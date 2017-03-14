package com.jodelXposed.utils

import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import com.jodelXposed.utils.Log.dlog
import com.jodelXposed.utils.Log.xlog
import com.jodelXposed.utils.Utils.getJXSharedImage
import java.io.*


object Bitmap {
    @JvmStatic
    fun loadBitmap(): android.graphics.Bitmap {
        dlog("Loading bitmap image")
        return BitmapFactory.decodeFile(getJXSharedImage())
    }

    @JvmStatic
    fun loadBitmap(inputStream: InputStream): android.graphics.Bitmap {
        dlog("Loading bitmap image")
        val byteArray: ByteArray = inputStream.readBytes()
        inputStream.close()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    @JvmStatic
    fun saveBitmap(bitmap: android.graphics.Bitmap) {
        saveBitmap(bitmap, null)
    }

    @JvmStatic
    fun saveBitmap(bitmap: android.graphics.Bitmap, path: String?) {
        try {
            dlog("Saving bitmap of size: ${bitmap.byteCount}")
            var fos: FileOutputStream? = null
            val file: File?
            if (path == null) {
                file = File(getJXSharedImage())
                if (!file.exists())
                    file.createNewFile()
                fos = FileOutputStream(file)
            } else {
                file = File(path)
                if (!file.exists())
                    file.createNewFile()
                fos = FileOutputStream(file)
            }
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()

            scanFile(file)

        } catch (e: FileNotFoundException) {
            xlog("File not found", e)
        } catch (e: IOException) {
            xlog("Error accessing file", e)
        }

    }

    fun scanFile(file: File){
        try {
            MediaScannerConnection.scanFile(XposedUtilHelpers.getActivityFromActivityThread(),
                    arrayOf(file.toString()), null
            ) { path, uri ->
                Log.dlog("ExternalStorageScanned $path:")
                Log.dlog("ExternalStorage" + "-> uri=" + uri)
            }
        }catch (e :Exception){
            e.printStackTrace()
        }
    }

}