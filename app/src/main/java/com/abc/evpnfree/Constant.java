package com.abc.evpnfree;

import android.content.Context;
import android.content.SharedPreferences;

public class Constant {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public static final String PrivacyPolicyUrl = "https://pharid.com/privacy-policy/"; // PUT YOUR Privacy Policy Url Here
    public static final String UpgradePro = "VPNPro2019"; //Please put something different for example you can put your app name here without spaces


    private static final String PREF_NAME = "snow-intro-slider";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public Constant(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}