//Workaround to get adjustResize functionality for input methos when the fullscreen mode is on
//found by Ricardo
//taken from http://stackoverflow.com/a/19494006

package com.lapism.searchview;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class AndroidBug5497Workaround {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams layoutParams;
    private int top = 0, leftRight = 0, bottom = 0;


    private ViewTreeObserver.OnGlobalLayoutListener globalListen = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            possiblyResizeChildOfContent();
        }
    };

    public void setContentView(View contentView) {
        mChildOfContent = contentView;
        mChildOfContent.getViewTreeObserver().removeOnGlobalLayoutListener(globalListen);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(globalListen);
        layoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
        top = layoutParams.topMargin;
        leftRight = layoutParams.leftMargin;
        bottom = layoutParams.bottomMargin;
    }

    private void possiblyResizeChildOfContent() {

        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int heightDifference = usableHeightPrevious - usableHeightNow;
            if (heightDifference > (Math.max(usableHeightNow, usableHeightPrevious) / 4)) {
                // keyboard probably just became visible
                layoutParams.setMargins(leftRight, top, leftRight, heightDifference + bottom);
            } else {
                // keyboard probably just became hidden
                layoutParams.setMargins(leftRight, top, leftRight, bottom);
            }
            mChildOfContent.setLayoutParams(layoutParams);
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

}