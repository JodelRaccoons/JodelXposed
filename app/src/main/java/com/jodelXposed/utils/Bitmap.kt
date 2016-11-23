package com.jodelXposed.utils

import android.graphics.BitmapFactory
import android.os.Environment
import com.jodelXposed.utils.Log.dlog
import com.jodelXposed.utils.Log.xlog
import java.io.*

object Bitmap {
    @JvmField var jodelImagePath = Environment.getExternalStorageDirectory().toString() + "/.jodel-input.jpg"

    @JvmStatic
    fun loadBitmap(): android.graphics.Bitmap {
        dlog("Loading bitmap image")
        return BitmapFactory.decodeFile(File(jodelImagePath).absolutePath)
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
            if (path == null) {
                fos = FileOutputStream(File(jodelImagePath))
            } else {
                fos = FileOutputStream(File(path))
            }
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
        } catch (e: FileNotFoundException) {
            xlog("File not found", e)
        } catch (e: IOException) {
            xlog("Error accessing file", e)
        }

    }

}