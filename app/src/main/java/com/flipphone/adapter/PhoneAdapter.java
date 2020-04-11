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
 package com.flipphone.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flipphone.R;
import com.flipphone.model.Phone;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a list of Phones.
 */
public class PhoneAdapter extends FirestoreAdapter<PhoneAdapter.ViewHolder> {

    public interface OnPhoneSelectedListener {

        void onPhoneSelected(DocumentSnapshot phone);

    }

    private OnPhoneSelectedListener mListener;

    public PhoneAdapter(Query query, OnPhoneSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_phone, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView;
        MaterialRatingBar ratingBar;
        TextView numRatingsView;
        TextView priceView;
        TextView categoryView;
        TextView cityView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.phone_item_image);
            nameView = itemView.findViewById(R.id.phone_item_name);
            //ratingBar = itemView.findViewById(R.id.phone_item_rating);
            //numRatingsView = itemView.findViewById(R.id.phone_item_num_ratings);
            priceView = itemView.findViewById(R.id.phone_item_price);
            //categoryView = itemView.findViewById(R.id.phone_item_category);
            //cityView = itemView.findViewById(R.id.phone_item_city);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnPhoneSelectedListener listener) {

            Phone phone = snapshot.toObject(Phone.class);
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(imageView.getContext())
                    .load(phone.getPhoto())
                    .into(imageView);

            nameView.setText(phone.getName());
            priceView.setText("$"+phone.getPrice());
            //ratingBar.setRating((float) phone.getAvgRating());
            //cityView.setText(phone.getCondition());
            //categoryView.setText(phone.getCategory());
            //numRatingsView.setText(resources.getString(R.string.fmt_num_ratings,
                    //phone.getNumRatings()));


            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onPhoneSelected(snapshot);
                    }
                }
            });
        }

    }
}
