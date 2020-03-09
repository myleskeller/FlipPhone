package com.flipphone.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import com.google.firebase.example.flipphone.R;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraActivity extends AppCompatActivity {

    @BindView(R.id.camera)
    CameraView camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // go full screen
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        camera.setLifecycleOwner(this);

        showPhotoTip();

        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                PicturePreviewActivity.setPictureResult(result);
                Intent intent = new Intent(CameraActivity.this, PicturePreviewActivity.class);
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
        alertDialog.setTitle(getString(R.string.front_photo_tip_title));
        alertDialog.setMessage(getString(R.string.front_photo_tip_message));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}
