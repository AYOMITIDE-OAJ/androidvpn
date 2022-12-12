package com.oajstudios.evpn.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.oajstudios.evpn.Fragments.PhoneBooster_Fragment;
import com.oajstudios.evpn.R;

public class BoostAlarm extends BroadcastReceiver {

    public final static String PREFERENCES_RES_BOOSTER = "pharid";

    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedpreferences = context.getSharedPreferences(PREFERENCES_RES_BOOSTER, Context.MODE_PRIVATE);

        editor = sharedpreferences.edit();
        editor.putString("booster", "1");
        editor.commit();

        try {
            PhoneBooster_Fragment.optimizebutton.setBackgroundResource(0);
            PhoneBooster_Fragment.optimizebutton.setImageResource(0);
            PhoneBooster_Fragment.optimizebutton.setImageResource(R.drawable.clear_btn);
        }
        catch(Exception e)
        {

        }

    }
}
