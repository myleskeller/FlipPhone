package com.flipphone.camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.flipphone.R;
import com.flipphone.listing.ListingDetails;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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


public class PicturePreviewActivity extends Activity {
    String url;
    String frontPic, backPic;
    ProgressBar progressBar;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        return;
    }
    @BindView(R.id.fab_save_picture)
    FloatingActionButton saveFAB;
    String message = "";
    String TAG = "MESSAGE";
    static byte[] byteImage;


    private static WeakReference<PictureResult> image;

    public static void setStream(PictureResult im){
        byteImage = im.getData();
    }

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
        //if(message == "back") {
        Bundle b = new Bundle();
        b = intent.getExtras();
        frontPic = b.getString("FRONT_PIC");
        progressBar = (ProgressBar) findViewById(R.id.progress_loader);
        Log.d("ALLPICTURES", "TESTING: " + frontPic);

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

        byteImage = result.getData();

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

        if (image == null) {
            return;
        }

//        PermissionUtils.requestReadWriteAppPermissions(this);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.US);
        String currentTimeStamp = dateFormat.format(new Date());

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "CameraViewFreeDrawing";
//        File outputDir= new File(path);
//        outputDir.mkdirs();
        File saveTo = new File(path + File.separator + currentTimeStamp + ".jpg");


        progressBar.setVisibility(View.VISIBLE);
//        image.get().toFile(saveTo, file -> {
//            if (file != null) {
                //Toast.makeText(PicturePreviewActivity.this, "Picture saved to " + file.getPath(), Toast.LENGTH_LONG).show();

                // should not need to save the picture again
//                saveFAB.setVisibility(View.GONE);

                // refresh gallery
//                MediaScannerConnection.scanFile(this,
//                        new String[] { file.toString() }, null,
//                        (filePath, uri) -> {
//                            Log.i("ExternalStorage", "Scanned " + filePath + ":");
//                            Log.i("ExternalStorage", "-> uri=" + uri);
//                        });
//            }
//        });
        Uri uri = Uri.fromFile(saveTo);
        final String cloudFilePath = uri.getLastPathSegment();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("users");
        StorageReference uploadRef = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(cloudFilePath);

//        UploadTask uploadTask = uploadRef.putFile(uri);
        UploadTask uploadTask = uploadRef.putBytes(byteImage);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                //Log.d("FILEPATH", uploadRef.getDownloadUrl().toString());
                //Log.d("FILEPATH", uploadTask.getSnapshot().getUploadSessionUri().toString());
                return uploadRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    //Log.d("FILEPATH", downloadUri.toString());
                    url = downloadUri.toString();

                    if(message == "front"){
                        frontPic = url;
                    }
                    else{
                        backPic = url;
                    }
                    if (message.equals("front")) {

                        TakeBackPhoto();
                        finish();
                    }
                    //janky navigation implementation
                    if (message.equals("back")) {
                        PriceAndDetails();
                        finish();
                    }
                }
            }
        });

    }

    public void TakeBackPhoto () {
        //Intent intent = new Intent(this, CameraActivity.class);
        Intent intent = new Intent();
        Bundle extras = new Bundle();
        extras.putString("EXTRA_MESSAGE", "back");
        extras.putString("FRONT_PIC", url);
        Log.d("ALLPICTURES", "FRONT: "+ url);
        intent.putExtras(extras);
        setResult(PicturePreviewActivity.RESULT_OK, intent);
        progressBar.setVisibility(View.GONE);

        //startActivity(intent);
        finish();
    }

    public void PriceAndDetails() {
        Intent intent = new Intent(this, ListingDetails.class);
        Bundle extras = new Bundle();
        extras.putString("EXTRA_MESSAGE", "unused");
        extras.putString("FRONT_PIC", frontPic);
        extras.putString("BACK_PIC",  backPic);
        Log.d("ALLPICTURES", "FRONT: "+frontPic);
        Log.d("ALLPICTURES", "BACK: " +backPic);
        intent.putExtras(extras);
        progressBar.setVisibility(View.GONE);

        startActivity(intent);

    }
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


}
