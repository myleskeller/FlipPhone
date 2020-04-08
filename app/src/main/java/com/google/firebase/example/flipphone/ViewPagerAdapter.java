package com.google.firebase.example.flipphone;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.example.flipphone.model.Phone;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import static com.google.firebase.example.flipphone.PhoneDetailActivity.KEY_PHONE_ID;


public class ViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private String[] arrayList;

    ViewPagerAdapter(Context context, String[] arrayList){
        this.mContext = context;
        this.arrayList = arrayList;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container , int position , @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container , int position) {

        ImageView imageView = new ImageView(mContext);
        Glide.with(mContext).load(arrayList[position]).into(imageView);
        container.addView(imageView);
        return imageView;

    }

    @Override
    public int getCount() {
        if(arrayList != null) {
            return arrayList.length;
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view , @NonNull Object object) {
        return view == object;
    }
}