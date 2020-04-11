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
 package com.flipphone.util;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.flipphone.R;
import com.flipphone.model.Phone;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Utilities for Phones.
 */
public class PhoneUtil {

    private static final String TAG = "PhoneUtil";

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    //private static final String PHONE_URL_FMT = "https://storage.googleapis.com/firestorequickstarts.appspot.com/food_%d.png";
    public static String[] phones = {"https://www.hutmobile.com/wp-content/uploads/2019/12/1-104.jpg",
    "https://cdn.arstechnica.net/wp-content/uploads/2018/05/1-980x735.jpg",
    "https://ksassets.timeincuk.net/wp/uploads/sites/54/2019/03/Xiaomi-Mi-9-front-angled-top-left-920x613.jpg",
    "https://ksassets.timeincuk.net/wp/uploads/sites/54/2019/10/OnePlus-7T-Pro-held-768x512.jpg",
    "https://ksassets.timeincuk.net/wp/uploads/sites/54/2019/11/Mi-Note-10_04-768x432.jpg"};

    private static final int MAX_IMAGE_NUM = 5;


    /**
     * Create a random Phone POJO.
     */
    public static Phone userPhone(Context context){
        Phone phone = new Phone();
        Random random = new Random();
        String[] categories = context.getResources().getStringArray(R.array.categories);
        categories = Arrays.copyOfRange(categories, 1, categories.length);
        phone.setCondition(getRandomString(categories, random));
        phone.setUserid(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        phone.setName("Samsung Galaxy S20");
        phone.setPhoto(getRandomImageUrl(random));
        phone.setCondition("New");
        return phone;
    }
    public static Phone getRandom(Context context) {
        Phone phone = new Phone();
        Random random = new Random();


        // Cities (first element is 'Any')
        String[] condition = context.getResources().getStringArray(R.array.condition);
        condition = Arrays.copyOfRange(condition, 1, condition.length);

        // Categories (first element is 'Any')
        String[] categories = context.getResources().getStringArray(R.array.categories);
        categories = Arrays.copyOfRange(categories, 1, categories.length);

        int[] prices = new int[]{1, 2, 3};

        //phone.setName(getRandomName(random));
        //phone.setCondition(getRandomString(condition, random));
        phone.setCondition(getRandomString(categories, random));
        phone.setPhoto(getRandomImageUrl(random));
        phone.setPrice(getRandomInt(prices, random));
        //phone.setAvgRating(getRandomRating(random));
        //phone.setNumRatings(random.nextInt(20));

        return phone;
    }


    /**
     * Get a random image.
     */
    private static String getRandomImageUrl(Random random) {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        int id = random.nextInt(MAX_IMAGE_NUM);

        //return String.format(Locale.getDefault(), phones[1]);
        return phones[id];
    }

    /**
     * Get price represented as dollar signs.
     */
    public static String getPriceString(Phone phone) {
        return getPriceString(phone.getPrice());
    }

    /**
     * Get price represented as dollar signs.
     */
    public static String getPriceString(int priceInt) {
        switch (priceInt) {
            case 1:
                return "Highest Price";
            case 2:
                return "Lowest Price";
            case 3:
            default:
                return "$$$";
        }
    }

    private static double getRandomRating(Random random) {
        double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }

    /*private  String getRandomName(Random random) {
        return getRandomString(BRAND, random) + " "
                + getRandomString(Samsung, random);
    }*/

    private static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    private static int getRandomInt(int[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

}
