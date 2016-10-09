package com.jodelXposed.models;

/**
 * Created by Admin on 02.10.2016.
 */

public class Hookvalues {

    public int versionCode;

    /*
    * package com.jodelapp.jodelandroidv3.model.Storage;
    * type: method
    * arg 0: String.class (String str)
    * return: boolean.class
    * modifier: public
    * search term: features
    * */
    public String BetaHook_UnlockFeatures = "bp";

    /*
    * package com.jodelapp.jodelandroidv3.view.PhotoEditFragment;
    * type: field
    * class: ImageView.class
    * search term: setImageBitmap
    * */
    public String ImageHookValues_ImageView = "ayZ";

    /*
    * package com.jodelapp.jodelandroidv3.view.adapter.PostDetailRecyclerAdapter;
    * type: method
    * arg 0: com.jodelapp.jodelandroidv3.view.adapter.PostDetailRecyclerAdapter$PostViewHolder.class (PostViewHolder postViewHolder)
    * arg 1: int.class (int i)
    * return: void.class
    * modifier: public
    * search term:  postViewHolder.AP();
    * */
    public String PostStuff_TrackPostsMethod = "a";


    /*
    * package com.jodelapp.jodelandroidv3.view.CreateTextPostFragment;
    * type: field
    * class: String.class
    * search term: arguments.getString("com.tellm.post.color");
    * */
    public String PostStuff_ColorField = "axJ";

    /*
    * package com.jodelapp.jodelandroidv3.features.mymenu.MyMenuPresenter;
    *
    * Settings_AddEntriesMethod:
    *       type: method
    *       args: null
    *       return: List<com.jodelapp.jodelandroidv3.view.MyMenuItem>.class
    *       modifier: private
    *       search term: arrayList.add(new MyMenuItem
    *
    * Settings_HandleClickEventsMethod:
    *       type: method
    *       arg 0: com.jodelapp.jodelandroidv3.view.MyMenuItem.class (MyMenuItem myMenuItem)
    *       return: void.class
    *       modifier: public
    *       search term: String str = myMenuItem.name;
    * */
    public String Settings_AddEntriesMethod = "xh";
    public String Settings_HandleClickEventsMethod = "a";

    /*
    * package com.jodelapp.jodelandroidv3.data.gcm.MyGcmListenerService;
    * type: method
    * arg 0: String.class (String str)
    * arg 1: Bundle.class (Bundle bundle)
    * return: void.class
    * modifier: public
    * search term: AnalyticsUtil.u(ShareConstants.WEB_DIALOG_PARAM_MESSAGE, "received");
    * */
    public String Theme_GCMReceiverMethod = "a";

    /*
    * package com.jodelapp.jodelandroidv3.utilities.UniqueDeviceIdentifier;
    * type: method
    * args: null
    * return: String.class
    * modifier: public
    * search term: (getValue()); //method call to getValue
    * */
    public String UDI_GetUdiMethod = "za";

}
