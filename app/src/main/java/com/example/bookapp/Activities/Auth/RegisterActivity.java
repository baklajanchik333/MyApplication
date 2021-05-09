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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import static com.example.bookapp.R.string.accountCreated;
import static com.example.bookapp.R.string.creatingAcc;
import static com.example.bookapp.R.string.enterYouEmail;
import static com.example.bookapp.R.string.enterYouName;
import static com.example.bookapp.R.string.enterYouPassword;
import static com.example.bookapp.R.string.confirmPassword;
import static com.example.bookapp.R.string.fillInAllTheFields;
import static com.example.bookapp.R.string.invalidEmailPattern;
import static com.example.bookapp.R.string.passwordDoNotMatch;
import static com.example.bookapp.R.string.pleaseWait;
import static com.example.bookapp.R.string.savingUserInformation;
import static com.example.bookapp.R.string.thePasswordMustContainAtLeast6Characters;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private String name = "", email = "", password = "", confPassword = "";
    private final long timestamp = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.registerBtn.setOnClickListener(v -> validateData());
    }

    private void validateData() {
        //get data
        name = Objects.requireNonNull(binding.nameTextEt.getText()).toString().trim();
        email = Objects.requireNonNull(binding.emailTextEt.getText()).toString().trim();
        password = Objects.requireNonNull(binding.passwordTextEt.getText()).toString().trim();
        confPassword = Objects.requireNonNull(binding.confirmPasswordTextEt.getText()).toString().trim();

        //validate data
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confPassword)) {
            Toast.makeText(this, getString(fillInAllTheFields), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(enterYouName), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(enterYouEmail), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(enterYouPassword), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confPassword)) {
            Toast.makeText(this, getString(confirmPassword), Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(invalidEmailPattern), Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confPassword)) {
            Toast.makeText(this, getString(passwordDoNotMatch), Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, getString(thePasswordMustContainAtLeast6Characters), Toast.LENGTH_SHORT).show();
        } else {
            createUserAcc();
        }
    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(pleaseWait));
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void createUserAcc() {
        setUpProgressDialog();
        progressDialog.setMessage(getString(creatingAcc));
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> updateUserInfo())
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage(getString(savingUserInformation));

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
                    Toast.makeText(RegisterActivity.this, getString(accountCreated), Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getApplicationContext(), DashboardUserActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}