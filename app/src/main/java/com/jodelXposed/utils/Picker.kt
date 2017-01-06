package com.jodelXposed.utils


import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.jodelXposed.App.Companion.lpparam
import com.jodelXposed.JXPreferenceActivity
import com.jodelXposed.utils.Log.dlog
import com.jodelXposed.utils.Log.xlog
import git.unbrick.xposedhelpers.XposedUtils
import id.zelory.compressor.Compressor
import java.io.File
import java.io.FileNotFoundException


class Picker : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsGranted())
                getAction()
            else
                requestPermissions(
                        arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), PERMISSION_REQUEST_CODE)
        } else getAction()

    }

    private fun getAction() {
        when (intent.getIntExtra("choice", 0)) {
            1 -> startLocationPicker()
            3 -> galleryPicker()
            4 -> startActivity(Intent(this, JXPreferenceActivity::class.java))
            else -> finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

    }

    override fun finish() {
        overridePendingTransition(0, 0)
        super.finish()
    }

    private fun galleryPicker() {
        //TODO only google photos works for me, a third party gallery app crashes / is not seen as such
        startActivityForResult(
                Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                GALLERY_REQUEST_CODE
        )
    }

    private fun startLocationPicker() {
        val builder = PlacePicker.IntentBuilder()
        val location = Options.location
        val latLngBounds = LatLngBounds.Builder().include(LatLng(location.lat, location.lng)).build()
        builder.setLatLngBounds(latLngBounds)
        try {
            val intent = builder.build(this)
            startActivityForResult(intent, PLACEPICKER_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun permissionsGranted(): Boolean {
        return this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return finish()
        if (data == null) {
            dlog("No ActivityResult data")
            return finish()
        }

        when (requestCode) {
            PLACEPICKER_REQUEST_CODE -> {
                val place = PlacePicker.getPlace(data, this)
                Options.location.lat = place.latLng.latitude
                Options.location.lng = place.latLng.longitude
                Options.save()
                Toast.makeText(this@Picker, "Success, please refresh your feed!", Toast.LENGTH_LONG).show()
            }
            GALLERY_REQUEST_CODE -> {
                try {
                    val compressor = Compressor.Builder(this@Picker)
                            .setQuality(90)
                            .build()
                    val selectedImage = data.data
                    dlog("SelectedImage:$selectedImage")
                    if (selectedImage.path.contains("contentprovider") || selectedImage.path.contains("content:")) {
                        val cursor = contentResolver.query(selectedImage, arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null)
                        cursor.moveToFirst()
                        val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                        val picturePath = cursor.getString(columnIndex)
                        cursor.close()
                        Bitmap.saveBitmap(compressor.compressToBitmap(File(picturePath)))
                    } else {
                        Bitmap.saveBitmap(compressor.compressToBitmap(File(selectedImage.path)))
                    }
                } catch (e: FileNotFoundException) {
                    xlog("Could not load gallery image", e)
                }
            }
        }
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                    getAction()
                } else {
                    Toast.makeText(applicationContext, "This app needs permissions in order to work", Toast.LENGTH_LONG).show()
                    finish()
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

