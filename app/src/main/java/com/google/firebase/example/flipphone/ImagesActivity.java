package com.google.firebase.example.flipphone;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.example.flipphone.model.Phone;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import static com.google.firebase.example.flipphone.PhoneDetailActivity.KEY_PHONE_ID;


public class ImagesActivity extends AppCompatActivity{


    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_zoomed_phone);


        String phoneId = getIntent().getExtras().getString(PhoneDetailActivity.KEY_PHONE_ID);
        FirebaseFirestore mRef = FirebaseFirestore.getInstance();
        mRef.collection("users").document(phoneId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Phone phone = documentSnapshot.toObject(Phone.class);
                ImageView view = findViewById(R.id.zoomed_image);
                Glide.with(ImagesActivity.this).load(phone.getPhotoBack()).into(view);
            }
        });

    }


}