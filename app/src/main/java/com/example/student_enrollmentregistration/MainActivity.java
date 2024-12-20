package com.example.student_enrollmentregistration;

import android.os.Bundle;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button registerButton = findViewById(R.id.registerButton);
        Button loginButton = findViewById(R.id.loginButton);

        registerButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("email", email);
            values.put("password", password);

            long result = db.insert("students", null, values);
            if (result != -1) {
                Toast.makeText(MainActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
            }
        });


        loginButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM students WHERE email = ? AND password = ?", new String[]{email, password});

            if (cursor.moveToFirst()) {
                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, EnrollmentActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        });
    }
}