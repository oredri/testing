package entity;

import java.util.HashMap;

public class ExamCopy {

	 
/************* Class Variables ***********************/
	private String studentID;
	private int grade;
	private HashMap<String,String> answeredQ = new HashMap<>(); //questionID - Key , Value - the answer of the student 
	private Boolean isApproved;
	private String reasonForChange ;
/*********** Class Constructor ***********************/
	public ExamCopy(String sID,int eGrade , HashMap<String, String> answers ) {
		studentID = sID;
		grade = eGrade;
		answeredQ = answers ;
		isApproved = false ; //false as default
		reasonForChange = "" ; //empty as default
	}

/********* ClassMethods ****************************/
	
	public String getStudentID() {
		return this.studentID;
	}
	public void setStudentID(String sID) {
		studentID = sID ; 
	}
	
	public int getGrade() {
		return this.grade;
	}
	public void setGrade(int eGrade) {
		grade = eGrade ; 
	}
	public HashMap<String, String> getAnswersQ() {
		return this.answeredQ;
	}
	public void setAnswersQ(HashMap<String, String> answers) {
		answeredQ = answers ; 
	}
	public Boolean getApproval() {
		return this.isApproved;
	}
	public void setApproval(Boolean approven) {
		isApproved = approven ; 
	}
	public String getReason() {
		return this.reasonForChange;
	}
	public void setReason(String reason) {
		reasonForChange = reason ; 
	}
		
}
