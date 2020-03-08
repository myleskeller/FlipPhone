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

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.flipphone.qrcode.QrCodeScannerActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.example.flipphone.adapter.PhoneAdapter;
import com.google.firebase.example.flipphone.model.Phone;
import com.google.firebase.example.flipphone.util.PhoneUtil;
import com.google.firebase.example.flipphone.viewmodel.MainActivityViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Collections;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        FilterDialogFragment.FilterListener,
        PhoneAdapter.OnPhoneSelectedListener {

    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private static final int LIMIT = 50;

    private static final String EXTRA_MESSAGE = "";

    private Toolbar mToolbar;
    private TextView mCurrentSearchView;
    private TextView mCurrentSortByView;
    private RecyclerView mPhonesRecycler;
    private ViewGroup mEmptyView;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FilterDialogFragment mFilterDialog;
    private PhoneAdapter mAdapter;

    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mCurrentSearchView = findViewById(R.id.text_current_search);
        mCurrentSortByView = findViewById(R.id.text_current_sort_by);
        mPhonesRecycler = findViewById(R.id.recycler_phones);
        mEmptyView = findViewById(R.id.view_empty);

        findViewById(R.id.filter_bar).setOnClickListener(this);
        findViewById(R.id.button_clear_filter).setOnClickListener(this);

        // View model
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);//.get(MainActivityViewModel.class); //ViewModelProviders.of

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Initialize Firestore and the main RecyclerView
        initFirestore();
        initRecyclerView();

        // Filter Dialog
        mFilterDialog = new FilterDialogFragment();
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("phones")
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new PhoneAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mPhonesRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mPhonesRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mPhonesRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        mPhonesRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

        // Apply filters
        onFilter(mViewModel.getFilters());

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    private void onAddItemsClicked() {
        CollectionReference phones = mFirestore.collection("phones");

        for (int i = 0; i < 10; i++){
            Phone phone = PhoneUtil.getRandom(this);
            phones.add(phone);
        }
    }

    @Override
    public void onFilter(Filters filters) {
        // Construct query basic query
        Query query = mFirestore.collection("phones");

        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.getCategory());
        }

        // City (equality filter)
        if (filters.hasCity()) {
            query = query.whereEqualTo("city", filters.getCity());
        }

        // Price (equality filter)
        if (filters.hasPrice()) {
            query = query.whereEqualTo("price", filters.getPrice());
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        // Limit items
        query = query.limit(LIMIT);

        // Update the query
        mQuery = query;
        mAdapter.setQuery(query);

        // Set header
        mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(this)));
        mCurrentSortByView.setText(filters.getOrderDescription(this));

        // Save filters
        mViewModel.setFilters(filters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_items:
                onAddItemsClicked();
                break;
            case R.id.menu_qr_code:
                OpenQRScanner();
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter_bar:
                onFilterClicked();
                break;
            case R.id.button_clear_filter:
                onClearFilterClicked();
        }
    }

    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    public void onClearFilterClicked() {
        mFilterDialog.resetFilters();

        onFilter(Filters.getDefault());
    }

    @Override
    public void onPhoneSelected(DocumentSnapshot phone) {
        // Go to the details page for the selected phone
        Intent intent = new Intent(this, PhoneDetailActivity.class);
        intent.putExtra(PhoneDetailActivity.KEY_RESTAURANT_ID, phone.getId());

        startActivity(intent);
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }

    private void showTodoToast() {
        Toast.makeText(this, "TODO: Implement", Toast.LENGTH_SHORT).show();
    }
    public void OpenQRScanner() {
        Intent intent = new Intent(this, QrCodeScannerActivity.class);
        String message = "maybe this will be useful later..";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
