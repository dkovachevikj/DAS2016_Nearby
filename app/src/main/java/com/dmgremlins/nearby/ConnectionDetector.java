package com.dmgremlins.nearby;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
    detects if the device is
    connected to the internet
 */
public class ConnectionDetector {

    Context context;

    public ConnectionDetector(Context context) {
        this.context = context;
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
                if(info != null) {
                    if(info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        }
        return false;
    }
}
