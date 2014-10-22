package com.example.androidapp;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    
	Bitmap bitmap;
	int lastImageRef;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // load/set default preferences
//        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        
        Button buttonSetWallpaper = (Button) findViewById(R.id.set);
        ImageView imagePreview = (ImageView) findViewById(R.id.preview);
        
        buttonSetWallpaper.setOnClickListener(new OnClickListener(){
        
        	 public void onClick(View v){
        		 WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        		 try {
        			 myWallpaperManager.setResource(R.drawable.salad);
        			 Toast.makeText(getApplicationContext(), "Wallpaper set sucessfully" ,Toast.LENGTH_SHORT).show();
        		 }catch(Exception e){
        		 }
        	 }
        	
        });
    }
	
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
		// do something with preferences on resume here...
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        
        switch(item.getItemId()) {
        	case R.id.action_settings:
	        	//open settings;
	        	Intent intent = new Intent(this, PrefActivity.class);
	        	startActivity(intent);
	
	        	break;
        	// load survey page URL in a browser window
	        case R.id.action_survey:
	        	// set user expectations
	        	Toast.makeText(this, "Opening Survey...", Toast.LENGTH_SHORT).show();
	        	// get path to survey
	        	String surveyPath = getResources().getString(R.string.uri_survey);

	        	try {
	        		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(surveyPath));
	        		startActivity(browserIntent);
	        	} 
	        	catch (Exception ew) {
	        		// surveyPath
	        		Toast.makeText(this, "Failed to load survey:\r\n" + surveyPath, Toast.LENGTH_SHORT).show();
	        		// Toast.makeText(this, ew.toString(), Toast.LENGTH_SHORT).show();
	        	}
	        	/*  // if you want then web browser open in your activity then do like this:
					WebView webView = (WebView) findViewById(R.id.webView1);
					WebSettings settings = webview.getSettings();
					settings.setJavaScriptEnabled(true);
					webView.loadUrl(URL);
					if you want to use zoom control in your browser then you can use:
					
					settings.setSupportZoom(true);
					settings.setBuiltInZoomControls(true);
	        	 */
	        	break;
	        default:            
	            return super.onOptionsItemSelected(item);
        }        
        
        return true;
    }
}
