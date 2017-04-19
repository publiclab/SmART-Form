package com.example.shane.smartform.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.ContentValues;

import com.example.shane.smartform.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

public class CameraActivity extends Activity implements PictureCallback, SurfaceHolder.Callback {

    private static  final int FOCUS_AREA_SIZE= 300;

    public static final String EXTRA_CAMERA_DATA = "camera_data";

    private static final String KEY_IS_CAPTURING = "is_capturing";

    private static final String TAG = "CameraActivity";

    private Camera mCamera;
    private Bitmap mCameraBitmap;
    private ImageView mCameraImage;
    private ImageView mCameraLayer;
    private SurfaceView mCameraPreview;
    private Button mCaptureImageButton;
    private Button mSaveImageButton;
    private Button mDoneImageButton;
    private byte[] mCameraData;
    private boolean mIsCapturing;
    private Uri imageUri;

    private OnClickListener mCaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            captureImage();
        }
    };

    private OnClickListener mRecaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setupImageCapture();
        }
    };

    private OnClickListener mDoneImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            File saveFile = openFileForImage();
            boolean success = true;

            if (saveFile != null) {
                saveImageToFile(saveFile);
            } else {
                success = false;
                Toast.makeText(CameraActivity.this, "Unable to open file for saving image.",
                        Toast.LENGTH_LONG).show();
            }
            // Set the photo filename on the result intent
            if (success) {
                Intent i = new Intent();
                i.putExtra(EXTRA_CAMERA_DATA, imageUri.toString());
                setResult(Activity.RESULT_OK, i);
            }
            else {
                setResult(Activity.RESULT_CANCELED);
            }
            finish();
        }
    };

    private OnClickListener mSaveImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            File saveFile = openFileForImage();
            if (saveFile != null) {
                saveImageToFile(saveFile);
            } else {
                Toast.makeText(CameraActivity.this, "Unable to open file for saving image.",
                        Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(CameraActivity.this,ResultActivity.class);
            intent.putExtra(EXTRA_CAMERA_DATA, imageUri.toString());
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        mCameraImage = (ImageView) findViewById(R.id.camera_image_view);
        mCameraImage.setVisibility(View.INVISIBLE);

        mCameraLayer = (ImageView) findViewById(R.id.camera_layer);
        mCameraLayer.setVisibility(View.VISIBLE);

        mCameraPreview = (SurfaceView) findViewById(R.id.preview_view);
        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCaptureImageButton = (Button) findViewById(R.id.capture_image_button);
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);

        mDoneImageButton = (Button) findViewById(R.id.done_image_button);
        mDoneImageButton.setOnClickListener(mDoneImageButtonClickListener);
        mDoneImageButton.setEnabled(false);

        mIsCapturing = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(KEY_IS_CAPTURING, mIsCapturing);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mIsCapturing = savedInstanceState.getBoolean(KEY_IS_CAPTURING, mCameraData == null);
        if (mCameraData != null) {
            setupImageDisplay();
        } else {
            setupImageCapture();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(mCameraPreview.getHolder());
                if (mIsCapturing) {
                    mCamera.setDisplayOrientation(90);
                    mCamera.startPreview();
                }
            } catch (Exception e) {
                Toast.makeText(CameraActivity.this, "Unable to open camera. Please go to settings for camera permission", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCameraData = data;
        setupImageDisplay();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            Camera.Parameters camParams = mCamera.getParameters();

            // Flatten camera parameters
            String flattened = camParams.flatten();
            StringTokenizer tokenizer = new StringTokenizer(flattened, ";");
            Log.d(TAG, "Dump all camera parameters:");
            while (tokenizer.hasMoreElements()) {
                Log.d(TAG, tokenizer.nextToken());
            }


            Log.i(TAG, "Supported Focus Models:" + camParams.get("focus-mode-values"));
            Log.i(TAG, "Supported ISO Modes:" + camParams.get("iso-values"));
            Log.i(TAG, "Supported Exposure Modes:" + camParams.get("exposure-values"));
            Log.i(TAG, "Supported White Balance Modes:" + camParams.get("whitebalance-values"));
            Log.i(TAG, "Supported Preview Sizes: " + camParams.get("preview-size-values"));

            // Set camera parameters
            camParams.set("focus-mode", "continuous-picture");
            camParams.set("iso", "200");
            camParams.set("whitebalance","fluorescent");
            camParams.set("preview-size","1920x1080");
            camParams.set("orientation", "portrait");

            Log.i(TAG, "Focus setting = " + camParams.get("focus-mode"));
            Log.i(TAG, "ISO setting = " + camParams.get("iso"));
            Log.i(TAG, "Exposure setting = " + camParams.get("exposure"));
            Log.i(TAG, "White Balance setting = " + camParams.get("whitebalance"));
            Log.i(TAG, "Preview Size setting = " + camParams.get("preview-size"));
            Log.i(TAG, "Preview orientation setting = " + camParams.get("orientation"));
            mCamera.setParameters(camParams);

            try {
                mCamera.setPreviewDisplay(holder);
                if (mIsCapturing) {
                    mCamera.setDisplayOrientation(90);
                    mCamera.startPreview();
                }
            } catch (IOException e) {
                Toast.makeText(CameraActivity.this, "Unable to start camera preview.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }

    private void captureImage() {
        mCamera.takePicture(null, null, this);
    }

    private void setupImageCapture() {
        mCameraImage.setVisibility(View.INVISIBLE);
        mCameraLayer.setVisibility(View.VISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);
        mDoneImageButton.setEnabled(false);

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
        mCaptureImageButton.setText(R.string.capture_image);
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);
    }

    private void setupImageDisplay() {
        Bitmap rotateBitmap= BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
        Bitmap cropBitmap = RotateBitmap(rotateBitmap, 90);
        mCameraBitmap = Bitmap.createBitmap(cropBitmap, cropBitmap.getWidth()/4, cropBitmap.getHeight()/2 - cropBitmap.getWidth()/4,
                cropBitmap.getWidth()/2, cropBitmap.getWidth()/2);
        mCameraImage.setImageBitmap(mCameraBitmap);
        mCamera.stopPreview();
        mCameraImage.setVisibility(View.VISIBLE);
        mCameraLayer.setVisibility(View.INVISIBLE);
        mCameraPreview.setVisibility(View.INVISIBLE);
        mDoneImageButton.setEnabled(true);
        mCaptureImageButton.setText(R.string.recapture_image);
        mCaptureImageButton.setOnClickListener(mRecaptureImageButtonClickListener);
    }

    private File openFileForImage() {
        File imageDirectory = null;
        String filename = UUID.randomUUID().toString() + ".png";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            imageDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "SmART-Form");
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                return new File(imageDirectory.getPath() + File.separator + filename);
            }
        }
        return null;
    }

    private void saveImageToFile(File file) {
        if (mCameraBitmap != null) {
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
                if (!mCameraBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)) {
                    Toast.makeText(CameraActivity.this, "Unable to save image to file.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CameraActivity.this, "Saved image to: " + file.getPath(),
                            Toast.LENGTH_LONG).show();
                    imageUri = getImageContentUri(getApplicationContext(), file);
                }
                outStream.close();
            } catch (Exception e) {
                Toast.makeText(CameraActivity.this, "Unable to save image to file.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Calculates and returns optimal preview size from supported by each device.
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size optimalSize = null;

        for (Camera.Size size : sizes) {
            if ((size.width <= width && size.height <= height) || (size.height <= width && size.width <= height)) {
                if (optimalSize == null) {
                    optimalSize = size;
                } else {
                    int resultArea = optimalSize.width * optimalSize.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        optimalSize = size;
                    }
                }
            }
        }

        return optimalSize;
    }
}