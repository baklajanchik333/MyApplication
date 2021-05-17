package com.example.bookapp.Activities.Dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.Activities.CategoryAddActivity;
import com.example.bookapp.MainActivity;
import com.example.bookapp.databinding.ActivityDashboardAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardAdminActivity extends AppCompatActivity {
    private ActivityDashboardAdminBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.logOutBtn.setOnClickListener(v -> {
            firebaseAuth.signOut();
            checkUser();
        });

        binding.addCategoryBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CategoryAddActivity.class)));

        checkUser();
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            String email = firebaseUser.getEmail();
            binding.emailTv.setText(email);
        }
    }
}