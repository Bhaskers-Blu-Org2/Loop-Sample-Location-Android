package sampleapp.loop.ms.locations.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class ResizeAnimation extends Animation {

    private final float mStartWeight;
    private final float mDeltaWeight;
    private View mContent;

    public ResizeAnimation(View content, float startWeight, float endWeight) {
        mStartWeight = startWeight;
        mDeltaWeight = endWeight - startWeight;
        mContent = content;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContent.getLayoutParams();
        lp.weight = (mStartWeight + (mDeltaWeight * interpolatedTime));
        mContent.setLayoutParams(lp);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}