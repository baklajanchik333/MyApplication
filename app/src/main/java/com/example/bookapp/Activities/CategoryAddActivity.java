package com.example.bookapp.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.databinding.ActivityCategoryAddBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import static com.example.bookapp.R.string.addCategory;
import static com.example.bookapp.R.string.categoryAddedSuccessfully;
import static com.example.bookapp.R.string.pleaseEnterCategory;
import static com.example.bookapp.R.string.pleaseWait;

public class CategoryAddActivity extends AppCompatActivity {
    private ActivityCategoryAddBinding binding;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private String category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.submitBtn.setOnClickListener(v -> validateData());
    }

    private void validateData() {
        category = Objects.requireNonNull(binding.categoryTextEt.getText()).toString().trim();

        if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, getString(pleaseEnterCategory), Toast.LENGTH_SHORT).show();
        } else {
            addCategoryFirebase();
        }
    }

    private void addCategoryFirebase() {
        setUpProgressDialog();
        progressDialog.setMessage(getString(addCategory));
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        String uid = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", "" + timestamp);
        hashMap.put("category", "" + category);
        hashMap.put("timestamp", timestamp);
        hashMap.put("uid", uid);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.child("" + timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    binding.categoryTextEt.setText("");
                    progressDialog.dismiss();
                    Toast.makeText(CategoryAddActivity.this, getString(categoryAddedSuccessfully), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(CategoryAddActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(pleaseWait));
        progressDialog.setCanceledOnTouchOutside(false);
    }
}