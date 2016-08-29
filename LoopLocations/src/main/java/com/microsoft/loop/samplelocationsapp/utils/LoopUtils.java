package com.microsoft.loop.samplelocationsapp.utils;

import com.microsoft.loop.samplelocationsapp.SampleAppApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ms.loop.loopsdk.api.LoopApiHelper;
import ms.loop.loopsdk.core.ILoopServiceCallback;
import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.profile.Drive;
import ms.loop.loopsdk.profile.IProfileDownloadCallback;
import ms.loop.loopsdk.profile.IProfileItemChangedCallback;
import ms.loop.loopsdk.profile.KnownLocation;
import ms.loop.loopsdk.profile.Locations;
import ms.loop.loopsdk.profile.LoopLocale;
import ms.loop.loopsdk.providers.LoopLocationProvider;
import ms.loop.loopsdk.signal.SignalConfig;
import ms.loop.loopsdk.util.LoopError;

/**
 */
public class LoopUtils {
    private static Locations knownLocations;

    public static void initialize() {
        if (!LoopSDK.isInitialized()) return;
        knownLocations = Locations.createAndLoad(Locations.class, KnownLocation.class);
        knownLocations.registerItemChangedCallback("Locations", new IProfileItemChangedCallback() {
            @Override
            public void onItemChanged(String entityId) {}

            @Override
            public void onItemAdded(String entityId) {
                SampleAppApplication.mixpanel.track("Known Location created");
            }

            @Override
            public void onItemRemoved(String entityId) {}
        });
    }

    public static List<KnownLocation> getLocations() {
        if (LoopSDK.isInitialized()) {
            return knownLocations.sortedByScore();
        } else {
            return new ArrayList<>();
        }
    }

    public static void downloadLocations(final IProfileDownloadCallback callback) {

        if (!LoopSDK.isInitialized()) {
            callback.onProfileDownloadFailed(new LoopError("Loop not initialized"));
            return;
        }
        knownLocations.download(true, new IProfileDownloadCallback() {
            @Override
            public void onProfileDownloadComplete(int itemCount) {

                if (itemCount == 0) {
                    loadSampleLocations();
                }
                callback.onProfileDownloadComplete(itemCount);
            }

            @Override
            public void onProfileDownloadFailed(LoopError error) {
                if (knownLocations.size() == 0) {
                    loadSampleLocations();
                }
            }
        });
    }

    public static void loadItems() {
        if (LoopSDK.isInitialized()) {
            knownLocations.load();
        }
    }

    public static void loadSampleLocations() {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset("sample_locations.json"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                knownLocations.createAndAddItem(jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void startLocationProvider() {
        if (LoopSDK.isInitialized()) {
            LoopLocationProvider.start(SignalConfig.SIGNAL_SEND_MODE_BATCH);
        }
    }

    public static void stopLocationProvider() {
        if (LoopSDK.isInitialized()) {
            LoopLocationProvider.stop();
        }
    }

    public static String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = SampleAppApplication.instance.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
