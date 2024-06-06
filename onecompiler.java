import java.util.*;

public class Main {
    public static void main(String[] args) {
      System.out.println("Welcome to Step Tracker");
      
      Step step1 = new Step(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
      
      step1.isStep();
      
      Step step2 = new Step(1.1, 2.1, 3.1, 4.0, 5.0, 6.0);
      
      step1.isStep();
      
      TrackManager.trackState = TrackManager.State.START;
      
      Step step3 = new Step(1.1, 2.1, 3.1, 4.0, 5.0, 6.0);
      
      step1.isStep();
      
      Step step4 = new Step(1.3, 2.3, 3.3, 4.0, 5.0, 6.0);
      
      step1.isStep();
      
      Step step5 = new Step(1.4, 2.4, 3.4, 4.0, 5.0, 6.0);
      
      step1.isStep();
  }
}

public class TrackManager {
  
  enum State {START,STOP,PAUSE}
  
  static State trackState = State.STOP;
  
}

public class Track {
  
  public static int stepsTaken = 0;
  
}

public class Step {

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
      
      Track.stepsTaken = Track.stepsTaken + 1;
      
      System.out.println("You made " + Track.stepsTaken + " steps!!!");

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
