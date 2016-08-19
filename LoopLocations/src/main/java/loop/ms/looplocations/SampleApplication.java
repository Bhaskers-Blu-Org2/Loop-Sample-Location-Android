package loop.ms.looplocations;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ms.loop.loopsdk.core.ILoopSDKCallback;
import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.core.LoopServiceManager;
import ms.loop.loopsdk.processors.KnownLocationProcessor;
import ms.loop.loopsdk.providers.LoopLocationProvider;
import ms.loop.loopsdk.signal.Signal;
import ms.loop.loopsdk.signal.SignalConfig;
import ms.loop.loopsdk.util.LoopError;

/**
 * Created on 6/1/16.
 */
public class SampleApplication extends Application implements ILoopSDKCallback{

    private KnownLocationProcessor knownLocationProcessor;

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the Loop SDK. create an account to get your appId and appToken
        String appId = BuildConfig.APP_ID; // Or replace your id here
        String appToken = BuildConfig.APP_TOKEN; // or replace your app token here

        String userId = "YOUR USER ID";
        String deviceId = "YOUR DEVICE ID";
        LoopSDK.initialize(this, appId, appToken);
    }

    @Override
    public void onInitialized() {
        Intent i = new Intent("android.intent.action.onInitialized").putExtra("status", "initialized");
        this.sendBroadcast(i);

        LoopServiceManager.startLocationProvider(SignalConfig.SIGNAL_SEND_MODE_REALTIME);
        LoopLocationProvider.sendLocation(null, "fix");
        knownLocationProcessor = new KnownLocationProcessor();
        knownLocationProcessor.initialize();
        LoopSDK.enableLogging("loggly", BuildConfig.LOGGLY_TOKEN);
    }
    @Override
    public void onInitializeFailed(LoopError loopError) {}

    @Override
    public void onServiceStatusChanged(String provider, String status, Bundle bundle) {}

    @Override
    public void onDebug(String output) {}
}
