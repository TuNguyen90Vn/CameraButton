/*
 * Copyright (C) 2017 Artem Hluhovskyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hluhovskyi.camerabutton.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.hluhovskyi.camerabutton.CameraButton;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.selector.FlashSelectors;
import io.fotoapparat.parameter.update.UpdateRequest;
import io.fotoapparat.view.CameraView;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    private CameraView mCameraView;
    private CameraButton mCameraButton;

    private View mFlashSwitch;
    private View mCameraSwitch;
    private RecyclerView mModesRecycler;

    private Fotoapparat mCameraManager;
    private boolean mIsFlashEnabled;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraView = findViewById(R.id.camera_view);
        mCameraButton = findViewById(R.id.camera_button);

        mFlashSwitch = findViewById(R.id.flash_switch);
        mFlashSwitch.setOnClickListener(v -> switchFlash());
        mModesRecycler = findViewById(R.id.recycler);

        mCameraSwitch = findViewById(R.id.camera_switch);
        mCameraSwitch.setOnClickListener(v -> switchCamera());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            openCamera();
            mCameraManager.start();
        }
    }

    private void openCamera() {
        mCameraManager = Fotoapparat.with(this)
                .into(mCameraView)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraManager != null) {
            mCameraManager.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraManager != null) {
            mCameraManager.stop();
        }
    }

    private void switchFlash() {
        mCameraManager.updateParameters(UpdateRequest.builder()
                .flash(mIsFlashEnabled
                        ? FlashSelectors.off()
                        : FlashSelectors.torch())
                .build());
        mIsFlashEnabled = !mIsFlashEnabled;
    }

    private void switchCamera() {
        Toast.makeText(this, "Imagine that camera is switched!", Toast.LENGTH_LONG).show();
    }

    @NonNull
    public CameraButton getCameraButton() {
        return mCameraButton;
    }

    public RecyclerView getModesRecycler() {
        return mModesRecycler;
    }

    @NonNull
    public View getFlashSwitch() {
        return mFlashSwitch;
    }

    @NonNull
    public View getCameraSwitch() {
        return mCameraSwitch;
    }
}
