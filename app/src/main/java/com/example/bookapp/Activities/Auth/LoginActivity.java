package com.example.bookapp.Activities.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.Activities.Dashboard.DashboardAdminActivity;
import com.example.bookapp.Activities.Dashboard.DashboardUserActivity;
import com.example.bookapp.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.example.bookapp.R.string.checkingUserType;
import static com.example.bookapp.R.string.enterYouEmail;
import static com.example.bookapp.R.string.enterYouPassword;
import static com.example.bookapp.R.string.fillInAllTheFields;
import static com.example.bookapp.R.string.invalidEmailPattern;
import static com.example.bookapp.R.string.loggingIn;
import static com.example.bookapp.R.string.pleaseWait;
import static com.example.bookapp.R.string.thePasswordMustContainAtLeast6Characters;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private String email = "", password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.noAccBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        binding.loginBtn.setOnClickListener(v -> validateData());
    }

    private void validateData() {
        //get data
        email = Objects.requireNonNull(binding.emailTextEt.getText()).toString().trim();
        password = Objects.requireNonNull(binding.passwordTextEt.getText()).toString().trim();

        //validate data
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(fillInAllTheFields), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(enterYouEmail), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(enterYouPassword), Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(invalidEmailPattern), Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, getString(thePasswordMustContainAtLeast6Characters), Toast.LENGTH_SHORT).show();
        } else {
            loginUser();
        }
    }

    private void loginUser() {
        setUpProgressDialog();
        progressDialog.setMessage(getString(loggingIn));
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> checkUser())
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkUser() {
        progressDialog.setMessage(getString(checkingUserType));

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(Objects.requireNonNull(firebaseUser).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        //get user type
                        String userType = "" + snapshot.child("userType").getValue();
                        //check user type
                        if (userType.equals("user")) {
                            startActivity(new Intent(getApplicationContext(), DashboardUserActivity.class));
                            finish();
                        } else if (userType.equals("admin")) {
                            startActivity(new Intent(getApplicationContext(), DashboardAdminActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(pleaseWait));
        progressDialog.setCanceledOnTouchOutside(false);
    }
}