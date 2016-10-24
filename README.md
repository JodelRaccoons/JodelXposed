Jodel Enhancements
==================

This modules utilises the [Xposed Framework](https://www.youtube.com/watch?v=uRR0Flqx9M8) to add features to [Jodel](https://jodel-app.com/).

**Table of Contents**

- [Install](#)
- [Features](#)
	- [Upload custom images](#)
	- [Increased control over account](#)
- [OTA Hook updates](#)
- [How can i help / update the hooks?](#)
- [Version compatibility](#)
- [Support](#)
- [Contributions](#)
- [Special thanks](#)

## Install
1. Install via the Xposed Installer
2. Disable automatic Jodel updates via Play Store

## Features
* Spoof location
* Disabled anti-xposed measures
* Track posters within a thread
* Removed blur on images
* Upload custom images
* Change/manage account

### Upload custom images
1. Take picture with Jodel camera
2. Select *Replace image*
3. Choose the image shared to Jodel or select from gallery

### Increased control over account
- The account id (UID) is automatically backed upp in `.jodel-settings-v2`
- The UID can be manually changed to change account
- **PS**: Clear all Jodel app data after changing the UID

### OTA Hook updates
- When Jodel is updated (version code change) JodelXposed is requesting a new hooks.json file from the Github repository

What the user needs to do for it to work:

1. When your Jodel was updated, JX will search automatically for new hooks. 
2. In case someone already has pushed new hooks to the github repo, you will get a toast message that your hooks have been updated and you should soft-reboot your device.
3. If up-to-date hooks arent available you will get an error message and you'll have to wait for it (or update it by yourself). 
4. Profit.

JX will search for new hooks when starting Jodel.

### How can i help / update the hooks? 
If you want to help the development you just can make a pull request containing a new hooks.json in a folder with a matching version code. You can find the version code folders in here: https://github.com/krokofant/JodelXposed/tree/master/hooks . 

If you need help with finding the hooks (methods / fields) you should take a look at [the hookvalue object](https://github.com/krokofant/JodelXposed/blob/master/app/src/main/java/com/jodelXposed/models/Models.kt) , in there you can find for every method/field a brief explanation where it should be located and how to find the right one.

## Version compatibility
The module aims for compatibility with the latest Jodel version. Jodel apks are usually linked in [the thread on XDA](http://forum.xda-developers.com/xposed/modules/mod-jodelxposed-enhancements-t3350019/).

The latest supported Jodel version is noted [here](https://github.com/krokofant/JodelXposed/blob/master/app/build.gradle#L7).

## Support
* Are you using Android?
* Do you have Xposed working?
* Are you sure your version is compatible?
* Have you read [the thread on XDA](http://forum.xda-developers.com/xposed/modules/mod-jodelxposed-enhancements-t3350019/)?

## Contributions
Pull requests are welcome if you would like to provide additional features or just update the hooks.

Please *test the PR* before you submit it.

# Special thanks
* Charlie Kelly
* Unbrick
