package edu.osu.siyang.smartform.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import edu.osu.siyang.smartform.R;

public class CameraActivity extends Activity implements PictureCallback, SurfaceHolder.Callback {

    public static final String EXTRA_CAMERA_DATA = "camera_data";
    public static final String EXTRA_FILE_NAME = "file_name";
    private static final String KEY_IS_CAPTURING = "is_capturing";
    private static final String TAG = "CameraActivity";

    private static final int focusAreaSize = 300;
    private float mLightReading;
    private Camera mCamera;
    private Bitmap mCameraBitmap;
    private ImageView mCameraImage;
    private ImageView mCameraLayer;
    private SurfaceView mCameraPreview;
    private Button mCaptureImageButton;
    private Button mDoneImageButton;
    private byte[] mCameraData;
    private boolean mIsCapturing;
    private String imagePath;
    private String imageName;
    private TextView text_lightreading;
    private Toast toast;

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

    private OnClickListener mCancelImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    };

    private OnClickListener mSaveImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            File saveFile = openFileForImage();
            boolean success = true;

            if (saveFile != null) {
                saveImageToFile(saveFile);
                imagePath = saveToInternalStorage(mCameraBitmap);
            } else {
                success = false;
                Toast.makeText(CameraActivity.this, "Unable to open file for saving image.",
                        Toast.LENGTH_SHORT).show();
            }
            // Set the photo filename on the result intent
            if (success) {
                Intent i = new Intent();
                i.putExtra(EXTRA_CAMERA_DATA, imagePath);
                i.putExtra(EXTRA_FILE_NAME, imageName);
                setResult(Activity.RESULT_OK, i);
            }
            else {
                setResult(Activity.RESULT_CANCELED);
            }
            finish();
        }
    };

    private final SensorEventListener LightSensorListener
            = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_LIGHT){
                mLightReading = event.values[0];
                if(mLightReading<20) {
                    text_lightreading.setText("LIGHT: " + mLightReading + "; " + "Low light condition!");
                } else {
                    text_lightreading.setText("LIGHT: " + mLightReading);
                }
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(edu.osu.siyang.smartform.R.layout.activity_camera);

        toast = new Toast(getApplicationContext());

        text_lightreading = (TextView)findViewById(R.id.text_lightreading);
        SensorManager mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        Sensor LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(LightSensor != null){
            mySensorManager.registerListener(
                    LightSensorListener,
                    LightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        }

        mCameraImage = (ImageView) findViewById(edu.osu.siyang.smartform.R.id.camera_image_view);
        mCameraImage.setVisibility(View.INVISIBLE);

        mCameraLayer = (ImageView) findViewById(edu.osu.siyang.smartform.R.id.camera_layer);
        mCameraLayer.setVisibility(View.VISIBLE);

        mCameraPreview = (SurfaceView) findViewById(edu.osu.siyang.smartform.R.id.preview_view);
        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCaptureImageButton = (Button) findViewById(edu.osu.siyang.smartform.R.id.capture_image_button);
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);

        mDoneImageButton = (Button) findViewById(R.id.done_image_button);
        mDoneImageButton.setOnClickListener(mCancelImageButtonClickListener);

        mIsCapturing = true;

        mCameraPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCamera != null) {
                    Camera camera = mCamera;
                    camera.cancelAutoFocus();
                    Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);

                    Camera.Parameters parameters = camera.getParameters();
                    if (parameters.getFocusMode().equals(
                            Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    }

                    if (parameters.getMaxNumFocusAreas() > 0) {
                        List<Camera.Area> mylist = new ArrayList<Camera.Area>();
                        mylist.add(new Camera.Area(focusRect, 1000));
                        parameters.setFocusAreas(mylist);
                    }

                    try {
                        camera.cancelAutoFocus();
                        camera.setParameters(parameters);
                        camera.startPreview();
                        camera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                if (camera.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                                    Camera.Parameters parameters = camera.getParameters();
                                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                                    if (parameters.getMaxNumFocusAreas() > 0) {
                                        parameters.setFocusAreas(null);
                                    }
                                    camera.setParameters(parameters);
                                    camera.startPreview();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

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
                Toast.makeText(CameraActivity.this, "Unable to open camera. Please go to settings for camera permission", Toast.LENGTH_SHORT    )
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

    /**
     * Convert touch position x:y to {@link Camera.Area} position -1000:-1000 to 1000:1000.
     */
    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int left = clamp((int) x - areaSize / 2, 0, mCameraPreview.getWidth() - areaSize);
        int top = clamp((int) y - areaSize / 2, 0, mCameraPreview.getHeight() - areaSize);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        Matrix matrix = new Matrix();
        matrix.mapRect(rectF);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            Camera.Parameters camParams = mCamera.getParameters();
            Camera.Size sc = getOptimalPreviewSize(camParams.getSupportedPreviewSizes(), width, height);
            camParams.setPreviewSize(sc.width, sc.height);

            Rect newRect = new Rect(-100, -100, 100, 100);
            Camera.Area focusArea = new Camera.Area(newRect, 1000);
            List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
            focusAreas.add(focusArea);

            // Flatten camera parameters
            String flattened = camParams.flatten();
            StringTokenizer tokenizer = new StringTokenizer(flattened, ";");
            Log.d(TAG, "Dump all camera parameters:");
            while (tokenizer.hasMoreElements()) {
                Log.d(TAG, tokenizer.nextToken());
            }


            // Customize camera parameters
            camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            camParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camParams.set("mode","m");
            camParams.set("aperture","28");
            camParams.set("shutter-speed",9);
            camParams.set("iso",200);
            camParams.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
            camParams.setPreviewSize(sc.width, sc.height);
            camParams.setExposureCompensation(0);
            camParams.setFocusAreas(focusAreas);
            mCamera.setParameters(camParams);

            // Check camera parameters
            Log.i(TAG, "Focus setting = " + camParams.getFocusMode());
            Log.i(TAG, "ISO setting = " + camParams.get("iso"));
            Log.i(TAG, "Exposure setting = " + camParams.getExposureCompensation());
            Log.i(TAG, "White Balance setting = " + camParams.getWhiteBalance());
            Log.i(TAG, "Preview Size setting = " + camParams.getPreviewSize());

            try {
                mCamera.setPreviewDisplay(holder);
                if (mIsCapturing) {
                    mCamera.setDisplayOrientation(90);
                    mCamera.startPreview();
                }
            } catch (IOException e) {
                Toast.makeText(CameraActivity.this, "Unable to start camera preview.", Toast.LENGTH_SHORT).show();
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

    /**
     * Init camera
     */
    private void setupImageCapture() {
        mCameraBitmap.recycle();
        mCameraBitmap = null;
        mCameraImage.setVisibility(View.INVISIBLE);
        mCameraLayer.setVisibility(View.VISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
        mCaptureImageButton.setText(edu.osu.siyang.smartform.R.string.capture_image);
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);
        mDoneImageButton.setText(R.string.cancel_camera);
        mDoneImageButton.setOnClickListener(mCancelImageButtonClickListener);
    }

    /**
     * Check available memory
     * @return
     */
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Scale sample bitmap for better performance
     * @param data
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap getSampleBitmap(byte[] data, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int scale = calculateInSampleSize(options, reqWidth, reqHeight);
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeByteArray(data, 0, data.length, o2);
    }

    /**
     * Draw bitmap image in app
     */
    private void setupImageDisplay() {
        Bitmap rotateBitmap = getSampleBitmap(mCameraData, 500, 500);
        Bitmap cropBitmap = RotateBitmap(rotateBitmap, 90);
        rotateBitmap.recycle();
        mCameraBitmap = Bitmap.createBitmap(cropBitmap, cropBitmap.getWidth()/4, cropBitmap.getHeight()/2 - cropBitmap.getWidth()/4,
                cropBitmap.getWidth()/2, cropBitmap.getWidth()/2);
        cropBitmap.recycle();
        mCameraBitmap = getResizedBitmap(mCameraBitmap, 250, 250);
        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
        Log.i(TAG, " memoryInfo.availMem " + memoryInfo.availMem + "\n" );
        Log.i(TAG, " memoryInfo.lowMemory " + memoryInfo.lowMemory + "\n" );
        Log.i(TAG, " memoryInfo.threshold " + memoryInfo.threshold + "\n" );

        Drawable drawable = new BitmapDrawable(getResources(), mCameraBitmap);

        if(mCameraImage.getDrawable() != null) ((BitmapDrawable)mCameraImage.getDrawable()).getBitmap().recycle();

        mCameraImage.setImageDrawable(drawable);
        mCamera.stopPreview();
        mCameraImage.setVisibility(View.VISIBLE);
        mCameraLayer.setVisibility(View.INVISIBLE);
        mCameraPreview.setVisibility(View.INVISIBLE);
        mDoneImageButton.setEnabled(true);
        mCaptureImageButton.setText(R.string.recapture_image);
        mCaptureImageButton.setOnClickListener(mRecaptureImageButtonClickListener);
        mDoneImageButton.setText(R.string.save_camera);
        mDoneImageButton.setOnClickListener(mSaveImageButtonClickListener);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("SmartForm", Context.MODE_PRIVATE);
        // Create imageDir
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM_dd_hh_mm_ss",
                Locale.getDefault());

        imageName = getIntent().getStringExtra("TEST_PARAM") + "_"
                + getIntent().getStringExtra("TEST_TAG") + "_" + dateFormat.format(new Date()) + ".png";
        File mypath=new File(directory,imageName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private File openFileForImage() {
        File imageDirectory = null;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            imageDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "SmartForm");
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM_dd_hh_mm_ss",
                        Locale.getDefault());

                return new File(imageDirectory.getPath() +
                        File.separator + getIntent().getStringExtra("TEST_PARAM") + "_"
                        + getIntent().getStringExtra("TEST_TAG") + "_" + dateFormat.format(new Date()) + ".png");
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
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Saved bitmap = "+ mCameraBitmap.getByteCount());

                    Toast.makeText(CameraActivity.this, "Saved image to the device",
                            Toast.LENGTH_SHORT).show();
                }
                outStream.close();
            } catch (Exception e) {
                Toast.makeText(CameraActivity.this, "Unable to save image to file.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Get image uri link in media store
     */
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

    /**
     * Returns rotated bitmap by angle
     */
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Calculates and returns optimal preview size from supported by each device.
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;
        for (Camera.Size size : sizes) {
            Log.d("CamSettings", "size "+ size.width +"-"+ size.height);
        }
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.d("CamSettings", "optimalSize "+ optimalSize.width +"-"+ optimalSize.height);

        return optimalSize;
    }
}