package entity;

import java.io.Serializable;


public class ExamDetailsMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String examID ;
	private final String examGrade;
	private final String examDate;
	private final String examCourse;
	private final String excecutedExamID ;
	 
	public String getExcecutedExamID() {
		return excecutedExamID;
	}

	public ExamDetailsMessage(String eID,String eGrade,String eDate ,String excecuted) {
		examID = eID;
		examGrade = eGrade;
		examDate = eDate;
		examCourse = eID.substring(2, 4);
		excecutedExamID = excecuted ; 
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getExamID() {
		return examID;
	}

	public String getExamGrade() {
		return examGrade;
	}

	public String getExamDate() {
		return examDate;
	}

	public String getExamCourse() {
		return examCourse;
	}

	


}
