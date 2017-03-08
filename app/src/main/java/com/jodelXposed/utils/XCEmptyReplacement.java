package com.jodelXposed.utils;

import de.robv.android.xposed.XC_MethodReplacement;

/**
 * Created by Admin on 11.02.2017.
 */

public class XCEmptyReplacement extends XC_MethodReplacement {
    @Override
    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
        return null;
    }
}
