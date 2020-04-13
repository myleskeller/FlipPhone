package com.flipphone;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;


public class ViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private String[] arrayList;

    public ViewPagerAdapter(Context context, String[] arrayList){
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