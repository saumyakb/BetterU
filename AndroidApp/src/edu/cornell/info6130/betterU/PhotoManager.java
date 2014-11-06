package edu.cornell.info6130.betterU;

import java.util.Calendar;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import android.net.Uri;
	

public class PhotoManager {
	 Uri getPrimingPhoto ( Set<String> PreferredFoodList, Set<String> MealTimes, Set<String> SnackTimes ) {
		 Uri randomPhoto = null;

		 // get random food from preference list
		 String randomFood = pickFood(PreferredFoodList);
		 
//		 String foodType = pickFoodType(MealTimes, SnackTimes);
		 
		 // get photo path, and convert to Uri
		 randomPhoto = Uri.parse(pickPhoto(randomFood));

		 // return Uri
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
