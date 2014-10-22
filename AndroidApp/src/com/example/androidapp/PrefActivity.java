package com.example.androidapp;

// import java.util.List;
import android.app.Activity;
import android.os.Bundle;

// settings activity for preferences
public class PrefActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragment())
                .commit();
    }
}