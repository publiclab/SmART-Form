package com.example.shane.smartform.Util;

import android.content.Context;

import com.example.shane.smartform.Bean.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class SmartFormJSONSerializer {
	
	private Context mContext;
	private String mFilename;
	
	public SmartFormJSONSerializer(Context c, String f){
		mContext = c;
		mFilename = f;
	}
	
	public void saveTests(ArrayList<Test> tests)
			throws JSONException, IOException {
		
		// Build an array in JSON
		JSONArray array = new JSONArray();
		for (Test c : tests){
			array.put(c.toJSON());
		}
		
		// Write File to disk
		Writer writer = null;
		try {
			OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null){
				writer.close();
			}
		}
		
	}
	
	public ArrayList<Test> loadTests() throws IOException, JSONException {
		ArrayList<Test> tests = new ArrayList<Test>();
		BufferedReader reader = null;
		
		try {
			// Open and read the file into a StringBuilder
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			
			while( (line = reader.readLine()) != null ) {
				// Line breaks are omitted and irrelevant
				jsonString.append(line);
			}
			
			// Parse the JSON using JSONTokener
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			
			// Build the array of tests from JSONObjects
			for (int i = 0; i < array.length(); i++) {
				tests.add(new Test(array.getJSONObject(i)));
			}
		} catch (FileNotFoundException e) {
			// Ignore this one; it happens when starting fresh
		} finally {
			if (reader != null){
				reader.close();
			}
		}
		
		return tests;
	}
	
}
