package com.microsoft.loop.samplelocationsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.loop.samplelocationsapp.utils.LocationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ms.loop.loopsdk.profile.KnownLocation;
import ms.loop.loopsdk.profile.Label;
import ms.loop.loopsdk.profile.Trip;
import ms.loop.loopsdk.profile.Visit;
import ms.loop.loopsdk.profile.Visits;


/**
 * Created on 5/30/16.
 *
 *
 */
public class LocationsViewAdapter extends ArrayAdapter<KnownLocation> {

    Context context;
    int layoutResourceId;
    List<KnownLocation> locations = new ArrayList<>();


    public LocationsViewAdapter(Context context, int layoutResourceId, List<KnownLocation> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        update(data);
    }

    public void update(List<KnownLocation> data) {

        locations.clear();
        for (KnownLocation location:data) {
            if (location.score > 0) {
                locations.add(location);
            }
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LocationView holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new LocationView(row);
            row.setTag(holder);
            row.setClickable(true);
        }
        else {
            holder = (LocationView) row.getTag();
        }


        if (locations.isEmpty()) return row;

        final KnownLocation location = locations.get(position);
        if (location == null ) return row;
        holder.update(context, location);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(context, MapsActivity.class);
                myIntent.putExtra("locationid", location.entityId); //Optional parameters
                context.startActivity(myIntent);
            }
        });
        return row;
    }

}
