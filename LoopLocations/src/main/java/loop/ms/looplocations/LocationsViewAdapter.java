package loop.ms.looplocations;

/**
 * Created on 6/1/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ms.loop.loopsdk.profile.KnownLocation;
import ms.loop.loopsdk.profile.Label;
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
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE h:mm a (MM/dd)");

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
        TripHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new TripHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtLocationInfo = (TextView)row.findViewById(R.id.txtlatlong);
            holder.txtLastVisited = (TextView)row.findViewById(R.id.lastUpdatedtxt);
            holder.locationIcon = (ImageView)row.findViewById(R.id.locationicon);
            holder.txtTotalScore = (TextView) row.findViewById(R.id.score);
            holder.txtVisit = (TextView) row.findViewById(R.id.visits);

            row.setTag(holder);
            row.setClickable(true);
        }
        else {
            holder = (TripHolder)row.getTag();
        }


        if (locations.isEmpty()) return row;

        final KnownLocation location = locations.get(position);
        if (location == null ) return row;

        String locationLabel = "Unknown";
        if (location.hasLabels()){
            locationLabel = location.labels.getLabels().get(0).name;

            if (locationLabel.equals("work")) locationLabel = "Work";
            if (locationLabel.equals("home")) locationLabel = "Home";
        }
        holder.txtTitle.setText(locationLabel);
        holder.locationIcon.setImageResource(locationLabel.equalsIgnoreCase("work")? R.drawable.work : R.drawable.home);
        holder.txtLocationInfo.setText(String.format("Latitude: %.3f, Longitutde: %.3f", location.latDegrees, location.longDegrees));
        holder.txtLastVisited.setText(String.format("Updated on %s", dateFormat.format(location.updatedAt)));
        holder.txtVisit.setText(getVisitInfo(location));
        holder.txtTotalScore.setText(String.format("Score: %.3f Visits: %d", location.score, location.visits.size()));
        row.setClickable(true);

        final String locationLabelTemp = locationLabel;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Uri gmmIntentUri = Uri.parse(String.format("geo:0,0?q=%f,%f(%s)", location.latDegrees, location.longDegrees, locationLabelTemp));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            }
            }
        });
        return row;
    }

    static class TripHolder {
        ImageView locationIcon;
        TextView txtTitle;
        TextView txtLocationInfo;
        TextView txtLastVisited;
        TextView txtVisit;
        TextView txtTotalScore;
    }

    public String getVisitInfo(KnownLocation knownLocation) {
        if (knownLocation.hasVisits()) {
            Visits visits = knownLocation.visits;
            Visit lastVisit = visits.getVisits().get(0);
            return String.format("Last visited on %s for %s", dateFormat.format(new Date(lastVisit.startTime)), getVisitDuration(lastVisit));
        }
        return "No visits yet!";
    }

    public String getVisitDuration(Visit visit)
    {
        long diffInSeconds = (visit.endTime - visit.startTime) / 1000;

        long diff[] = new long[] {0, 0, 0 };
        diff[2] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        diff[0] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;

        return String.format(
                "%s%d:%s%d:%s%d",
                diff[0] < 9 ? "0" : "",
                diff[0],
                diff[1] < 9 ? "0": "",
                diff[1],
                diff[2] < 9 ? "0":"",
                diff[2]);
    }
}
