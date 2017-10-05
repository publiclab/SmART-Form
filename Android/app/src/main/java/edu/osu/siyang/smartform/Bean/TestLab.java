package edu.osu.siyang.smartform.Bean;

import android.content.Context;
import android.util.Log;

import edu.osu.siyang.smartform.Util.SmartFormJSONSerializer;

import java.util.ArrayList;
import java.util.UUID;

// Singleton to store one instance of the Test Lab
public class TestLab {

	private static final String TAG = "TestLab";
	private static final String FILENAME = "tests.json";

	private ArrayList<Test> mTests;

	private static TestLab sTestLab;
	private Context mAppContext;
	SmartFormJSONSerializer mSerializer;

	// Constructor
	private TestLab(Context appContext) {
		mAppContext = appContext;
		mSerializer = new SmartFormJSONSerializer(mAppContext, FILENAME);
		
		try {
			mTests = mSerializer.loadTests();
		} catch (Exception e) {
			mTests = new ArrayList<Test>();
			Log.e(TAG, "Error loading tests: ", e);
		}
		
	}
	
	// Create Singleton
	public static TestLab get(Context c) {
		// If the singleton doesn't exist yet, create it.
		if ( sTestLab == null ) {
			// Don't assume that context 'c' will always be what you expect!  Be
			//    extra safe and call the method to make sure.
			sTestLab = new TestLab(c.getApplicationContext());
		}
		
		return sTestLab;
	}
	
	// Add a single Test to the list
	public void addTest(Test c){
		mTests.add(c);
	}
	
	// Delete a single Test to the list
	public void deleteTest(Test c){
		Log.d(TAG, "Deleting this test: " + c.getTitle() );
		mTests.remove(c);
	}
	
	// Return entire list of tests
	public ArrayList<Test> getTests() {
		return mTests;
	}
	
	// Return only a specific Test
	public Test getTest(UUID id) {
		for(Test c : mTests) {
			if(c.getId().equals(id)) {
				return c;
			}
		}
		return null;
	}
	
	// Save all Tests to private app sandbox
	public boolean saveTests(){
		try {
			mSerializer.saveTests(mTests);
			Log.d(TAG, "Tests saved to file");
			return true;
		} catch (Exception e) {
			Log.d(TAG, "Error saving Tests: ", e);
			return false;
		}
	}

}
