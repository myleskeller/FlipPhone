package com.flipphone.listing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flipphone.PhoneDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.flipphone.MainActivity;
import com.flipphone.R;
import com.flipphone.model.Phone;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ListingDetails extends AppCompatActivity implements
        View.OnClickListener{
    private FirebaseFirestore mFirestore;
    private CollectionReference mPhoneRef;

    private String listing;
    ProgressBar progressBar;
    private int price;
    EditText description;
    private String condition = "Good";
    String photoFront;
    String photoBack;
    String name = "null";

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        return;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_details_activity);


        TextView priceTextView = findViewById(R.id.price_value);
        description = findViewById(R.id.description_text);
        SeekBar conditionBar = findViewById(R.id.condition_seekBar);
        SeekBar priceBar = findViewById(R.id.price_seekBar);
        TextView condTextView = findViewById(R.id.condition_value);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        photoFront = extras.getString("FRONT_PIC");
        photoBack = extras.getString("BACK_PIC");
        progressBar = findViewById(R.id.progress_loader_listing);
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

                progressBar.setVisibility(View.VISIBLE);
                Phone phone = new Phone();
                String user = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                phone.setDescription(description.getText().toString());
                phone.setPrice(price);
                phone.setCondition(condition);
                phone.setUserid(user);
                phone.setName(name);
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference =  firebaseStorage.getReference().child("users").child(user);
                phone.setPhoto(photoFront);

                phone.setPhotoBack(photoBack);
                //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                //String listing = mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).limitToLast(1).toString();
                //listing = listing.replaceAll("[^0-9]+"," ")
                //mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child()
                Task<DocumentReference> firebaseFirestore = FirebaseFirestore.getInstance().collection("users").add(phone);

                //firebaseFirestore.set(phone);

                firebaseFirestore.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        listing = firebaseFirestore.getResult().getId();
                        AlertDialog alertDialog = new AlertDialog.Builder(ListingDetails.this).create();
                        progressBar.setVisibility(View.GONE);
                        alertDialog.setTitle("Listing Successful!");
                        alertDialog.setMessage("Would you like to view the listing?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"View", ((dialog , which) -> {
                            Intent i = new Intent(ListingDetails.this, PhoneDetailActivity.class);
                            i.putExtra(PhoneDetailActivity.KEY_PHONE_ID, listing);
                            startActivity(i);}));
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", ((dialog , which) -> {
                            ReturnToMain();
                        }));
                        alertDialog.show();
                    }
                });


                break;
        }
    }

    public void ReturnToMain() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }
}