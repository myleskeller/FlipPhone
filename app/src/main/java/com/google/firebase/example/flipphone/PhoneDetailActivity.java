/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.google.firebase.example.flipphone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.example.flipphone.adapter.RatingAdapter;
import com.google.firebase.example.flipphone.model.Phone;
import com.google.firebase.example.flipphone.model.Rating;
import com.google.firebase.example.flipphone.util.PhoneUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class PhoneDetailActivity extends AppCompatActivity implements
        View.OnClickListener,
        EventListener<DocumentSnapshot>,
        RatingDialogFragment.RatingListener {

    private static final String TAG = "PhoneDetail";

    public static final String KEY_PHONE_ID = "key_phone_id";
    String phoneId;
    private ImageView mImageView;
    private TextView mNameView;
    private MaterialRatingBar mRatingIndicator;
    private TextView mNumRatingsView;
    private TextView mConditionView;
    private TextView mCategoryView;
    private TextView mPriceView;
    private ViewGroup mEmptyView;
    private RecyclerView mRatingsRecycler;

    private RatingDialogFragment mRatingDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mPhoneRef;
    private ListenerRegistration mPhoneRegistration;

    private RatingAdapter mRatingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_detail);
        
        mImageView = findViewById(R.id.phone_image);
        mNameView = findViewById(R.id.phone_name);
        //mRatingIndicator = findViewById(R.id.phone_rating);
        //mNumRatingsView = findViewById(R.id.phone_num_ratings);
        mConditionView = findViewById(R.id.phone_condition);
        mCategoryView = findViewById(R.id.phone_category);
        mPriceView = findViewById(R.id.phone_price);
        mEmptyView = findViewById(R.id.view_empty_ratings);
        //mRatingsRecycler = findViewById(R.id.recycler_ratings);

        findViewById(R.id.phone_button_back).setOnClickListener(this);
        findViewById(R.id.fab_show_rating_dialog).setOnClickListener(this);
        findViewById(R.id.phone_image).setOnClickListener(this);
        // Get phone ID from extras
        phoneId = getIntent().getExtras().getString(KEY_PHONE_ID);
        if (phoneId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_PHONE_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the phone
        mPhoneRef = mFirestore.collection("users").document(phoneId);

        // Get ratings
        Query ratingsQuery = mPhoneRef
                .collection("users")
                .orderBy("price", Query.Direction.DESCENDING)
                .limit(50);

        // RecyclerView
        mRatingAdapter = new RatingAdapter(ratingsQuery) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    //mRatingsRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    //mRatingsRecycler.setVisibility(View.VISIBLE);
                    // mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        //mRatingsRecycler.setLayoutManager(new LinearLayoutManager(this));
       // mRatingsRecycler.setAdapter(mRatingAdapter);

        mRatingDialog = new RatingDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        mRatingAdapter.startListening();
        mPhoneRegistration = mPhoneRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRatingAdapter.stopListening();

        if (mPhoneRegistration != null) {
            mPhoneRegistration.remove();
            mPhoneRegistration = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_button_back:
                onBackArrowClicked(v);
                break;
            case R.id.fab_show_rating_dialog:
                onAddRatingClicked(v);
                break;
            case R.id.phone_image:
                Intent intent = new Intent(this, ImagesActivity.class);
                intent.putExtra(PhoneDetailActivity.KEY_PHONE_ID, phoneId);
                startActivity(intent);
        }
    }

    private Task<Void> addRating(final DocumentReference phoneRef, final Rating rating) {
        // TODO(developer): Implement
        return Tasks.forException(new Exception("not yet implemented"));
    }

    /**
     * Listener for the Phone document ({@link #mPhoneRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "phone:onEvent", e);
            return;
        }

        onPhoneLoaded(snapshot.toObject(Phone.class));
    }

    private void onPhoneLoaded(Phone phone) {
        mNameView.setText(phone.getName());
        //mRatingIndicator.setRating((float) phone.getAvgRating());
        //mNumRatingsView.setText(getString(R.string.fmt_num_ratings, phone.getNumRatings()));
        mConditionView.setText(phone.getCity());
        mCategoryView.setText(phone.getCategory());
        mPriceView.setText(PhoneUtil.getPriceString(phone));

        // Background image
        Glide.with(mImageView.getContext())
                .load(phone.getPhoto())
                .into(mImageView);
    }

    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    public void onAddRatingClicked(View view) {
        Intent chatIntent = new Intent(getApplicationContext(), Chat.class);
        startActivity(chatIntent);
    }

    @Override
    public void onRating(Rating rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(mPhoneRef, rating)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Rating added");

                        // Hide keyboard and scroll to top
                        hideKeyboard();
                        mRatingsRecycler.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add rating failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to add rating",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
