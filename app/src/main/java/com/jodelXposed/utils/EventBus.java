package com.jodelXposed.utils;

import com.jodelXposed.App;

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
        public static Class UserSyncRequestEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.UserSyncRequestEvent", App.Companion.getLpparam().classLoader);
    }
}
