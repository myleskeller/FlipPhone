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

    public void updateNodeSpecs() {
        DatabaseReference nodeRef = listingRef.child(nodeID);
        Map<String, Object> updatedChat = new HashMap<>();
//        updatedChat.put("listingAccessed", dbChat.listingAccessed);
//        updatedChat.put("frontPhotoTaken", dbChat.frontPhotoTaken);
//        updatedChat.put("flipped", dbChat.flipped);
//        updatedChat.put("backPhotoTaken", dbChat.backPhotoTaken);
//        updatedChat.put("listingPosted", dbChat.listingPosted);
        updatedChat.put("specifications", dbChat.specifications);
        nodeRef.updateChildren(updatedChat);

        Log.w(TAG, "updated '" + nodeID + "' with " + dbChat.specifications.toString());
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
                        if (MainActivity.which_phone == "old")
                            updateNodeSpecs();
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
                        dbChat.frontPhotoSuccessfullyTaken();
                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                try { //checks for backPhotoTaken
                    if (dataSnapshot.child(DB_CHILD).child(nodeID).child("backPhotoTaken").getValue().toString().equals("true")) {
                        dbChat.backPhotoSuccessfullyTaken();

                        //my many, many failed attempts at extracting a json string into a POJO...

                        //update local dbChat object specifications with correct device info from rtdb

//                        PhoneSpecifications oldPhoneSpecs = null;
//                        for (DataSnapshot messageSnapshot: dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").getChildren()) {
//                            oldPhoneSpecs = messageSnapshot.getValue(PhoneSpecifications.class);
//                        }


//                        PhoneSpecifications oldPhoneSpecs = null;
//                        for (DataSnapshot messageSnapshot: dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").getChildren()) {
//                            oldPhoneSpecs.expandableStorage = (String) messageSnapshot.child("expandableStorage").getValue();
//                            oldPhoneSpecs.battery = (String) messageSnapshot.child("battery").getValue();
//                            oldPhoneSpecs.cpu = (String) messageSnapshot.child("cpu").getValue();
//                            oldPhoneSpecs.internalStorage = (String) messageSnapshot.child("internalStorage").getValue();
//                            oldPhoneSpecs.manufacturer = (String) messageSnapshot.child("manufacturer").getValue();
//                            oldPhoneSpecs.model = (String) messageSnapshot.child("model").getValue();
//                            oldPhoneSpecs.name = (String) messageSnapshot.child("name").getValue();
//                            oldPhoneSpecs.os = (String) messageSnapshot.child("os").getValue();
//                            oldPhoneSpecs.ram = (String) messageSnapshot.child("ram").getValue();
//                            oldPhoneSpecs.resolution = (String) messageSnapshot.child("resolution").getValue();
//                            oldPhoneSpecs.screen = (String) messageSnapshot.child("screen").getValue();
//                            oldPhoneSpecs.telephony = (String) messageSnapshot.child("telephony").getValue();
//                        } 
//                        PhoneSpecifications oldPhoneSpecs = new PhoneSpecifications();
//                        String data = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").getValue().toString();
//                        Log.e(TAG, "specs?:" + data);
//
//                        data = data.replace("{", "");
//                        data = data.replace("}", "");
//                        List<String> dataList = Arrays.asList(data.split(","));
//
//                        Log.e(TAG, "specs?:" + data);
//
////                            oldPhoneSpecs.setExpandableStorage(dataList.get());
//                        String ss = "";
//                            for (String s: dataList) {
//                                if (s.contains("battery")){
//                                    ss = s.substring(s.indexOf("=")+1);
//                                    String sss = ss.trim();
//                                    oldPhoneSpecs.setBattery(sss);}
//                        if (dataList.contains("cpu")){
//                            ss = s.substring(s.indexOf("=")+1);
//                            String sss = ss.trim();
//                            oldPhoneSpecs.setCpu(sss);}
//                        if (dataList.contains("internalStorage")){
//                            ss = s.substring(s.indexOf("=")+1);
//                            String sss = ss.trim();
//                            oldPhoneSpecs.setInternalStorage(sss);}
//                        if (dataList.contains("manufacturer")){
//                            ss = s.substring(s.indexOf("=")+1);
//                            String sss = ss.trim();
//                            oldPhoneSpecs.setManufacturer(sss);}
//                        if (dataList.contains("model")){
//                            ss = s.substring(s.indexOf("=")+1);
//                            String sss = ss.trim();
//                            oldPhoneSpecs.setModel(sss);}
//                        if (dataList.contains("name")){
//                            ss = s.substring(s.indexOf("=")+1);
//                            String sss = ss.trim();
//                            oldPhoneSpecs.setName(sss);}
//                        if (dataList.contains("os")){
//                            ss = s.substring(s.indexOf("=")+1);
//                            String sss = ss.trim();
//                            oldPhoneSpecs.setOs(sss);}
//                        if (dataList.contains("ram")){
//                            ss = s.substring(s.indexOf("=")+1);
//                            String sss = ss.trim();
//                            oldPhoneSpecs.setRam(sss);}
//                        if (dataList.contains("resolution")){
//                            ss = s.substring(s.indexOf("=")+1);
//                            String sss = ss.trim();
//                            oldPhoneSpecs.setResolution(sss);}
//                        if (dataList.contains("screen")){
//                            ss = s.substring(s.indexOf("=")+1);
//                            String sss = ss.trim();
//                            oldPhoneSpecs.setScreen(sss);}
//                        if (dataList.contains("telephony")){
//                            ss = s.substring(s.indexOf("=")+1);
//                            String sss = ss.trim();
//                            oldPhoneSpecs.setTelephony(sss);}
//                        }
//                        PhoneSpecifications oldPhoneSpecs = new PhoneSpecifications();
//                        oldPhoneSpecs.expandableStorage = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").child("expandableStorage").getValue().toString();
//                        oldPhoneSpecs.battery = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").child("battery").getValue().toString();
//                        oldPhoneSpecs.cpu = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").child("cpu").getValue().toString();
//                        oldPhoneSpecs.internalStorage = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").child("internalStorage").getValue().toString();
//                        oldPhoneSpecs.manufacturer = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").child("manufacturer").getValue().toString();
//                        oldPhoneSpecs.model = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").child("model").getValue().toString();
//                        oldPhoneSpecs.name = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").child("name").getValue().toString();
//                        oldPhoneSpecs.os = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").child("os").getValue().toString();
//                        oldPhoneSpecs.ram = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").child("ram").getValue().toString();
//                        oldPhoneSpecs.resolution = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").child("resolution").getValue().toString();
//                        oldPhoneSpecs.screen = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").child("screen").getValue().toString();
//                        oldPhoneSpecs.telephony = dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").child("telephony").getValue().toString();

//                        PhoneSpecifications oldPhoneSpecs = (PhoneSpecifications) dataSnapshot.child(DB_CHILD).child(nodeID).child("specifications").getValue();
//                        if (oldPhoneSpecs != null)
//                        dbChat.setSpecificationObject(oldPhoneSpecs);
                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                try { //checks for listingPosted
                    if (dataSnapshot.child(DB_CHILD).child(nodeID).child("listingPosted").getValue().toString().equals("true")) {
                        dbChat.listingSuccessfullyPosted();
                        myRef.removeEventListener(this); //maybe stops listening after listing posted?
                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
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

        public void setSpecificationObject(PhoneSpecifications oldPhoneSpecs) {
            this.specifications = oldPhoneSpecs;
        }
    }
}