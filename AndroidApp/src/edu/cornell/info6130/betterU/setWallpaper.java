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
import android.widget.Toast;

public class setWallpaper extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent){	
		try{
		    	Log.d("Set Wallpaper:", "In schedule time set wallpaper");
		    	WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
		    	
		    	Bundle bundle=intent.getExtras();
		    	String[] foodPlateStrArr=bundle.getStringArray("foodPlateBundle");
		    	String[] mealTimesStrArr=bundle.getStringArray("mealTimesBundle");
		    	//Log.d("foodPlateStrArr BR",foodPlateStrArr[0]);
		    	//Log.d("mealTimesStrArr BR",mealTimesStrArr[0]);
		    	Set<String> foodPlate = new HashSet<String> (Arrays.asList(foodPlateStrArr));
		    	Set<String> mealTimes = new HashSet<String> (Arrays.asList(mealTimesStrArr));
		    	
		   // 	SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		  //  	Set<String> foodPlate = appPreferences.getStringSet("pref_food_list_key", null);
		//		Set<String> mealTimes = appPreferences.getStringSet("pref_meal_list_key", null);
		//		Set<String> snackTimes = appPreferences.getStringSet("pref_snack_list_key", null);
		
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
				    Toast.makeText(context, "Original Wallpaper set sucessfully" ,Toast.LENGTH_SHORT).show();
		
				}
				else {
				    final Bitmap wallpaperBitmap = ((BitmapDrawable)displayImage).getBitmap();
			   		myWallpaperManager.setBitmap(wallpaperBitmap);
			   		//setResource(R.drawable.salad);
			   		Toast.makeText(context, "Wallpaper set sucessfully" ,Toast.LENGTH_SHORT).show();
				}
		} catch(Exception e){
			Log.e("Error:", e.toString(), e);
		} 
	}
}
