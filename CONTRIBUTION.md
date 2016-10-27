# Contributing & development
Hi, if you want to contribute to this project this document may help you. If there are any questions just create an issue.

# Finding hooks
There are a bunch of tools out the for decompiling and digging through apks.
The one I've found easiest to use is [jadx](https://github.com/skylot/jadx) in which you can open apks directly.

# Updating the hooks
If you want to help the development you just can make a pull request containing a new hooks.json in a folder with a matching version code. You can find the version code folders [in here](https://github.com/krokofant/JodelXposed/tree/master/hooks).

If you need help with finding the hooks (methods / fields) you should take a look at [the hookvalue object](https://github.com/krokofant/JodelXposed/blob/master/app/src/main/java/com/jodelXposed/models/Models.kt) , in there you can find for every method/field a brief explanation where it should be located and how to find the right one.
