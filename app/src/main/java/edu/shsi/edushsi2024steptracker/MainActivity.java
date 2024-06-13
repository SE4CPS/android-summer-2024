package edu.shsi.edushsi2024steptracker;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.WindowManager;
import android.widget.Button;

import edu.shsi.edushsi2024steptracker.databinding.ActivityMainBinding;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Duration;
import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private TrackManager trackManager = new TrackManager();
    private TextView stepsTakenView;
    private TextView caloriesTakenView;
    private TextView distanceTakenView;
    private TextView timeTakenView;
    private Track track;
    private DatabaseHelper dbHelper;
    private LocalDateTime start;
    private LocalDateTime end;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Vibrator vibrator;
    private SeekBar seekBar;
    private int zUser;
    private float zSensor;
    private LocalDateTime timestampLastUpdate;
    private LocalDateTime now;
    Button buttonStart;
    Button buttonStop;
    Button buttonPause;
    Button buttonTrackLog;
    private TextView seekBarLabelView;
    private TextView animationTextView;
    private ObjectAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Animation
        animationTextView = (TextView) findViewById(R.id.textView10);
        animationTextView.setText("");

        animator = ObjectAnimator.ofFloat(animationTextView, "translationY", 0f, 200f);
        animator.setDuration(1000);
        animator.setRepeatCount(3);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();

        dbHelper = new DatabaseHelper(this);

        NavController navController = Navigation.findNavController(this, R.id.seekBarLabelId);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        buttonStart = (Button) findViewById(R.id.buttonStartId);
        buttonStart.setOnClickListener(buttonStartListener);

        buttonStop = (Button) findViewById(R.id.buttonStopId);
        buttonStop.setOnClickListener(buttonStopListener);

        buttonStop.setEnabled(false);

        buttonPause = (Button) findViewById(R.id.buttonPauseId);
        buttonPause.setOnClickListener(buttonPauseListener);

        buttonPause.setEnabled(false);

        buttonTrackLog = (Button) findViewById(R.id.buttonTrackLogsId);
        buttonTrackLog.setOnClickListener(buttonTrackLogListener);

        seekBarLabelView = findViewById(R.id.textView6);

        seekBarLabelView.setText("Please adjust the bar according to your walking pace. Current value is 5.");

        stepsTakenView = findViewById(R.id.textView2);

        caloriesTakenView = findViewById(R.id.textView7);

        distanceTakenView = findViewById(R.id.textView4);

        timeTakenView = findViewById(R.id.textView9);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );


        seekBar = (SeekBar) findViewById(R.id.seekBarId);

        seekBar.setMax(20);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                zUser = progress;

                String label = "Please adjust the bar according to your walking pace";

                label += "\nCurrent user pace is " + String.valueOf(progress);

                label += "\nCurrent sensor pace is " + String.valueOf(String.format("%.02f", zSensor));

                seekBarLabelView.setText(label);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar.setProgress(5);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (sensorManager == null){
            Toast.makeText(this, "Sensor not supported", Toast.LENGTH_LONG).show();
        } else {
            // Toast.makeText(this, "Sensor supported", Toast.LENGTH_LONG).show();

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        timestampLastUpdate = LocalDateTime.now();

    }

    private View.OnClickListener buttonTrackLogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, TrackLogActivity.class);
            startActivity(i);
        }
    };

    private View.OnClickListener buttonStartListener = new View.OnClickListener() {
        public void onClick(View v) {

            if (trackManager.trackState == TrackManager.State.STOP) {
                start = LocalDateTime.now();
                end = LocalDateTime.now();
                track = new Track();
            }

            buttonTrackLog.setEnabled(false);

            trackManager.trackState = TrackManager.State.START;

            buttonStart.setEnabled(false);
            buttonPause.setEnabled(true);
            buttonStop.setEnabled(true);
        }
    };

    private View.OnClickListener buttonStopListener = new View.OnClickListener() {
        public void onClick(View v) {

            end = LocalDateTime.now();

            trackManager.trackState = TrackManager.State.STOP;

            addItem();

            buttonTrackLog.setEnabled(true);

            buttonStart.setEnabled(true);
            buttonPause.setEnabled(false);
            buttonStop.setEnabled(false);
        }
    };

    private void addItem() {

        String steps = stepsTakenView.getText().toString();
        String distance = distanceTakenView.getText().toString();
        String calories = caloriesTakenView.getText().toString();
        Duration duration = Duration.between(start,end);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("steps", steps);
        values.put("distance", distance);
        values.put("calories",calories);
        values.put("starting", start.toString());
        values.put("ending",end.toString());
        values.put("duration", (duration.toMinutes() == 0? "": duration.toMinutes() + "min ")  + duration.getSeconds() + "s");

        db.insert("tracks", null, values);

        db.close();
    }

    private View.OnClickListener buttonPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("Tracks", String.valueOf(TrackLogger.tracklist.toArray().length));
            // Toast.makeText(getApplicationContext(), "Pause tracking", Toast.LENGTH_SHORT).show();
            trackManager.trackState = TrackManager.State.PAUSE;

            end = LocalDateTime.now();

            buttonStart.setEnabled(true);
            buttonPause.setEnabled(false);
            buttonStop.setEnabled(true);
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.seekBarLabelId);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TrackManager.trackState = TrackManager.State.STOP;
    }

    @Override
    protected void onPause() {
        super.onPause();
        TrackManager.trackState = TrackManager.State.PAUSE;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, 5000000);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float z = event.values[2];
        String data = "z sensor: " + z + "\nz user: " + zUser;

        zSensor = z;

        now = LocalDateTime.now();

        if (
                Math.abs(z) > zUser &&
                TrackManager.trackState == TrackManager.State.START &&
                Math.abs(Duration.between(timestampLastUpdate,now).getSeconds()) >= 1
        ) {

            timestampLastUpdate = LocalDateTime.now();

            // A STEP WAS MADE YAY!!!!!

            Step step = new Step(event.values[0], event.values[1], event.values[2], 4, 5, 6);

            track.stepList.add(step);

            stepsTakenView.setText(String.valueOf( Math.ceil(track.stepList.toArray().length) * 2 ));
            caloriesTakenView.setText(String.valueOf(Math.ceil(track.stepList.toArray().length * step.caloriesPerStep)) + " kcal");
            distanceTakenView.setText(String.valueOf(Math.ceil(track.stepList.toArray().length * TrackManager.averageDistancePerStep)) + " ft");

            end = LocalDateTime.now();

            Duration duration = Duration.between(end, start);

            timeTakenView.setText(String.valueOf(Math.abs(duration.toMinutes()) + " min " + Math.abs(duration.getSeconds() % 60) + " sec"));

            // Toast.makeText(this, String.valueOf(z) + "\n"+ String.valueOf(zUser), Toast.LENGTH_LONG).show();
            vibrator.vibrate(500);

            if (track.stepList.toArray().length % 10 == 0) {

                animationTextView.setText("Yay, " + track.stepList.toArray().length * 2 + " steps!");

                animator = ObjectAnimator.ofFloat(animationTextView, "translationY", 0f, 200f);
                animator.setDuration(1000);
                animator.setRepeatCount(1);
                animator.setRepeatMode(ObjectAnimator.REVERSE);
                animator.start();

                // animationTextView.setText("");

            }
        }
    }

    public void stopBlinkEffect(View view){
        animator.cancel();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}