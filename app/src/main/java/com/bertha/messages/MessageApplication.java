package com.bertha.messages;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by liliana on 01/09/15.
 */
public class MessageApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "6QYKTNWyXBCRA7TxqcUXC5P2SZ45CRkpnvzANiLg", "dqmM31fQVi4BU1vXGPJ0wU8tKfAsXkVhNocP1UMY");
    }
}
