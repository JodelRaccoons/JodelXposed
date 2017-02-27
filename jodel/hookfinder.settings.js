const PACKAGE_BASE = 'smali/com/jodelapp/jodelandroidv3'

const targets = {
  'model.Storage': {
    methods: [
      {
        name: 'Method_BetaHook_UnlockFeatures',
        filter: /\.method public (\w+)\(Ljava\/lang\/String;\)Z/g,
        implementationFilter: /features/g
      }
    ]
  },
  'view.adapter.PostDetailRecyclerAdapter': {
    methods: [
      {
        name: 'Method_PostStuff_TrackPostsMethod',
        filter: /\.method public (\w+)\(Lcom\/jodelapp\/jodelandroidv3\/view\/adapter\/PostDetailRecyclerAdapter\$PostViewHolder;I\)V/g
      }
    ]
  },
  'features.mymenu.MyMenuPresenter': {
    methods: [
      {
        name: 'Method_Settings_AddEntriesMethod',
        filter: /\.method private (\w+)\(\)Ljava\/util\/List;/g
      },
      {
        name: 'Method_Settings_HandleClickEventsMethod',
        filter: /\.method public (\w+)\(Lcom\/jodelapp\/jodelandroidv3\/view\/MyMenuItem;\)V/g
      }
    ]
  },
  'utilities.UniqueDeviceIdentifier': {
    methods: [
      {
        name: 'Method_UDI_GetUdiMethod',
        filter: /\.method public (?!getValue)(\w+)\(\)Ljava\/lang\/String;/g
      }
    ]
  },
  'view.gesture.JodelGestureListener': {
    fields: [
      {
        name: 'Field_JodelGestureListener_Post',
        filter: /\.field protected (\w+):Lcom\/jodelapp\/jodelandroidv3\/api\/model\/Post;/g
      }
    ]
  }
}

module.exports = {
  targets,
  PACKAGE_BASE
}
