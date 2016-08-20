package sampleapp.loop.ms.locations.utils;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ms.loop.loopsdk.profile.KnownLocation;
import ms.loop.loopsdk.profile.Label;
import ms.loop.loopsdk.profile.Visit;
import sampleapp.loop.ms.locations.R;

public class VisitView {

    TextView txtLastVisited;
    TextView txtLastVisitEnterTime;
    TextView txtLastVisitExitTime;
    TextView txtVisitDuration;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.US);
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("EEE (MM/dd)", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);

    public VisitView(View view) {

        txtLastVisited = (TextView) view.findViewById(R.id.txtlastvisited);
        txtLastVisitEnterTime = (TextView) view.findViewById(R.id.lastvisitentertime);
        txtLastVisitExitTime = (TextView) view.findViewById(R.id.lastvisitexittime);
        txtVisitDuration = (TextView) view.findViewById(R.id.visitduration);
    }
    public void update(Context context, Visit visit){

        txtLastVisited.setText(getLastVisitTime(visit));
        txtLastVisitEnterTime.setText(timeFormat.format(new Date(visit.startTime)));
        txtLastVisitExitTime.setText(timeFormat.format(new Date(visit.endTime)));
        txtVisitDuration.setText(getVisitDuration(visit));
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
