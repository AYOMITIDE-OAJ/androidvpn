package com.oajstudios.evpn.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.oajstudios.evpn.App;

public class NetworkState {

    public static boolean isOnline()
    {
        ConnectivityManager cm =
                (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
}
