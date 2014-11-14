package edu.cornell.info6130.betterU;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

// original code base: http://7lanterns.wordpress.com/2013/02/23/parcelable-in-android-for-data-passing-between-activities/

public class UserProfile implements Parcelable {
	 
    public String _participantID;
    public Drawable _originalWallpaper;
 
    private static final long serialVersionUID = 1L;
 
    /**
     * constructor to rebuild object from the Parcel
     * @param source, a parcel data
     */
    public UserProfile(Parcel source) {
        this._participantID = source.readString();
        this._originalWallpaper = new BitmapDrawable(
                ( (Bitmap) source.readValue(Bitmap.class.getClassLoader()) )
        );
    }
 
    /**
     * initialization of user profile
     * @param username
     * @param age
     * @param profilePicture
     */
    public UserProfile(String participantID, Drawable profilePicture) {
        this._participantID = participantID;
        this._originalWallpaper = profilePicture;
    }
 
    public static final Parcelable.Creator<UserProfile> CREATOR = new Parcelable.Creator<UserProfile>() {
        public UserProfile createFromParcel(Parcel source) {
            return new UserProfile(source);
        }
 
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };
 
    /**
     * describe the kind of special object
     */
    @Override
    public int describeContents() {
        // hashCode() of this class
        return hashCode();
    }
 
    /**
     * write this object in to a Parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_participantID);
        // hey, BitmapDrawable class implements Parcelable.
        // so we can write the object directly
        dest.writeValue(((BitmapDrawable)_originalWallpaper).getBitmap());
    }
}