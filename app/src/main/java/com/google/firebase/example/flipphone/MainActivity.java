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
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.flipphone.camera.CameraActivity;
import com.flipphone.qrcode.QRCodeGeneratorActivity;
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

import java.util.Arrays;
import java.util.Collections;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        FilterDialogFragment.FilterListener,
        PhoneAdapter.OnPhoneSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int LIMIT = 50;
    private static final String EXTRA_MESSAGE = "";
    public static String which_phone = "";
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
    public void onBackPressed() {
       // super.onBackPressed();
        return;
    }
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
        mQuery = mFirestore.collection("users")
                .orderBy("price", Query.Direction.DESCENDING)
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
        CollectionReference phones = mFirestore.collection("users");

        Phone phone = PhoneUtil.userPhone(this);
        phones.add(phone);
        /*
        for (int i = 0; i < 10; i++){
            Phone phone = PhoneUtil.getRandom(this);
            phones.add(phone);
        }*/
    }

    @Override
    public void onFilter(Filters filters) {
        // Construct query basic query
        Query query = mFirestore.collection("users");

        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo("condition", filters.getCategory());
        }

        // City (equality filter)
        //if (filters.hasCity()) {
            //query = query.whereEqualTo("city", filters.getCondition());
        //}

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
            case R.id.menu_qr_make:
                OpenQRGenerator();
                break;
            case R.id.menu_photo_front:
                TakeFrontPhoto();
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
            case R.id.toFlip:
                Intent chatIntent = new Intent(getApplicationContext(), SellFlip.class);
                startActivity(chatIntent);
                break;
            case R.id.menu_sell:
                Intent intent = new Intent(this, SellActivity.class);
                startActivity(intent);
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
                break;
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
        intent.putExtra(PhoneDetailActivity.KEY_PHONE_ID, phone.getId());

        startActivity(intent);
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.PhoneBuilder().build())
                )
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
        Bundle extras = new Bundle();
        extras.putString("EXTRA_MESSAGE", "unused");
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void OpenQRGenerator() {
        Intent intent = new Intent(this, QRCodeGeneratorActivity.class);
        Bundle extras = new Bundle();
        extras.putString("EXTRA_MESSAGE", "unused");
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void TakeFrontPhoto() {
        Intent intent = new Intent(this, CameraActivity.class);
        Bundle extras = new Bundle();
        //TODO: this would probably be the ideal place to keep track of the container for the listing
        extras.putString("EXTRA_MESSAGE", "front");
        intent.putExtras(extras);
        startActivity(intent);
    }

//    public void TakeBackPhoto() {
//        Intent intent = new Intent(this, CameraActivity.class);
//        String message = "back";
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
//    }

//    public void PriceAndDetails() {
//        Intent intent = new Intent(this, CameraActivity.class);
//        String message = "maybe this will be useful later..";
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
//    }
}
