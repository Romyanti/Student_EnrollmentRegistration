package com.example.student_enrollmentregistration;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EnrollmentActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    Spinner subjectSpinner;
    TextView resultsTextView;
    ArrayList<String> subjectsList = new ArrayList<>();
    ArrayList<Integer> subjectIds = new ArrayList<>();
    int selectedSubjectId = -1;
    int maxCredits = 24;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        dbHelper = new DatabaseHelper(this);

        subjectSpinner = findViewById(R.id.subjectSpinner);
        Button enrollButton = findViewById(R.id.enrollButton);
        Button viewSummaryButton = findViewById(R.id.viewSummaryButton);
        resultsTextView = findViewById(R.id.resultsTextView);

        loadSubjects();

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubjectId = subjectIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSubjectId = -1;
            }
        });

        enrollButton.setOnClickListener(v -> enrollSubject());

        viewSummaryButton.setOnClickListener(v -> viewEnrollmentSummary());
    }

    private void loadSubjects() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM subjects", null);
        subjectsList.clear();
        subjectIds.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            subjectsList.add(name);
            subjectIds.add(id);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapter);
    }

    private void enrollSubject() {
        if (selectedSubjectId == -1) {
            Toast.makeText(this, "Please select a subject", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor creditCursor = db.rawQuery("SELECT SUM(subjects.credits) as total_credits FROM enrollments INNER JOIN subjects ON enrollments.subject_id = subjects.id", null);
        int totalCredits = 0;

        if (creditCursor.moveToFirst()) {
            totalCredits = creditCursor.getInt(creditCursor.getColumnIndexOrThrow("total_credits"));
        }
        creditCursor.close();

        if (totalCredits >= maxCredits) {
            Toast.makeText(this, "You have reached the maximum credit limit", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("student_id", 1); // Assume student ID is 1 for demo purposes
        values.put("subject_id", selectedSubjectId);

        long result = db.insert("enrollments", null, values);
        if (result != -1) {
            Toast.makeText(this, "Enrollment Successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to enroll", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewEnrollmentSummary() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT subjects.name, subjects.credits FROM enrollments INNER JOIN subjects ON enrollments.subject_id = subjects.id WHERE enrollments.student_id = ?",
                new String[]{"1"}); // Assume student ID is 1 for demo purposes

        StringBuilder summary = new StringBuilder();
        int totalCredits = 0;

        while (cursor.moveToNext()) {
            String subjectName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            int credits = cursor.getInt(cursor.getColumnIndexOrThrow("credits"));
            totalCredits += credits;
            summary.append(subjectName).append(" - ").append(credits).append(" credits\n");
        }
        cursor.close();

        summary.append("\nTotal Credits: ").append(totalCredits);
        resultsTextView.setText(summary.toString());
    }
}