package com.flipphone.listing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.example.flipphone.MainActivity;
import com.google.firebase.example.flipphone.R;
import com.google.firebase.example.flipphone.model.Phone;
import com.google.firebase.example.flipphone.util.PhoneUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.gson.internal.$Gson$Preconditions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ListingDetails extends AppCompatActivity implements
        View.OnClickListener{
    private FirebaseFirestore mFirestore;
    private CollectionReference mPhoneRef;
    private Spinner phoneBrand;
    private Spinner phoneModel;
    private int price;
    EditText description;
    private String condition;
    String photoFront;
    String photoBack;
    String brand;
    String model;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        return;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_details_activity);
        String[] temp = {"-Select Model-"};
        phoneBrand = findViewById(R.id.brand_textView);
        phoneModel = findViewById(R.id.model_textView);
        ArrayAdapter<String> startingValue = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, temp);
        phoneModel.setAdapter(startingValue);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, PhoneUtil.BRAND);
        phoneBrand.setAdapter(adapter1);
        phoneBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                brand = phoneBrand.getSelectedItem().toString();
               switch (brand) {
                   case "Apple":
                       ArrayAdapter<String> adapter2 = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, PhoneUtil.Apple);
                       phoneModel.setAdapter(adapter2);
                       break;
                   case "Samsung":
                       ArrayAdapter<String> adapter3 = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, PhoneUtil.Samsung);
                       phoneModel.setAdapter(adapter3);
                       break;
                   case "Motorola":
                       ArrayAdapter<String> adapter4 = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, PhoneUtil.Motorola);
                       phoneModel.setAdapter(adapter4);
                       break;
                   case "Huawei":
                       ArrayAdapter<String> adapter5 = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, PhoneUtil.Huawei);
                       phoneModel.setAdapter(adapter5);
                       break;
                   case "HTC":
                       ArrayAdapter<String> adapter6 = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, PhoneUtil.HTC);
                       phoneModel.setAdapter(adapter6);
                       break;
                   case "Razer":
                       ArrayAdapter<String> adapter7 = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, PhoneUtil.Razer);
                       phoneModel.setAdapter(adapter7);
                       break;
                   case "OnePlus":
                       ArrayAdapter<String> adapter8 = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, PhoneUtil.OnePlus);
                       phoneModel.setAdapter(adapter8);
                       break;
                   case "Google":
                       ArrayAdapter<String> adapter9 = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, PhoneUtil.Google);
                       phoneModel.setAdapter(adapter9);
                       break;
                   case "LG":
                       ArrayAdapter<String> adapter10 = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, PhoneUtil.LG);
                       phoneModel.setAdapter(adapter10);
                       break;
                   case "Sony":
                       ArrayAdapter<String> adapter11 = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, PhoneUtil.Sony);
                       phoneModel.setAdapter(adapter11);
                       break;
                   case "Xiaomi":
                       ArrayAdapter<String> adapter12 = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, PhoneUtil.Xiaomi);
                       phoneModel.setAdapter(adapter12);
                       break;
                   case "Oppo":
                       ArrayAdapter<String> adapter13 = new ArrayAdapter<>(ListingDetails.this, android.R.layout.simple_list_item_1, PhoneUtil.OPPO);
                       phoneModel.setAdapter(adapter13);
                       break;
               }
               phoneModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                       model = phoneModel.getSelectedItem().toString();
                   }

                   @Override
                   public void onNothingSelected(AdapterView<?> parent) {

                   }
               });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView priceTextView = findViewById(R.id.price_value);
        description = findViewById(R.id.description_text);
        SeekBar conditionBar = findViewById(R.id.condition_seekBar);
        SeekBar priceBar = findViewById(R.id.price_seekBar);
        TextView condTextView = findViewById(R.id.condition_value);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        photoFront = extras.getString("FRONT_PIC");
        photoBack = extras.getString("BACK_PIC");

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
                        condition = "Broken";
                                break;
                    case 1:
                        condition = "Damaged";
                                break;
                    case 2:
                        condition = "Good";
                        break;
                    case 3:
                        condition = "Refurbished";
                        break;
                    case 4:
                        condition = "New";
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
                String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                phone.setDescription(description.getText().toString());
                phone.setPrice(price);
                phone.setCity(condition);
                phone.setUserid(user);
                phone.setName(brand + " " + model);
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference =  firebaseStorage.getReference().child("users").child(user);
                phone.setPhoto(photoFront);

                phone.setPhotoBack(photoBack);
                //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                //String listing = mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getEmail()).limitToLast(1).toString();
                //listing = listing.replaceAll("[^0-9]+"," ")
                //mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getEmail()).child()
                Task<DocumentReference> firebaseFirestore = FirebaseFirestore.getInstance().collection("users").add(phone);
                //firebaseFirestore.set(phone);
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