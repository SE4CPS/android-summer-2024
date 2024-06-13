package edu.shsi.edushsi2024steptracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "steptracker.db";

    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tracks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "steps TEXT," +
                "duration TEXT," +
                "calories TEXT," +
                "distance TEXT," +
                "starting TEXT," +
                "ending TEXT" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }
}