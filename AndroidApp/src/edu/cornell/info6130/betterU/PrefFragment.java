package edu.cornell.info6130.betterU;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import edu.cornell.info6130.betterU.R;
 
public class PrefFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preference);
	} 
}