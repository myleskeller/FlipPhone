package com.flipphone;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.flipphone.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteActivity extends AppCompatActivity {
    private ProgressBar spinner;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        return;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        spinner = (ProgressBar)findViewById(R.id.progress_circular);

        spinner.getIndeterminateDrawable().setColorFilter(Color.parseColor("#4285F4"), PorterDuff.Mode.SRC_IN);

        Intent intent = getIntent();
        Bundle extras = new Bundle();
        extras = intent.getExtras();
        String phoneId = extras.getString("DELETE");
        FirebaseFirestore mRef = FirebaseFirestore.getInstance();


        mRef.collection("users").document(phoneId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DeleteActivity.this,"Listing Deleted", Toast.LENGTH_SHORT).show();
                Intent newIntent = new Intent(DeleteActivity.this, MainActivity.class);
                spinner.setVisibility(View.GONE);
                startActivity(newIntent);
            }
        });
        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent newIntent = new Intent(DeleteActivity.this, MainActivity.class);
                startActivity(newIntent);
            }
            }, 1000);*/

    }
}
