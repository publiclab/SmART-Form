package com.example.shane.smartform.Bean;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class Test {

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE="title";
    private static final String JSON_FINISHED = "finished";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_BEFORE= "before";
    private static final String JSON_AFTER= "after";
    private static final String JSON_RESULT= "result";
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mFinished;
    private Photo mPhoto;

    private Uri mBefore;
    private Uri mAfter;
    private int mResult;

    public Uri getBefore() {
        return mBefore;
    }

    public void setBefore(Uri before) {
        mBefore = before;
    }

    public Uri getAfter() {
        return mAfter;
    }

    public void setAfter(Uri after) {
        mAfter = after;
    }

    public Test() {
        // Generate a unique identifier
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Test( JSONObject json ) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_TITLE)) {
            mTitle = json.getString(JSON_TITLE);
        }

        mFinished = json.getBoolean(JSON_FINISHED);

        mDate = new Date(json.getLong(JSON_DATE));

        if( json.has(JSON_PHOTO) ) {
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
        }

        if( json.has(JSON_RESULT) ) {
            //mResult = json.getInt(JSON_RESULT);
        }
    }

    public JSONObject toJSON() throws JSONException {

        JSONObject json = new JSONObject();

        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_FINISHED, mFinished);
        json.put(JSON_DATE, mDate.getTime());
        if (mPhoto != null) {
            json.put(JSON_PHOTO, mPhoto.toJSON());
        }
        json.put(JSON_RESULT, mFinished);

        return json;
    }

    // Override this value so that when our ArrayAdapter grabs these guys to display, it can
    //   print out some meaningful text instead of the class name and memory address (default)
    @Override
    public String toString() {
        return mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isFinished() {
        return mFinished;
    }

    public void setFinished(boolean finished) {
        mFinished = finished;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo p){
        mPhoto = p;
    }

    public int getResult() { return mResult; }

    public void setResult(int result) { mResult = result; }
}
