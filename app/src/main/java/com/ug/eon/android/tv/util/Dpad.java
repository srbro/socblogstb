package com.ug.eon.android.tv.util;

import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Created by nikola.djokic on 1/27/2017.
 */

public class Dpad {
    int directionPressed = -1; // initialized to -1

    public int getDirectionPressed(InputEvent event) {
        // If the input event is a MotionEvent, check its hat axis values.
        if (event instanceof MotionEvent) {

            // Use the hat axis value to find the D-pad direction
            MotionEvent motionEvent = (MotionEvent) event;
            float xaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X);
            float yaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y);

            // Check if the AXIS_HAT_X value is -1 or 1, and set the D-pad
            // LEFT and RIGHT direction accordingly.
            if (Float.compare(xaxis, -1.0f) == 0) {
                directionPressed =  KeyEvent.KEYCODE_DPAD_LEFT;
            } else if (Float.compare(xaxis, 1.0f) == 0) {
                directionPressed =  KeyEvent.KEYCODE_DPAD_RIGHT;
            }
            // Check if the AXIS_HAT_Y value is -1 or 1, and set the D-pad
            // UP and DOWN direction accordingly.
            else if (Float.compare(yaxis, -1.0f) == 0) {
                directionPressed =  KeyEvent.KEYCODE_DPAD_UP;
            } else if (Float.compare(yaxis, 1.0f) == 0) {
                directionPressed =  KeyEvent.KEYCODE_DPAD_DOWN;
            }
        } else {
            directionPressed = -1;
        }
        Log.i("Button clicked", directionPressed + "");
        Log.i("Button clicked", event.toString());
        return directionPressed;
    }
}