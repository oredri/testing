package entity;

import java.io.Serializable;

public class Course implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String courseID;
	String name;
	public Course(String courseID, String name) {
		super();
		this.courseID = courseID;
		this.name = name;
	}
	public String getCourseID() {
		return courseID;
	}
	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
