package com.google.firebase.example.flipphone;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.example.flipphone.model.Phone;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ImageSliderActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        String phoneId = getIntent().getExtras().getString(PhoneDetailActivity.KEY_PHONE_ID);
        FirebaseFirestore mRef = FirebaseFirestore.getInstance();
        mRef.collection("users").document(phoneId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Phone phone = documentSnapshot.toObject(Phone.class);
                String[] imgUrls = new String[]{
                        phone.getPhoto(),
                        phone.getPhotoBack()
                };
                ViewPager viewPager = findViewById(R.id.view_pager);
                ViewPagerAdapter adapter = new ViewPagerAdapter(ImageSliderActivity.this , imgUrls);
                viewPager.setAdapter(adapter);
            }
        });

    }
}
