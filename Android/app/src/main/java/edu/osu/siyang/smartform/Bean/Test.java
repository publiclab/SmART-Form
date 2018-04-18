package edu.osu.siyang.smartform.Bean;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

public class Test {

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE="title";
    private static final String JSON_STATE = "state";
    private static final String JSON_DATE = "date";
    private static final String JSON_BEFORE = "before";
    private static final String JSON_AFTER = "after";
    private static final String JSON_START = "start";
    private static final String JSON_END = "end";
    private static final String JSON_RESULT= "result";
    private UUID mId;
    private String mTitle;
    private int mState;
    private Date mDate;
    private Uri mBefore;
    private Uri mAfter;
    private Date mStart;
    private Date mEnd;
    private String mResult;

    // Override this value so that when our ArrayAdapter grabs these guys to display, it can
    //   print out some meaningful text instead of the class name and memory address (default)
    /*@Override
    public String toString() {
        return mTitle;
    }*/

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
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


    public String getResult() { return mResult; }

    public void setResult(String result) { mResult = result; }

    public Date getStart() {return mStart;}

    public void setStart(Date date) {
        mStart = date;
    }

    public Date getEnd() {return mEnd;}

    public void setEnd(Date date) {
        mEnd = date;
    }

    public int getState() {return mState;}

    public void setState(int state) {mState = state;}

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
        mState = 0;
    }

    public Test( JSONObject json ) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));

        if (json.has(JSON_TITLE)) {
            mTitle = json.getString(JSON_TITLE);
        }

        mState = json.getInt(JSON_STATE);

        mDate = new Date(json.getLong(JSON_DATE));

        if(json.has(JSON_BEFORE)) mBefore = Uri.parse(json.getString(JSON_BEFORE));
        if(json.has(JSON_AFTER)) mAfter = Uri.parse(json.getString(JSON_AFTER));
        if(json.has(JSON_START)) mStart = new Date(json.getLong(JSON_START));
        if(json.has(JSON_END)) mEnd = new Date(json.getLong(JSON_END));
        if(json.has(JSON_RESULT)) mResult = json.getString(JSON_RESULT);

    }

    public JSONObject toJSON() throws JSONException {

        JSONObject json = new JSONObject();

        json.put(JSON_ID, mId.toString());
        if(mTitle != null) json.put(JSON_TITLE, mTitle);
        json.put(JSON_STATE, mState);
        json.put(JSON_DATE, mDate.getTime());
        if(mResult != null) json.put(JSON_RESULT, mResult);
        if(mBefore != null) json.put(JSON_BEFORE, mBefore.toString());
        if(mAfter != null) json.put(JSON_AFTER, mAfter.toString());
        if(mStart != null) json.put(JSON_START, mStart.getTime());
        if(mEnd != null) json.put(JSON_END, mEnd.getTime());

        return json;
    }

}
