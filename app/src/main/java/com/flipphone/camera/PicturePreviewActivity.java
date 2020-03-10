package com.flipphone.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.flipphone.listing.ListingDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.example.flipphone.MainActivity;
import com.google.firebase.example.flipphone.R;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.size.AspectRatio;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class PicturePreviewActivity extends Activity {

    @BindView(R.id.fab_save_picture)
    FloatingActionButton saveFAB;
    String message = "";

    private static WeakReference<PictureResult> image;

    public static void setPictureResult(@Nullable PictureResult im) {
        image = im != null ? new WeakReference<>(im) : null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);

        //get the current intent
        Intent intent = getIntent();
        message = intent.getStringExtra("EXTRA_MESSAGE");
        Log.v("StringExtra: ",message);

        ButterKnife.bind(this);
        final ImageView imageView = findViewById(R.id.image);
        PictureResult result = image == null ? null : image.get();
        if (result == null) {
            finish();
            return;
        }
        AspectRatio ratio = AspectRatio.of(result.getSize());
        //TODO: get native portrait camera resolution and apply over the constants below
        result.toBitmap(1000, 1000, imageView::setImageBitmap);

        if (result.isSnapshot()) {
            // Log the real size for debugging reason.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(result.getData(), 0, result.getData().length, options);
            if (result.getRotation() % 180 != 0) {
                Log.e("PicturePreview", "The picture full size is " + result.getSize().getHeight() + "x" + result.getSize().getWidth());
            } else {
                Log.e("PicturePreview", "The picture full size is " + result.getSize().getWidth() + "x" + result.getSize().getHeight());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            setPictureResult(null);
        }
    }

    @OnClick(R.id.fab_save_picture)
    void savePicture() {
        //TODO: figure out how/where to pass this bitmap to get it to firebase

        if (image == null) {
            return;
        }

        PermissionUtils.requestReadWriteAppPermissions(this);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.US);
        String currentTimeStamp = dateFormat.format(new Date());

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "CameraViewFreeDrawing";
        File outputDir= new File(path);
        outputDir.mkdirs();
        File saveTo = new File(path + File.separator + currentTimeStamp + ".jpg");


        image.get().toFile(saveTo, file -> {
            if (file != null) {
                Toast.makeText(PicturePreviewActivity.this, "Picture saved to " + file.getPath(), Toast.LENGTH_LONG).show();

                // should not need to save the picture again
                saveFAB.setVisibility(View.GONE);

                // refresh gallery
                MediaScannerConnection.scanFile(this,
                        new String[] { file.toString() }, null,
                        (filePath, uri) -> {
                            Log.i("ExternalStorage", "Scanned " + filePath + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        });
            }
        });

        //janky navigation implementation
        if (message.equals("front")) {
            TakeBackPhoto();
        }

        //janky navigation implementation
        if (message.equals("back")) {
            PriceAndDetails();
        }


    }

    public void TakeBackPhoto () {
        Intent intent = new Intent(this, CameraActivity.class);
        Bundle extras = new Bundle();
        extras.putString("EXTRA_MESSAGE", "back");
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void PriceAndDetails() {
        Intent intent = new Intent(this, ListingDetails.class);
        Bundle extras = new Bundle();
        extras.putString("EXTRA_MESSAGE", "unused");
        intent.putExtras(extras);
        startActivity(intent);
    }
}
