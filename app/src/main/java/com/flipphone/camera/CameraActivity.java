package com.flipphone.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.flipphone.R;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraActivity extends AppCompatActivity {

    @BindView(R.id.camera)
    CameraView camera;
    String message = "this didn't work";
    String frontPic = "error";


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

        PermissionUtils.requestReadWriteAppPermissions(this);

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
                        finish();
                    }
                });

                PermissionUtils.requestReadWriteAppPermissions(this);
            }

    }

    @OnClick(R.id.fab_picture)
    void capturePictureSnapshot() {

        camera.takePictureSnapshot();
    }

    public void showPhotoTip() { //this was surprisingly easy to implement...
        AlertDialog alertDialog = new AlertDialog.Builder(CameraActivity.this).create();
        if (message.equals("front")) {
            alertDialog.setTitle(getString(R.string.front_photo_tip_title));
            alertDialog.setMessage(getString(R.string.front_photo_tip_message));
        }
        if (message.equals("back")) {
            alertDialog.setTitle(getString(R.string.back_photo_tip_title));
            alertDialog.setMessage(getString(R.string.back_photo_tip_message));
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}
