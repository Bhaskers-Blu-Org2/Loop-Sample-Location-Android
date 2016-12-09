package com.microsoft.loop.samplelocationsapp.utils;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;
import com.microsoft.loop.samplelocationsapp.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ms.loop.loopsdk.profile.KnownLocation;
import ms.loop.loopsdk.profile.Label;
import ms.loop.loopsdk.profile.Visit;

public class VisitView {

    TextView txtLastVisited;
    TextView txtLastVisitEnterTime;
    TextView txtLastVisitExitTime;
    TextView txtVisitDuration;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.US);
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("M/d/yy", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);

    public VisitView(View view) {

        txtLastVisited = (TextView) view.findViewById(R.id.txtlastvisited);
        txtLastVisitEnterTime = (TextView) view.findViewById(R.id.lastvisitentertime);
        txtLastVisitExitTime = (TextView) view.findViewById(R.id.lastvisitexittime);
        txtVisitDuration = (TextView) view.findViewById(R.id.visitduration);
    }
    public void update(Context context, Visit visit){

        txtLastVisited.setText(getLastVisitTime(visit));
        txtLastVisitEnterTime.setText(timeFormat.format(visit.startTime));
        txtLastVisitExitTime.setText(timeFormat.format(visit.endTime));
        txtVisitDuration.setText(getVisitDuration(visit));
    }

    public String getLastVisitTime(Visit visit) {

        if (DateUtils.isToday(visit.startTime.getTime())) {
            return "Today";
        } else if (ViewUtils.isYesterday(visit.startTime.getTime())) {
            return "Yesterday";
        }
        else if (ViewUtils.isThisWeek(visit.startTime.getTime())){
            return String.format(Locale.US, "%s", dateFormat.format(visit.startTime));
        }
        return String.format(Locale.US, "%s", dateFormat2.format(visit.startTime));
    }

    public String getVisitDuration(Visit visit)
    {
        long diffInSeconds = (visit.endTime.getTime() - visit.startTime.getTime()) / 1000;

        long diff[] = new long[] {0, 0, 0 };
        diff[2] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        diff[0] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;

        return String.format(Locale.US,
                "%s%d:%s%d:%s%d",
                diff[0] <= 9 ? "0" : "",
                diff[0],
                diff[1] <= 9 ? "0": "",
                diff[1],
                diff[2] <= 9 ? "0": "",
                diff[2]);
    }
}
