package com.jodelXposed.models

import com.google.gson.annotations.Expose

data class Location(var active: Boolean = false, var lat: Double = 0.0, var lng: Double = 0.0)

data class UDI(var active: Boolean = true, var udi: String = "")

data class HookValues(
        @JvmField @Expose var versionCode: Int = 1,

        @JvmField @Expose var updateMessage: String = "(no message)",

        @JvmField @Expose var Class_Storage: String = "",
        @JvmField @Expose var Method_BetaHook_UnlockFeatures: String = "",

        @JvmField @Expose var Class_PhotoEditFragment: String = "",
        @JvmField @Expose var Method_ImageHookValues_ImageView: String = "",

        @JvmField @Expose var Class_PostDetailRecyclerAdapter: String = "",
        @JvmField @Expose var Method_PostStuff_TrackPostsMethod: String = "",

        @JvmField @Expose var Class_MyMenuPresenter: String = "",
        @JvmField @Expose var Method_Settings_AddEntriesMethod: String = "",
        @JvmField @Expose var Method_Settings_HandleClickEventsMethod: String = "",

        @JvmField @Expose var Class_UniqueDeviceIdentifier: String = "",
        @JvmField @Expose var Method_UDI_GetUdiMethod: String = "",

        @JvmField @Expose var Class_LocationChangeListener: String = "",

        @JvmField @Expose var Class_JodelGestureListener: String = "",
        @JvmField @Expose var Field_JodelGestureListener_Post: String = ""
)