package com.example.app2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app2.GLOBAL;
import com.example.app2.R;
import com.google.android.material.button.MaterialButton;

import java.util.regex.Pattern;

import database.DatabaseHelper;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordEditText, confirmPasswordEditText;
    private MaterialButton resetButton;
    private DatabaseHelper databaseHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_page);

        newPasswordEditText = findViewById(R.id.new_password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        resetButton = findViewById(R.id.btn_reset);

        databaseHelper = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("email");

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidPassword(newPassword)) {
            Toast.makeText(ResetPasswordActivity.this, "Password must contain at least one uppercase letter, one number a symbol and it should be at least 8 charachters long", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }


        String hashPassword = GLOBAL.hashPassword(newPassword);
        if (hashPassword == null) {
            Toast.makeText(this, "Error hashing password. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", hashPassword);

        int rowsUpdated = db.update("users", values, "email = ?", new String[]{userEmail});
        db.close();

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else {
            Toast.makeText(this, "Error resetting password. User not found.", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isValidPassword(String password) {

        Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*\\W).{8,}$");
        return pattern.matcher(password).matches();
    }

}
