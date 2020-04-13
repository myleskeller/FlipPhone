package com.flipphone;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.flipphone.camera.CameraActivity;
import com.flipphone.listing.PhoneSpecifications;
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

    public void updateNode() {
        DatabaseReference nodeRef = listingRef.child(nodeID);
        Map<String, Object> updatedChat = new HashMap<>();
        updatedChat.put("listingAccessed", dbChat.listingAccessed);
        updatedChat.put("frontPhotoTaken", dbChat.frontPhotoTaken);
        updatedChat.put("flipped", dbChat.flipped);
        updatedChat.put("backPhotoTaken", dbChat.backPhotoTaken);
        updatedChat.put("listingPosted", dbChat.listingPosted);
        nodeRef.updateChildren(updatedChat);

        Log.w(TAG, "updated '" + nodeID + "' with " + dbChat.toString());
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

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String _nodeID) {
        nodeID = _nodeID;
    }

    // Read from the database (continuously)
    public void listenForData(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                String data = dataSnapshot.getValue().toString();                                 //returns /listings
//                String data = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").getValue().toString();  //returns listings/[listingID]
//                String data = dataSnapshot.child(DB_CHILD).child(listingID).child("type").getValue().toString(); //returns listings/[listingID]/[type]
//                String data = dataSnapshot.child(DB_CHILD).child(nodeID).getValue().toString();

                try { //checks for listingAccessed
                    if (dataSnapshot.child(DB_CHILD).child(nodeID).child("listingAccessed").getValue().toString().equals("true")) {
                        dbChat.listingSuccessfullyAccessed();
                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                try { //checks for flipped
                    if (dataSnapshot.child(DB_CHILD).child(nodeID).child("flipped").getValue().toString().equals("true")) {
                        CameraActivity.flipReceived(); //idk how useful this is...
                        dbChat.deviceFlipDetected();
                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                try { //checks for frontPhotoTaken
                    if (dataSnapshot.child(DB_CHILD).child(nodeID).child("frontPhotoTaken").getValue().toString().equals("true")) {
//                        PicturePreviewActivity.frontPhotoReceived();
                        dbChat.frontPhotoSuccessfullyTaken();
                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                try { //checks for backPhotoTaken
                    if (dataSnapshot.child(DB_CHILD).child(nodeID).child("backPhotoTaken").getValue().toString().equals("true")) {
                        dbChat.backPhotoSuccessfullyTaken();
//                        PicturePreviewActivity.backPhotoReceived();
                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                try { //checks for listingPosted
                    if (dataSnapshot.child(DB_CHILD).child(nodeID).child("listingPosted").getValue().toString().equals("true")) {
                        dbChat.listingSuccessfullyPosted();
//                        PicturePreviewActivity.backPhotoReceived();
                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

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

        public String getSpecifications() {
            return this.specifications.toString();
        }

        public PhoneSpecifications getSpecificationObject() {
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

        public boolean getFlipStatus() {
            return this.flipped;
        }

        public boolean getPostStatus() {
            return this.listingPosted;
        }

        public boolean getAccessStatus() {
            return this.listingAccessed;
        }

        public boolean getFrontPhotoStatus() {
            return this.frontPhotoTaken;
        }

        public boolean getBackPhotoStatus() {
            return this.backPhotoTaken;
        }
    }
}