package edu.cornell.info6130.betterU;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;



public class setOriginal extends BroadcastReceiver {
		
	@Override
    public void onReceive(Context context, Intent intent  ){	
		
		try{
		Log.d("Set Original", "In set original set Original wallpaper");
    	WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
    	SharedPreferences sharedPrefOrgWall = PreferenceManager.getDefaultSharedPreferences(context);
    	String previouslyEncodedImage = sharedPrefOrgWall.getString("imagePreferance", "");
    	byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
    	Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
	    myWallpaperManager.setBitmap(bitmap);	
	    Toast.makeText(context, "Original Wallpaper set sucessfully" ,Toast.LENGTH_SHORT).show();
    		} catch(Exception e){
    			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
    		} 
    }
}

