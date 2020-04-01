package com.google.firebase.example.flipphone;


import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

//import static com.google.firebase.example.flipphone.PhoneDetailActivity.KEY_PHONE_ID;


public class ImagesActivity extends AppCompatActivity{


    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.item_zoomed_phone);

        //String phoneId = getIntent().getExtras().getString(KEY_PHONE_ID);

        //ImageView view = findViewById(R.id.zoomed_image);
       // Glide.with(this).load("https://www.smartphones2020.com/wp-content/uploads/2018/09/1.-Samsung-Galaxy-S10-Plus-%E2%80%93-The-Best-Smartphone-Overall-in-2019.jpg").into(view);

    }


}