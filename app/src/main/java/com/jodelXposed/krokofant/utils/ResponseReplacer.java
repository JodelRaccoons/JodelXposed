package com.jodelXposed.krokofant.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.jodelXposed.krokofant.utils.Log.xlog;

public class ResponseReplacer {
    public static Pattern[] processables = {
        //Pattern.compile("https://api.go-tellm.com(:443)?/api/v3/user/moderation")
    };

    public static boolean processable(String url) {
        for (Pattern p : processables) {
            if (p.matcher(url).matches())
                return true;
        }
        return false;
    }

    public static String processBody(String bodyString) {
        Settings settings = Settings.getInstance();
        try {
            if (!settings.isLoaded())
                settings.load();

            if (!settings.isActive()) {
                xlog("Feature not active");
                return bodyString;
            }

            JSONObject jsonObject = new JSONObject(bodyString);
            boolean isModerator = jsonObject.getBoolean("moderator");
            xlog("isModerator:" + String.valueOf(isModerator));
            jsonObject.put("moderator", true);
            xlog("Place JSON: " + jsonObject.toString());
            return jsonObject.toString();
        } catch (JSONException | IOException e) {
            xlog("Error: " + e.getLocalizedMessage());
        }
        return bodyString;
    }
}
