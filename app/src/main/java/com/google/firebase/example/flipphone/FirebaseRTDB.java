package com.google.firebase.example.flipphone;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseRTDB extends Service {
    private String TAG = "RTDB";
    private String DB_CHILD = "listings";
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private DatabaseReference myRef;
    private LocalBroadcastManager broadcaster;

    public FirebaseRTDB() {
        Log.d(TAG, "FirebaseRTDB service started");

        database = FirebaseDatabase.getInstance();
         myRef = database.getReference();
    }

    // Write to database
    public void sendData(String listingID, String type, String data){
//        myRef.child(DB_CHILD).push().setValue(new deviceChat(type, data));        this works 100% for RTDB/listings/...../deviceChat{}

        String dook = myRef.child(DB_CHILD).child(listingID).push().getKey();
        myRef.child(DB_CHILD).child(listingID).setValue(new deviceChat(type, data));
        Log.w(TAG, "sent: '" + type + "', '" + data + "' to database");
        Log.w(TAG, "getKey() = "+ dook);
    }

    // Write to database
    public void makeNode(String listingID){
        String dook = myRef.child(DB_CHILD).child(listingID).push().getKey();
        myRef.child(DB_CHILD).child(listingID).setValue(new deviceChat("auth_string", listingID));
        Log.e(TAG, "created node '" + listingID + "' in database");
        Log.w(TAG, "getKey() = " + dook);
    }

    // Read from the database (continuously)
    public void listenForData(String data){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //TODO: do more with this to extract exactly what you need
                String data = dataSnapshot.getValue().toString();

                Log.e(TAG, "received data: " + data);

                // if new data is received
                Intent intent = new Intent("MyData");
                intent.putExtra("data", data);
                // intent.putExtra("lng", remoteMessage.getData().get("DriverLongitude"));

                //allegedly sends this event to another activity
//                broadcaster.sendBroadcast(intent);

                // if the data exchange is completed.
                //TODO: maybe put an escape parameter into the RTDB..
                if (dataSnapshot.hasChild("attemptFinish_"+"nodeId")) {
                    boolean isFinished = (boolean) dataSnapshot.child("attemptFinish_"+"nodeId").getValue();
                    if(isFinished){
                        myRef.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        Log.w(TAG, "listening for: '" + data + "' being added to database");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class deviceChat {
        public String type;
        public String data;

        public deviceChat() {
        }

        public deviceChat(String type, String data) {
            this.type = type;
            this.data = data;
        }
    }
}