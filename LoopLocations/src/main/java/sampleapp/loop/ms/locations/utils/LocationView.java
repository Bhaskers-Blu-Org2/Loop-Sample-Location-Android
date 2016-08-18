package sampleapp.loop.ms.locations.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ms.loop.loopsdk.profile.Drive;
import ms.loop.loopsdk.profile.KnownLocation;
import ms.loop.loopsdk.profile.Label;
import ms.loop.loopsdk.profile.Trip;
import ms.loop.loopsdk.profile.Visit;
import sampleapp.loop.ms.locations.MainActivity;
import sampleapp.loop.ms.locations.R;

/**
 * Created on 6/22/16.
 */
public class LocationView {

    ImageView locationIcon;
    TextView txtLocantionName;
    TextView txtLastVisited;
    TextView txtLastVisitEnterTime;
    TextView txtLastVisitExitTime;
    TextView txtVisitDuration;
    TextView txtVisitCount;
    TextView txtVisitLabel;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.US);
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("EEE (MM/dd)", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);

    public LocationView(View view) {

        txtLocantionName = (TextView) view.findViewById(R.id.locationName);
        txtLastVisited = (TextView) view.findViewById(R.id.txtlastvisited);
        txtLastVisitEnterTime = (TextView) view.findViewById(R.id.lastvisitentertime);
        txtLastVisitExitTime = (TextView) view.findViewById(R.id.lastvisitexittime);
        txtVisitDuration = (TextView) view.findViewById(R.id.visitduration);

        locationIcon = (ImageView) view.findViewById(R.id.locationicon);
        txtVisitCount = (TextView) view.findViewById(R.id.visitcount);
        txtVisitLabel = (TextView) view.findViewById(R.id.visitlable);

    }
    public void update(Context context, KnownLocation  location){
        String locationLabel = getLocationLabels(location);
        txtLocantionName.setText(locationLabel);
        locationIcon.setImageResource(locationLabel.equalsIgnoreCase("work")? R.drawable.work : R.drawable.home);
        txtVisitCount.setText(""+location.visits.size());

        List<Visit> visits = location.visits.getVisits();

        if (visits.size() > 0){

            Visit visit = visits.get(visits.size() -  1);
            txtLastVisited.setText(getLastVisitTime(visit));
            txtLastVisitEnterTime.setText(timeFormat.format(new Date(visit.startTime)));
            txtLastVisitExitTime.setText(timeFormat.format(new Date(visit.endTime)));
            txtVisitDuration.setText(getVisitDuration(visit));

            if (visits.size() > 1){
                txtVisitLabel.setText(context.getResources().getString(R.string.locationview_visits));
            }
        }
    }

    public String getLastVisitTime(Visit visit) {

        if (DateUtils.isToday(visit.startTime)) {
            return "Today";
        } else if (ViewUtils.isYesterday(visit.startTime)) {
            return "Yesterday";
        }
        else if (ViewUtils.isThisWeek(visit.startTime)){
            return String.format(Locale.US, "%s", dateFormat.format(visit.startTime));
        }
        return String.format(Locale.US, "%s", dateFormat2.format(visit.startTime));
    }

    public String getVisitDuration(Visit visit)
    {
        long diffInSeconds = (visit.endTime - visit.startTime) / 1000;

        long diff[] = new long[] {0, 0, 0 };
        diff[2] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        diff[0] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;

        return String.format(Locale.US,
                "%s%d:%s%d:%s%d",
                diff[0] < 9 ? "0" : "",
                diff[0],
                diff[1] < 9 ? "0": "",
                diff[1],
                diff[2] < 9 ? "0":"",
                diff[2]);
    }

    private String getLocationLabels(KnownLocation location) {

        StringBuffer locationLabel = new StringBuffer();
        if (location.hasLabels()){
            for (Label label:location.labels) {
                String tmpLabel = label.name;
                if (tmpLabel.equals("work")) tmpLabel = "Work";
                if (tmpLabel.equals("home")) tmpLabel = "Home";

                if (!TextUtils.isEmpty(locationLabel.toString())){
                    locationLabel.append(", ");
                }
                locationLabel.append(tmpLabel);

            }
        }
        else {
            return "Unknown";
        }

        return locationLabel.toString();
    }

}
