package com.google.firebase.example.flipphone;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Chat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent flipIntent = new Intent(getApplicationContext(), SellFlip.class);
                startActivity(flipIntent);
            }
        });
    }
}
