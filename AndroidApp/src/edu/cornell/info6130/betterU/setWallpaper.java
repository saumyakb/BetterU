package edu.cornell.info6130.betterU;

import java.util.Set;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class setWallpaper extends BroadcastReceiver {
	
@Override
public void onReceive(Context context, Intent intent){	
    	try{
    	Log.d("Set Wallpaper:", "In schedule time set wallpaper");
    	WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
    	SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	Set<String> foodPlate = appPreferences.getStringSet("pref_food_list_key", null);
		Set<String> mealTimes = appPreferences.getStringSet("pref_meal_list_key", null);
//		Set<String> snackTimes = appPreferences.getStringSet("pref_snack_list_key", null);

		// get next priming photo
		PhotoManager iPhoto = new PhotoManager(context);
		Drawable displayImage = iPhoto.getPrimingPhoto(foodPlate, mealTimes);
	    final Bitmap wallpaperBitmap = ((BitmapDrawable)displayImage).getBitmap();
   		myWallpaperManager.setBitmap(wallpaperBitmap);
   		//setResource(R.drawable.salad);
   		Toast.makeText(context, "Wallpaper set sucessfully" ,Toast.LENGTH_SHORT).show();
    		} catch(Exception e){
    			Log.e("Error:", e.toString(), e);
    		} 
    }
}
