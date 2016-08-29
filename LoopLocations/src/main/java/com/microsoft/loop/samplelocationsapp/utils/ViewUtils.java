package com.microsoft.loop.samplelocationsapp.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created on 6/19/16.
 */
public class ViewUtils {

    /*public static void applyDriodFont(Context context, TextView textView) {
        final Typeface font = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "DroidSansMono.ttf");
        textView.setTypeface(font);
    }*/

    /*public static void applyRobotoMediumFont(Context context, TextView textView) {
        final Typeface font = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "Roboto-Medium.ttf");
        textView.setTypeface(font);
    }*/

    /*public static void applyRobotoRegularFont(Context context, TextView textView) {
        final Typeface font = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "Roboto-Regular.ttf");
        textView.setTypeface(font);
    }*/

    public static void applyFontToMenuItem(Context context, MenuItem mi, String fontName) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), fontName + ".ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public static boolean isYesterday(long date) {
        Calendar now = Calendar.getInstance();
        Calendar cdate = Calendar.getInstance();
        cdate.setTimeInMillis(date);

        now.add(Calendar.DATE,-1);

        return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR)
                && now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH)
                && now.get(Calendar.DATE) == cdate.get(Calendar.DATE);
    }

    public static boolean isThisWeek(long date) {
        Calendar now = Calendar.getInstance();
        Calendar cdate = Calendar.getInstance();
        cdate.setTimeInMillis(date);

        now.add(Calendar.DATE,-7);

        return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR)
                && now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH)
                && cdate.get(Calendar.DATE) >= now.get(Calendar.DATE);
    }
}
