package entity;

import java.io.Serializable;
import java.sql.Time;

public class RequestForChangingTimeAllocated implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String RequestID;
	private String teacherName;
	private String reason;
	private String menagerApprove; 
	private String IDexecutedExam;
	private String timeAdded;
	
	
	public RequestForChangingTimeAllocated() {
		menagerApprove="waiting";
	}
	public RequestForChangingTimeAllocated(String rID,String tName,String reason,String approved,String examID,Time time) {
		RequestID=rID;
		teacherName=tName;
		menagerApprove=approved;
		this.reason=reason;
		timeAdded=time.toString();
		IDexecutedExam=examID;
	}
	public String getRequestID() { 
		return RequestID;
	}
	public void setRequestID(String requestID) {
		RequestID = requestID;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public String getMenagerApprove() {
		return menagerApprove;
	}
	public void setMenagerApprove(String menagerApprove) {
		this.menagerApprove = menagerApprove;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getTimeAdded() {
		return timeAdded;
	}
	public void setTimeAdded(String timeAdded) {
		this.timeAdded = timeAdded;
	}
	public String getIDexecutedExam(){
		return IDexecutedExam;
	}
	public void  setIDexecutedExam(String examID) {
		IDexecutedExam=examID;
	}
}
