package control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import entity.Course;
import entity.Exam;
import entity.ExamDetailsMessage;
import entity.ExecutedExam;
import entity.Question;
import entity.QuestionInExam;
import entity.RequestForChangingTimeAllocated;
import entity.StudentPerformExam;
import entity.TeachingProfessionals;
import entity.User;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class MysqlConnection {
	static Connection conn;
	private Statement stmt;
	public String user;
	private String dbUser;
	private String dbPass;
	ArrayList<TeachingProfessionals> subjectList = null;

	/************************** Class Constructor ********************************/
	/**
	 *  MysqlConnection(String user, String password)
	 *  Arguments:String user, String password
	 * The method set the db name and password
	 * 
	 * @author Orit Aharon
	 */
	public MysqlConnection(String user, String password) {

		dbUser = user;
		dbPass = password;
	}

	/**************************** Class Methods **********************************/
	/**
	 *  runDB()
	 *  Arguments:NA
	 * The method create connection to the DB
	 * 
	 * @author Lior Hammer
	 */
	public void runDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			/* handle the error */}
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/shitot", dbUser, dbPass);
			System.out.println("SQL connection succeed");
		} catch (SQLException ex) {/* handle any errors */
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	/**
	 * setStatusToAddingTimeRequest(Object RequestID, String newStatus)
	 *  Arguments:Object RequestID, String newStatus
	 * The method set  in the DB the status of request 'RequestID' to 'newStatus'
	 * 
	 * @author Lior Hammer
	 */
	public void setStatusToAddingTimeRequest(Object RequestID, String newStatus) {
		try {
			String reqID = (String) RequestID;
			stmt = conn.createStatement();
			stmt.executeUpdate("Update shitot.requestforchangingtimeallocated SET isApproved='" + newStatus
					+ "' where requestID='" + reqID + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * createQuestion(Object subject, Object question, Object courses)
	 *  Arguments:Object subject, Object question, Object courses
	 * The method set  new question in the DB 
	 * The method return true if the question where added,otherwise return false
	 * 
	 * @author Aviv Mahulya
	 */
	public synchronized Boolean createQuestion(Object subject, Object question, Object courses) {
		String fullQuestionNumber;
		int questionNumber;
		int first = 0;
		int last = 0;
		int flagFirst = 0;
		Question q = (Question) question;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT (question_id) FROM questions" + " WHERE question_id like " + "\"" + subject + "%\"" + ";");
			if (!rs.isBeforeFirst()) {/*If there is question of the subject in stock*/
				first = 0;
			} else {
				while (rs.next()) {
					first = Integer.parseInt(rs.getString(1).substring(2, 5));
					if (rs.next()) {
						last = Integer.parseInt(rs.getString(1).substring(2, 5));
						rs.previous();
					}
					if (first != 1 && flagFirst == 0) {
						first = 0;
						break;
					}
					flagFirst = 1;
					if (last - first > 1) {
						break;
					}

				}
			}
			questionNumber = first + 1;
			fullQuestionNumber = (String) subject;
			fullQuestionNumber = fullQuestionNumber + "" + String.format("%03d", questionNumber);
			stmt.executeUpdate("INSERT INTO shitot.questions VALUES(\"" + fullQuestionNumber.trim() + "\",\""
					+ q.getTeacherName().trim() + "\",\"" + q.getQuestionContent() + "\",\"" + q.getAnswer1() + "\",\""
					+ q.getAnswer2() + "\",\"" + q.getAnswer3() + "\",\"" + q.getAnswer4() + "\",\""
					+ String.valueOf(q.getCorrectAnswer()) + "\");");

			for (String s : (ArrayList<String>) courses) {
				String[] courseSubString = s.split(" ");
				stmt.executeUpdate("INSERT INTO shitot.questionincourse VALUES(\"" + fullQuestionNumber.trim() + "\",\""
						+ courseSubString[0].trim() + "\");");

			}
			rs.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Boolean updateQuestion(Object question)
	 *  Arguments:Oquestion
	 * The method update the relevant question in the DB
	 * The method return true if the question where updated,otherwise return false
	 * 
	 * @author Tom Zarhin
	 */
	public synchronized Boolean updateQuestion(Object question) {

		Question q = (Question) question;

		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("select distinct questioninexam.question_ID , executedexam.executedExamID from "
							+ "shitot.questioninexam , shitot.executedexam where questioninexam.e_id ="
							+ " executedexam.exam_id and executedexam.status = 'open' AND executedexam.numOfStudentStarted>0");

			while (rs.next()) {
				if (q.getId().equals(rs.getString(1))) {
					rs.close();
					return false;
				}
			}
			rs.close();
			// query update on DB the correct answer of question that have the given
			// questionID from client
			stmt.executeUpdate("UPDATE questions SET question_text=\"" + q.getQuestionContent() + "\", answer1=\""
					+ q.getAnswer1() + "\", answer2 = \"" + q.getAnswer2() + "\", answer3= \"" + q.getAnswer3() + "\","
					+ "answer4= \"" + q.getAnswer4() + "\", correct_answer= \"" + q.getCorrectAnswer()
					+ "\" WHERE Question_id=\"" + q.getId() + "\";");

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Boolean deleteQuestion(Object question)
	 *  Arguments:Oquestion
	 * The method delete question from the DB if there is such question and 
	 * the question didnt appear in any running test 
	 * The method return true if the question have been deleted,otherwise return false
	 * 
	 * @author Tom Zarhin
	 */
	public Boolean deleteQuestion(Object question) throws SQLException {
		try {
			// Statement stmt;
			stmt = conn.createStatement();
			Question q = (Question) question;
			ResultSet rs = stmt
					.executeQuery("select distinct questioninexam.question_ID , executedexam.executedExamID from "
							+ "shitot.questioninexam , shitot.executedexam where questioninexam.e_id ="
							+ " executedexam.exam_id and executedexam.status = 'open' AND executedexam.numOfStudentStarted>0 ");

			while (rs.next()) {
				if (q.getId().equals(rs.getString(1))) {
					rs.close();
					return false;
				}
			}
			rs.close();
			// questionID from client
			stmt.executeUpdate("DELETE FROM questions WHERE question_id=\"" + q.getId() + "\";");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * setExecutedExamLocked(Object executedExamID)
	 *  Arguments:executedExamID
	 * The method lock a "running" exam on the DB 
	 * by setting status close in the relevant row (by  executedExamID)
	 * The method return true if the query success ,otherwise return false
	 * 
	 * @author Aviv Mahulya
	 */
	public synchronized boolean setExecutedExamLocked(Object executedExamID) {
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("Update shitot.executedexam SET status=\"close\" where executedExamID=\""
					+ executedExamID.toString() + "\";");
			return (true);
		} catch (SQLException e) {
			e.printStackTrace();
			return (false);
		}
	}
	/**
	 * checkUserDetails(Object userID, Object userPass)
	 *  Arguments:Object userID, Object userPass
	 * The method check if the user name and password are
	 * match to some user in the DB 
	 * The method return User object if there is such user,otherwise return null
	 * 
	 * @author Lior Hammer 
	 */
	public Object[] checkUserDetails(Object userID, Object userPass) {
		Object[] objectToClient = new Object[2];
		try {
			stmt = conn.createStatement();
			// query check existent of such details base on user name and password
			ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username=\"" + userID + "\" AND password=\""
					+ userPass + "\"" + "AND status = \"unconnected\";");
			// if there is no user with given details
			rs.next();
			if (!rs.first()) {
				ResultSet rs1 = stmt.executeQuery("SELECT * FROM users WHERE username=\"" + userID + "\" AND password=\""
						+ userPass + "\"" + "AND status = \"connected\";");
				objectToClient[0] = null;
				rs1.next();
					if(rs1.first())
						objectToClient[1] = "connected";
					
					else
						objectToClient[1] = "wrong";
						
					return objectToClient;
			}
			// if the user is existing
			User newUser = new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),"connected",
					rs.getString(6));
			objectToClient[0] = newUser;
			performLogin(userID);
			return objectToClient;
			// in the end userDetails will have the UserID,userName,role
		} catch (SQLException e) {
			e.printStackTrace();
			return objectToClient;
		}
	}
	/**
	 * performLogout(Object userName)
	 *  Arguments:userName
	 * The method set status='unconnected' on the relevant row on users Table on the DB 
	 * The method return doesn't return anything
	 * @author Lior Hammer 
	 */
	public void performLogout(Object userName) {
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE users " + "SET status=\"unconnected\" WHERE username=\"" + userName + "\";");
			System.out.println("user set as unconnected");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * performLogin(Object userName)
	 *  Arguments:userName
	 * The method set status='connected' on the relevant row on users Table on the DB 
	* The method return doesn't return anything
	 * @author Lior Hammer 
	 */
	public void performLogin(Object userName) {
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE users " + "SET status=\"connected\" WHERE username=\"" + userName + "\";");
			System.out.println("user set as connected");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * getPrefExamDetails(String userName)
	 *  Arguments:String userName
	 * The method get executed exam data from the db (relevat to the student see his grades window)
	* The method return relevant data or null if the query faield
	 * @author Lior Hammer 
	 */
	public ArrayList<ExamDetailsMessage> getPrefExamDetails(String userName) {
		ArrayList<ExamDetailsMessage> detailsList = new ArrayList<ExamDetailsMessage>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT  E.exam_id  , stdE.grade  , stdE.date , E.executedExamID "
					+ " FROM shitot.executedexam  E , shitot.studentperformedexam  stdE "
					+ "WHERE E.executedExamID = stdE.executedexam_id AND stdE.student_UserName =\"" + userName
					+ "\" AND E.status = 'checked' AND stdE.isApproved='approved' ; ");

			while (rs.next()) {
				detailsList.add(new ExamDetailsMessage(rs.getString(1), "" + rs.getString(2), rs.getDate(3).toString(),
						rs.getString(4)));
			}
			rs.close();
		} catch (NullPointerException e) {
			System.out.println("No data from server");
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("sending exams details");
		return detailsList;
	}
	/**
	 * getSubjectList(Object teacherUserName)
	 *  Arguments:Object teacherUserName
	 * The method return from the DB the relevant subjects (by the teacher user name) 
	 * @author Or Edri
	 */
	public ArrayList<TeachingProfessionals> getSubjectList(Object teacherUserName) {
			
			subjectList = new ArrayList<TeachingProfessionals>();
			// Statement stmt;
			TeachingProfessionals teachingprofessions;
			try {
				stmt = conn.createStatement();
				ResultSet rs = null;
				if (teacherUserName == null) {
					rs = stmt.executeQuery("SELECT DISTINCT tp.tp_ID,tp.name FROM teachingprofessionals tp;");
				} else {
					rs = stmt.executeQuery(
							"SELECT DISTINCT tp.tp_ID,tp.name FROM teachingprofessionals tp,teacherincourse tc,courses c WHERE "
									+ "tp.tp_ID=c.tp_ID AND c.courseID=tc.courseID AND tc.UserNameTeacher=\""
									+ teacherUserName.toString() + "\";");
				}
				while (rs.next()) {
					teachingprofessions = new TeachingProfessionals();
					teachingprofessions.setTp_id(rs.getString(1));
					teachingprofessions.setName(rs.getString(2));
					subjectList.add(teachingprofessions);
				}
				rs.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		return (subjectList);
	}
	/**
	 * getCourseList(Object subject, Object teacherUserName)
	 *  Arguments:subject, teacherUserName
	* The method return course list , in case the director ask for the
	*  courses the reacher name will be null and the method will return 
	*  the whole courses under the given subject
	 * @author Or Edri
	 */
	public ArrayList<Course> getCourseList(Object subject, Object teacherUserName) {
		/*
		 * The function return the course list by the given subject code
		 */
		// Statement stmt;
		ArrayList<Course> courseList = new ArrayList<Course>();
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			if (teacherUserName == null) {
				rs = stmt.executeQuery("SELECT DISTINCT c.courseID,c.name FROM courses c,teacherincourse tc"
						+ " WHERE tp_ID=\"" + subject + "\" AND tc.courseID=c.courseID");
			} else {
				rs = stmt.executeQuery("SELECT c.courseID,c.name FROM courses c,teacherincourse tc" + " WHERE tp_ID=\""
						+ subject + "\" AND tc.courseID=c.courseID AND tc.UserNameTeacher=\""
						+ teacherUserName.toString() + "\";");
			}
			while (rs.next()) {
				courseList.add(new Course(rs.getString(1), rs.getString(2)));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return courseList;
	}
	/**
	 * getAddingTimeRequests()
	 *  Arguments:NA
	 * The method return all the open request for adding time (director need them 
	 * for approvment)
	 * @author Orit Hammer
	 */
	public ArrayList<RequestForChangingTimeAllocated> getAddingTimeRequests() {
		
		ArrayList<RequestForChangingTimeAllocated> requestList = new ArrayList<RequestForChangingTimeAllocated>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM shitot.requestforchangingtimeallocated;");
			while (rs.next()) {

				requestList.add(new RequestForChangingTimeAllocated(rs.getString(1), rs.getString(2), rs.getString(3),
						rs.getString(4), rs.getString(5), rs.getTime(6)));

			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return requestList;
	}
	/**
	 * getQuestionListToTable(Object subject, Object teacherUserName)
	 *  Arguments:subject , teacherUserName
	* The method return the relevant questions , in case the director ask for 
	* the question the teacher user name will be null and the method return the whole 
	* questions that belong to the given subject
	 * @author Tom Zarhin
	 */
	public ArrayList<Question> getQuestionListToTable(Object subject, Object teacherUserName) {
		/*
		 * The function return the question list by the given subject code
		 */
		ArrayList<Question> questionList = new ArrayList<Question>();
		String userName = null;
		String courseid = null;
		if (teacherUserName != null) {
			userName = (String) teacherUserName;
			courseid = ((String) subject).substring(2, 4);
		}
		String subjectid = ((String) subject).substring(0, 2);

		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			if (teacherUserName == null)
				rs = stmt.executeQuery("SELECT * FROM shitot.questions where question_id like '" + subjectid + "%';");
			else {
				rs = stmt.executeQuery(

						"SELECT Q.question_id,Q.teacher_name,Q.question_text,Q.answer1,Q.answer2,Q.answer3,Q.answer4,"
								+ "Q.Correct_answer FROM questions Q,teacherincourse TIC,questionincourse QIC WHERE Q.question_id like \""
								+ subjectid + "%\" AND " + "TIC.UserNameTeacher=\"" + userName
								+ "\" AND TIC.courseID like \"" + courseid + "%\""
								+ " AND QIC.q_id=Q.question_id And QIC.course_id=\"" + courseid
								+ "\" AND TIC.courseID=QIC.course_id;");
			}
			while (rs.next()) {
				questionList.add(new Question(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return questionList;
	}
	/**
	 * Question getQuestionDetails(Object quest)
	 *  Arguments:quest
	 * The method return question details by the question text
	 * @author Lior Hammer
	 */
	public Question getQuestionDetails(Object quest) {
		Question question = null;
		try {
			stmt = conn.createStatement();
			// Query return all the details of specific question
			ResultSet rs = stmt.executeQuery(
					"SELECT Question_id,Teacher_Name,answer1,answer2,answer3,answer4,Correct_Answer FROM questions "
							+ "WHERE Question_Text=\"" + quest + "\";");
			// The next commands get the returned details from DB and insert them to
			// question object

			rs.next();
			// inserting the data to String List , order by the same order in DB
			question = new Question(rs.getString(1), rs.getString(2), null, rs.getString(3), rs.getString(4),
					rs.getString(5), rs.getString(6), rs.getString(7));

			rs.close();

			// end insert details
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return question;
	}
	//
	/**
	 * getAddingTimeRequestsDetails(String requestID)
	 *  Arguments:requestID
	 * The method return details about relevant adding time request by the 
	 * give request ID 
	 * @author Tom Zarhin 
	 */
	public RequestForChangingTimeAllocated getAddingTimeRequestsDetails(String requestID) {

		RequestForChangingTimeAllocated tmpRequest = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM requestforchangingtimeallocated " + "WHERE requestID=\"" + requestID + "\";");
			rs.next();
			// inserting the data to String List , order by the same order in DB
			tmpRequest = new RequestForChangingTimeAllocated(rs.getString(1), rs.getString(2), rs.getString(3),
					rs.getString(4), rs.getString(5), rs.getTime(6));

			rs.close();

			// end insert details
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tmpRequest;
	}

	/**
	 * updateAnswer(Object questionID, Object newAnswer)
	 *  Arguments:questionID , newAnswer
	 * The method update the answer on the relevant question by question ID
	 * @author Lior Hammer
	 */
	public void updateAnswer(Object questionID, Object newAnswer) throws SQLException {
		try {
			stmt = conn.createStatement();
			// query update on DB the correct answer of question that have the given
			// questionID from client
			stmt.executeUpdate("UPDATE questions SET Correct_answer=\"" + newAnswer + "\" WHERE Question_id=\""
					+ questionID + "\";");
			System.out.println("question:" + questionID + "new answer:" + newAnswer);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * createExam(Object questionInExams, Object examDetails)
	 *  Arguments: questionInExams , examDetails
	 *  
	 * The method create new exam ID and a new exam with the question
	 * the teacher choose 
	 * @author Lior Hammer
	 */
	public synchronized String createExam(Object questionInExams, Object examDetails) {
		@SuppressWarnings("unchecked")
		ArrayList<QuestionInExam> questionInExam = (ArrayList<QuestionInExam>) questionInExams;
		Exam exam = (Exam) examDetails;
		String fullExamNumber = null;
		String examNumber = exam.getE_id();
		int examNum;
		int first = 0;
		int last = 0;
		int flagFirst = 0;
		int questionCounter = 1;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT e_id FROM exams" + " WHERE e_id like " + "\"" + examNumber + "%\"" + ";");
			if (!rs.isBeforeFirst()) {
				first = 0;
			} else {
				while (rs.next()) {
					first = Integer.parseInt(rs.getString(1).substring(4, 6));
					if (rs.next()) {
						last = Integer.parseInt(rs.getString(1).substring(4, 6));
						rs.previous();
					}
					if (first != 1 && flagFirst == 0) {
						first = 0;
						break;
					}
					flagFirst = 1;
					if (last - first > 1) {
						break;
					}

				}
				rs.close();
			}
			examNum = first + 1;
			fullExamNumber = examNumber;
			fullExamNumber = fullExamNumber + "" + String.format("%02d", examNum);
			stmt.executeUpdate(
					"INSERT INTO shitot.exams VALUES(\"" + fullExamNumber.trim() + "\",\"" + exam.getSolutionTime()
							+ "\",\"" + exam.getRemarksForTeacher() + "\",\"" + exam.getRemarksForStudent() + "\",\""
							+ exam.getType() + "\",\"" + exam.getTeacherUserName() + "\");");

			for (QuestionInExam q : questionInExam) {
				stmt.executeUpdate("INSERT INTO shitot.questioninexam VALUES(\"" + fullExamNumber.trim() + "\",\""
						+ q.getQuestionID() + "\",\"" + (questionCounter++) + "\",\"" + q.getPoints() + "\");");

			}
		} catch (SQLException e) {
			return null;
		}
		return fullExamNumber;
	}
	/**
	 * createExam(Object questionInExams, Object examDetails)
	 *  Arguments: questionInExams , examDetails
	 *  
	 * The method return the questions that belong to some exam
	 * @author Tom Zarhin
	 */
	public synchronized ArrayList<Question> getQuestions(Object questionInExams) {
		@SuppressWarnings("unchecked")
		ArrayList<QuestionInExam> questionInExam = (ArrayList<QuestionInExam>) questionInExams;
		ArrayList<Question> questions = new ArrayList<Question>();
		Question q;
		for (QuestionInExam qIa : questionInExam) {
			q = new Question();
			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT * FROM questions" + " WHERE question_id = " + "\"" + qIa.getQuestionID() + "\"" + ";");
				rs.next();
				q.setId(rs.getString(1));
				q.setTeacherName(rs.getString(2));
				q.setQuestionContent(rs.getString(3));
				q.setAnswer1(rs.getString(4));
				q.setAnswer2(rs.getString(5));
				q.setAnswer3(rs.getString(6));
				q.setAnswer4(rs.getString(7));
				q.setCorrectAnswer(rs.getString(8));
				questions.add(q);
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return questions;
	}

	////////
	/**
	 * getExam(Object examID)
	 *  Arguments: examID 
	 *  
	 * The method return exam details by the given exam id 
	 * @author Or Edri
	 */
	public synchronized Exam getExam(Object examID) {
		String examId = (String) examID;
		Exam exam = new Exam();

		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM exams" + " WHERE e_id = " + "\"" + examId + "\"" + ";");
			rs.next();
			exam.setE_id(rs.getString(1));
			exam.setSolutionTime(rs.getString(1));
			exam.setRemarksForTeacher(rs.getString(3));
			exam.setRemarksForStudent(rs.getString(4));
			exam.setType(rs.getString(5));
			exam.setTeacherUserName(rs.getString(6));
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return exam;
	}
	/**
	 * getExam(Object examID)
	 *  Arguments: examID 
	 *  
	 * The method return the student that waiting for their grade (for teacher)
	 * and the whole student that execute some exam (doesn't matter if the status is
	 * waiting or not ) for director use 
	 * @author Tom Zarhin
	 */
	public ArrayList<StudentPerformExam> getStudenstInExam(Object executedExamId,Object isDirector) {
		ArrayList<StudentPerformExam> studentsInExam = new ArrayList<StudentPerformExam>();
		try {
			stmt = conn.createStatement();
			ResultSet rs;
			if(isDirector!=null) {
				rs = stmt.executeQuery(
						"SELECT date,time,finished,executedexam_id,student_UserName,grade,isApproved,reasonForChangeGrade,"
								+ "userID,name FROM users,studentperformedexam WHERE executedexam_id " + "= " + "\""
								+ (String) executedExamId + "\" AND student_UserName = UserName");
			}else {
				rs = stmt.executeQuery(
						"SELECT date,time,finished,executedexam_id,student_UserName,grade,isApproved,reasonForChangeGrade,"
								+ "userID,name FROM users,studentperformedexam WHERE executedexam_id " + "= " + "\""
								+ (String) executedExamId + "\" AND student_UserName = UserName AND isApproved='waiting'");
			}
	
			while (rs.next()) {
				studentsInExam.add(new StudentPerformExam(rs.getString(1), rs.getString(2), rs.getString(3),
						rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), null, rs.getString(9),
						rs.getString(10)));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return studentsInExam;
	}

	///////
	/**
	 * updateQuestionInExam(Object questionInExams, Object examId)
	 *  Arguments: questionInExams , examId
	 *  
	 * The method update specific exam questions (according to both given parameters )
	 * 
	 * @author Tom Zarhin
	 */
	@SuppressWarnings("unused")
	public synchronized void updateQuestionInExam(Object questionInExams, Object examId) {
		int questionCounter = 1;
		@SuppressWarnings("unchecked")
		ArrayList<QuestionInExam> questionInExam = (ArrayList<QuestionInExam>) questionInExams;
		try {
			stmt = conn.createStatement();
			for (QuestionInExam q : questionInExam) {
				stmt.executeUpdate("DELETE FROM questioninexam WHERE e_id=\"" + (String) examId + "\" ;");

			}
			for (QuestionInExam q : questionInExam) {
				stmt.executeUpdate("INSERT INTO shitot.questioninexam VALUES(\"" + (String) examId + "\",\""
						+ q.getQuestionID() + "\",\"" + (questionCounter++) + "\",\"" + q.getPoints() + "\");");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		// stmt. executeUpdate("INSERT INTO shitot.exams VALUES(

	}
	/**
	 * deleteExam(Object exam)
	 *  Arguments: exam 
	 *  
	 * The method delete exam from the DB by his id including delete in the 
	 * "question in exam Table" 
	 * 
	 * @author Or Edri
	 */
	public Boolean deleteExam(Object exam) {
		Exam ex = (Exam) exam;

		if (!checkIfExamIsNotActive(ex.getE_id()))
			return false;

		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM questioninexam WHERE e_id=\"" + ex.getE_id() + "\";");
			stmt.executeUpdate("DELETE FROM exams WHERE e_id=\"" + ex.getE_id() + "\";");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public synchronized Boolean createExamCode(Object excutedExam) {

		ExecutedExam exam = (ExecutedExam) excutedExam;

		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT executedExamID FROM shitot.executedexam WHERE executedExamID = \""
					+ exam.getExecutedExamID() + "\";");

			if (rs.isBeforeFirst()) {
				System.out.println("There is already a code like that, please choose another code");
				rs.close();
				return false;
			} else {
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
				Date date = new Date();
				stmt.executeUpdate("INSERT INTO shitot.executedexam VALUES(\"" + exam.getExecutedExamID().trim()
						+ "\",0,0,0,0,0,\"" + exam.getTeacherName() + "\",\"" + exam.getExam_id()
						+ "\",0,0,0,0,0,0,\"open\",\"" + dateFormat.format(date) + "\", (select e.solutionTime\r\n"
						+ " from exams as e where e.e_id = \"" + exam.getExam_id() + "\"),0,0,0,0);");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return (true);
		// stmt. executeUpdate("INSERT INTO shitot.exams VALUES(

	}

	/************************************************************
	 * director functions
	 *********************************************************************************************/
	
	/**
	 * getExecutedExam(Object examId, Object teacherUserName, Object type)
	 *  Arguments: examId ,  teacherUserName , type
	 *  
	 * The method return the executed Exam that on status open for 
	 * teacher uses like locking them or approve their grade 
	 * 
	 * @author Or Edri
	 */
	public ArrayList<ExecutedExam> getExecutedExam(Object examId, Object teacherUserName, Object type) {
		
		ArrayList<ExecutedExam> executedexamARR = new ArrayList<ExecutedExam>();
		try {
			stmt = conn.createStatement();
			ResultSet rs;
			if (((String) type).equals("LockType")) {
				rs = stmt.executeQuery("SELECT * FROM executedexam WHERE teacherName=\"" + teacherUserName.toString()
						+ "\" AND status='open' AND exam_id like \"" + (String) examId + "%\"" + ";");
			}

			else {
				rs = stmt.executeQuery(
						"SELECT distinct * FROM executedexam EE,studentperformedexam SPE WHERE EE.teacherName=\""
								+ teacherUserName.toString()
								+ "\" AND EE.status='close' AND EE.numOfStudentStarted > 0 AND EE.exam_id like \""
								+ (String) examId + "%\"" + " "
								+ "AND SPE.executedexam_id=EE.executedExamID AND (SPE.isApproved='waiting' OR SPE.isApproved='copy') GROUP BY EE.executedExamID;");
			}
			while (rs.next()) {
				ExecutedExam executedExem = new ExecutedExam();
				executedExem.setExecutedExamID(rs.getString(1));
				executedExem.setNumOfStudentStarted(Integer.parseInt(rs.getString(2)));
				executedExem.setNumOfStudentFinished(Integer.parseInt(rs.getString(3)));
				executedExem.setNumOfStudentDidntFinished(Integer.parseInt(rs.getString(4)));
				executedExem.setAverage(Float.parseFloat(rs.getString(5)));
				executedExem.setMedian(Float.parseFloat(rs.getString(6)));
				executedExem.setTeacherName(rs.getString(7));
				executedExem.setExam_id(rs.getString(8));
				executedExem.setStatus(rs.getString(15));
				executedexamARR.add(executedExem);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return executedexamARR;
	}
	/**
	 * getExams(Object examIDStart)
	 *  Arguments: examIDStart 
	 *  
	 * The method return all the exams 
	 * 
	 * @author Tom Zarhin And Orit Hammer 
	 */
	public ArrayList<Exam> getExams(Object examIDStart) {
		ArrayList<Exam> examList = new ArrayList<Exam>();
		Exam exam;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM exams WHERE e_id like \"" + examIDStart + "%\";");
			while (rs.next()) {
				exam = new Exam();
				exam.setE_id(rs.getString(1));
				exam.setSolutionTime(rs.getString(2));
				exam.setRemarksForTeacher(rs.getString(3));
				exam.setRemarksForStudent(rs.getString(4));
				exam.setType(rs.getString(5));
				exam.setTeacherUserName(rs.getString(6));
				examList.add(exam);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return examList;
	}
	/**
	 * createChangingRequest(Object requestDetails)
	 *  Arguments: requestDetails 
	 *  
	 * The method create a new adding time request on the db 
	 * (it take the details from requestDetails)
	 * 
	 * @author Tom Zarhin
	 */
	public synchronized Boolean createChangingRequest(Object requestDetails) {
		RequestForChangingTimeAllocated request = (RequestForChangingTimeAllocated) requestDetails;
		String fullRequestNumber;
		int requestNum;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT MAX(requestID) FROM requestforchangingtimeallocated;");
			rs.next();
			if ((rs.getString(1) == null))
				requestNum = 0;
			else
				requestNum = Integer.parseInt(rs.getString(1));
			requestNum++;
			fullRequestNumber = String.valueOf(requestNum);
			stmt.executeUpdate("INSERT INTO shitot.requestforchangingtimeallocated VALUES(\"" + fullRequestNumber.trim()
					+ "\",\"" + request.getTeacherName() + "\",\"" + request.getReason() + "\",\""
					+ request.getMenagerApprove() + "\",\"" + request.getIDexecutedExam() + "\",\""
					+ request.getTimeAdded() + "\");");
			rs.close();
			return true;
		} catch (SQLException e) {
			return false;
		}
		// stmt. executeUpdate("INSERT INTO shitot.exams VALUES(

	}

	public Object[] checkExecutedExam(Object executedExamID, Object userName) {
		Object[] details = new Object[2];
		boolean canPerformExam = checkStudentUserNameToPerformExam((String) userName, (String) executedExamID);
		if (canPerformExam) {
			details[0] = getQuestionByExecutedExam(executedExamID);
			details[1] = getExamsByExecutedExam(executedExamID);
		} else {
			return (null);
		}
		return (details);
	}
	/**
	 * checkStudentUserNameToPerformExam(String userName, String executedExamID)
	 *  Arguments: userName ,  executedExamID
	 *  
	 * The method return true in case that the student can 
	 * perform the exam , else return false
	 * 
	 * @author Or Edri
	 */
	private boolean checkStudentUserNameToPerformExam(String userName, String executedExamID) {
		boolean flag = false;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT SIC.studentUserName FROM shitot.studentincourse SIC,shitot.executedexam EE WHERE "
							+ "SIC.studentUserName=\"" + userName + "\" AND EE.executedExamID=\"" + executedExamID
							+ "\" AND EE.exam_id LIKE CONCAT('__',SIC.course_ID, '%') AND "
							+ "SIC.studentUserName NOT IN (SELECT SPE.student_UserName "
							+ "FROM shitot.studentperformedexam SPE WHERE SPE.student_UserName=\"" + userName
							+ "\" AND SPE.executedexam_id=\"" + executedExamID + "\");");
			if (rs.isBeforeFirst()) {
				flag = true;
			} else {
				flag = false;
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * getExamsByExecutedExam(Object executedExamID)
	 *  Arguments:  executedExamID
	 *  
	 * The method return the exams that are now running by executedExamID
	 * 
	 * @author Tom Zarhin
	 */
	public Exam getExamsByExecutedExam(Object executedExamID) {
		Exam exam = new Exam();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT E.e_id,EE.actuallySolutionTime,E.remarksForTeacher,"
					+ "E.remarksForStudent,E.type,E.tUserName FROM executedexam EE,exams E WHERE "
					+ "EE.executedExamID=\"" + executedExamID + "\" AND EE.status=\"open\" AND EE.exam_id=E.e_id;");
			if (!rs.isBeforeFirst()) {
				System.out.println("no code found");
				rs.close();
				return (null);
			}
			rs.next();
			exam.setE_id(rs.getString(1));
			exam.setSolutionTime(rs.getString(2));
			exam.setRemarksForTeacher(rs.getString(3));
			exam.setRemarksForStudent(rs.getString(4));
			exam.setType(rs.getString(5));
			exam.setTeacherUserName(rs.getString(6));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return (exam);
	}
	/**
	 * getQuestionByExecutedExam(Object executedExamID)
	 *  Arguments:  executedExamID
	 *  
	 * The method return list of questions that belong to the executed exam by
	 * his ID
	 * 
	 * @author Orit Hammer
	 */
	public ArrayList<Question> getQuestionByExecutedExam(Object executedExamID) {
		ArrayList<Question> questionsinexam = new ArrayList<Question>();
		Question question;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * " + "from (select questioninexam.question_ID id "
					+ "from   shitot.questioninexam , shitot.executedexam,exams"
					+ " where  executedexam.executedExamID ='" + executedExamID
					+ "' AND exams.e_id=executedexam.exam_id "
					+ "AND  questioninexam.e_id = exams.e_id) QID , questions "
					+ "where QID.id = questions.question_id;");
			while (rs.next()) {
				question = new Question(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9));
				questionsinexam.add(question);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return (questionsinexam);
	}
	/**
	 * getSolutionTime(Object executedExamID)
	 *  Arguments:  executedExamID
	 *  
	 * The method return the solution time that belong to the 
	 * exam by his id 
	 * his ID
	 * 
	 * @author Orit Hammer
	 */
	public Time getSolutionTime(Object executedExamID) {

		Time solutionTime = null;

		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT exams.solutionTime " + "from  shitot.executedexam,exams"
					+ " where  executedexam.executedExamID ='" + executedExamID
					+ "' AND exams.e_id=executedexam.exam_id ");
			rs.next();
			solutionTime = rs.getTime(1);
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return (solutionTime);
	}
	/**
	 *public void finishExam
	 *  Arguments:(String[] details, HashMap<String, Integer> answers, boolean finishedexam)
	 *  
	 * The method add the student answers to the DB 
	 * and check if the student copy from his friends 
	 * in case the student perform an manual exam the method 
	 * save just the soft data . 
	 * 
	 * @author Or Edri and Tom zarhin 
	 */
	public void finishExam(String[] details, HashMap<String, Integer> answers, boolean finishedexam) {
		String executedID = details[0];
		String studentId = details[1];
		String status = "waiting";
		int mistakes = 0;
		float points = 0;
		try {
			int ans;
			Set<String> s = answers.keySet();
			for (String q_id : s) {
				ans = answers.get(q_id);
				try {
					stmt.executeUpdate("INSERT INTO shitot.studentanswerquestions VALUES(\"" + executedID + "\",\""
							+ studentId + "\",\"" + q_id + "\",\"" + ans + "\");");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			// Checking the answers to give a grade
			for (String q_id : s) {
				ans = answers.get(q_id);
				try {
					ResultSet rs = stmt.executeQuery(
							"SELECT Q.question_id,Q.correct_answer,QIE.points FROM shitot.questions Q,shitot.questioninexam QIE,shitot.executedexam EE"
									+ " WHERE Q.question_id=\"" + q_id
									+ "\" AND QIE.question_ID=Q.question_id AND EE.executedExamID=\"" + executedID
									+ "\" AND EE.exam_id=QIE.e_id;");
					while (rs.next()) {
						if (rs.getString(2).equals(String.valueOf(ans))) {
							points += Float.valueOf(rs.getString(3));
						} else
							mistakes++;
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			// checking if the student copied from another students
			s = answers.keySet();
			HashMap<String, Integer> studentAnswers = new HashMap<String, Integer>();// saves the question id and the
																						// answers
			boolean sameErrors = false;
			String currentStudent;
			if (mistakes >= 3) {
				try {
					ResultSet rs = stmt.executeQuery("SELECT * FROM shitot.studentanswerquestions WHERE "
							+ "executedID=\"" + executedID + "\" AND studentUserName!=\"" + studentId + "\";");
					if (rs.isBeforeFirst()) {
						rs.next();
						currentStudent = rs.getString(2);
						do {
							if (!currentStudent.equals(rs.getString(2))) {
								sameErrors = true;
								currentStudent = rs.getString(2);
								for (String q_id : s)
									if (!answers.get(q_id).equals(studentAnswers.get(q_id)))
										sameErrors = false;
								if (sameErrors) {
									status = "copy";
									break;
								}
								studentAnswers = new HashMap<String, Integer>();
								studentAnswers.put(rs.getString(3), Integer.valueOf(rs.getString(4)));
							} else
								studentAnswers.put(rs.getString(3), Integer.valueOf(rs.getString(4)));
						} while (rs.next());
						if (!sameErrors) {
							sameErrors = true;
							for (String q_id : s)
								if (!answers.get(q_id).equals(studentAnswers.get(q_id)))
									sameErrors = false;
							if (sameErrors)
								status = "copy";
						}

					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		} catch (NullPointerException exception) {
			System.out.println("Student performed manual exam");
			points = -1;
		}
		// Insert the exam to studentPerformExam table
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		try {
			stmt.executeUpdate("INSERT INTO shitot.studentperformedexam VALUES(\"" + dateFormat.format(date) + "\",\""
					+ time.format(cal.getTime()) + "\",\"" + (finishedexam == true ? "yes" : "no") + "\",\""
					+ executedID + "\",\"" + studentId + "\",\"" + points + "\",\"" + status + "\",\"none\");");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Student can't perform this exam any more

	}
	/**
	 *getadditionalTime(String requestId)
	 *  Arguments:requestId
	 *  
	 * The method return the additional time and the executed exam id 
	 * by the give request ID 
	 * 
	 * @author Or Edri 
	 */
	public synchronized Object[] getadditionalTime(String requestId) {
		Object details[] = new Object[3];

		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT requestforchangingtimeallocated.IDexecutedExam,requestforchangingtimeallocated.timeAdded "
							+ "from  requestforchangingtimeallocated"
							+ " where  requestforchangingtimeallocated.requestID ='" + requestId + "'");
			rs.next();
			details[0] = rs.getString(1);
			details[1] = rs.getTime(2);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return details;

	}
	/**
	 *updateExam(Object examToChange) 
	 *  examToChange
	 *  
	 * The method return true if the exam was update (
	 * such as remarks for student or teacher , solution time etc ... ) 
	 * @author Lior Hammer 
	 */
	public Boolean updateExam(Object examToChange) {
		Exam exam = (Exam) examToChange;

		try {
			if (!checkIfExamIsNotActive(exam.getE_id()))
				return false;
			
			stmt.executeUpdate("UPDATE exams SET solutionTime=\"" + exam.getSolutionTime() + "\", remarksForTeacher=\""
					+ exam.getRemarksForTeacher() + "\", remarksForStudent = \"" + exam.getRemarksForStudent()
					+ "\", type= \"" + exam.getType() + "\"," + "tUserName=\"" + exam.getTeacherUserName()
					+ "\" WHERE e_id=\"" + exam.getE_id() + "\";");

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 *checkIfExamIsNotActive(Object examToChange)
	 * Arguments: examToChange
	 *  
	 * The method return true if the give exam is not active 
	 * @author Or Edri
	 */
	public Boolean checkIfExamIsNotActive(Object examToChange) {
		// TODO Auto-generated method stub
		String exam = (String) examToChange;

		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select distinct executedexam.exam_id from "
					+ "shitot.executedexam where executedexam.status = 'open' AND executedexam.numOfStudentStarted>'0'");

			while (rs.next()) {
				if (exam.equals(rs.getString(1))) {
					rs.close();
					return false;
				}
			}
			rs.close();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 *getQuestionInExam(Object examid) 
	 * Arguments: examid
	 *  
	 * The method return a list of questions that belong to the exam 
	 * with examid 
	 * @author Or Edri
	 */
	public ArrayList<QuestionInExam> getQuestionInExam(Object examid) {
		String examID = (String) examid;
		ArrayList<QuestionInExam> questioninexam = new ArrayList<QuestionInExam>();
		QuestionInExam question;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM questioninexam" + " WHERE e_id = \"" + examID + "\" ;");
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					question = new QuestionInExam();
					question.setQuestionID(rs.getString(2));
					question.setQuestionIndexInExam(Integer.parseInt(rs.getString(3)));
					question.setPoints(Float.parseFloat(rs.getString(4)));
					questioninexam.add(question);
				}
				rs.close();
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return questioninexam;
	}
	/**
	 *returnListForGetReport(String getBy)
	 * Arguments: getBy
	 *  
	 * The method return names of teachers or student 
	 * @author Orit Hammer
	 */
	public ArrayList<String> returnListForGetReport(String getBy) {
		ArrayList<String> listForGetReport = new ArrayList<String>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = null;
			switch (getBy) {
			case "Student": {
				rs = stmt.executeQuery("SELECT UserName FROM shitot.users where role='student';");
				break;
			}
			case "Teacher": {
				rs = stmt.executeQuery("SELECT UserName FROM shitot.users where role='teacher';");
				break;
			}
			}
			while (rs.next())
				listForGetReport.add(rs.getString(1));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listForGetReport;
	}
	/**
	 *getStudentAns(String userName, String executedExamID)
	 * Arguments: userName , executedExamID
	 *  
	 * The method return a list of the questionId and his answers by user userName
	 * @author Orit Hammer
	 */
	public HashMap<String, Integer> getStudentAns(String userName, String executedExamID) {
		HashMap<String, Integer> stdAns = new HashMap<>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select saq.questionID , saq.answer"
					+ " from shitot.studentanswerquestions saq " + "where saq.executedID = \"" + executedExamID
					+ "\" AND saq.studentUserName =\"" + userName + "\" ;  ");

			while (rs.next()) {
				int ans = rs.getInt(2);
				stdAns.put(rs.getString(1), ans);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stdAns;
	}

	public ArrayList<Question> getQuestionFromCloseExam(String executedECode) {
		return getQuestionByExecutedExam(executedECode);
	}
	/**
	 *returnReportByTeacherOrCoursesDetails(Object reportBy, Object idOrUserName)
	 * Arguments: reportBy , idOrUserName
	 *  
	 * The method return executed exam details by teacher user name (
	 * to get all the exams that some teacher done) or by  user name  
	 * where the executed exam status is 'checked'
	 * @author Orit Hammer And tom Zarhin
	 */
	public ArrayList<ExecutedExam> returnReportByTeacherOrCoursesDetails(Object reportBy, Object idOrUserName) {
		ArrayList<ExecutedExam> executedExamList = new ArrayList<ExecutedExam>();
		String eEid_userName = (String) idOrUserName;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			switch ((String) reportBy) {
			case "getReportByTeacher":
				rs = stmt.executeQuery("SELECT average, median, between0to9, between10to19, between20to29, " + 
						"between30to39, between40to49, between50to59, between60to69, between70to79, "+
						"between80to89,between90to100 FROM shitot.executedexam where teacherName='"
						+ eEid_userName + "'AND status='checked';");
				break;
			case "getReportByCourse":
				rs = stmt.executeQuery("SELECT average, median, between0to9, between10to19, between20to29, " + 
						"between30to39, between40to49, between50to59, between60to69, between70to79, " + 
						"between80to89,between90to100  FROM shitot.executedexam where exam_id like \"__"
						+ eEid_userName + "%\" AND status='checked';");
				break;
			}
			while (rs.next()) {
				executedExamList.add(new ExecutedExam(null, 0, 0, 0, rs.getFloat(1),
						rs.getFloat(2), null, null, rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7),
						rs.getInt(8), null, rs.getInt(9), rs.getInt(10), rs.getInt(11), rs.getInt(12), null, null));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return executedExamList;
	}
	/**
	 *returnReportByStudent(Object userName)
	 * Arguments: userName
	 *  
	 * The method return the grades of specific student by his user name
	 * @author Lior Hammer
	 */
	public ArrayList<Integer> returnReportByStudent(Object userName) {
		ArrayList<Integer> studentGradesList = new ArrayList<Integer>();
		String studentName = (String) userName;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT grade FROM shitot.studentperformedexam WHERE student_UserName='"
					+ studentName + "'AND isApproved='approved';");
			while (rs.next())
				studentGradesList.add(Integer.parseInt(rs.getString(1)));
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentGradesList;
	}
	/**
	 *confirmExecutedExam(Object studentInExam, Object flagIfExamChecked)
	 * Arguments: studentInExam , flagIfExamChecked
	 *  
	 * The method update a specific grade for student and when 
	 * all of the students that executed the exam it update 
	 * the relevant row with statistic details 
	 * @author Lior Hammer And Tom Zarhin 
	 */
	public void confirmExecutedExam(Object studentInExam, Object flagIfExamChecked) {
		Boolean flagExamChecked = (Boolean) flagIfExamChecked;
		String eid = ((StudentPerformExam) studentInExam).getExcecutedExamID();
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE studentperformedexam " + "SET isApproved=\"approved\" , grade=\""
					+ ((StudentPerformExam) studentInExam).getGrade() + "\" , " + "reasonForChangeGrade=\""
					+ ((StudentPerformExam) studentInExam).getReasonForChangeGrade() + "\" "
					+ " WHERE student_UserName=\"" + ((StudentPerformExam) studentInExam).getUserName() + "\" AND executedexam_id=\""+
					eid+"\";");
			if (flagExamChecked == true) {
				stmt.executeUpdate("UPDATE executedexam " + "SET status=\"checked\" WHERE executedExamID=\""
						+ ((StudentPerformExam) studentInExam).getExcecutedExamID() + "\";");
				stmt.executeUpdate("update shitot.executedexam " + "set" + " between0to9 = (select count(*) "
						+ " from studentperformedexam as spe " + " where spe.executedexam_id = \"" + eid
						+ "\" and spe.grade > -1 and spe.grade<10) ," + "   between10to19 = (select count(*)"
						+ "          from studentperformedexam as spe " + "   where spe.executedexam_id = \"" + eid
						+ "\" and spe.grade > 9 and spe.grade<20) ," + "     between20to29 = (select count(*)"
						+ "   from studentperformedexam as spe" + "   where spe.executedexam_id = \"" + eid
						+ "\" and spe.grade > 19 and spe.grade<30) ," + "      between30to39 = (select count(*)"
						+ "   from studentperformedexam as spe " + "   where spe.executedexam_id = \"" + eid
						+ "\" and spe.grade > 29 and spe.grade<40) ," + " between40to49 = (select count(*)"
						+ "   from studentperformedexam as spe" + "   where spe.executedexam_id = \"" + eid
						+ "\" and spe.grade > 39 and spe.grade<50) ," + "  between50to59 = (select count(*)"
						+ "     from studentperformedexam as spe " + "      where spe.executedexam_id = \"" + eid
						+ "\" and spe.grade > 49 and spe.grade<60) ," + "       between60to69 = (select count(*)"
						+ " from studentperformedexam as spe " + " where spe.executedexam_id = \"" + eid
						+ "\" and spe.grade > 59 and spe.grade<70) ," + "  between70to79 = (select count(*)"
						+ " from studentperformedexam as spe " + " where spe.executedexam_id = \"" + eid
						+ "\" and spe.grade > 69 and spe.grade<80) ," + "  between80to89 = (select count(*)"
						+ " from studentperformedexam as spe " + " where spe.executedexam_id = \"" + eid
						+ "\" and spe.grade > 79 and spe.grade<90) ," + "  between90to100 = (select count(*)"
						+ " from studentperformedexam as spe " + " where spe.executedexam_id = \"" + eid
						+ "\" and spe.grade > 89 and spe.grade<101)" + "where executedExamID = \"" + eid + "\" ;");
	
		ArrayList <Integer> grades=new ArrayList<Integer>();
		ResultSet rs = null;
		
		
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT grade FROM studentperformedexam WHERE executedexam_id = \"" + eid + "\" ;");

			while (rs.next()) {
				 
				grades.add(rs.getInt(1));
			}
		grades.sort(null);
		Float med;
		int size=grades.size();
		float avg=0;
		if(size==1) {
			med=(float) grades.get(0);
		}
		else {
		med=(float) (grades.get(((size+1)/2)-1));
		}
		for (int i=0;i<grades.size();i++) {
			avg+=grades.get(i);
		}
		if(size>0) {
			avg=avg/size;
		}
		stmt.executeUpdate("update shitot.executedexam as ex "
				+ "set ex.median = \"" + med + "\" WHERE ex.executedExamID = \"" + eid + "\" ;");
                                    
		stmt.executeUpdate("update shitot.executedexam as ex "
				+ "set ex.average = \"" + avg + "\" WHERE ex.executedExamID = \"" + eid + "\" ;");
				
				stmt.executeUpdate("update shitot.executedexam " + "set "
						+ "numOfStudentDidntFinished = (select count(*) as numStodentNF "
						+ " from studentperformedexam as sp" + " where executedexam_id = \"" + eid
						+ "\" and sp.finished = 'no') , " + "numOfStudentFinished =(select count(*) as numStodentF "
						+ "   from studentperformedexam as sp" + "  where executedexam_id = \"" + eid
						+ "\" and sp.finished = 'yes'  ) " + " where executedExamID = \"" + eid + "\" ;");
			}
			

		} catch (SQLException e) {
			e.printStackTrace();

		}

	}

	/** lock the exam if the all students finished the */
	public void checkIfAllStudentFinishedExam(Object executedExamID) {
		String executedID = (String) executedExamID;
		ResultSet rs = null;
		int numOfStudentInCourse, numOfStudentPerformedExam;
		try {
			stmt = conn.createStatement();// לסיים את השאילתא
			rs = stmt.executeQuery(
					"SELECT count(SIC.studentUserName) as numOfStudentInCourse FROM shitot.studentincourse SIC,shitot.executedexam "
							+ "E WHERE E.exam_id LIKE CONCAT('__',SIC.course_ID, '%') AND E.executedExamID=\""
							+ executedID + "\";");
			rs.next();
			numOfStudentInCourse = Integer.parseInt(rs.getString(1));
			rs = stmt.executeQuery(
					"SELECT numOfStudentStarted FROM shitot.executedexam WHERE executedExamID=\"" + executedID + "\";");
			rs.next();
			numOfStudentPerformedExam = Integer.parseInt(rs.getString(1));
			if (numOfStudentInCourse == numOfStudentPerformedExam) {
				stmt.executeUpdate(
						"UPDATE shitot.executedexam SET status = 'close' WHERE executedExamID=\"" + executedID + "\";");
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 *updateStudentToExecutedExam(Object executedExamID) 
	 * Arguments: executedExamID 
	 *  
	 * The method update the number of student started (by executedExamID)
	 * @author Lior Hammer 
	 */
	public void updateStudentToExecutedExam(Object executedExamID) {

		String executedID = (String) executedExamID;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("update shitot.executedexam set numOfStudentStarted = numOfStudentStarted+1 "
					+ "where executedexam.executedExamID = \"" + executedID + "\" ; ");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	/**
	 *setRealTimeOfExecutedExam(String requestID)
	 * Arguments: requestID 
	 *  
	 * The method update the actually real time on specific executed exam 
	 * and update the request status to approve 
	 * @author Lior Hammer 
	 */
	@SuppressWarnings("deprecation")
	public void setRealTimeOfExecutedExam(String requestID) {
		// getting executed exam id
		ResultSet rs = null;
		String executedID = "";
		Time timeToAdd=new Time(0,0,0);
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT request.IDexecutedExam FROM requestforchangingtimeallocated as request "
					+ "WHERE request.requestID = \"" + requestID + "\" ;");
			if (rs.isBeforeFirst()) {
				rs.next();
				executedID =""+ rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// adding solution time to the actual solution time by executed exam id
		try {
			rs=stmt.executeQuery("SELECT request.timeAdded FROM requestforchangingtimeallocated as request WHERE "
					+ "request.IDexecutedExam = \"" + executedID + "\" AND isApproved='approved';");
			while(rs.next()) {
				timeToAdd=timeToAddFunction(timeToAdd,rs.getTime(1));
			}
			rs=stmt.executeQuery("SELECT E.solutionTime FROM shitot.exams E,shitot.executedexam EE WHERE EE.executedExamID=\""+executedID+"\""
					+ " AND E.e_id = EE.exam_id;");
			while(rs.next()) {
				timeToAdd=timeToAddFunction(timeToAdd,rs.getTime(1));
			}
			stmt.executeUpdate("UPDATE shitot.executedexam SET actuallySolutionTime = \""+timeToAdd+"\" "
					+ "WHERE executedexam.executedExamID = \""
					+ executedID + "\";");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	  
	@SuppressWarnings("deprecation")
	public Time timeToAddFunction(Time timeToAdd,Time time) {
		timeToAdd.setHours(time.getHours()+timeToAdd.getHours());
		timeToAdd.setMinutes(time.getMinutes()+timeToAdd.getMinutes());
		timeToAdd.setSeconds(time.getSeconds()+timeToAdd.getSeconds());
		return(timeToAdd);
	}
	/**
	 *getAllExecutedExams(Object teacherUserName)
	 * Arguments: teacherUserName 
	 *  
	 * The method get the details of executed for the teacher or for the director 
	 * (for the director in case teacher user name is null )
	 * @author Orit Hammer And Tom Zarhin 
	 */
	public ArrayList<ExecutedExam> getAllExecutedExams(Object teacherUserName) {
		ArrayList<ExecutedExam> executedexams = new ArrayList<ExecutedExam>();
		
		try {
			stmt = conn.createStatement();
			ResultSet rs;
			if (teacherUserName == null) {
				rs = stmt.executeQuery(
						"select EE.executedExamID ,teacherExams.timeGiven , EE.actuallySolutionTime ,EE.numOfStudentStarted,"
								+ "EE.numOfStudentFinished, EE.numOfStudentDidntFinished, EE.startDate, EE.between0to9, EE.between10to19, "
								+ "EE.between20to29, EE.between30to39, EE.between40to49, EE.between50to59, EE.between60to69, EE.between70to79, "
								+ "EE.between80to89, EE.between90to100 ,EE.average, EE.median "
								+ "FROM (select exams.e_id as eid , exams.solutionTime as timeGiven "
								+ "from exams)  teacherExams , executedexam as EE "
								+ "where teacherExams.eid = EE.exam_id;");
			} else {
				rs = stmt.executeQuery(
						"select EE.executedExamID ,teacherExams.timeGiven , EE.actuallySolutionTime ,EE.numOfStudentStarted,"
								+ "EE.numOfStudentFinished, EE.numOfStudentDidntFinished, EE.startDate, EE.between0to9, EE.between10to19, "
								+ "EE.between20to29, EE.between30to39, EE.between40to49, EE.between50to59, EE.between60to69, EE.between70to79, "
								+ "EE.between80to89, EE.between90to100, EE.average, EE.median "
								+ "FROM (select exams.e_id as eid , exams.solutionTime as timeGiven " + "from exams "
								+ "where exams.tUserName = \"" + (String) teacherUserName
								+ "\" )  teacherExams , executedexam as EE " + "where teacherExams.eid = EE.exam_id;");
			}

			while (rs.next()) {
				int[] gradesRang = new int[10];
				ExecutedExam executedexam = new ExecutedExam();
				executedexam.setExecutedExamID(rs.getString(1));
				executedexam.setSolutionTime(rs.getString(2));
				executedexam.setActuallySolutionTime(java.sql.Time.valueOf(rs.getString(3)));
				executedexam.setNumOfStudentStarted(Integer.parseInt(rs.getString(4)));
				executedexam.setNumOfStudentFinished(Integer.parseInt(rs.getString(5)));
				executedexam.setNumOfStudentDidntFinished(Integer.parseInt(rs.getString(6)));
				executedexam.setAverage(rs.getFloat(18));
				executedexam.setMedian(rs.getFloat(19));
				executedexam.setDate(rs.getString(7));
				for (int i = 0; i < 10; i++)
					gradesRang[i] = rs.getInt(i + 8);
				executedexam.setGradeRang(gradesRang);
				executedexams.add(executedexam);

				executedexam.setGradeRang(gradesRang);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return executedexams;
	}

}