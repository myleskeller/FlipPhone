package com.google.firebase.example.flipphone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.flipphone.qrcode.QRCodeGeneratorActivity;
import com.flipphone.qrcode.QrCodeScannerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SellActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);

        showChooseDialog();
    }

    public void onOldPhoneClicked() {
        MyFirebaseMessagingService.subscribeToFCMTopic(getString(R.string.FCM_topic));
        MainActivity.which_phone = "old";
        Log.d("UI", "old button pressed");

        Intent intent = new Intent(getApplicationContext(), QRCodeGeneratorActivity.class);
        startActivity(intent);
    }

    public void onNewPhoneClicked() {
        MyFirebaseMessagingService.subscribeToFCMTopic(getString(R.string.FCM_topic));
        MainActivity.which_phone = "new";
        Log.d("UI", "new button pressed");

        Intent intent = new Intent(getApplicationContext(), QrCodeScannerActivity.class);
        startActivity(intent);
    }

    public void showChooseDialog() { //this was surprisingly easy to implement...
        AlertDialog alertDialog = new AlertDialog.Builder(SellActivity.this).create();
        alertDialog.setTitle("Select Device");

        alertDialog.setMessage("Identify if this device is the phone being sold or a phone being used to create the public listing on FlipPhone's servers.");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Old", ResourcesCompat.getDrawable(getResources(),R.drawable.ic_cellphone_basic, null), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); //makes back button in following activity return to MainActivity
                onOldPhoneClicked();
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "New", ResourcesCompat.getDrawable(getResources(),R.drawable.ic_phone_android_black_24dp, null), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); //makes back button in following activity return to MainActivity
                onNewPhoneClicked();
            }
        });

        alertDialog.show();
    }
}
