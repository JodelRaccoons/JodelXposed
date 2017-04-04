package com.jodelXposed.hooks.helper;

import com.jodelXposed.App;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.Utils;

import de.robv.android.xposed.XposedHelpers;

/**
 * Created by Admin on 12.03.2017.
 */

public class EventBus {

    private static Object eventBusInstance;

    public static void post(Object o) {
        if (eventBusInstance == null)
            eventBusInstance = Utils.getEventBus();

        XposedHelpers.callMethod(eventBusInstance, Options.INSTANCE.getHooks().Method_Otto_Append_Bus_Event, o);
    }

    public static class Events {

        public static Class HideKeyboardEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.HideKeyboardEvent", App.Companion.getLpparam().classLoader);
        public static Class UserSyncRequestEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.UserSyncRequestEvent", App.Companion.getLpparam().classLoader);
        public static Class ShowKeyboardEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.ShowKeyboardEvent", App.Companion.getLpparam().classLoader);
        public static Class UpdateMyMenuEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.UpdateMyMenuEvent", App.Companion.getLpparam().classLoader);
    }
}
