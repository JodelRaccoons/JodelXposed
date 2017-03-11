Jodel Enhancements
==================

This modules utilises the [Xposed Framework](https://www.youtube.com/watch?v=uRR0Flqx9M8) to add features to [Jodel](https://jodel-app.com/).

<!-- toc -->

- [Install](#install)
- [Features](#features)
  * [Upload custom images](#upload-custom-images)
  * [Increased control over account](#increased-control-over-account)
  * [OTA Hook updates](#ota-hook-updates)
- [Version compatibility](#version-compatibility)
- [Report a bug / Make a suggestion] (#Report-a-bug-Make-a-suggestion)
- [Support](#support)
  * [OnePlus OxygenOS rom issues](#oneplus-oxygenos-rom-issues)
  * [You dont have Google play services? No problem!](#you-dont-have-google-play-services-no-problem)
- [Contributions](#contributions)
- [Special thanks](#special-thanks)

<!-- tocstop -->

# Install
1. Install via the Xposed Installer
2. Disable automatic Jodel updates via Play Store

# Features
* Spoof location
* Track posters within a thread
* Removed blur on images
* Save images (double tap thumbnail)
* Copy post message (double tap post)
* Upload custom images
* Change/manage account
* OTA hook updates

## Upload custom images
1. Press the Gallery button next to the Jodel camera button
2. Select an image and post

## Increased control over account
- The account id (UID) is automatically backed upp in `.jodel-settings-v2`
- The UID can be manually changed to change account
- **PS**: Clear all Jodel app data after changing the UID

## OTA Hook updates
1. JX will automatically search for new hooks when you update Jodel
2. If hooks are available they'll be downloaded. Force restart Jodel when you're notified.
3. If hooks are not available you can:
    * Downgrade Your Jodel version
    * Wait for a hook update
    * Supply a hook update yourself - see [contributions](CONTRIBUTION.md) 

# Version compatibility
The module aims for compatibility with the latest Jodel version. Jodel apks are usually linked in [the thread on XDA](http://forum.xda-developers.com/xposed/modules/mod-jodelxposed-enhancements-t3350019/).

The latest supported Jodel version is noted [here](https://github.com/krokofant/JodelXposed/blob/master/app/build.gradle#L7) or as a hooks folder [here](https://github.com/krokofant/JodelXposed/tree/master/hooks).

# Report a bug / Make a suggestion
We'd love to see you reporting a bug to make this Xposed module even better.
If you want to report a bug we need some information:
1. Steps to reproduce the bug.
2. Some details about your enviroment like:
    * Android OS and Xposed version
    * Your phone model and rom information (this helps to sort out rom issues)
    * The Jodel/JodelXposed log (depends on what is crashing)
    * The Xposed log
3. Create a issue, providing the information. We'll give our best to solve your problem!

If you want to give a suggestion for a new feature just create a issue providing a short description!


# Support
* Are you using Android?
* Do you have Xposed working?
* Are you sure your version is compatible?
* Have you read [the thread on XDA](http://forum.xda-developers.com/xposed/modules/mod-jodelxposed-enhancements-t3350019/)?
* Create an issue if you still have questions

## OnePlus OxygenOS rom issues
There are many issues relating to the OxygenOS rom on different OnePlus devices. Big part of the issue are buggy xposed resources. We can't do anything about it, a issue at the Xposed Github repo has already been created. The only workaround til now is switching to a different rom.

## You dont have Google play services? No problem!
Just edit the file file /sdcard/JodelXposed/jodel-settings-v2.json and edit your lat/lng coordinates manually.
```json
"location":{"active":true,"lat":52.5200066,"lng":13.404954}
```

# Contributions
Take a look at [CONTRIBUTION.md](CONTRIBUTION.md). Pull requests are welcome if you would like to provide additional features or just update the hooks.

Please *test the PR* before you submit it.

# Special thanks
* Charlie Kelly
* Unbrick
