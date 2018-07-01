package entity;

import java.io.Serializable;

public class Exam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String e_id;
	private String solutionTime;
	private String remarksForTeacher;
	private String remarksForStudent;
	private String type;
	private String teacherUserName;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemarksForStudent() {
		return remarksForStudent;
	}

	public void setRemarksForStudent(String remarksForStudent) {
		this.remarksForStudent = remarksForStudent;
	}

	public String getRemarksForTeacher() {
		return remarksForTeacher;
	}

	public void setRemarksForTeacher(String remarksForTeacher) {
		this.remarksForTeacher = remarksForTeacher;
	}

	public String getSolutionTime() {
		return solutionTime;
	}

	public void setSolutionTime(String solutionTime) {
		this.solutionTime = solutionTime;
	}

	public String getE_id() {
		return e_id;
	}

	public void setE_id(String e_id) {
		this.e_id = e_id;
	}

	public String getTeacherUserName() {
		return teacherUserName;
	}

	public void setTeacherUserName(String teacherUserName) {
		this.teacherUserName = teacherUserName;
	}
}
