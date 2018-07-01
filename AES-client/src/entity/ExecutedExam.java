package entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class ExecutedExam implements Serializable,Comparable {
	private static final long serialVersionUID = 1L;
	private String executedExamID;
	private int numOfStudentStarted;
	private int numOfStudentFinished;
	private int numOfStudentDidntFinished;
	private float average;
	private float median;
	private String teacherName;
	private String exam_id;
	private int[] gradeRang=new int[10];
	private String status;
	private String date;
	private String SolutionTime;
	private Time actuallySolutionTime;
	private Date startDate;
	private Exam exam;
	
	
	public ExecutedExam(String executedExamID, int numOfStudentStarted, int numOfStudentFinished,
			int numOfStudentDidntFinished, float average, float median, String teacherName, String exam_id,
			int range0to9, int range10to19, int range20to29, int range30to39, int range40to49, int range50to59,String status,
			int range60to69, int range70to79, int range80to89, int range90to100,Time actuallySolutionTime,Date startDate) {
		super();
		this.executedExamID = executedExamID;
		this.numOfStudentStarted = numOfStudentStarted;
		this.numOfStudentFinished = numOfStudentFinished;
		this.numOfStudentDidntFinished = numOfStudentDidntFinished;
		this.average = average;
		this.median = median;
		this.teacherName = teacherName;
		this.exam_id = exam_id;
		this.gradeRang[0] = range0to9;
		this.gradeRang[1] = range10to19;
		this.gradeRang[2] = range20to29;
		this.gradeRang[3] = range30to39;
		this.gradeRang[4]= range40to49;
		this.gradeRang[5] = range50to59;
		this.gradeRang[6] =range60to69;
		this.gradeRang[7] =range70to79;
		this.gradeRang[8] =range80to89;
		this.gradeRang[9] =range90to100;
		this.status=status;
		this.setActuallySolutionTime(actuallySolutionTime);
		this.setStartDate(startDate);
	}

	public int[] getGradeRang() {
		return gradeRang;
	}

	public void setGradeRang(int[] gradeRang) {
		this.gradeRang = gradeRang;
	}

	public ExecutedExam() {
		exam=new Exam();
	}
	public String getExecutedExamID() {
		return executedExamID;
	}
	public void setExecutedExamID(String executedExamID) {
		this.executedExamID = executedExamID;
	}
	public int getNumOfStudentStarted() {
		return numOfStudentStarted;
	}
	public void setNumOfStudentStarted(int numOfStudentStarted) {
		this.numOfStudentStarted = numOfStudentStarted;
	}
	public int getNumOfStudentFinished() {
		return numOfStudentFinished;
	}
	public void setNumOfStudentFinished(int numOfStudentFinished) {
		this.numOfStudentFinished = numOfStudentFinished;
	}
	public int getNumOfStudentDidntFinished() {
		return numOfStudentDidntFinished;
	}
	public void setNumOfStudentDidntFinished(int numOfStudentDidntFinished) {
		this.numOfStudentDidntFinished = numOfStudentDidntFinished;
	}
	public float getAverage() {
		return average;
	}
	public void setAverage(float average) {
		this.average = average;
	}
	public float getMedian() {
		return median;
	}
	public void setMedian(float median) {
		this.median = median;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public String getExam_id() {
		return exam_id;
	}
	public void setExam_id(String exam_id) {
		this.exam_id = exam_id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public int compareTo(Object o) {// compare between 2 executedExam by they grade
		ExecutedExam temp=(ExecutedExam)o;
		if (this.average==temp.average) return 0;
		if (this.average>temp.average) return 1;
		return -1;
	}
	public String getSolutionTime() {
		return SolutionTime;
	}
	public void setSolutionTime(String solutionTime) {
		SolutionTime = solutionTime;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public Time getActuallySolutionTime() {
		return actuallySolutionTime;
	}

	public void setActuallySolutionTime(Time actuallySolutionTime) {
		this.actuallySolutionTime = actuallySolutionTime;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Exam getExam() {
		return exam;
	}

	public void setExam(Exam exam) {
		this.exam = exam;
	}
	
	
}
