package com.iwebnext.vchatt.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class UiUtil {
    /**
     * Utility method to remove auto capitalization of buttons
     * @param viewGroup
     */
    public static void removeButtonTransformationMethod(ViewGroup viewGroup) {

        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                removeButtonTransformationMethod((ViewGroup) view);
            } else if (view instanceof Button) {
                Button button = (Button) view;
                button.setTransformationMethod(null);
            }
        }
    }
}
