package com.jodelXposed.utils

import android.os.FileObserver
import com.jodelXposed.models.HookValues
import com.jodelXposed.models.Location
import com.jodelXposed.models.UDI
import com.jodelXposed.utils.Log.xlog
import com.jodelXposed.utils.Utils.SettingsPath
import com.squareup.moshi.Moshi
import java.io.File
import java.io.IOException

object Options : FileObserver(SettingsPath, CLOSE_WRITE) {

    private val jsonAdapter = Moshi.Builder().build().adapter<OptionsObject>(OptionsObject::class.java)
    private val settingsFile = File(SettingsPath)
    private var options = OptionsObject()

    init {
        xlog("Init options")
        if (!settingsFile.exists())
            save()
        else
            load()
        startWatching()
    }

    fun save() {
        val settingsJson = jsonAdapter.toJson(options)
        try {
            xlog("Writing $settingsJson to file")
            settingsFile.writeText(settingsJson)
        } catch (e: IOException) {
            xlog("Could not write to file")
            xlog(e.message)
        }
    }

    fun load() {
        try {
            xlog("Loading json from settings")
            options = jsonAdapter.fromJson(settingsFile.readText())
        } catch (e: IOException) {
            xlog("Could not load options file")
        }
    }

    fun resetLocation() {
        // TODO: Consider this
    }

    override fun onEvent(event: Int, path: String?) = load()

    val udi: UDI get() = options.udi
    val location: Location get() = options.location
    var hooks: HookValues
        get() = options.hooks
        set(hooks) {
            options.hooks = hooks
        }


    /**
     * The JSON conversion object
     */
    class OptionsObject(var udi: UDI = UDI(), var location: Location = Location(), var hooks: HookValues = HookValues())
}