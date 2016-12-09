package com.microsoft.loop.samplelocationsapp;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.microsoft.loop.samplelocationsapp.utils.LoopUtils;

import java.util.ArrayList;
import java.util.Calendar;

import ms.loop.loopsdk.api.ILoopApiCallback;
import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.profile.KnownLocation;
import ms.loop.loopsdk.profile.Locations;
import ms.loop.loopsdk.providers.LoopLocationProvider;
import ms.loop.loopsdk.signal.SignalConfig;
import ms.loop.loopsdk.util.LoopError;

public class NavigationViewAdapter extends BaseAdapter {
    private String[] listData;
    private LayoutInflater layoutInflater;
    private Activity context;

    private static String Loop_URL = "https://www.loop.ms/";


    public NavigationViewAdapter(Activity aContext, String[] listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
        context = aContext;
    }

    @Override
    public int getCount() {
        return listData.length;
    }

    @Override
    public Object getItem(int position) {
        return listData[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.drawerview, null);
            holder = new ViewHolder();
            holder.menuTitle = (TextView) convertView.findViewById(R.id.menutitle);
            holder.locationswitch = (SwitchCompat) convertView.findViewById(R.id.switch_compat);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.menuTitle.setText(listData[position]);
        holder.locationswitch.setTag(listData[position]);

        if (holder.menuTitle.getText().equals(context.getResources().getString(R.string.locationtracker))) {
            holder.locationswitch.setVisibility(View.VISIBLE);

            boolean isChecked = SampleAppApplication.getBooleanSharedPrefValue(context.getApplicationContext(), "AppTracking", true);
            holder.locationswitch.setChecked(isChecked);
        } else if (holder.menuTitle.getText().equals(context.getResources().getString(R.string.helpusimprove))) {
            holder.locationswitch.setVisibility(View.VISIBLE);
            boolean isChecked = SampleAppApplication.instance.isLoopEnabled();
            holder.locationswitch.setChecked(isChecked);
        } else {
            holder.locationswitch.setVisibility(View.GONE);
        }

        holder.locationswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.getTag().equals(context.getResources().getString(R.string.locationtracker))){
                    toggleLocationTracking(isChecked);
                }
                if (buttonView.getTag().equals(context.getResources().getString(R.string.helpusimprove))) {
                    toggleHelpUsImprove(isChecked);
                }

            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleMenuClickEvent(v, (String) holder.menuTitle.getText());
            }
        });
        return convertView;
    }

    public void toggleLocationTracking(boolean isChecked){
        boolean isLocationOn = SampleAppApplication.isLocationTurnedOn(context);
        if (isChecked) {
            if (!isLocationOn) {
                SampleAppApplication.instance.openLocationServiceSettingPage(context);
            }
            LoopUtils.startLocationProvider();
        } else {
            LoopUtils.stopLocationProvider();
        }
        SampleAppApplication.setSharedPrefValue(context.getApplicationContext(), "AppTracking", isChecked);
    }

    public void toggleHelpUsImprove(boolean isChecked){
        if (!isChecked ) {
            if (LoopSDK.isInitialized()) {
                LoopSDK.deleteUser(new ILoopApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void value) {
                        SampleAppApplication.instance.uninitializeLoop();
                    }

                    @Override
                    public void onError(LoopError error) {}
                });
            }
        } else {
            SampleAppApplication.instance.initializeLoopSDK();
        }
    }


    public void handleMenuClickEvent(View v, String menuTitle){
        if (menuTitle.equals(context.getResources().getString(R.string.clearalllocations))) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            Locations knownLocations = Locations.createAndLoad(Locations.class, KnownLocation.class);
                            knownLocations.deleteAll();
                            Intent i = new Intent("android.intent.action.onInitialized").putExtra("status", "initialized");
                            context.sendBroadcast(i);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }
        else  if (menuTitle.equals(context.getResources().getString(R.string.learnmore))) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Loop_URL));
            context.startActivity(browserIntent);
        }
    }
    static class ViewHolder {
        TextView menuTitle;
        SwitchCompat locationswitch;
    }
}
