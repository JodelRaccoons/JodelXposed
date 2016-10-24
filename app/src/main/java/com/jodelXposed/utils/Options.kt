package com.jodelXposed.utils

import android.os.FileObserver
import com.jodelXposed.models.Hookvalues
import com.jodelXposed.models.Location
import com.jodelXposed.models.UDI
import com.jodelXposed.utils.Log.xlog
import com.jodelXposed.utils.Utils.SettingsPath
import com.squareup.moshi.Moshi
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

object Options : FileObserver(SettingsPath, CLOSE_WRITE) {

    private val moshi = Moshi.Builder().build()
    private val jsonAdapter = moshi.adapter<OptionsObject>(OptionsObject::class.java)
    private val settingsFile = File(SettingsPath)
    private var options = OptionsObject()

    init {
        if (!settingsFile.exists())
            save()
        else
            load()
        startWatching()
    }

    fun save() {
        val settingsJson = jsonAdapter.toJson(options)
        try {
            xlog(String.format("Writing %s to file", settingsJson))
            val output = FileUtils.openOutputStream(settingsFile)
            output.write(settingsJson.toByteArray())
            output.close()
        } catch (e: IOException) {
            xlog("Could not write to file")
            xlog(e.message)
        }
    }

    fun load() {
        try {
            val json = FileUtils.readFileToString(settingsFile, org.apache.commons.io.Charsets.UTF_8)
            xlog("Loading json from settings")
            xlog(json)
            options = jsonAdapter.fromJson(json)

        } catch (e: IOException) {
            xlog("Could not load options file")
        }

    }

    fun getHooks() = options.hooks

    fun getLocationObject() = options.location

    fun getUDIObject() = options.udi

    fun resetLocation() {
        // TODO: Consider this
    }

    override fun onEvent(event: Int, path: String?) = load()

    /**
     * The JSON conversion object
     */
    private class OptionsObject(val udi: UDI = UDI(), val location: Location = Location(), val hooks: Hookvalues = Hookvalues())
}