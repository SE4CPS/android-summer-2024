package edu.shsi.edushsi2024steptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TrackLogActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> itemList;
    private DatabaseHelper dbHelper;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_log);

        dbHelper = new DatabaseHelper(this);

        listView = (ListView) findViewById(R.id.listViewLogTrackId);

        deleteButton = (Button) findViewById(R.id.deleteAllTracksId);

        deleteButton.setOnClickListener(deleteButtonClicked);

        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        displayItems();
    }

    private View.OnClickListener deleteButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            db.execSQL("delete from tracks");
            adapter.notifyDataSetChanged();
            displayItems();
        }
    };

    private void displayItems() {
        itemList.clear();

        Float totalStepsToday = 0.0F;
        Float totalDistanceToday = 0.0F;
        Float totalTimeToday = 0.0F;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tracks ORDER BY starting DESC", null);
        int counter = 1;
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String steps = cursor.getString(cursor.getColumnIndex("steps"));
                @SuppressLint("Range") String distance = cursor.getString(cursor.getColumnIndex("distance"));
                @SuppressLint("Range") String calories = cursor.getString(cursor.getColumnIndex("calories"));
                @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex("duration"));
                @SuppressLint("Range") String starting = cursor.getString(cursor.getColumnIndex("starting"));
                @SuppressLint("Range") String ending = cursor.getString(cursor.getColumnIndex("ending"));

                String dateString = starting.split("T")[0];

                Date today = new Date();

                String formatToday = new SimpleDateFormat("yyyy-MM-dd").format(today);

                String todayLabel = "";

                if (String.valueOf(dateString).trim().equals(String.valueOf(formatToday).trim())) {

                    totalStepsToday = totalStepsToday + Float.valueOf(steps);
                    totalDistanceToday = totalDistanceToday + Float.valueOf(distance.split(" ")[0]);
                    totalTimeToday = totalTimeToday + Float.valueOf(duration.replaceAll("[^\\d.]", ""));

                    todayLabel = "Today: ";

                }

                Log.i("starting", String.valueOf(dateString).trim() + " ::: " + String.valueOf(formatToday).trim());

                itemList.add( counter + ") " + todayLabel + ": " + steps + " steps in " + duration + " ( " + distance + " ) ");

                counter++;

            } while (cursor.moveToNext());

            itemList.add(0, "Today: " + String.valueOf(Math.floor(totalStepsToday)) + " steps, " + Math.floor(totalDistanceToday) * 2 + "ft, " + (Math.floor(totalTimeToday / 60)) + "m "+ (Math.floor(totalTimeToday) % 60) + "s");
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
}