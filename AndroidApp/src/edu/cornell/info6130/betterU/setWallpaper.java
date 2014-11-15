package edu.cornell.info6130.betterU;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

public class setWallpaper extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent){	
		try{
		    	Log.d("Set Wallpaper:", "In schedule time set wallpaper");
		    	WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
		    	
		    	Bundle bundle=intent.getExtras();
		    	String[] foodPlateStrArr=bundle.getStringArray("foodPlateBundle");
		    	String[] mealTimesStrArr=bundle.getStringArray("mealTimesBundle");
		    	Set<String> foodPlate = new HashSet<String> (Arrays.asList(foodPlateStrArr));
		    	Set<String> mealTimes = new HashSet<String> (Arrays.asList(mealTimesStrArr));
		  
		    	// get next priming photo
				PhotoManager iPhoto = new PhotoManager(context.getApplicationContext());
				Drawable displayImage = iPhoto.getPrimingPhoto(foodPlate, mealTimes);
				
				if (displayImage == null) {
					Log.d("Set Original", "In set original set Original wallpaper");
			    	//WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
			    	SharedPreferences sharedPrefOrgWall = PreferenceManager.getDefaultSharedPreferences(context);
			    	String previouslyEncodedImage = sharedPrefOrgWall.getString("imagePreferance", "");
			    	byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
			    	Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
				    myWallpaperManager.setBitmap(bitmap);			
				}
				else {
				    final Bitmap wallpaperBitmap = ((BitmapDrawable)displayImage).getBitmap();
			   		myWallpaperManager.setBitmap(wallpaperBitmap);
				}
		} catch(Exception e){
			Log.e("Error:", e.toString(), e);
		} 
	}
}
