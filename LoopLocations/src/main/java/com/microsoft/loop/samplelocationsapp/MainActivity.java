package com.microsoft.loop.samplelocationsapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.microsoft.loop.samplelocationsapp.utils.LoopUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ms.loop.loopsdk.api.LoopApiHelper;
import ms.loop.loopsdk.core.ILoopServiceCallback;
import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.profile.IProfileDownloadCallback;
import ms.loop.loopsdk.profile.IProfileItemChangedCallback;
import ms.loop.loopsdk.profile.KnownLocation;
import ms.loop.loopsdk.profile.Label;
import ms.loop.loopsdk.profile.Labels;
import ms.loop.loopsdk.profile.Locations;
import ms.loop.loopsdk.profile.LoopLocale;
import ms.loop.loopsdk.providers.LoopLocationProvider;
import ms.loop.loopsdk.util.LoopError;


public class MainActivity extends AppCompatActivity {
    //drives
    private BroadcastReceiver mReceiver;
    private LocationsViewAdapter adapter;
    private ListView tripListView;
    private TextView termsTextView;
    private TextView privacyTextView;

    private static RelativeLayout currentLocationContainer;
    private static ImageView locationIcon;
    private static TextView locationName;
    private NavigationView navigationView;

    private static String TOU_URL = "http://go.microsoft.com/fwlink/?LinkID=530144";
    private static String PRIVACY_URL = "http://go.microsoft.com/fwlink/?LinkId=521839";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.locations);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
       // navigationView.setNavigationItemSelectedListener(this);

        IntentFilter intentFilter = new IntentFilter("android.intent.action.onInitialized");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadKnownLocations();
            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);


        List<KnownLocation> locations = new ArrayList<KnownLocation>(LoopUtils.getLocations());
        adapter = new LocationsViewAdapter(this,
                R.layout.locationview, locations);

        tripListView = (ListView)findViewById(R.id.tripslist);
        tripListView.setAdapter(adapter);

        locationName = (TextView) this.findViewById(R.id.txtlocationName);
        currentLocationContainer = (RelativeLayout) this.findViewById(R.id.locationstrackingcontainer);
        locationIcon = (ImageView) this.findViewById(R.id.locationIcon);

        termsTextView = (TextView) navigationView.findViewById(R.id.terms);

        termsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInBrowser(TOU_URL);
            }
        });

        privacyTextView = (TextView) navigationView.findViewById(R.id.privacy);

        privacyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInBrowser(PRIVACY_URL);
            }
        });

        String[] ids = getResources().getStringArray(R.array.navigationmenu);
        final ListView navigationList = (ListView) findViewById(R.id.custom_list);
        navigationList.setAdapter(new NavigationViewAdapter(this, ids));
        navigationList.setItemsCanFocus(false);
        loadKnownLocations();
        SampleAppApplication.instance.openLocationServiceSettingPage(this);
    }

    @Override
    protected void onStart(){
        super.onStart();

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void loadKnownLocations()
    {
        if (LoopUtils.getLocations().size() > 0 || !LoopSDK.isInitialized() || TextUtils.isEmpty(LoopSDK.userId)) {
            loadKnownLocationsInUI();
            return;
        }

        LoopUtils.downloadLocations( new IProfileDownloadCallback() {
            @Override
            public void onProfileDownloadComplete(int itemCount) {
                loadKnownLocationsInUI();
            }

            @Override
            public void onProfileDownloadFailed(LoopError error) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadKnownLocationsInUI();
        //SampleAppApplication.instance.openLocationServiceSettingPage(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }


    public void loadKnownLocationsInUI() {
        LoopUtils.loadItems();
        final List<KnownLocation> locations = new ArrayList<KnownLocation>(LoopUtils.getLocations());;

        runOnUiThread(new Runnable() {
            public void run() {
                adapter.update(locations);
            }
        });

        Location currentLocation = LoopLocationProvider.getLastLocation();
        if (currentLocation !=null){
            for (KnownLocation location: locations){
                if (location.isLocationInsideRadius(currentLocation)){
                    currentLocationContainer.setVisibility(View.VISIBLE);
                    String label = "UNKNOWN";
                    if (location.hasLabels()){
                        label = location.labels.getTopLabel().name;
                    }
                    locationName.setText(label.toUpperCase());
                    if (label.equalsIgnoreCase("home")){
                        locationIcon.setImageResource(R.drawable.home);
                    }
                    else if (label.equalsIgnoreCase("work")){
                            locationIcon.setImageResource(R.drawable.work);
                        }
                    break;
                    }

                }
            }
    }

    public void openUrlInBrowser(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    /*public String checkSelectedItemType() {
        final TextView titleTextView = (TextView) findViewById(R.id.toolbar_title);
        return (String) titleTextView.getText();
    }*/



   /* public static boolean isKnownLocation(String entityId, String knownLocationType)
    {
        KnownLocation knownLocation = knownLocations.byEntityId(entityId);
        if (knownLocation == null || !knownLocation.isValid()) return false;
        Labels labels = knownLocation.labels;

        for (Label label:labels){
            if (label.name.equalsIgnoreCase(knownLocationType))
                return true;
        }
        return false;
    }*/
}
