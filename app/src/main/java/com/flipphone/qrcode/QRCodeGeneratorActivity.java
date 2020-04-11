package com.flipphone.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.flipphone.FirebaseRTDB;
import com.flipphone.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class QRCodeGeneratorActivity extends AppCompatActivity {

    TextView textView;
    TextView textView2;
    ImageView qrCode;
    ImageView userPhoto;
    FirebaseRTDB rtdb = new FirebaseRTDB();

    protected void onCreate(Bundle savedInstanceState) {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int dimensions = metrics.widthPixels;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generator);

        textView = findViewById(R.id.date_time);
        textView2 = findViewById(R.id.user_email);
        qrCode = findViewById(R.id.imageView);
        userPhoto = findViewById(R.id.imageView2);

        //timestamp
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        Log.v("TIME", currentDateTimeString);
        textView.setText(currentDateTimeString);

        //user email
        textView2.setText(R.string.default_user_email);
        userPhoto.setImageResource(R.drawable.pizza_monster);

        FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
        if (account != null) {
            textView2.setText(account.getPhoneNumber());
            Log.v("FIREBASE", "account email: " + account.getPhoneNumber());
            if (account.getPhotoUrl() != null)
                Glide.with(this).load(account.getPhotoUrl()).into(userPhoto);
            Log.v("FIREBASE", "account photo url: " + account.getPhotoUrl());
        }

        //make qrcode
//        String url = "dookie";
        String url = getRandomString(24);

        Log.v("QR_GENERATE", "i received: " + url); //returned null???
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1); /* default = 4 */

            BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, dimensions, dimensions, hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        // should make a (probably) empty node in the database with the key of the random string
        rtdb.makeNode(url);
        rtdb.listenForData("idk");
    }

    private static  String getRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder randomStringBuilder = new StringBuilder();
        Random generator = new Random();
        while (randomStringBuilder.length() < length) { // length of the random string.
            int index = (int) (generator.nextFloat() * chars.length());
            randomStringBuilder.append(chars.charAt(index));
        }
        return randomStringBuilder.toString();
    }
}