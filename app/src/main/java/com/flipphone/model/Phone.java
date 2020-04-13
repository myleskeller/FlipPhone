/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.flipphone.model;

import com.flipphone.listing.PhoneSpecifications;
import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Phone POJO.
 */
@IgnoreExtraProperties
public class Phone {

    //public static final String FIELD_CITY = "city";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_POPULARITY = "numRatings";
    public static final String FIELD_AVG_RATING = "avgRating";

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    private String userid;
    private String description= "None";
    private PhoneSpecifications specifications;
    private String name;

    public String getPhotoFront() {
        return photoFront;
    }

    public void setPhotoFront(String photoFront) {
        this.photoFront = photoFront;
    }

    public String getPhotoBack() {
        return photoBack;
    }

    public void setPhotoBack(String photoBack) {
        this.photoBack = photoBack;
    }

    private String photoFront;
    private String photoBack;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PhoneSpecifications getSpecifications() {
        return specifications;
    }

    public void setSpecifications(PhoneSpecifications specifications) {
        this.specifications = specifications;
    }

    //private String city;
    private String condition;
    private String photo;
    private int price;
    //private int numRatings;
    //private double avgRating;

    public Phone() {}

    public Phone(String userid, String description, String city, String name, String condition, String photo,
                 int price) {
        this.userid = userid;
        this.description = description;
        this.name = name;
        //this.city = city;
        this.condition = condition;
        this.photo = photo;
        this.price = price;
        //this.numRatings = numRatings;
        //this.avgRating = avgRating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

   /* public void setCondition(String city) {
        this.city = city;
    }
    public String getCity(){
        return city;
    }*/

    public String getCategory() {
        return "Condition";
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
/*
    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }*/
}
