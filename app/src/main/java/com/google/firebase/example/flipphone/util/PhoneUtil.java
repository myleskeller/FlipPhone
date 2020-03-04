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

import com.google.firebase.example.flipphone.R;
import com.google.firebase.example.flipphone.model.Phone;

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

    //private static final String RESTAURANT_URL_FMT = "https://storage.googleapis.com/firestorequickstarts.appspot.com/food_%d.png";
    public static String[] phones = {"https://www.google.com/imgres?imgurl=https%3A%2F%2Ffscl01.fonpit.de%2Fuserfiles%2F7640001%2Fimage%2Fbest-high-end-smartphones%2FAndroidPIT-Best-High-End-Smartphones-Hero-1.jpg&imgrefurl=https%3A%2F%2Fwww.androidpit.com%2Fdo-not-buy-a-new-smartphone-now&tbnid=UYHMfIr6HEIKRM&vet=12ahUKEwiskaiu1f_nAhVMHVMKHd8pDF0QMygAegUIARCNAw..i&docid=iuk8nQNCXMTo8M&w=3840&h=2160&q=smartphones&safe=off&ved=2ahUKEwiskaiu1f_nAhVMHVMKHd8pDF0QMygAegUIARCNAw",
    "https://www.google.com/imgres?imgurl=https%3A%2F%2Fcdn.arstechnica.net%2Fwp-content%2Fuploads%2F2018%2F05%2F1-980x735.jpg&imgrefurl=https%3A%2F%2Farstechnica.com%2Fgadgets%2F2018%2F06%2Foneplus-finally-promises-to-update-its-smartphones%2F&tbnid=x2A1SZibnyv0mM&vet=12ahUKEwiskaiu1f_nAhVMHVMKHd8pDF0QMygXegUIARDhAQ..i&docid=-nJBkgcQdGbJlM&w=980&h=735&q=smartphones&safe=off&ved=2ahUKEwiskaiu1f_nAhVMHVMKHd8pDF0QMygXegUIARDhAQ",
    "https://ksassets.timeincuk.net/wp/uploads/sites/54/2019/03/Xiaomi-Mi-9-front-angled-top-left-920x613.jpg",
    "https://ksassets.timeincuk.net/wp/uploads/sites/54/2019/10/OnePlus-7T-Pro-held-768x512.jpg",
    "https://ksassets.timeincuk.net/wp/uploads/sites/54/2019/11/Mi-Note-10_04-768x432.jpg"};

    private static final int MAX_IMAGE_NUM = 22;

    private static final String[] NAME_FIRST_WORDS = {
            "Foo",
            "Bar",
            "Baz",
            "Qux",
            "Fire",
            "Lorem",
            "Ipsum",
            "Google",
            "Best",
    };

    private static final String[] NAME_SECOND_WORDS = {
            "Phone",
            "HTC",
            "iPhone",
            "Samsung Galaxy",
            "Motorola",
            "Razer",
            "OnePlus",
    };


    /**
     * Create a random Phone POJO.
     */
    public static Phone getRandom(Context context) {
        Phone phone = new Phone();
        Random random = new Random();

        // Cities (first element is 'Any')
        String[] cities = context.getResources().getStringArray(R.array.cities);
        cities = Arrays.copyOfRange(cities, 1, cities.length);

        // Categories (first element is 'Any')
        String[] categories = context.getResources().getStringArray(R.array.categories);
        categories = Arrays.copyOfRange(categories, 1, categories.length);

        int[] prices = new int[]{1, 2, 3};

        phone.setName(getRandomName(random));
        phone.setCity(getRandomString(cities, random));
        phone.setCategory(getRandomString(categories, random));
        phone.setPhoto(getRandomImageUrl(random));
        phone.setPrice(getRandomInt(prices, random));
        phone.setAvgRating(getRandomRating(random));
        phone.setNumRatings(random.nextInt(20));

        return phone;
    }


    /**
     * Get a random image.
     */
    private static String getRandomImageUrl(Random random) {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        //int id = random.nextInt(3) + 1;

        return String.format(Locale.getDefault(), phones[1]);
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
                return "$";
            case 2:
                return "$$";
            case 3:
            default:
                return "$$$";
        }
    }

    private static double getRandomRating(Random random) {
        double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }

    private static String getRandomName(Random random) {
        return getRandomString(NAME_FIRST_WORDS, random) + " "
                + getRandomString(NAME_SECOND_WORDS, random);
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
