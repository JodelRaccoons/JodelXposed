package com.jodelXposed.models

import com.google.gson.annotations.Expose

data class Location(var active: Boolean = false, var lat: Double = 0.0, var lng: Double = 0.0,

                    var namefastChange1: String = "", var latfastChange1: Double = 0.0, var lngfastChange1: Double = 0.0,

                    var namefastChange2: String = "", var latfastChange2: Double = 0.0, var lngfastChange2: Double = 0.0,

                    var namefastChange3: String = "", var latfastChange3: Double = 0.0, var lngfastChange3: Double = 0.0,

                    var namefastChange4: String = "", var latfastChange4: Double = 0.0, var lngfastChange4: Double = 0.0,

                    var overrideHometown: Boolean = false
)


data class UDI(var active: Boolean = false, var udi: String = "", var originalUdi: String = "")

data class HookValues(
        @JvmField @Expose var version: Int = 0,
        @JvmField @Expose var versionCode: Int = 1,

        @JvmField @Expose var updateMessage: String = "(no message)",

        @JvmField @Expose var Class_Storage: String = "",
        @JvmField @Expose var Method_BetaHook_UnlockFeatures: String = "",
        @JvmField @Expose var Array_FeaturesEnabled: Array<String> = arrayOf<String>(),

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
        @JvmField @Expose var Field_JodelGestureListener_Post: String = "",

        @JvmField @Expose var Method_Otto_Append_Bus_Event: String = ""
)