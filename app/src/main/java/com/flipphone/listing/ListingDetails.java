package com.flipphone.listing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.example.flipphone.MainActivity;
import com.google.firebase.example.flipphone.R;

public class ListingDetails extends AppCompatActivity implements
        View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_details_activity);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_listing:
                ReturnToMain();
                break;
        }
    }

    public void ReturnToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("EXTRA_MESSAGE", "unused");
        intent.putExtras(extras);
        startActivity(intent);
    }
}