package entity;

import java.io.Serializable;

public class StudentPerformExam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String date = " ";
	private String time = " ";
	private Boolean finished = false;
	private Float grade;
	private String excecutedExamID = " ";
	private String userName = " ";
	private String isApproved = " ";
	private String reasonForChangeGrade = " ";
	private String userId;
	private String userFullname;

	public StudentPerformExam(String dateDB, String timeDB, String finishedDB, String excecutedExamIDDB,
			String userNameDB, String gradeDB, String isApprovedDB, String reasonForChangeGradeDB, String id,
			String name) {
		super();
		date = dateDB;
		time = timeDB;
		if (finishedDB == "yes") {
			finished = true;
		}
		grade = Float.parseFloat(gradeDB);
		excecutedExamID = excecutedExamIDDB;
		userName = userNameDB;
		setIsApproved(isApprovedDB);
		reasonForChangeGrade = reasonForChangeGradeDB;
		setUserId(id);
		setUserFullname(name);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Boolean getFinished() {
		return finished;
	}

	public void setFinished(Boolean finished) {
		this.finished = finished;
	}

	public Float getGrade() {
		return grade;
	}

	public void setGrade(Float grade) {
		this.grade = grade;
	}

	public String getExcecutedExamID() {
		return excecutedExamID;
	}

	public void setExcecutedExamID(String excecutedExamID) {
		this.excecutedExamID = excecutedExamID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getReasonForChangeGrade() {
		return reasonForChangeGrade;
	}

	public void setReasonForChangeGrade(String reasonForChangeGrade) {
		this.reasonForChangeGrade = reasonForChangeGrade;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserFullname() {
		return userFullname;
	}

	public void setUserFullname(String userFullname) {
		this.userFullname = userFullname;
	}

	public String getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(String isApproved) {
		this.isApproved = isApproved;
	}

}
