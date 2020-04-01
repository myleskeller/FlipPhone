package com.google.firebase.example.flipphone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SellFlip extends AppCompatActivity {
    TextView ProximitySensor, ProximityMax, ProximityReading;

    private SensorManager mySensorManager;
    private Sensor myProximitySensor;
    private SensorEventListener proximitySensorListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_flip);
        ProximitySensor = (TextView)findViewById(R.id.proximitySensor);
        ProximityMax = (TextView)findViewById(R.id.proximityMax);
        ProximityReading = (TextView)findViewById(R.id.proximityReading);

        mySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        myProximitySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (myProximitySensor == null){
            ProximitySensor.setText("No Proximity Sensor!");
        }else{
            ProximitySensor.setText(myProximitySensor.getName());
            ProximityMax.setText("Maximum Range: "
                    + String.valueOf(myProximitySensor.getMaximumRange()));
        }
        proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.values[0] < myProximitySensor.getMaximumRange()/8){
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                }
                else
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mySensorManager.registerListener(proximitySensorListener,myProximitySensor,
                2*1000*1000);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mySensorManager.unregisterListener(proximitySensorListener);
    }
}
