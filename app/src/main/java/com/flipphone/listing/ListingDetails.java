package com.flipphone.listing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.example.flipphone.MainActivity;
import com.google.firebase.example.flipphone.R;
import com.google.firebase.example.flipphone.model.Phone;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ListingDetails extends AppCompatActivity implements
        View.OnClickListener{
    private FirebaseFirestore mFirestore;
    private CollectionReference mPhoneRef;
    private int price;
    EditText description;
    private String condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_details_activity);
        TextView priceTextView = findViewById(R.id.price_value);
        description = findViewById(R.id.description_text);
        SeekBar conditionBar = findViewById(R.id.condition_seekBar);
        SeekBar priceBar = findViewById(R.id.price_seekBar);
        TextView condTextView = findViewById(R.id.condition_value);
        mFirestore = FirebaseFirestore.getInstance();
        //mPhoneRef = mFirestore.collection("users");
        int step = 1;
        int max = 1500;
        int min = 0;
        int max_cond = 4;
        conditionBar.setMax(max_cond);
        conditionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch(progress){
                    case 0:
                        condition = "broken";
                                break;
                    case 1:
                        condition = "damaged";
                                break;
                    case 2:
                        condition = "good";
                        break;
                    case 3:
                        condition = "refurbished";
                        break;
                    case 4:
                        condition = "new";
                        break;
                }
                condTextView.setText(condition);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        priceBar.setMax((max-min)/step);
        priceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                priceTextView.setText("$"+ String.valueOf(progress));
                price = (int) progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_listing:
                Phone phone = new Phone();
                phone.setDescription(description.getText().toString());
                phone.setPrice(price);
                phone.setCondition(condition);
                CollectionReference phones = mFirestore.collection("users");
                phones.add(phone);
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