package com.flipphone;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SellSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.mRTDB.dbChat.listingSuccessfullyPosted();
        MainActivity.mRTDB.updateNode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_success);
    }
}
