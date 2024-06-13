package edu.shsi.edushsi2024steptracker;

public class TrackManager {

    static int averageDistancePerStep = 2; // feet

    enum State {START,STOP,PAUSE}

    static State trackState = State.STOP;

}