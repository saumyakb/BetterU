package edu.cornell.info6130.betterU;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
	

public class PhotoManager {
	
	private final String LOG_TAG = "MainActivity";	
	private final Locale _locale;
	// breakfast, lunch, dinner
	private final Meal[] _meals = {new Meal("B", 02, 11), new Meal("L", 11, 14), new Meal("D", 14, 02)};
	
	private AssetManager _am;
	
	/**
	 * Requires context object to access to application's AssetsManager. 
	 * @param  Context			 	Context.
	 * @return 						N/A
	 */
	public PhotoManager(Context context) {
		_am = context.getAssets();
		_locale = context.getResources().getConfiguration().locale;
		
		return;
	}
	
	/**
	 * Returns a Drawable object that can then be used on a WallPaper or ImageView. 
	 * If the PreferredFoodList is empty, will use all food categories. 
	 * <p>
	 * PreferredFoodList names MUST align with the asset folders. 
	 * <p>
	 * Support for Set<String> SnackTimes parameter has been deprecated
	 *
	 * @param  PreferredFoodList 	user selected food categories.
	 * @param  MealTimes 			user specified, typical eating periods
	 * @return 						the priming image to display
	 * @see         				Drawable
	 */
	public Drawable getPrimingPhoto (Set<String> PreferredFoodList, Set<String> MealTimes) {
		Drawable randomPhoto = null;

		try {
			Meal activeMeal = pickMeal(MealTimes);

			// if there is no activeMeal 'window', we don't prime
			if (activeMeal != null) {
				// TODO: use activeMeal.getKey and verify string is in file name
						// ... or build a pool of all allowed foodCats that have permissible mealTypes
							// and if none found, make it a FFA (i.e., just pick one regardless)?
				//         by using the file names B L D (if instr [all filenames are otherwise lowercase])
				//				if foodCat doesn't contain, then need to repeat
				//					how to handle if allowed foodCats do NOT contain meal time? (i.e., breakfast, but only fish selected...)
				
				// pick a food from preference list
				String randomFood = pickFood(PreferredFoodList);
				// pick a photo in that food category
				String randomFile = pickPhoto(randomFood, activeMeal);
			 
				// open handle to file from asset manager
				InputStream assetReader = _am.open(randomFood + File.separator + randomFile);
				// prepare photo for use
				randomPhoto = Drawable.createFromStream(assetReader, null);
			}

		} catch (Exception ex) {
			if (BuildConfig.DEBUG) {
				Log.e(LOG_TAG + ".getPrimingPhoto", ex.toString(), ex);
			}
		}
		 // return photo
        return randomPhoto;
	}

	// returns a random item from (Set<String>) of foods
	private String pickFood (Set<String> FoodList) {
		boolean exists = true;
		String food = null;
		
		try {
			if (!FoodList.isEmpty()) {
				// pick from list
				food = pickItem(FoodList.toArray(new String[] {}));
				// verify it exists as an asset
				try {
					exists = Arrays.asList(_am.list("")).contains(food);
				} catch (IOException ie) {
					exists = false;
					if (BuildConfig.DEBUG) {
						Log.d(LOG_TAG + ".pickFood", "Invalid Asset folder: " + food + System.getProperty("line.separator") + ie.toString(), ie);
					}
				}
			}
			// fail-safe for no valid food category...
			if ( (FoodList.isEmpty()) || (!exists) )  {
				// if food list is empty, just pick a random category from assets
				food = pickItem(_am.list(""));
			}
			
		} catch (Exception ex) {
			if (BuildConfig.DEBUG) {
				Log.e(LOG_TAG + ".pickFood", ex.toString(), ex);
			}
		}
		 return food;
	}
	
	private String pickPhoto(String food) {
		String filename = null;
		
		try {
			filename = this.pickItem(_am.list(food));
		} catch (Exception ex) {
			if (BuildConfig.DEBUG) {
				Log.e(LOG_TAG + ".pickPhoto", ex.toString(), ex);
			}
		}
		
		// debug output
		if (BuildConfig.DEBUG) {
			Log.v(LOG_TAG + ".getPrimingPhoto", food + File.separator + filename);
		}
		
		return filename;
	}

