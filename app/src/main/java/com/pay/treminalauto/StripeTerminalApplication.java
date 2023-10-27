
package com.pay.treminalauto;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import com.stripe.stripeterminal.TerminalApplicationDelegate;

public class StripeTerminalApplication extends Application {
    @Override
    public void onCreate() {
    /*    StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectAll()
                        .penaltyLog()
                        .build());

        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .penaltyLog()
                        .build());*/

        super.onCreate();
        try{
        TerminalApplicationDelegate.onCreate(this);}
        catch (Exception e ){
            Log.e("TAG", "StripeTerminalApplication onCreate: "+e.getMessage() );
            Log.e("TAG", "StripeTerminalApplication onCreate: "+e.getLocalizedMessage() );
            Log.e("TAG", "StripeTerminalApplication onCreate: "+e.getCause() );
        }
    }
}

