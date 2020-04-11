package com.flipphone;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.flipphone.listing.PhoneSpecifications;
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
        myRef.child(DB_CHILD).child(listingID).setValue(new DeviceChat(type, data));
        Log.w(TAG, "sent: '" + type + "', '" + data + "' to database");
        Log.w(TAG, "getKey() = "+ dook);
    }

    // Write to database
    public void makeNode(String listingID){
        String dook = myRef.child(DB_CHILD).child(listingID).push().getKey();
        myRef.child(DB_CHILD).child(listingID).setValue(new DeviceChat("auth_string", listingID));
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

    public static class DeviceChat {
        public String type; //probably going to get rid of these
        public String data; //probably going to get rid of these

        private boolean listingAccessed;
        private boolean frontPhotoTaken;
        private boolean flipped;
        private boolean backPhotoTaken;
        private boolean listingPosted;
        private PhoneSpecifications specifications;

        public DeviceChat() {
            this.listingAccessed = false;
            this.frontPhotoTaken = false;
            this.flipped = false;
            this.backPhotoTaken = false;
            this.listingPosted = false;
//            this.specifications = new PhoneSpecifications(context);
        }

        public DeviceChat(PhoneSpecifications _specifications) {
            this.listingAccessed = false;
            this.frontPhotoTaken = false;
            this.flipped = false;
            this.backPhotoTaken = false;
            this.listingPosted = false;
            this.specifications = _specifications;
        }

        public DeviceChat(String type, String data) { //probably going to get rid of this version
            this.type = type; //probably going to get rid of these
            this.data = data; //probably going to get rid of these
        }

        public void listingSuccessfullyAccessed() {
            this.listingAccessed = true;
        }

        public void frontPhotoSuccessfullyTaken() {
            this.frontPhotoTaken = true;
        }

        public void deviceFlipDetected() {
            this.flipped = true;
        }

        public void backPhotoSuccessfullyTaken() {
            this.backPhotoTaken = true;
        }

        public void listingSuccessfullyPosted() {
            this.listingPosted = true;
        }
    }
}