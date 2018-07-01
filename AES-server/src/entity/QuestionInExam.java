package entity;

import java.io.Serializable;

public class QuestionInExam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String examID;
	private String questionID;
	private String teacherUserName;
	private String questionContent;
	private int questionIndexInExam;
	private float points;

	public float getPoints() {
		return points;
	}

	public void setPoints(float points) {
		this.points = points;
	}

	public int getQuestionIndexInExam() {
		return questionIndexInExam;
	}

	public void setQuestionIndexInExam(int questionIndexInExam) {
		this.questionIndexInExam = questionIndexInExam;
	}

	public String getQuestionID() {
		return questionID;
	}

	public void setQuestionID(String questionID) {
		this.questionID = questionID;
	}

	public String getExamID() {
		return examID;
	}

	public void setExamID(String examID) {
		this.examID = examID;
	}

	public String getTeacherUserName() {
		return teacherUserName;
	}

	public void setTeacherUserName(String teacherUserName) {
		this.teacherUserName = teacherUserName;
	}

	public String getQuestionContent() {
		return questionContent;
	}

	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}

}
