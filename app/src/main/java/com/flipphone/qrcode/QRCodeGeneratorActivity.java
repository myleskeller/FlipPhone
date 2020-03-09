package com.flipphone.qrcode;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.flipphone.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.flipphone.qrcode.BarcodeEncoder;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class QRCodeGeneratorActivity extends AppCompatActivity {

    // Button generate_QRCode;
    TextView textView;
    TextView textView2;
    ImageView qrCode;
    ImageView userPhoto;
//    EditText mEditText;

    protected void onCreate(Bundle savedInstanceState) {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int dimensions = metrics.widthPixels;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generator);

        textView = findViewById(R.id.date_time);
        textView2 = findViewById(R.id.user_email);
        qrCode = findViewById(R.id.imageView);
        userPhoto = findViewById(R.id.imageView2);
//        mEditText = findViewById(R.id.editText);

        //timestamp
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        Log.v("TIME", currentDateTimeString);
        textView.setText(currentDateTimeString);

        //user email
        textView2.setText(R.string.default_user_email);
        userPhoto.setImageResource(R.drawable.pizza_monster);

        FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
        if (account != null) {
            textView2.setText(account.getEmail());
            Log.v("FIREBASE", "account email: " + account.getEmail());
            if (account.getPhotoUrl() != null)
                Glide.with(this).load(account.getPhotoUrl()).into(userPhoto);
            Log.v("FIREBASE", "account photo url: " + account.getPhotoUrl());
        }

        //make qrcode
        //TODO: figure out how to pass url to this variable
        String url = "dookie";

//        String text = mEditText.getText().toString();
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
    }
}