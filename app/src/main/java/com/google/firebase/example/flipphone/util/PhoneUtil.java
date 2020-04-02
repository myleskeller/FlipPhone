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
 package com.google.firebase.example.flipphone.util;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.example.flipphone.R;
import com.google.firebase.example.flipphone.model.Phone;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Locale;
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

    public static String[] BRAND = {
            "-Select Brand-",
            "Samsung",
            "Apple",
            "Motorola",
            "Huawei",
            "HTC",
            "Razer",
            "OnePlus",
            "Google",
            "LG",
            "Sony",
            "Xiaomi",
            "Oppo"
    };
    public static String[] Apple = {
            "-Select Model-",
            "iPhone 6",
            "iPhone 6 Plus",
            "iPhone 6s",
            "iPhone 6s Plus",
            "iPhone SE",
            "iPhone 7",
            "iPhone 7 Plus",
            "iPhone 8",
            "iPhone 8 Plus",
            "iPhone X",
            "iPhone XR",
            "iPhone XS",
            "iPhone XS Max",
            "iPhone 11",
            "iPhone 11 Pro",
            "iPhone 11 Pro Max"
    };
     public static String[] Samsung = {
             "-Select Model-",
            "Galaxy S7",
            "Galaxy S7 Edge",
            "Galaxy S8",
            "Galaxy S8+",
            "Galaxy S8 Active",
            "Galaxy S9",
            "Galaxy S9+",
            "Galaxy S9 Light Luxury",
            "Galaxy S10",
            "Galaxy S10+",
            "Galaxy S10e",
            "Galaxy S10 5G",
            "Galaxy S20",
            "Galaxy S20 Ultra",
            "Galaxy S20 Ultra 5G",
            "Galaxy S20+ 5G",
            "Galaxy S20 5G",
            "Galaxy Fold",
            "Galaxy Fold 5G",
             "Galaxy Note 8",
             "Galaxy Note 9",
             "Galaxy Note 10",
             "Galaxy Note 10+",
             "Galaxy A01",
             "Galaxy A11",
             "Galaxy A31",
             "Galaxy A41",
             "Galaxy A51",
             "Galaxy A71",
             "Galaxy A3",
             "Galaxy A5",
             "Galaxy A6",
             "Galaxy A6+",
             "Galaxy A6s",
             "Galaxy A7",
             "Galaxy A8",
             "Galaxy A8+",
             "Galaxy A8s",
             "Galaxy A8 Star",
             "Galaxy A9",
             "Galaxy A10e",
             "Galaxy A10s",
             "Galaxy A20",
             "Galaxy A30",
             "Galaxy A40",
             "Galaxy A50",
             "Galaxy A60",
             "Galaxy A70",
             "Galaxy A80"

    };
     public static String [] Motorola = {
             "-Select Model-",
             "Moto Z",
             "Moto Z Play",
             "Moto Z2 Play",
             "Moto Z3",
             "Moto Z3 Play",
             "Moto Z4",
             "Moto X4",
             "Motorola Razr",
             "Motorola One",
             "Motorola One Power",
             "Motorola One Vision",
             "Motorola One Action",
             "Motorola One Zoom",
             "Motorola One Macro",
             "Motorola One Hyper",
             "Moto E4",
             "Moto E4 Play",
             "Moto E5",
             "Moto E5 Play",
             "Moto E5 Plus",
             "Moto E6",
             "Moto E6s",
             "Moto E6 Play",
             "Moto E6 Plus",
             "Moto G5",
             "Moto G5 Plus",
             "Moto G6",
             "Moto G6 Play",
             "Moto G6 Plus",
             "Moto G7",
             "Moto G7 Play",
             "Moto G7 Plus",
             "Moto G8",
             "Moto G8 Play",
             "Moto G8 Plus"
     };
    public static String[] Huawei ={
            "-Select Model-",
            "Mate 10",
            "Mate 10 Pro",
            "Mate 10 Lite",
            "Porsche Design Mate 10",
            "Porsche Design Mate RS",
            "Mate 20",
            "Mate 20 Pro",
            "Mate 20 Lite",
            "Mate 20 X",
            "Mate 20 Porsche RS",
            "Mate X",
            "Mate 30",
            "Mate 30 Pro",
            "Mate 30",
            "Mate 30 Pro",
            "Mate 30 RS",
            "Mate Xs"
    };
    public static String[] HTC ={
            "-Select Model-",
            "U11",
            "U11+",
            "U12",
            "U12+",
            "U12 Life",
            "Exodus",
            "U19e",
            "Desire 19+",
            "Wildfire X",
            "Desire 12",
            "Desire 12+"
    };
    public static String[] Razer = {
            "Phone",
            "Phone 2"
    };
    public static String[] OnePlus ={
            "-Select Model-",
        "OnePlus 5",
            "OnePlus 5T",
            "OnePlus 6",
            "OnePlus 6T",
            "OnePlus 7",
            "OnePlus 7 Pro",
            "OnePlus 7T",
            "OnePlus 7T Pro"
    };
    public static String[] Google = {
            "-Select Model-",
            "Pixel 3",
            "Pixel 3 XL",
            "Pixel 3a",
            "Pixel 3a XL",
            "Pixel 4",
            "Pixel 4 XL"
    };
    public static String[] LG = {
            "-Select Model-",
            "V50 ThinQ",
            "V40 ThinQ",
            "V35 ThinQ",
            "V30",
            "G8 ThinQ",
            "G7 ThinQ",
            "G6"
    };
    public static String[] Sony = {
            "-Select Model-",
        "Xperia L2",
            "Xperia XA2",
            "Xperia XA2 Ultra",
            "Xperia XA2 Plus",
            "Xperia XZ2",
            "Xperia XZ2 Compact",
            "Xperia XZ2 Premium",
            "Xperia XZ3",
            "Xperia L3",
            "Xperia 10",
            "Xperia 10 Plus",
            "Xperia 1",
            "Xperia Ace",
            "Xperia 5",
            "Xperia 8",
            "Xperia L4",
            "Xperia 1 II",
            "Xperia 10 II",
            "Xperia Pro"



    };
    public static String[] Xiaomi = {
            "-Select Model-",
                "Mi 9",
                "Mi 9T",
                "9T Pro",
                "Mi Note 10",
                "Mi Mix 3",
                "Redmi Note 7 series",
                "Mi A3",
                "Redmi Note 8T",
                "Redmi Note 8 Pro",
                "Redmi 7",
                "Redmi 8A",
                "Redmi 8"
    };
    public static String[] OPPO = {
            "-Select Model-",
                    "Reno 10x Zoom",
                    "Reno 2",
                    "Reno 2Z",
                    "Reno Z",
                    "Reno",
                    "A9 2020",
                    "RX17 Pro",
                    "Find X",
                    "RX17 Neo"
    };

    /**
     * Create a random Phone POJO.
     */
    public static Phone userPhone(Context context){
        Phone phone = new Phone();
        Random random = new Random();
        String[] categories = context.getResources().getStringArray(R.array.categories);
        categories = Arrays.copyOfRange(categories, 1, categories.length);
        phone.setCondition(getRandomString(categories, random));
        phone.setUserid(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        phone.setName("Samsung Galaxy S20");
        phone.setPhoto(getRandomImageUrl(random));
        phone.setCity("New");
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
        //phone.setCity(getRandomString(condition, random));
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

    private  String getRandomName(Random random) {
        return getRandomString(BRAND, random) + " "
                + getRandomString(Samsung, random);
    }

    private static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    private static int getRandomInt(int[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

}
