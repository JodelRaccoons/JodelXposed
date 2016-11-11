# Contributing & development
Hi, if you want to contribute to this project this document may help you. If there are any questions just create an issue.

# Finding the apk
I usually get the currently installed apk by copying it from `/data/app/[package name]/base.apk`.

# Finding hooks
There are a bunch of tools out there for decompiling and digging through apks.
The one I've found easiest to use is [jadx](https://github.com/skylot/jadx) in which you can open apks directly.

# Updating the hooks
If you want to help the development you just can make a pull request containing a new hooks.json in a folder with a matching version code. You can find the version code folders [in here](https://github.com/krokofant/JodelXposed/tree/master/hooks).

If you need help with finding the hooks (methods / fields) you should take a look at an older `hooks.json` and there you'll find, for every method/field, a brief explanation where it should be located and how to find the right one.
