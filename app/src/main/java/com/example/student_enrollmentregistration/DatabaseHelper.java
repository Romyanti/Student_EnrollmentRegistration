package com.example.student_enrollmentregistration;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "studentEnrollment.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create students table
        db.execSQL("CREATE TABLE students (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT)");

        // Create subjects table
        db.execSQL("CREATE TABLE subjects (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, credits INTEGER)");

        // Create enrollments table
        db.execSQL("CREATE TABLE enrollments (id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER, subject_id INTEGER, FOREIGN KEY(student_id) REFERENCES students(id), FOREIGN KEY(subject_id) REFERENCES subjects(id))");

        // Insert sample subjects
        db.execSQL("INSERT INTO subjects (name, credits) VALUES ('Software Engineering', 3), ('Wireless and Mobile Programming', 3), ('Data Structure Algorithm', 3), ('Network Security', 3), ('Numarical Method', 3)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS students");
        db.execSQL("DROP TABLE IF EXISTS subjects");
        db.execSQL("DROP TABLE IF EXISTS enrollments");
        onCreate(db);
    }
}