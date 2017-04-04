package com.jodelXposed.utils

import android.os.FileObserver
import com.google.gson.GsonBuilder
import com.jodelXposed.hooks.helper.Log.dlog
import com.jodelXposed.hooks.helper.Log.vlog
import com.jodelXposed.hooks.helper.Log.xlog
import com.jodelXposed.models.HookValues
import com.jodelXposed.models.Location
import com.jodelXposed.models.UDI
import com.jodelXposed.utils.Utils.OldSettingsPath
import com.jodelXposed.utils.Utils.getJXSettingsFile
import java.io.File
import java.io.IOException

object Options : FileObserver(getJXSettingsFile(), CLOSE_WRITE) {

    private val gson = GsonBuilder().setLenient().setPrettyPrinting().create()
    private val settingsFile = File(getJXSettingsFile())
    private val oldSettingsFile = File(OldSettingsPath)
    private var options = OptionsObject()

    init {
        dlog("Init options")
        if (!settingsFile.exists() && oldSettingsFile.exists()) {
            dlog("Migrating settings file to new directory")
            oldSettingsFile.renameTo(settingsFile)
        }
        if (!settingsFile.exists())
            save()
        else
            load()
        startWatching()
    }

    fun save() {
        try {
            val settingsJson = gson.toJson(options)
            vlog("Writing $settingsJson to file")
            settingsFile.writeText(settingsJson)
        } catch (e: IOException) {
            xlog("Could not write to file", e)
        }
    }

    fun load() {
        dlog("Loading json from settings")
        try {
            options = gson.fromJson(settingsFile.readText(),OptionsObject::class.java)
        } catch (e: IOException) {
            xlog("Options cannot be loaded", e)
        }
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