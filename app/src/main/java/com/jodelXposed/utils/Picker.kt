package com.jodelXposed.utils


import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.jodelXposed.utils.Log.dlog
import com.jodelXposed.utils.Log.xlog
import id.zelory.compressor.Compressor
import java.io.File
import java.io.FileNotFoundException


class Picker : AppCompatActivity() {
    private var fastLocationPickerChoice = 0

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
            2 -> fastChangeLocationPicker(intent)
            3 -> galleryPicker()
            else -> finish()
        }
    }

    private fun fastChangeLocationPicker(intent: Intent) {
        fastLocationPickerChoice = intent.getIntExtra("fastChange", 0)
        startLocationPicker()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
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
        val intent = builder.build(this)
        try {
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
                val name = place.name
                val lat = place.latLng.latitude
                val lng = place.latLng.longitude
                if (fastLocationPickerChoice != 0) {
                    when (fastLocationPickerChoice) {
                        1 -> {
                            Options.location.namefastChange1 = name.toString()
                            Options.location.latfastChange1 = lat
                            Options.location.lngfastChange1 = lng
                        }
                        2 -> {
                            Options.location.namefastChange2 = name.toString()
                            Options.location.latfastChange2 = lat
                            Options.location.lngfastChange2 = lng
                        }
                        3 -> {
                            Options.location.namefastChange3 = name.toString()
                            Options.location.latfastChange3 = lat
                            Options.location.lngfastChange3 = lng
                        }
                        4 -> {
                            Options.location.namefastChange4 = name.toString()
                            Options.location.latfastChange4 = lat
                            Options.location.lngfastChange4 = lng
                        }
                    }
                    fastLocationPickerChoice = 0
                    Toast.makeText(this@Picker, "Saved successfully!", Toast.LENGTH_LONG).show()
                } else {
                    Options.location.lat = lat
                    Options.location.lng = lng
                    Toast.makeText(this@Picker, "Success, please refresh your feed!", Toast.LENGTH_LONG).show()
                }

                Options.save()
            }
            GALLERY_REQUEST_CODE -> {
                try {
                    val compressor = Compressor.Builder(this@Picker)
                            .setQuality(90)
                            .build()
                    val selectedImage = data.data
                    dlog("SelectedImage:$selectedImage")
                    try { //(selectedImage.path.contains("content") || selectedImage.path.contains("content:"))
                        val cursor = contentResolver.query(selectedImage, arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null)
                        cursor.moveToFirst()
                        val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                        val picturePath = cursor.getString(columnIndex)
                        cursor.close()
                        Bitmap.saveBitmap(compressor.compressToBitmap(File(picturePath)))
                    } catch (e: Exception) {
                        e.printStackTrace()
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

