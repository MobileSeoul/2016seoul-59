package kr.edcan.lumihana.itravelu;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import io.realm.Realm;

/**
 * Created by kimok_000 on 2016-10-30.
 */
public class Application extends MultiDexApplication {
    public void Application(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(Application.this);
        FacebookSdk.sdkInitialize(Application.this);
        AppEventsLogger.activateApp(this);
    }
}
