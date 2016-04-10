package com.jodelXposed.krokofant.utils;

import org.apache.commons.io.Charsets;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.jodelXposed.krokofant.utils.Log.xlog;

public class RequestReplacer {
    public static Pattern[] processables = {
        Pattern.compile("https://api.go-tellm.com(:443)?/api/v2/users/location"),
        Pattern.compile("https://api.go-tellm.com(:443)?/api/v2/users/"),
        Pattern.compile("https://api.go-tellm.com(:443)?/api/v2/posts/"),
        Pattern.compile("https://api.go-tellm.com(:443)?/api/v2/posts/location/combo\\?lat=\\d+\\.\\d+&lng=\\d+\\.\\d+"),
        Pattern.compile("https://api.go-tellm.com(:443)?/api/v2/posts/location/\\?skip=\\d*&limit=\\d*\\&lat=\\d+.\\d+&lng=\\d+\\.\\d+")
    };

    public static boolean processable(String url) {
        for (Pattern p : processables) {
            if (p.matcher(url).matches())
                return true;
        }
        return false;
    }

    public static String processURL(String url) {
        Settings settings = Settings.getInstance();
        try {
            if (!settings.isLoaded())
                settings.load();

            if (!settings.isActive()) {
                xlog("Feature not active");
                return url;
            }
            url = url.replaceAll("lat=\\d+\\.\\d+", "lat=" + String.valueOf(settings.getLat()));
            url = url.replaceAll("lng=\\d+\\.\\d+", "lng=" + String.valueOf(settings.getLng()));
        } catch (JSONException | IOException e) {
            xlog("Error: " + e.getLocalizedMessage());
        }
        return url;
    }

    public static byte[] processBody(byte[] jsonBytes) {
        Settings settings = Settings.getInstance();
        try {
            if (!settings.isLoaded())
                settings.load();

            if (!settings.isActive()) {
                xlog("Feature not active");
                return jsonBytes;
            }

            JSONObject jsonObject = new JSONObject(new String(jsonBytes));
            JSONObject location = jsonObject
                .getJSONObject("location")
                .put("country", settings.getCountry())
                .put("name", settings.getCity())
                .put("city", settings.getCity());
            location.getJSONObject("loc_coordinates")
                .put("lat", settings.getLat())
                .put("lng", settings.getLng());
            xlog("Place JSON: " + jsonObject.toString());
            return jsonObject.toString().getBytes(Charsets.UTF_8);
        } catch (JSONException | IOException e) {
            xlog("Error: " + e.getLocalizedMessage());
        }
        return jsonBytes;
    }
}