	private String pickPhoto(String food, Meal activeMeal) {
		String filename = null;
		
		try {
			// cache local list of photos
			String[] foodList = _am.list(food);
			
			while ( ((filename == null) || filename.isEmpty()) && (foodList.length != 0) ) {
				// get next random photo
				filename = this.pickItem(foodList);
				// verify it is meal compatible
				if (!filename.contains(activeMeal.getKey())) {
					// debug output
					if (BuildConfig.DEBUG) {
						Log.v(LOG_TAG + ".pickPhoto", "Discarding food photo, meal type conflict: " + filename);
					}

					// if not, discard from list and pick again
					List<String> tempList = new ArrayList<String>(Arrays.asList(foodList));
					tempList.remove(filename);
					foodList = tempList.toArray(new String[0]);
					filename = null;					
				}
			}
			
			// if meal specific food is not found, just pick a random photo from this food group as fallback
			if (foodList.length == 0) {
				filename = pickPhoto(food);
			}
			
		} catch (Exception ex) {
			if (BuildConfig.DEBUG) {
				Log.e(LOG_TAG + ".pickPhoto", ex.toString(), ex);
			}
		}
		
		// debug output
		if (BuildConfig.DEBUG) {
			Log.v(LOG_TAG + ".pickPhoto", food + File.separator + filename);
		}
		
		return filename;
	}
	
	//  returns active Meal
	private Meal pickMeal(Set<String> MealTimes) {
		boolean allowMeal = true;
		// return value; default to dinner
		Meal activeMeal = _meals[2];
		// get currentHour in military time, and currentMinute
		Calendar now = Calendar.getInstance();
		int currentHour = now.get(Calendar.HOUR_OF_DAY);
		
		for (Meal meal: _meals) {
			if ( (meal.getStartHour() <= currentHour) && (meal.getEndHour() >= currentHour) ) {
			 	activeMeal = meal;
				break;
			}	
		}
		
		// if user has specified their meal times, only prime during one of those intervals
		if (!MealTimes.isEmpty()) {
			// convert to a sorted type of set
			Set<String> allowedTimes = new TreeSet<String>();
			// populate new set
			allowedTimes.addAll(MealTimes);
			// convert to military hour, in string format
			String thisHour = String.format(_locale, "%02d", currentHour);
			
			if (BuildConfig.DEBUG) {
				Log.v(LOG_TAG + ".getMeal", "currentHour: " + thisHour);
			}
			
			// assume it is not allowed
			allowMeal = false;
			
			Iterator<String> it = allowedTimes.iterator();
			while(it.hasNext() && (!allowMeal)) {
				int mealStart = Integer.valueOf(it.next());
				
				switch (currentHour) {
				case 0:
				case 1:
				case 23:
					// special case needed becase times no longer start at 00
					if (mealStart == 23)
						allowMeal = true;
					break;
				default:
					if ( (currentHour >= mealStart) && (currentHour < (mealStart + 3)) )
						allowMeal = true;
					break;
				}				
			}
		}
		
		if (!allowMeal)
			activeMeal = null;
		
		if (BuildConfig.DEBUG) {
			if (allowMeal)
				Log.v(LOG_TAG + ".getMeal", activeMeal.getKey());
			else
				Log.v(LOG_TAG + ".getMeal", "Not an allowed meal time.");
		}
		
		return activeMeal;
	}
	 
	// returns a randomly selected item from string array
	private String pickItem (String [] ItemList) {
		 String randomItem = null;

		 try {
			 if ((ItemList != null) && (ItemList.length != 0)) {	 		 
				 randomItem = ItemList[new Random().nextInt(ItemList.length)];
			 }
		 } catch (Exception ex) {
			 if (BuildConfig.DEBUG) {
				 Log.e(LOG_TAG + ".pickFood", ex.toString(), ex);
			 }
		 }
		 
		 return randomItem;
	} 
}
