package com.flipphone.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.google.firebase.example.flipphone.R;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class CameraActivity extends AppCompatActivity {

    @BindView(R.id.camera)
    CameraView camera;
    String message = "this didn't work";

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
                Intent intent = new Intent(CameraActivity.this, PicturePreviewActivity.class);
                Bundle extras = new Bundle();
                extras.putString("EXTRA_MESSAGE", message);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        PermissionUtils.requestReadWriteAppPermissions(this);
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
