package edu.cornell.info6130.betterU;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DatabaseLog {
	private long 	id;
	private String 	imageName;
	private Date 	primingTime;
	private String 	note;

	public DatabaseLog() {
		// unsaved state
		this.id = -1;
		this.imageName = "";
		this.primingTime = null;
		this.note = "";
	}	
	
	public DatabaseLog(String ImageName, Date PrimingTime, String Note) {
		// unsaved state
		this.id = -1;
		this.imageName = ImageName;
		this.primingTime = PrimingTime;
		this.note = Note;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getImageName() {
		return imageName;
	}
	
	public void setImageName(String name) {
		this.imageName = name;
	}
	
	public Date getPrimingTime() {
		return primingTime;
	}
	
	public void setPrimingTime(Date time) {
		this.primingTime = time;
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	// intended for debug purposes only
	@Override
	public String toString() {
		String output = "";

		// build output
		output = "ID=" + String.valueOf(this.id);
		output += ";ImageShown=" + this.imageName;
		SimpleDateFormat sdf = new SimpleDateFormat(DatabaseHelper.DATE_TIME_FORMAT);
		output += "; PrimingTime=" + sdf.format(this.primingTime);
		output += "; Note=" + this.note;
		// new line characters
		output += System.getProperty("line.separator");
		
		return output;
	}
	
}
