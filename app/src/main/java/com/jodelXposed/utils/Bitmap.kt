package com.jodelXposed.utils

import android.graphics.BitmapFactory
import android.os.Environment
import com.jodelXposed.utils.Log.xlog
import java.io.*

object Bitmap {
    @JvmField var jodelImagePath = Environment.getExternalStorageDirectory().toString() + "/.jodel-input.jpg"

    @JvmStatic
    fun loadBitmap(): android.graphics.Bitmap {
        xlog("Loading bitmap image")
        return BitmapFactory.decodeFile(File(jodelImagePath).absolutePath)
    }

    @JvmStatic
    fun saveBitmap(bitmap: android.graphics.Bitmap) {
        try {
            xlog("Saving bitmap of size: ${bitmap.byteCount}")
            val fos = FileOutputStream(File(jodelImagePath))
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
        } catch (e: FileNotFoundException) {
            xlog("File not found: ${e.message}")
        } catch (e: IOException) {
            xlog("Error accessing file: ${e.message}")
        }

    }
}