package com.example.shane.smartform.Util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

public class PictureUtils {
	
	private static final String TAG = "PictureUtils";
	
	/**
	 * Get a BitmapDrawable from a local file that is scaled down
	 * to fid the current Window size.
	 */
	@SuppressWarnings("deprecation")
	public static BitmapDrawable getScaledDrawable(Activity a, String path) {
		Log.d(TAG, "Inside getScaledDrawable()");
		
		Display display = a.getWindowManager().getDefaultDisplay();
		float destWidth = display.getWidth();
		float destHeight = display.getHeight();
		
		// Read in the dimensions of the image on disk
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		
		float srcWidth = options.outWidth;
		float srcHeight = options.outHeight;
		
		Log.d(TAG, "srcWidth: " + srcWidth);
		Log.d(TAG, "srcHeight: " + srcHeight);
		
		int inSampleSize = 1;
		if( srcHeight > destHeight || srcWidth > destWidth) {
			if(srcWidth > srcHeight){
				inSampleSize = Math.round( srcHeight / destHeight );
			}
			else {
				inSampleSize = Math.round(srcWidth / destWidth);
			}
		}
		
		options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize;
		
		Log.d(TAG, "inSampleSize: " + inSampleSize);
		
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return new BitmapDrawable(a.getResources(), bitmap);
	}
	
	public static void cleanImageView(ImageView imageView) {
		if( !(imageView.getDrawable() instanceof BitmapDrawable) ) {
			return;
		}
		
		// Clean up the view's image for the sake of memory
		BitmapDrawable b = (BitmapDrawable) imageView.getDrawable();
		b.getBitmap().recycle();
		imageView.setImageDrawable(null);
	}
}
