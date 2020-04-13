package com.flipphone.camera;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flipphone.R;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flipphone.MainActivity.mRTDB;

public class CameraActivity extends AppCompatActivity {

    @BindView(R.id.camera)
    CameraView camera;
    String message = "this didn't work";
    String frontPic = "error";
    static AlertDialog flipAlertDialog;
    static boolean visible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: add a window title to discern between taking a front and back photo

        requestWindowFeature(Window.FEATURE_NO_TITLE); // go full screen
        setContentView(R.layout.activity_camera);

        Intent intent = getIntent();
        message = intent.getStringExtra("EXTRA_MESSAGE");
        Log.v("StringExtra: ",message);

        if (message.equals("back"))
            findViewById(R.id.camera_overlay).setScaleX(-1);

        ButterKnife.bind(this);

        camera.setLifecycleOwner(this);

        showPhotoTip();

        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                PicturePreviewActivity.setPictureResult(result);
                PicturePreviewActivity.setStream(result);

                Intent intent = new Intent(CameraActivity.this, PicturePreviewActivity.class);
                Bundle extras = new Bundle();
                extras.putString("EXTRA_MESSAGE", message);
                intent.putExtras(extras);
                startActivityForResult(intent, 0);

            }
        });

        //i don't think this is necessary anymore since we're not saving the file locally
//        PermissionUtils.requestReadWriteAppPermissions(this);

    }

    public static void flipReceived() {
        Log.e("FLIP", "alert dialog visible: " + String.valueOf(visible));
        if (visible == true) {
            flipAlertDialog.dismiss();
            visible = false;
        }
    }

    @OnClick(R.id.fab_picture)
    void capturePictureSnapshot() {
        camera.takePictureSnapshot();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == 0 && resultCode == PicturePreviewActivity.RESULT_OK && data !=null){
                //requestWindowFeature(Window.FEATURE_NO_TITLE); // go full screen
                //setContentView(R.layout.activity_camera);

                //Intent intent = getIntent();
                message = data.getStringExtra("EXTRA_MESSAGE");
                Bundle b = new Bundle();
                b = data.getExtras();
                frontPic = b.getString("FRONT_PIC");
                Log.v("StringExtra: ",message);
                Log.v("ALLPICTURES", "CAMERAACTIVITY: "+frontPic);

                if (message.equals("back"))
                    findViewById(R.id.camera_overlay).setScaleX(-1);

                ButterKnife.bind(this);

                camera.setLifecycleOwner(this);

                showPhotoTip();

                camera.addCameraListener(new CameraListener() {
                    @Override
                    public void onPictureTaken(@NonNull PictureResult result) {
                        PicturePreviewActivity.setPictureResult(result);
                        Intent intent = new Intent(CameraActivity.this, PicturePreviewActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("EXTRA_MESSAGE", message);
                        extras.putString("FRONT_PIC", frontPic);
                        intent.putExtras(extras);
                        startActivity(intent);
//                        finish();
                    }
                });

//                PermissionUtils.requestReadWriteAppPermissions(this);
            }

    }

    public void showPhotoTip() { //this was surprisingly easy to implement...
        if (message.equals("front")) {
            AlertDialog alertDialog = new AlertDialog.Builder(CameraActivity.this).create();

            alertDialog.setTitle(getString(R.string.front_photo_tip_title));
            alertDialog.setMessage(getString(R.string.front_photo_tip_message));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        }
        if (message.equals("back")) {
            visible = true;
            mRTDB.listenForData();

            flipAlertDialog = new AlertDialog.Builder(CameraActivity.this).create();

            flipAlertDialog.setTitle(getString(R.string.back_photo_tip_title));
            flipAlertDialog.setMessage(getString(R.string.back_photo_tip_message));
            flipAlertDialog.setButton(flipAlertDialog.BUTTON_NEUTRAL, "OK(debug)",
                    (dialog, which) -> dialog.dismiss());

            flipAlertDialog.setCancelable(false);
            flipAlertDialog.setCanceledOnTouchOutside(false);
            flipAlertDialog.show();
        }
    }
}
