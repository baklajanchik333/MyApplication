package com.example.bookapp.Activities.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.Activities.Dashboard.DashboardUserActivity;
import com.example.bookapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    private FirebaseAuth firebaseAuth;

    private final ProgressDialog progressDialog = new ProgressDialog(this);

    private String name = "", email = "", password = "", confPassword = "";
    private final long timestamp = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.registerBtn.setOnClickListener(v -> {
            //get data
            name = Objects.requireNonNull(binding.nameTextEt.getText()).toString().trim();
            email = Objects.requireNonNull(binding.emailTextEt.getText()).toString().trim();
            password = Objects.requireNonNull(binding.passwordTextEt.getText()).toString().trim();
            confPassword = Objects.requireNonNull(binding.confirmPasswordTextEt.getText()).toString().trim();

            //validate data
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confPassword)) {
                Toast.makeText(this, "Fill in all the fields...", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Enter you name...", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter you email...", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter you password...", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(confPassword)) {
                Toast.makeText(this, "Confirm password...", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email pattern...!", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confPassword)) {
                Toast.makeText(this, "Password doesn't match...", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "The password must contain at least 6 characters...", Toast.LENGTH_SHORT).show();
            } else {
                createUserAcc();
            }
        });
    }

    private void setUpProgressDialog() {
        progressDialog.setTitle("Please wait...;");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void createUserAcc() {
        setUpProgressDialog();
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving user information...");

        String uid = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", "");
        hashMap.put("userType", "user");
        hashMap.put("timestamp", timestamp);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(Objects.requireNonNull(uid))
                .setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getApplicationContext(), DashboardUserActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}