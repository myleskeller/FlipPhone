package com.flipphone.listing;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.flipphone.ViewPagerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.flipphone.model.Phone;
import com.flipphone.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
