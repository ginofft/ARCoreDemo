package com.google.arcore.java.common.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class PlaneSettings {
    public static final String SHARED_PREFERENCES_ID = "SHARE_PREFERENCES_PLANE_OPTIONS";
    public static final String SHARED_PREFERENCES_PLANE_SETTINGS = "vertical_plane_enabled";
    private boolean verticalPlaneSetting = true;
    private SharedPreferences sharedPreferences;

    public void onCreate(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_ID, context.MODE_PRIVATE);
        verticalPlaneSetting = sharedPreferences.getBoolean(SHARED_PREFERENCES_PLANE_SETTINGS, false);
    }

    public boolean isVerticalPlaneEnabled() {return verticalPlaneSetting;}

    public void setVerticalPlaneEnabled(boolean enable){
        if (enable == verticalPlaneSetting){
            return;
        }
        verticalPlaneSetting = enable;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHARED_PREFERENCES_PLANE_SETTINGS, verticalPlaneSetting);
        editor.apply();
    }
}
