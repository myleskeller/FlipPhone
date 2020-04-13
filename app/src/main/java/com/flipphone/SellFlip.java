package com.flipphone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flipphone.listing.PhoneDetailActivity;

import static com.flipphone.MainActivity.mRTDB;

public class SellFlip extends AppCompatActivity {
    //    TextView ProximitySensor, ProximityMax, ProximityReading;
    TextView status;

    private SensorManager mySensorManager;
    private Sensor myProximitySensor;
    private SensorEventListener proximitySensorListener;
    Thread thread = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_flip);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        status = (TextView) findViewById((R.id.textView));


        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        status.setTextColor(Color.BLACK);

        status.setText("Take front device photo.");
//        status.setText(R.string.flip_command);

        mySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        myProximitySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                checkIfFrontPhotoTaken();
                                checkIfPosted();
                                checkIfBackPhotoTaken();
                                Log.w("THREAD", "running.");
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        mRTDB.listenForData();
        thread.start();


        proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.w("FLIP", "getFrontPhotoStatus: " + String.valueOf(MainActivity.mRTDB.dbChat.getFrontPhotoStatus()));
                if (mRTDB.dbChat.getFrontPhotoStatus() == true) {
                    status.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_pressed));
                    if (event.values[0] < myProximitySensor.getMaximumRange() / 8) {
                        Log.w("FLIP", "Sensor: " + String.valueOf(myProximitySensor.getMaximumRange()));
                        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
                        MainActivity.mRTDB.dbChat.deviceFlipDetected();
                        MainActivity.mRTDB.updateNode();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        mRTDB.listenForData();
//                        thread.start();
                    } else
                        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mySensorManager.registerListener(proximitySensorListener,myProximitySensor,
                2 * 1000 * 1000);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mySensorManager.unregisterListener(proximitySensorListener);
    }

    public void checkIfPosted() {
        if (MainActivity.mRTDB.dbChat.getPostStatus() == true) {
            thread.interrupt();
            postDetected();
        }
    }

    public void checkIfFrontPhotoTaken() {
        if (MainActivity.mRTDB.dbChat.getFrontPhotoStatus() == true) {
            getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            status.setText(R.string.flip_command);
        }
    }

    public void checkIfBackPhotoTaken() {
        if (MainActivity.mRTDB.dbChat.getBackPhotoStatus() == true) {
            getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            status.setText("Complete the listing.");
        }
    }

    public void postDetected() {
//        listing = firebaseFirestore.getResult().getId();
        AlertDialog alertDialog = new AlertDialog.Builder(SellFlip.this).create();
        alertDialog.setTitle("Listing Successful!");
        alertDialog.setMessage("Would you like to view the listing?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "View Listing", ((dialog, which) -> {
            Intent i = new Intent(SellFlip.this, PhoneDetailActivity.class);
            i.putExtra(PhoneDetailActivity.KEY_PHONE_ID, MainActivity.mRTDB.getNodeID()); //i hope the nodeid works in that
            startActivity(i);
            finish();
        }));
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Back to Main", ((dialog, which) -> {
            ReturnToMain();
        }));
        alertDialog.show();
    }

    public void ReturnToMain() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();
    }
}
