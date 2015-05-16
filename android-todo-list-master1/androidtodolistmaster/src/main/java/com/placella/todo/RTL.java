package com.placella.todo;

import android.os.Build;

import java.util.Locale;

/**
 * Used to determine whether additional support for RTL languages is required. If the device is
 * running an Android version below 4.2 (which offers native RTL support), additional support is
 * needed to achieve a RTL layout.
 *
 * @author Alan Sheehan
 */
public final class RTL {

    /**
     * Default constructor set to private - no instances should be created.
     */
    private RTL(){}


    /**
     * Determines whether or not the SDK version on the running device requires additional RTL
     * support.
     *
     * @return true if additional RTL support is required, false otherwise
     */
    public static boolean requiresSupport(){

        if (Build.VERSION.SDK_INT < 17){
            Locale locale = Locale.getDefault();
            int direction = Character.getDirectionality(locale.getDisplayName().charAt(0));

            return direction == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                    direction == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
        }
        else {
            return false;
        }

    }

}
