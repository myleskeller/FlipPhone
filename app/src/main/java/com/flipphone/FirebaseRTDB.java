package com.flipphone;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.flipphone.listing.PhoneSpecifications;
import com.flipphone.model.Phone;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRTDB extends Service {
    private String TAG = "RTDB";
    private String DB_CHILD = "listings";
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private DatabaseReference myRef;
    private DatabaseReference listingRef;
    private LocalBroadcastManager broadcaster;
    private String nodeID;

    //    private PhoneSpecifications dbSpecs = null;
    public DeviceChat dbChat = new DeviceChat(); //init communication channel

    public void setPhoneSpecs(@NonNull PhoneSpecifications _chatSpecs){ dbChat.specifications = _chatSpecs; }

//    public void setChannel(@NonNull DeviceChat _dbChat){ dbChat = _dbChat; }

    public FirebaseRTDB() {
        Log.d(TAG, "FirebaseRTDB service started");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        listingRef = myRef.child(DB_CHILD);
    }

    public void updateNode(String listingID, DeviceChat deviceChat){
        DatabaseReference nodeRef = listingRef.child(nodeID);
        Map<String, Object> updatedChat = new HashMap<>();
        updatedChat.put("listingAccessed", deviceChat.listingAccessed);
        updatedChat.put("frontPhotoTaken", deviceChat.frontPhotoTaken);
        updatedChat.put("flipped", deviceChat.flipped);
        updatedChat.put("backPhotoTaken", deviceChat.backPhotoTaken);
        updatedChat.put("listingPosted", deviceChat.listingPosted);
        nodeRef.updateChildren(updatedChat);

        Log.w(TAG, "updated '" + listingID + "' with " + deviceChat.toString());
    }

    public void makeNode(){
        DatabaseReference keyReference = listingRef.push();
        nodeID = keyReference.getKey();
        keyReference.setValue(dbChat);

        Map<String, Object> newChat = new HashMap<>();
        newChat.put("listingAccessed", dbChat.listingAccessed);
        newChat.put("frontPhotoTaken", dbChat.frontPhotoTaken);
        newChat.put("flipped", dbChat.flipped);
        newChat.put("backPhotoTaken", dbChat.backPhotoTaken);
        newChat.put("listingPosted", dbChat.listingPosted);
        newChat.put("specifications", dbChat.specifications);  //we'll see how this turns out..
        keyReference.setValue(newChat);

        Log.w(TAG, "created node '" + nodeID + "' in database with " + dbChat.specifications.toString());
    }

    public String getNodeID(){
        return nodeID;
    }

    // Read from the database (continuously)
    public void listenForData(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //TODO: do more with this to extract exactly what you need
                String data = dataSnapshot.getValue().toString();                                 //returns /listings
//                String data = dataSnapshot.child(DB_CHILD).child(listingID).getValue().toString();  //returns listings/[listingID]
//                String data = dataSnapshot.child(DB_CHILD).child(listingID).child("type").getValue().toString(); //returns listings/[listingID]/[type]

                Log.e(TAG, "received data: " + data);

                // if new data is received
//                Intent intent = new Intent("MyData");
//                intent.putExtra("data", data);
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
        Log.w(TAG, "listening for changes to '" + nodeID + "' in database");
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
        public PhoneSpecifications specifications;

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
        public PhoneSpecifications getSpecifications() {
            return this.specifications;
        }
        @Override
        public String toString(){
            String output = "Node:\n";
            output += "listingAccessed: " + String.valueOf(this.listingAccessed) + '\n';
            output += "frontPhotoTaken: " + String.valueOf(this.frontPhotoTaken) + '\n';
            output += "flipped: " + String.valueOf(this.flipped) + '\n';
            output += "backPhotoTaken: " + String.valueOf(this.backPhotoTaken) + '\n';
            output += "listingPosted: " + String.valueOf(this.listingPosted) + '\n';
            output += "specifications: " + this.specifications.toString() + '\n';
            return output;
        }

    }
}