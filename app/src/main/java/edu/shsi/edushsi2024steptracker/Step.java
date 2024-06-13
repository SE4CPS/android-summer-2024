package edu.shsi.edushsi2024steptracker;

public class Step {

    // Calories

    static double caloriesPerStep = 0.04;

    // Previous
    static double previousSensorAccelerometerX = 0;
    static double previousSensorAccelerometerY = 0;
    static double previousSensorAccelerometerZ = 0;
    static double previousSensorGpsLat = 0;
    static double previousSensorGpsLon = 0;
    static double previousSensorGpsAlt = 0;

    // Current
    static double currentSensorAccelerometerX = 0;
    static double currentSensorAccelerometerY = 0;
    static double currentSensorAccelerometerZ = 0;
    static double currentSensorGpsLat = 0;
    static double currentSensorGpsLon = 0;
    static double currentSensorGpsAlt = 0;

    Step(double ax, double ay, double az, double glat, double glon, double galt) {
        currentSensorAccelerometerX = ax;
        currentSensorAccelerometerY = ay;
        currentSensorAccelerometerZ = az;
        currentSensorGpsAlt = galt;
        currentSensorGpsLon = glon;
        currentSensorGpsLat = glat;
    }

    // Behavior
    public boolean isStep() {

        boolean previousSensorAccelerometerXChanged = false;
        boolean previousSensorAccelerometerYChanged = false;

        if (previousSensorAccelerometerX != currentSensorAccelerometerX) {
            previousSensorAccelerometerXChanged = true;
        }

        if (previousSensorAccelerometerY != currentSensorAccelerometerY) {
            previousSensorAccelerometerYChanged = true;
        }

        if (previousSensorAccelerometerXChanged && previousSensorAccelerometerYChanged && TrackManager.trackState == TrackManager.State.START ) {

            previousSensorAccelerometerX = currentSensorAccelerometerX;
            previousSensorAccelerometerY = currentSensorAccelerometerY;
            previousSensorAccelerometerZ = currentSensorAccelerometerZ;
            previousSensorGpsLat = currentSensorAccelerometerZ;
            previousSensorGpsLon = currentSensorGpsAlt;
            previousSensorGpsAlt = currentSensorGpsLat;

            return true;

        } else {
            System.out.println("No step");
            return false;
        }
    }
}