package com.jodelXposed.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

import com.jodelXposed.JXPreferenceActivity
import com.jodelXposed.models.Location
import com.schibstedspain.leku.LocationPickerActivity

import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

import id.zelory.compressor.Compressor

import com.jodelXposed.utils.Log.xlog


class Picker : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!permissionsGranted()) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            } else
                getAction()
        } else {
            getAction()
        }
    }

    private fun getAction() {
        when (intent.getIntExtra("choice", 0)) {
            1 -> startLocationPicker()
            2 -> {
                Options.resetLocation()
                finish()
            }
            3 -> galleryPicker()
            4 -> startActivity(Intent(this, JXPreferenceActivity::class.java))
            else -> finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(0, 0)
    }

    private fun galleryPicker() {
        //        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        //        photoPickerIntent.setType("image/*");
        //        startActivityForResult(photoPickerIntent, GALLERY_REQUEST_CODE);
        val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, GALLERY_REQUEST_CODE)
    }

    private fun startLocationPicker() {
        val location = Options.location
        val intent = Intent(this, LocationPickerActivity::class.java)
        intent.putExtra(LocationPickerActivity.LATITUDE, location.lat)
        intent.putExtra(LocationPickerActivity.LONGITUDE, location.lng)
        startActivityForResult(intent, PLACEPICKER_REQUEST_CODE)
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun permissionsGranted(): Boolean {
        return this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        xlog("ActivityResult called")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PLACEPICKER_REQUEST_CODE -> {
                    val op = Options
                    op.location.lat = data.getDoubleExtra(LocationPickerActivity.LATITUDE, 0.0)
                    op.location.lng = data.getDoubleExtra(LocationPickerActivity.LONGITUDE, 0.0)
                    op.save()
                    Toast.makeText(this@Picker, "Success, please refresh your feed!", Toast.LENGTH_LONG).show()
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImage = data.data
                    xlog("SelectedImage:" + selectedImage.toString())
                    try {
                        val inputStream = contentResolver.openInputStream(selectedImage)
                        if (inputStream == null) {
                            xlog("input stream for image is null")
                        } else {

                        }
                        val bitmap = Bitmap.loadBitmap(inputStream)
                        Bitmap.saveBitmap(bitmap)
                    } catch (e: FileNotFoundException) {
                        xlog("Could not load gallery image")
                        xlog(e.message)
                    }

                }
            }
        }
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAction()
                } else {
                    Toast.makeText(applicationContext, "This app needs permissions in order to work", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {


        private val GALLERY_REQUEST_CODE = 202
        private val PERMISSION_REQUEST_CODE = 201
        private val PLACEPICKER_REQUEST_CODE = 200
    }

}

