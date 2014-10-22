package com.example.androidapp;

import android.support.v7.app.ActionBarActivity;
import android.view.View.OnClickListener;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
        
        Button buttonSetWallpaper = (Button) findViewById(R.id.set);
        Button buttonResetWallpaper = (Button) findViewById(R.id.reset);
        ImageView imagePreview = (ImageView) findViewById(R.id.preview);
        
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
		final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
		final Bitmap bitmap = ((BitmapDrawable)wallpaperDrawable).getBitmap();
        
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
        
        buttonResetWallpaper.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
       		 
       		 try {
       			 wallpaperManager.setBitmap(bitmap);
       			 Toast.makeText(getApplicationContext(), "Original Wallpaper set sucessfully" ,Toast.LENGTH_SHORT).show();
       		 }catch(Exception e){
       		 }
       	 }
       	
       });
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
