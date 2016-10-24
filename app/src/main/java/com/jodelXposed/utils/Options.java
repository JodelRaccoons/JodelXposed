package com.jodelXposed.utils;

import android.os.FileObserver;

import com.jodelXposed.models.Hookvalues;
import com.jodelXposed.models.Location;
import com.jodelXposed.models.UDI;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.jodelXposed.utils.Log.dlog;
import static com.jodelXposed.utils.Log.xlog;
import static com.jodelXposed.utils.Utils.SettingsPath;

public class Options extends FileObserver {
    private static Options singleton;
    public OptionsObject options;
    private File settingsFile;
    private JsonAdapter<OptionsObject> jsonAdapter;

    public Options() {
        super(SettingsPath, CLOSE_WRITE);
        Moshi moshi = new Moshi.Builder().build();
        jsonAdapter = moshi.adapter(OptionsObject.class);
        xlog("Init file object with path: " + SettingsPath);
        settingsFile = new File(SettingsPath);

        if (!settingsFile.exists()) {
            options = new OptionsObject();
            save();
        } else {
            load();
        }
        startWatching();
    }

    public static Options getInstance() {
        if (singleton == null)
            singleton = new Options();
        return singleton;
    }

    public void resetLocation() {
        options.location = new Location();
        options.location.setActive(true);
        save();
    }

    public Hookvalues getHooks() {
        return this.options.hookvalues;
    }

    public Location getLocationObject(){
        return this.options.location;
    }

    public UDI getUDIObject(){
        return this.options.udi;
    }

    private void writeFile()  {
        String settingsJson = jsonAdapter.toJson(options);
        try {
            xlog(String.format("Writing %s to file", settingsJson));
            FileOutputStream output = FileUtils.openOutputStream(settingsFile);
            output.write(settingsJson.getBytes());
            output.close();
        } catch (IOException e) {
            xlog("Could not write to file");
            xlog(e.getMessage());
        }
    }

    public void save(){
        writeFile();
    }

    public void load() {
        dlog("Loading settings file");
        try {
            String json = FileUtils.readFileToString(settingsFile, Charsets.UTF_8);
            options = jsonAdapter.fromJson(json);

            dlog(
                        "++++ Location: ++++"
                        + "\nEnabled: " + options.location.isActive()
                        + "\nLatitude: " + options.location.getLat()
                        + "\nLongitude: " + options.location.getLng()

                        + "\n++++ UDI: ++++"
                        + "\nEnabled: " + options.udi.isActive()
                        + "\nUDI: " + options.udi.getUdi()
            );

        } catch (IOException e) {

            xlog("Could not load options file");
        }
    }

    @Override
    public void onEvent(int event, String path) { load(); }

    public static class OptionsObject {
        public UDI udi = new UDI();
        public Location location = new Location();
        public Hookvalues hookvalues = new Hookvalues();
    }

}
