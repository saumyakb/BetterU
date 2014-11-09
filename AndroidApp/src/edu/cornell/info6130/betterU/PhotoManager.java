package edu.cornell.info6130.betterU;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
	

public class PhotoManager {
	private AssetManager _am;
	private String LOG_TAG = "MainActivity";
	
	public PhotoManager(Context context) {
		_am = context.getAssets();
		return;
	}
	
	// returns primiing photo based on foodlist
	// DEVNOTE: decision made NOT to use snack data to trigger priming //, Set<String> SnackTimes 
	Drawable getPrimingPhoto ( Set<String> PreferredFoodList, Set<String> MealTimes ) {
		Drawable randomPhoto = null;

		try {
			 // pick a food from preference list
			 String randomFood = pickFood(PreferredFoodList);
			 // pick a photo in that food category
			 String randomFile = this.pickItem(_am.list(randomFood));
		 
			 if (BuildConfig.DEBUG) {
					 Log.v(LOG_TAG + ".getPrimingPhoto", randomFood + File.separator + randomFile);
			 }
	//		 String foodType = pickFoodType(MealTimes, SnackTimes);
	
			 // open handle to file from asset manager
			 InputStream assetReader = _am.open(randomFood + File.separator + randomFile);
			 // prepare Drawable object
			 randomPhoto = Drawable.createFromStream(assetReader, null);

		} catch (Exception ex) {
			 Log.e(LOG_TAG + ".getPrimingPhoto", ex.toString(), ex);
		}
		 // return photo
        return randomPhoto;
	}
	 
	private String pickPhoto(String Food) {
		 // test pic
		 return "https://70da95bb-a-62cb3a1a-s-sites.googlegroups.com/site/eatingandanxietylab/resources/food-pics/hamburger.jpg";
	}
	 
	//  FoodType
	private String pickFoodType(Set<String> MealTimes, Set<String> SnackTimes) {
		 // for now, make a sorted set
		 Set<String> allTimes = new TreeSet<String>();
		 
		 allTimes.addAll(MealTimes);
		 allTimes.addAll(SnackTimes);
		 
		 Calendar now = Calendar.getInstance();
		 int hour = now.get(Calendar.HOUR_OF_DAY);
		 
		 // TODO: convert to nearest 3 hour increment?
		 if ((hour % 3) != 0) {
		 }
		 
		 // OR? just look ahead to the next nearest time (if not already in an active 3 hour priming window)?
		 
		 String thisHour = String.format("%02d", hour);	 
		 
		 return thisHour; //allTimes.toString();
		 
		 // return null;
	}
	 
	 
	// returns a random item from (Set<String>) of foods
	private String pickFood (Set<String> FoodList) {
		 return pickItem(FoodList.toArray(new String[] {}));
	}
	 
	// returns a randomly selected item from string array
	private String pickItem (String [] ItemList) {
		 String randomItem = null;
		 
		 if ((ItemList != null) && (ItemList.length != 0)) {	 		 
			 randomItem = ItemList[new Random().nextInt(ItemList.length)];
		 }
		 
		 return randomItem;
	} 
}
