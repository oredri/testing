package control;

import javafx.scene.control.Alert;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import entity.Course;
import control.StudentControl;
import entity.Exam;
import entity.ExecutedExam;
import entity.Question;
import entity.QuestionInExam;
import entity.RequestForChangingTimeAllocated;
import entity.StudentPerformExam;
import entity.TeachingProfessionals;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.FloatStringConverter;

public class TeacherControl extends UserControl implements Initializable {

	private static ObservableList<QuestionInExam> questionInExamObservable = FXCollections.observableArrayList();// this
																													// variable
																													// is
																													// for
																													// saving
																													// the
																													// questions
																													// in
																													// exam
																													// and
																													// display
																													// it
	private ObservableList<String> coursesListToCreateQuestion = FXCollections.observableArrayList();
	private ObservableList<Question> questionObservableList;
	private static boolean blockPassQuestionButton;
	private TeacherControl tController;
	private ActionEvent tempEvent;
	private ObservableList<Exam> exams;
	private Question questionSelected;
	private Question oldQuestion;
	private StudentPerformExam studentInExamRow;
	protected static String tempExamId;
	private Exam examSelected;
	private Exam oldExam;
	/* fxml variables */
	@FXML
	private Text userText;
	@FXML
	private Text allertText;
	@FXML
	private TextField teacherNameOnCreate;

	@FXML
	private Label pageLabel;

	@FXML
	private TextArea reason;

	@FXML
	private TextField newGrade;

	@FXML
	private TextField answer1;
	@FXML
	private TextField answer2;
	@FXML
	private TextField answer3;
	@FXML
	private TextField answer4;

	@FXML
	private TextField questionName;
	@FXML
	private TextField questionID;
	@FXML
	private TextField teacherName;
	@FXML
	private TextField remarksForStudent;
	@FXML
	private TextField remarksForTeacher;
	@FXML
	private TextField timeForExamHours;
	@FXML
	private TextField timeForExamMinute;
	@FXML
	private TextField reasonForChange;
	@FXML
	private TextField examCode;

	/* RadioButton of display the correct answer */
	@FXML
	private RadioButton correctAns1;
	@FXML
	private RadioButton correctAns2;
	@FXML
	private RadioButton correctAns3;
	@FXML
	private RadioButton correctAns4;

	@FXML
	private ToggleGroup group1;

	@FXML
	private TableView<QuestionInExam> questionsInExamTableView;
	@FXML
	private TableColumn<QuestionInExam, String> questionNameTableView;
	@FXML
	private TableColumn<QuestionInExam, Float> questionPointsTableView;

	@FXML
	private TableView<ExecutedExam> executedExamTableView;
	@FXML
	private TableColumn<ExecutedExam, String> exam_idTableView;
	@FXML
	private TableColumn<ExecutedExam, String> executedExamIDTableView;
	@FXML
	private TableColumn<ExecutedExam, String> teacherNameTableView;

	@FXML
	private TableView<Question> questionTableView;
	@FXML
	private TableColumn<Question, String> qid;
	@FXML
	private TableColumn<Question, String> tname;
	@FXML
	private TableColumn<Question, String> qtext;
	@FXML
	private TableColumn<Question, String> a1;
	@FXML
	private TableColumn<Question, String> a2;
	@FXML
	private TableColumn<Question, String> a3;
	@FXML
	private TableColumn<Question, String> a4;
	@FXML
	private TableColumn<Question, String> correctAns;

	@FXML
	private TableView<Exam> examsTableView;
	@FXML
	private TableColumn<Exam, String> examITable;
	@FXML
	private TableColumn<Exam, String> teacherNameTable;
	@FXML
	private TableColumn<Exam, String> remarksForTeacherTable;
	@FXML
	private TableColumn<Exam, String> solutionTimeTable;
	@FXML
	private TableColumn<Exam, String> typeTable;
	@FXML
	private TableColumn<Exam, String> examIDTable;
	@FXML
	private TableColumn<Exam, String> remarksForStudentTable;

	@FXML
	private TableView<StudentPerformExam> studnetInExamTableView;
	@FXML
	private TableColumn<StudentPerformExam, String> studentId;
	@FXML
	private TableColumn<StudentPerformExam, String> studentName;
	@FXML
	private TableColumn<StudentPerformExam, String> grade;
	@FXML
	private TableColumn<StudentPerformExam, String> status;
	@FXML
	private TableColumn<StudentPerformExam, String> reasonForChangeGrade;

	@FXML
	private ComboBox<String> subjectsComboBox;
	@FXML
	private ComboBox<String> coursesComboBox;
	@FXML
	private ComboBox<String> examComboBox;
	@FXML
	private ComboBox<String> typeComboBox;
	@FXML
	private ComboBox<String> executedExamsComboBox;

	@FXML
	private ListView<String> courseInCreateQuestion;

	@FXML
	private Button passQuestionL;
	@FXML
	private Button passQuestionR;
	@FXML
	private Button backButton;
	@FXML
	private Button updateBtn;

	@FXML
	private Button btnDelete;
	@FXML
	private Button confirmButton;
	@FXML
	private Button changeGradeButton;

	/**
	 * loadExamCopy(MouseEvent event)
	 * 
	 * @param MouseEvent
	 *            event The method order exam Copy of specific student
	 * @author Or Edri
	 */
	@SuppressWarnings("static-access")
	public void loadExamCopy(MouseEvent event) {
		if (event.getClickCount() == 2) {
			connect(this);
			messageToServer[0] = "getStudentAnswers";
			// this condition is for the director to load copies of exams
			if (getMyUser().getRole().equals("director")) {
				messageToServer[1] = tempExamId;
			} else {
				messageToServer[1] = executedExamsComboBox.getValue(); /* sending executed exam id */
			}
			messageToServer[2] = studnetInExamTableView.getSelectionModel().getSelectedItem()
					.getUserName(); /* send the user name */
			chat.handleMessageFromClientUI(messageToServer);
			StudentControl studentControl = new StudentControl();
			studentControl.tempEvent = event;
		}
	}

	public void refreshPageAfterCreate(ActionEvent event, String screen) throws IOException {
		Parent tableViewParent = FXMLLoader.load(getClass().getResource("/boundary/" + screen + ".fxml"));
		Scene tableViewScene = new Scene(tableViewParent);
		tableViewScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		// This line gets the Stage information
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.setScene(tableViewScene);
		window.show();
	}

	/**
	 * confirmExecutedExam(ActionEvent event)
	 * 
	 * @param ActionEvent
	 *            event The method send message to server with the exam details in
	 *            order to confirm grade of executed exam of spesific student
	 * 
	 * @author Or Edri
	 */
	public void confirmExecutedExam(ActionEvent event) throws IOException {
		if (studnetInExamTableView.getSelectionModel().getSelectedItem() == null)// if ther is nothing chosen in the
																					// table view
			return;

		StudentPerformExam studentinexm = studnetInExamTableView.getSelectionModel()
				.getSelectedItem();/* get the chosen student in exam */
		studnetInExamTableView.getItems().remove(studentinexm);/* remove the object from the table view */
		if (studnetInExamTableView.getItems().isEmpty()) {/* if the table view is empty */
			infoMsg("All exam are checked");
			refreshPageAfterCreate(event, "CheckExam");
			messageToServer[2] = true;
		} else
			messageToServer[2] = false;
		messageToServer[0] = "confirmExecutedExam";
		messageToServer[1] = studentinexm;
		/* connect to server and send the message */
		connect(this);
		chat.handleMessageFromClientUI(messageToServer);
	}

	/**
	 * closeChange(ActionEvent event)
	 * 
	 * @param ActionEvent
	 *            event The method exit from the changeGrade window.
	 * @author Or Edri
	 */
	public void closeChange(ActionEvent event) {
		final Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();

	}

	/**
	 * finalChange(ActionEvent event)
	 * 
	 * @param ActionEvent
	 *            event The method changs the grade of the student The method handle
	 *            grade change
	 * 
	 * @author Or Edri
	 */
	@SuppressWarnings("unchecked")
	public void finalChange(ActionEvent event) {
		/* Check the inputs from the textFields */
		if (newGrade.getText().equals("") && reason.getText().equals(""))
			new Alert(Alert.AlertType.ERROR, "You must enter values.").showAndWait();
		else if (!(newGrade.getText()).matches("[0-9]+"))
			new Alert(Alert.AlertType.ERROR, "The grade must be number").showAndWait();
		else if (!(Integer.parseInt(newGrade.getText()) >= 0 && Integer.parseInt(newGrade.getText()) <= 100))
			new Alert(Alert.AlertType.ERROR, "The grade must be between 0-100").showAndWait();
		else if (reason.getText().equals(""))
			new Alert(Alert.AlertType.ERROR, "If you want to change the grade you must enter a reason").showAndWait();
		/* If the values are proper */
		else {
			StudentPerformExam student = tController
					.getSelectedStudentPerformExam();/*
														 * set new controller in order to send values between
														 * controllers
														 */
			student.setGrade(Float.valueOf(newGrade.getText()));
			student.setReasonForChangeGrade(reason.getText());
			tController.setOnTableView(student);
			tController.studnetInExamTableView.refresh();

			final Node source = (Node) event.getSource();
			Stage stage = (Stage) source.getScene().getWindow();
			stage.close();
			tController.studnetInExamTableView.getSortOrder().setAll(tController.getStudentId());
			tController.studnetInExamTableView.getSelectionModel().clearSelection();
		}

	}

	/**
	 * The setOnTableView function add student to studnetInExam TableView
	 *
	 * @author Tom Zarhin
	 */
	private void setOnTableView(StudentPerformExam student) {
		/* Set the objects in the table view */
		studnetInExamTableView.getItems().remove(studnetInExamTableView.getSelectionModel().getSelectedIndex());
		studnetInExamTableView.getItems().add(student);
	}

	/**
	 * The getSelectedStudentPerformExam function get selected student from
	 * studnetInExam table view
	 *
	 * @author Tom Zarhin
	 */
	private StudentPerformExam getSelectedStudentPerformExam() {
		return (studnetInExamTableView.getSelectionModel().getSelectedItem());
	}

	/**
	 * The getStudentId function get the table column studentId from studnetInExam
	 * table view
	 *
	 * @author Tom Zarhin
	 */
	private TableColumn<StudentPerformExam, String> getStudentId() {
		return (studentId);
	}

	/**
	 * The changeGrade function open new window with option to change grade and
	 * write the reason
	 *
	 * @author Or Edri
	 */
	public void changeGrade(ActionEvent event) throws IOException {

		studentInExamRow = studnetInExamTableView.getSelectionModel()
				.getSelectedItem();/* get the details of the chosen student */
		if (studentInExamRow == null)
			return;

		final Stage dialog = new Stage();
		/* open change grade screen with the relevant values */
		final Node source = (Node) event.getSource();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner((Stage) source.getScene().getWindow());
		FXMLLoader fxmlLoader = new FXMLLoader();
		AnchorPane myPane = fxmlLoader.load(getClass().getResource("/boundary/ChangeGrade.fxml").openStream());
		Scene dialogScene = new Scene(myPane);
		dialogScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		tController = (TeacherControl) fxmlLoader.getController();
		tController.setTeacherController(this);
		dialog.setScene(dialogScene);
		dialog.show();
	}

	public void openSceneInTheSameWindow(ActionEvent event, String screen) throws IOException {
		final Stage dialog = new Stage();
		/* open change grade screen with the relevant values */
		final Node source = (Node) event.getSource();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner((Stage) source.getScene().getWindow());
		FXMLLoader fxmlLoader = new FXMLLoader();
		AnchorPane myPane = fxmlLoader.load(getClass().getResource("/boundary/" + screen + ".fxml").openStream());

		Scene dialogScene = new Scene(myPane);
		dialogScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		dialog.setScene(dialogScene);
		dialog.show();
	}

	/**
	 * The setTeacherController function is for setting teacherControl
	 *
	 * @author Tom Zarhin
	 */
	private void setTeacherController(TeacherControl teacherControl) {
		tController = teacherControl;

	}

	/**
	 * checkMessage(Object message)
	 * 
	 * @param Object
	 *            message The method handle the message from server
	 * 
	 * @author Aviv Mahulya
	 */
	@SuppressWarnings("unchecked")
	public void checkMessage(Object message) {
		try {
			final Object[] msg = (Object[]) message;
			if (messagesRead.contains((int) msg[5])) {/* if the client already read the current message */
				return;
			}
			messagesRead.add((int) msg[5]);/* save the message the client read */
			if (msg[4].equals(
					getMyUser().getUsername())) {/* If the message should be read by the current client(by username) */
				chat.closeConnection();// close the connection

				Platform.runLater(() -> {
					switch (msg[0].toString()) {

					/***************************************
					 * General "get" items from server to client
					 ************************************/

					case "showingCopy": {/* show copy of exam */
						MyGradesControl scontrol = new MyGradesControl();
						scontrol.showingCopy((ArrayList<Question>) msg[1], (HashMap<String, Integer>) msg[2]);
						break;
					}

					case ("getSubjects"): /* get the subjects list from server */
					{
						ObservableList<String> observableList = FXCollections.observableArrayList();
						for (TeachingProfessionals tp : (ArrayList<TeachingProfessionals>) msg[1]) {
							observableList.add(tp.getTp_id() + " - " + tp.getName());
						}

						subjectsComboBox.setItems(observableList);
						break;
					}

					case ("getCourses"): /* get the courses list from server */
					{
						ObservableList<String> observableList = FXCollections.observableArrayList();
						for (Course c : (ArrayList<Course>) msg[1]) {
							observableList.add(c.getCourseID() + " - " + c.getName());
						}
						coursesComboBox.setItems(observableList);
						break;
					}

					/************************************************************
					 * All Question cases
					 ************************************/
					case ("SetQuestion"): {
						if ((Boolean) msg[1] == true) {
							infoMsg("Question added.");
							try {
								refreshPageAfterCreate(tempEvent, "CreateQuestion");
							} catch (IOException e) {

								e.printStackTrace();
							}
						} else
							errorMsg("Question could not add to the database");
						break;
					}

					case ("getQuestionsToTable"): /* get the questions list from server */
					{
						questionObservableList = FXCollections.observableArrayList((ArrayList<Question>) msg[1]);// kaki
						qid.setCellValueFactory(new PropertyValueFactory<>("id"));
						tname.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
						qtext.setCellValueFactory(new PropertyValueFactory<>("questionContent"));
						if (pageLabel.getText().equals("Update question")) {
							a1.setCellValueFactory(new PropertyValueFactory<>("answer1"));
							a2.setCellValueFactory(new PropertyValueFactory<>("answer2"));
							a3.setCellValueFactory(new PropertyValueFactory<>("answer3"));
							a4.setCellValueFactory(new PropertyValueFactory<>("answer4"));
							correctAns.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));
							if (getMyUser().getRole().equals("teacher"))
								questionTableView.setEditable(true);
							ObservableList<String> numbers = FXCollections.observableArrayList("1", "2", "3", "4");
							qtext.setCellFactory(TextFieldTableCell.forTableColumn());
							a1.setCellFactory(TextFieldTableCell.forTableColumn());
							a2.setCellFactory(TextFieldTableCell.forTableColumn());
							a3.setCellFactory(TextFieldTableCell.forTableColumn());
							a4.setCellFactory(TextFieldTableCell.forTableColumn());
							correctAns.setCellFactory(TextFieldTableCell.forTableColumn());
							correctAns.setCellFactory(
									ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), numbers));
							questionObservableList = FXCollections.observableArrayList((ArrayList<Question>) msg[1]);
						}
						questionTableView.setItems(questionObservableList);
						questionTableView.getSortOrder().setAll(qid);
						break;
					}

					case ("updateQuestion"): /* the server return true/false if the question updated or not */
					{
						if ((boolean) msg[1] == true) {
							infoMsg("Edited Successfully.");
							questionTableView.refresh();
						} else {
							questionObservableList.remove(questionObservableList.indexOf(questionSelected));
							questionObservableList.add(oldQuestion);
							errorMsg("This question is in active exam.");
							questionTableView.getSortOrder().setAll(qid);
						}
						break;
					}

					case ("deleteQuestion"): /* the server return true/false if the question deleted or not */
					{
						if ((boolean) msg[1]) {
							if ((boolean) msg[1] == true) {
								int index = questionObservableList.indexOf(questionSelected);
								questionObservableList.remove(index);
								infoMsg("Deleted Successfully.");
								questionTableView.refresh();
							} else {
								errorMsg("This question is in active exam.");
							}
						} else {
							errorMsg("This question is in exam, first delete the exam");
						}
						break;
					}

					/*****************************************************
					 * All Exam cases
					 *********************************************/

					case ("getExams"): /* get the exams list from server */
					{
						if (pageLabel.getText().equals("Update exam")) {
							/* set the javaFX containers */
							exams = FXCollections.observableArrayList((ArrayList<Exam>) msg[1]);
							examIDTable.setCellValueFactory(new PropertyValueFactory<>("e_id"));
							teacherNameTable.setCellValueFactory(new PropertyValueFactory<>("teacherUserName"));
							solutionTimeTable.setCellValueFactory(new PropertyValueFactory<>("solutionTime"));
							remarksForTeacherTable.setCellValueFactory(new PropertyValueFactory<>("remarksForTeacher"));
							remarksForStudentTable.setCellValueFactory(new PropertyValueFactory<>("remarksForStudent"));
							typeTable.setCellValueFactory(new PropertyValueFactory<>("type"));
							ObservableList<String> type = FXCollections.observableArrayList("computerized", "manual");
							solutionTimeTable.setCellFactory(TextFieldTableCell.forTableColumn());
							remarksForTeacherTable.setCellFactory(TextFieldTableCell.forTableColumn());
							remarksForStudentTable.setCellFactory(TextFieldTableCell.forTableColumn());
							typeTable.setCellFactory(
									ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), type));
							examsTableView.setItems(exams);
						} else {
							ObservableList<String> observableList = FXCollections.observableArrayList();
							ArrayList<Exam> exams = (ArrayList<Exam>) msg[1];
							for (Exam e : exams) {
								observableList.add(e.getE_id());
							}
							examComboBox.setItems(observableList);

						}
						break;
					}

					case ("getExecutedExams"): /* get the executed exams list from server */
					{
						if (!pageLabel.getText().equals("Check exam")) {
							ObservableList<ExecutedExam> observablelist = FXCollections
									.observableArrayList((ArrayList<ExecutedExam>) msg[1]);
							executedExamTableView.setItems(observablelist);
							executedExamIDTableView.setCellValueFactory(new PropertyValueFactory<>("executedExamID"));
							teacherNameTableView.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
							exam_idTableView.setCellValueFactory(new PropertyValueFactory<>("exam_id"));
							break;
						} else {
							ObservableList<String> observablelistToExecutedExamComboBox = FXCollections
									.observableArrayList();
							ArrayList<ExecutedExam> exams = (ArrayList<ExecutedExam>) msg[1];
							for (ExecutedExam e : exams) {
								observablelistToExecutedExamComboBox.add(e.getExecutedExamID());
							}
							executedExamsComboBox.setItems(observablelistToExecutedExamComboBox);
						}
						break;
					}

					case ("getQuestionInExam"): /*
												 * get the question list of specific exam from server and check if the
												 * exam active or not
												 */
					{
						try {
							((ArrayList<QuestionInExam>) msg[1]).forEach(questionInExamObservable::add);
							final boolean flag1 = (boolean) msg[2];
							Platform.runLater(() -> {
								if (flag1 == false) {
									blockPassQuestionButton = true;
								} else {
									blockPassQuestionButton = false;
								}
								try {
									openScreen(tempEvent, "UpdateQuestionInExam");
								} catch (IOException e) {

									e.printStackTrace();
								}
							});
						} catch (NullPointerException exception) {
							errorMsg("exam does not have any question");
							blockPassQuestionButton = false;

						}
						break;
					}
					case ("setExam"): {
						if ((String) msg[1] != null) {
							infoMsg("The exam added successfully");
							try {
								refreshPageAfterCreate(tempEvent, "CreateExam");
								for (int i = 0; i < questionsInExamTableView.getItems().size(); i++) {
									questionsInExamTableView.getItems().clear();
								}
							} catch (IOException e) {

								e.printStackTrace();
							}
						} else
							errorMsg("There was a problem in database");
						break;
					}

					case ("createChangingRequest"): {
						if ((Boolean) msg[1] != null)
							infoMsg("The Exatnd time for the exam added successfully");
						else
							errorMsg("There was a problem in database");
						break;
					}

					case ("setExamCode"): /* the server return true/false if the executed exam created or not */
					{
						if ((boolean) msg[1] == true) {/* if the answer is correct */
							infoMsg("Exam code created successfully");
							try {
								refreshPageAfterCreate(tempEvent, "CreateExamCode");
							} catch (IOException e) {

								e.printStackTrace();
							}
						} else {
							errorMsg("There is already a code like that, please choose another code");
						}
						break;
					}

					case ("updateExam"): /* the server return true/false if the exam updated or not */
					{
						if ((boolean) msg[1] == true) {/* The exam was udated */
							infoMsg("Exam updated successfully");
							examsTableView.refresh();
						} else {
							exams.remove(exams.indexOf(examSelected));/* The exam didn't changed */
							exams.add(oldExam);/* Load the old exam */
							errorMsg("This exam is in active exam.");
							examsTableView.getSortOrder().setAll(examIDTable);
						}
						break;
					}

					case ("deleteExam"): /* the server return true/false if the exam deleted or not */
					{
						if ((boolean) msg[1] == true) {
							infoMsg("Exam deleted successfully");
							exams.remove(exams.indexOf(examSelected));
						} else {

							errorMsg("This exam is in active exam.");
						}
						break;
					}

					case ("getStudenstInExam"): /* get the student who performed the exam before */
					{
						/* set the values to the containers */
						ObservableList<StudentPerformExam> observablelistOfStudentInExam = FXCollections
								.observableArrayList((ArrayList<StudentPerformExam>) msg[1]);
						studentId.setCellValueFactory(new PropertyValueFactory<>("userId"));
						studentName.setCellValueFactory(new PropertyValueFactory<>("userFullname"));
						grade.setCellValueFactory(new PropertyValueFactory<>("grade"));
						status.setCellValueFactory(new PropertyValueFactory<>("isApproved"));
						reasonForChangeGrade.setCellValueFactory(new PropertyValueFactory<>("reasonForChangeGrade"));
						studnetInExamTableView.setItems(observablelistOfStudentInExam);
						break;
					}
					}
				});
			}
		} catch (IOException e) {

			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The initialize function intialize function go first after loading fxml The
	 * method initialize the javaFX screens
	 * 
	 * @author Aviv Mahulya
	 */
	public void initialize(URL url, ResourceBundle rb) {

		messageToServer[4] = getMyUser().getUsername();
		setUnVisible();
		switch (pageLabel.getText()) {
		case ("Home screen"): {/* If its the home page */
			{
				userText.setText(getMyUser().getFullname());
				dateLabel.setText(dateFormat.format(currentTime));// Setting Current Date
			}
			break;
		}
		case ("Update question in exam"): {/* If its the "update question page */
			updateBtn.setDisable(true);
			if (blockPassQuestionButton) {
				passQuestionL.setDisable(true);
				passQuestionR.setDisable(true);
				questionsInExamTableView.setEditable(false);
				allertText.setFill(Color.RED);
				allertText.setText("You can't edit this exam cause its an active exam");
			}

			setToQuestionInExamTableView();/* Load the questionInExam TableView */
			connect(this); // connecting to server
			messageToServer[0] = "getQuestionsToTable";
			messageToServer[1] = tempExamId.substring(0, 4);
			messageToServer[2] = getMyUser().getUsername();
			chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
			break;
		}
		case ("Create question"):
		case ("Create exam"):
		case ("Update question"):
		case ("Create exam code"):
		case ("Extend exam time"):
		case ("Check exam"):
		case ("Lock exam"):

		case ("Update exam"): {

			connect(this);

			switch (pageLabel.getText()) {
			case ("Create exam"): {
				typeComboBox.setItems(FXCollections.observableArrayList("computerized", "manual"));
				break;
			}
			// this case is for the director for loading the students to the tableview
			case ("Check exam"): {
				if (getMyUser().getRole().equals("director")) {
					subjectsComboBox.setVisible(false);
					coursesComboBox.setVisible(false);
					executedExamsComboBox.setVisible(false);
					studnetInExamTableView.setPrefHeight(470);
					studnetInExamTableView.setLayoutY(80);
					confirmButton.setVisible(false);
					changeGradeButton.setVisible(false);
					try {
						loadStudenstInExam(null);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				break;
			}
			}
			messageToServer[0] = "getSubjects";
			if (getMyUser().getRole().equals("director"))
				messageToServer[1] = null;
			else
				messageToServer[1] = getMyUser().getUsername();
			messageToServer[2] = null;
			chat.handleMessageFromClientUI(messageToServer);// send the message to server
			break;

		}
		}

	}

	/***********************************************************
	 * Opening screens action-events
	 ***************************************/

	/**
	 * The openScreen function getting ActionEvent and "String" address to fmxl file
	 * and open it
	 *
	 * @author Or Edri
	 */
	public void openScreen(ActionEvent e, String screen) throws IOException {

		Parent tableViewParent = FXMLLoader.load(getClass().getResource("/boundary/" + screen + ".fxml"));
		Scene tableViewScene = new Scene(tableViewParent);
		tableViewScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		// This line gets the Stage information
		Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();

		window.setOnCloseRequest(event -> {
			uc.connect(uc);
			messageToServer[0] = "performLogout";
			messageToServer[1] = getMyUser().getUsername();
			messageToServer[2] = null;
			messageToServer[4] = getMyUser().getUsername();
			chat.handleMessageFromClientUI(messageToServer);
			Platform.exit();

		});
		window.setScene(tableViewScene);
		window.show();
		  Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
		  window.setX((primScreenBounds.getWidth() - window.getWidth()) / 2);
		  window.setY((primScreenBounds.getHeight() - window.getHeight()) / 2);
	}

	/**
	 * The openExtendExamTimeScreen function open the screen ExtendExamTime
	 *
	 * @author Tom Zarhin
	 */
	public void openExtendExamTimeScreen(ActionEvent e) throws IOException {
		openScreen(e, "ExtendExamTime");
	}

	/**
	 * The openTeacherSeeExamScreen function open the screen TeacherSeeExam
	 *
	 * @author Tom Zarhin
	 */
	public void openTeacherSeeExamScreen(ActionEvent e) throws IOException {
		openScreen(e, "TeacherSeeExams");
	}

	/**
	 * The openUpdateQuestionScreen function open the screen UpdateQuestion
	 *
	 * @author Tom Zarhin
	 */
	public void openUpdateQuestionScreen(ActionEvent e) throws IOException {
		openScreen(e, "UpdateQuestion");
	}

	/**
	 * The openExamCodeScreen function open the screen CreateExamCode
	 *
	 * @author Tom Zarhin
	 */
	public void openExamCodeScreen(ActionEvent e) throws IOException {
		openScreen(e, "CreateExamCode");
	}

	/**
	 * The openCreateExam function open the screen CreateExam
	 *
	 * @author Tom Zarhin
	 */
	public void openCreateExam(ActionEvent e) throws IOException {
		openScreen(e, "CreateExam");
	}

	/**
	 * The openCreateQuestion function open the screen CreateQuestion
	 *
	 * @author Tom Zarhin
	 */
	public void openCreateQuestion(ActionEvent e) throws IOException {
		openScreen(e, "CreateQuestion");
	}

	/**
	 * The openUpdateExamScreen function open the screen UpdateExam
	 *
	 * @author Tom Zarhin
	 */
	public void openUpdateExamScreen(ActionEvent e) throws IOException {
		openScreen(e, "UpdateExam");
	}

	/**
	 * The openCheckExamScreen function open the screen CheckExam
	 *
	 * @author Tom Zarhin
	 */
	public void openCheckExamScreen(ActionEvent e) throws IOException {
		openScreen(e, "CheckExam");
	}

	/**
	 * The openLockExamScreen function open the screen LockExam
	 *
	 * @author Tom Zarhin
	 */
	public void openLockExamScreen(ActionEvent e) throws IOException {
		openScreen(e, "LockExam");
	}

	/**
	 * The openScreen function getting "String" address to fmxl file and open it
	 *
	 * @author Tom Zarhim
	 */
	public void openScreen(String screen) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/boundary/" + screen + ".fxml"));
			Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
			Stage stage = Main.getStage();
			stage.setTitle("Error message");
			stage.setScene(scene);
			stage.show();
		} catch (Exception exception) {
			System.out.println("Error in opening the page");
		}
	}

	/**
	 * The closeScreen function close the window if close button was pressed
	 *
	 * @author Tom Zarhin
	 */
	public void closeScreen(ActionEvent e) throws IOException, SQLException {
		final Node source = (Node) e.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
		questionInExamObservable.clear();
		if (getMyUser().getRole().equals("teacher"))
			openScreen("boundary", "HomeScreenTeacher");
		else
			openScreen("directorBoundary", "systemInformationDirector");
	}

	/**************************************************
	 * update question screen
	 *******************************************************/

	/**
	 * The createBackUpQuestion function make a new question to save the oldest
	 * question before changing it
	 *
	 * @author Or Edri
	 */
	public Question createBackUpQuestion(Question questionSelected) {
		return new Question(questionSelected.getId(), questionSelected.getTeacherName(),
				questionSelected.getQuestionContent(), questionSelected.getAnswer1(), questionSelected.getAnswer2(),
				questionSelected.getAnswer3(), questionSelected.getAnswer4(), questionSelected.getCorrectAnswer());
	}

	/**
	 * The loadQuestions function request to load questions to table view
	 *
	 * @author Or Edri
	 */
	public void loadQuestions(ActionEvent e) throws IOException {
		/* ask for the qustions text */
		try {
			String subject = subjectsComboBox.getValue(); // get the subject code

			String[] coursesSubString;
			if (subject == null)
				return;
			String course = coursesComboBox.getValue();
			String[] subjectSubString = subject.split("-");
			connect(this); // connecting to server
			messageToServer[0] = "getQuestionsToTable";
			if (getMyUser().getRole().equals("teacher")) {
				coursesSubString = course.split("-");
				messageToServer[1] = subjectSubString[0].trim() + "" + coursesSubString[0].trim();
				messageToServer[2] = getMyUser().getUsername();
			} else {
				messageToServer[1] = subjectSubString[0].trim();
				messageToServer[2] = null;
			}
			chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * The changeQuestionContentOnTable function change the question content on
	 * table view
	 *
	 * @author Or Edri
	 */
	public void changeQuestionContentOnTable(CellEditEvent<Question, String> edittedCell) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		oldQuestion = createBackUpQuestion(questionSelected);
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getQuestionContent())) {
			questionSelected.setQuestionContent(edittedCell.getNewValue().toString());
			updateQuestion(questionSelected);
		}
	}

	/**
	 * The changeAnswer1OnTable function change the answer 1 content on table view
	 *
	 * @author Or Edri
	 */
	public void changeAnswer1OnTable(CellEditEvent<Question, String> edittedCell) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		oldQuestion = createBackUpQuestion(questionSelected);
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getAnswer1())) {
			questionSelected.setAnswer1(edittedCell.getNewValue().toString());
			updateQuestion(questionSelected);
		}
	}

	/**
	 * The changeAnswer2OnTable function change the answer 2 content on table view
	 *
	 * @author Or Edri
	 */
	public void changeAnswer2OnTable(CellEditEvent<Question, String> edittedCell) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		oldQuestion = createBackUpQuestion(questionSelected);
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getAnswer2())) {
			questionSelected.setAnswer2(edittedCell.getNewValue().toString());
			updateQuestion(questionSelected);

		}
	}

	/**
	 * The changeAnswer3OnTable function change the answer 3 content on table view
	 *
	 * @author Or Edri
	 */
	public void changeAnswer3OnTable(CellEditEvent<Question, String> edittedCell) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		oldQuestion = createBackUpQuestion(questionSelected);
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getAnswer3())) {
			questionSelected.setAnswer3(edittedCell.getNewValue().toString());
			updateQuestion(questionSelected);

		}
	}

	/**
	 * The changeAnswer4OnTable function change the answer 4 content on table view
	 *
	 * @author Or Edri
	 */
	public void changeAnswer4OnTable(CellEditEvent<Question, String> edittedCell) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		oldQuestion = createBackUpQuestion(questionSelected);
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getAnswer4())) {
			questionSelected.setAnswer4(edittedCell.getNewValue().toString());
			updateQuestion(questionSelected);
		}
	}

	/**
	 * The changeCorrectAnswerOnTable function change the correct answer on table
	 * view
	 *
	 * @author Or Edri
	 */
	public void changeCorrectAnswerOnTable(CellEditEvent<Question, String> edittedCell) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		oldQuestion = createBackUpQuestion(questionSelected);
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getCorrectAnswer())) {
			questionSelected.setCorrectAnswer(edittedCell.getNewValue().toString());
			updateQuestion(questionSelected);
		}
	}

	/**
	 * The updateQuestion function updating the question that has been selected
	 * 
	 * @author Or Edri
	 */
	public void updateQuestion(Question questionSelected) {
		messageToServer[0] = "updateQuestion";
		messageToServer[1] = questionSelected;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer); // send the request to the server
	}

	/**
	 * The deleteQuestion function deleting question from the table view and from
	 * the database
	 * 
	 * @author Or Edri
	 */
	public void deleteQuestion(ActionEvent e) {
		questionSelected = questionTableView.getSelectionModel().getSelectedItem();
		if (questionSelected == null) {
			errorMsg("Please select question");
			return;
		}
		messageToServer[0] = "deleteQuestion";
		messageToServer[1] = questionSelected;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer); // send the request to the server
	}

	/**********************************************************
	 * create question screen
	 ***********************************************/

	/**
	 * The createQuestionClick function create a new question
	 * 
	 * @author Tom Zarhin
	 */
	public void createQuestionClick(ActionEvent e) throws IOException {
		tempEvent = e;
		if (subjectsComboBox.getValue() == null) {
			errorMsg("Please choose subject");
			return;
		}
		if (courseInCreateQuestion.getItems().isEmpty()) {
			errorMsg("Please choose course");
			return;
		}
		Question question;

		int correctAnswer = 0;
		if (correctAns1.isSelected()) {
			correctAnswer = 1;
		}
		if (correctAns2.isSelected()) {
			correctAnswer = 2;
		}
		if (correctAns3.isSelected()) {
			correctAnswer = 3;
		}
		if (correctAns4.isSelected()) {
			correctAnswer = 4;
		}

		if ((answer1.getText().equals("")) || ((answer2.getText().equals(""))) || (answer3.getText().equals(""))
				|| (answer4.getText().equals("")) || (correctAnswer == 0) || (questionName.getText().equals(""))) {
			errorMsg("Not all fields are completely full");
		} else {
			question = new Question(null, getMyUser().getUsername(), questionName.getText().trim(),
					answer1.getText().trim(), answer2.getText().trim(), answer3.getText().trim(),
					answer4.getText().trim(), String.valueOf(correctAnswer));
			String subject = subjectsComboBox.getValue();
			String[] subjectSubString = subject.split("-");
			ArrayList<String> courses = (ArrayList<String>) coursesListToCreateQuestion.stream()
					.collect(Collectors.toList());
			/* Send to the server request to add question in the DB */
			connect(this); // connecting to server
			messageToServer[0] = "SetQuestion";
			messageToServer[1] = subjectSubString[0].trim();
			messageToServer[2] = question;
			messageToServer[3] = courses;

			chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
		}
	}

	/**
	 * this listener set in comboBox the courses from system
	 * 
	 * @param event
	 */
	public void coursesToList(ActionEvent event) {

		try {

			String[] courseSubString = coursesComboBox.getValue().split("-");
			if (!coursesListToCreateQuestion.contains(courseSubString[0] + courseSubString[1])) {
				coursesListToCreateQuestion.add(courseSubString[0] + courseSubString[1]);
				courseInCreateQuestion.setItems(coursesListToCreateQuestion);
				subjectsComboBox.setDisable(true);
			}
		} catch (NullPointerException e) {

		}
	}

	/**
	 * removeCoursesFromList(ActionEvent event)
	 * 
	 * @param ActionEvent
	 *            event If there are no items in the Courses combobox remove the
	 *            subject combobox
	 * 
	 * @author Or Edri
	 */
	public void removeCoursesFromList(ActionEvent event) {
		if (courseInCreateQuestion.getSelectionModel().getSelectedItem() != null) {
			coursesListToCreateQuestion.remove(courseInCreateQuestion.getSelectionModel().getSelectedItem());
			coursesComboBox.getSelectionModel().clearSelection();
			if (courseInCreateQuestion.getItems().isEmpty())
				subjectsComboBox.setDisable(false);
		}
	}

	/**************************************
	 * Update exam screen
	 ***********************************************************************/

	/**
	 * The loadExams function requesting the exams from the database
	 * 
	 * @author Tom Zarhin
	 */
	public void loadExams(ActionEvent e) throws IOException {
		String examIDStart;
		String toSend;
		if (!pageLabel.getText().equals("Create exam code") && !pageLabel.getText().equals("Update exam")) {
			toSend = "getExecutedExams";
			messageToServer[2] = getMyUser().getUsername();// send the user name of the client
			if (pageLabel.getText().equals("Lock exam") || pageLabel.getText().equals("Extend exam time"))
				messageToServer[3] = "LockType";
			else
				messageToServer[3] = "CheckType";

		} else
			toSend = "getExams";
		/* ask for the exams name */
		if (coursesComboBox.getValue() == null)
			return;
		String[] subjectSubString = subjectsComboBox.getValue().split("-");
		String[] examSubString = coursesComboBox.getValue().split("-");
		examIDStart = subjectSubString[0].trim() + "" + examSubString[0].trim();
		if (examIDStart.equals("") || examIDStart == null)
			return;
		connect(this); // connecting to server
		messageToServer[0] = toSend;
		messageToServer[1] = examIDStart;
		chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
	}

	/**
	 * loadStudenstInExam(ActionEvent e)
	 * 
	 * @param ActionEvent
	 *            event The method send request of the student who performed
	 *            specific exam
	 * 
	 * @author Or Edri
	 */
	public void loadStudenstInExam(ActionEvent e) throws IOException {
		connect(this); // connecting to server
		messageToServer[0] = "getStudenstInExam";
		if (getMyUser().getRole().equals("director")) {
			messageToServer[1] = tempExamId;
			messageToServer[2] = "director";
		} else {
			messageToServer[1] = executedExamsComboBox.getValue();
		}
		chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
	}

	/**
	 * The changeRemarksForTeacherOnTable function changing the remarks for teacher
	 * 
	 * @author Tom Zarhin
	 */
	public void changeRemarksForTeacherOnTable(CellEditEvent<Exam, String> edittedCell) throws IOException {
		examSelected = examsTableView.getSelectionModel().getSelectedItem();
		oldExam = new Exam();/* save a copy of the exam'will be used in case that the changing failed */
		oldExam.setE_id(examSelected.getE_id());
		oldExam.setSolutionTime(examSelected.getSolutionTime());
		oldExam.setRemarksForTeacher(examSelected.getRemarksForTeacher());
		oldExam.setRemarksForStudent(examSelected.getRemarksForStudent());
		oldExam.setType(examSelected.getType());
		oldExam.setTeacherUserName(examSelected.getTeacherUserName());

		if (!edittedCell.getNewValue().toString().equals(examSelected.getRemarksForTeacher())) {
			examSelected.setRemarksForTeacher(edittedCell.getNewValue().toString());
			updateExam(examSelected);
		}
	}

	/**
	 * The changeRemarksForStudentOnTable function changing the remarks for student
	 * on the table view
	 * 
	 * @author Tom Zarhin
	 */
	public void changeRemarksForStudentOnTable(CellEditEvent<Exam, String> edittedCell) throws IOException {
		examSelected = examsTableView.getSelectionModel().getSelectedItem();
		oldExam = new Exam();/* save a copy of the exam'will be used in case that the changing failed */
		oldExam.setE_id(examSelected.getE_id());
		oldExam.setSolutionTime(examSelected.getSolutionTime());
		oldExam.setRemarksForTeacher(examSelected.getRemarksForTeacher());
		oldExam.setRemarksForStudent(examSelected.getRemarksForStudent());
		oldExam.setType(examSelected.getType());
		oldExam.setTeacherUserName(examSelected.getTeacherUserName());
		if (!edittedCell.getNewValue().toString().equals(examSelected.getRemarksForStudent())) {
			examSelected.setRemarksForStudent(edittedCell.getNewValue().toString());
			updateExam(examSelected);
		}
	}

	/**
	 * The changeTypeOnTable function changing the type of the exam
	 * 
	 * @author Tom Zarhin
	 */
	public void changeTypeOnTable(CellEditEvent<Exam, String> edittedCell) throws IOException {
		examSelected = examsTableView.getSelectionModel().getSelectedItem();
		oldExam = new Exam();/* save a copy of the exam'will be used in case that the changing failed */
		oldExam.setE_id(examSelected.getE_id());
		oldExam.setSolutionTime(examSelected.getSolutionTime());
		oldExam.setRemarksForTeacher(examSelected.getRemarksForTeacher());
		oldExam.setRemarksForStudent(examSelected.getRemarksForStudent());
		oldExam.setType(examSelected.getType());
		oldExam.setTeacherUserName(examSelected.getTeacherUserName());
		if (!edittedCell.getNewValue().toString().equals(examSelected.getType())) {
			examSelected.setType(edittedCell.getNewValue().toString());
			updateExam(examSelected);
		}
	}

	/**
	 * The viewQuestion function get the question in a specific exam
	 * 
	 * @author Tom Zarhin
	 */
	public void viewQuestion(ActionEvent e) throws IOException {
		try {
			tempEvent = e;
			Exam exam = examsTableView.getSelectionModel().getSelectedItem();
			connect(this); // connecting to server
			messageToServer[0] = "getQuestionInExam";
			messageToServer[1] = exam.getE_id();
			tempExamId = exam.getE_id();
			chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
		} catch (NullPointerException exception) {
			errorMsg("Please select exam");
		}
	}

	/**
	 * The updateExam function updating the exam in the database
	 * 
	 * @author Tom Zarhin
	 */
	public void updateExam(Exam examSelected) {
		messageToServer[0] = "updateExam";
		messageToServer[1] = examSelected;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer); // send the request to the server
	}

	/**
	 * The deleteExam function removing the exam from the database
	 * 
	 * @author Or Edri
	 */
	public void deleteExam(ActionEvent e) {
		examSelected = examsTableView.getSelectionModel().getSelectedItem();
		if (examSelected == null) {
			errorMsg("Please select exam");
			return;
		}
		messageToServer[0] = "deleteExam";
		messageToServer[1] = examSelected;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer); // send the request to the server
	}

	/**************************************
	 * (Create + Update) questions in exam screens
	 ***********************************************/

	/**
	 * The createExam function creating exam
	 * 
	 * @author Tom Zarhin
	 */
	@SuppressWarnings("static-access")
	public void createExam(ActionEvent e) {
		int sumOfPoints = 0;
		tempEvent = e;
		/* check if the teacher fill the form coreect */
		if (timeForExamHours.getText().equals("") || timeForExamMinute.getText().equals("")
				|| Integer.valueOf(timeForExamHours.getText()) < 0) {
			errorMsg("Please fill time for exam");
			return;
		}
		if (typeComboBox.getValue() == null) {
			errorMsg("Please select the type of exam");
			return;
		}
		if ((Integer.parseInt(timeForExamHours.getText()) <= 0 && Integer.parseInt(timeForExamMinute.getText()) <= 0)
				|| (Integer.parseInt(timeForExamHours.getText()) > 99
						|| Integer.parseInt(timeForExamMinute.getText()) > 99)) {
			errorMsg("invalid time");
			return;
		}
		/* check if the number of points is 100 */
		for (QuestionInExam q : questionInExamObservable) {
			sumOfPoints += q.getPoints();
			if (q.getPoints() == 0) {
				errorMsg("You cant set a question with 0 points.");
				return;
			}
		}
		if (sumOfPoints != 100) {
			errorMsg("Points are not match to 100");
			return;
		}
		Exam exam = new Exam();// creating a new exam;
		Time time = null;
		String[] courseID = coursesComboBox.getValue().split("-");// we want the course id
		String[] subjectSubString = subjectsComboBox.getValue().split("-");
		exam.setE_id(subjectSubString[0].trim() + "" + courseID[0].trim());// making the start of the id of the exam
		ArrayList<QuestionInExam> questioninexam = (ArrayList<QuestionInExam>) questionInExamObservable.stream()
				.collect(Collectors.toList());// making the observable a lis
		exam.setRemarksForStudent(remarksForStudent.getText());
		exam.setRemarksForTeacher(remarksForTeacher.getText());
		exam.setTeacherUserName(getMyUser().getUsername());
		time = time.valueOf(timeForExamHours.getText() + ":" + timeForExamMinute.getText() + ":00");// making a Time
																									// class format
		exam.setSolutionTime(time.toString());
		exam.setType(typeComboBox.getValue());
		/* ask the server to create exam in the DB */
		messageToServer[0] = "setExam";
		messageToServer[1] = questioninexam;
		messageToServer[2] = exam;
		messageToServer[4] = getMyUser().getUsername();
		connect(this);
		chat.handleMessageFromClientUI(messageToServer);// send the message to server
	}

	/**
	 * The createExam function moving the question to the question in exam table
	 * view
	 * 
	 * @author Tom Zarhin
	 */
	@SuppressWarnings("unchecked")
	public void toQuestionInExam(ActionEvent e) {
		int flag = 0;
		if (questionTableView.getSelectionModel().getSelectedItem() == null) {
			errorMsg("Please choose question");
			return;
		}
		if (!pageLabel.getText().equals("Update question in exam")) {
			subjectsComboBox.setDisable(true);
			coursesComboBox.setDisable(true);
		}
		QuestionInExam questioninexam = new QuestionInExam();// creating new questioninexam
		Question questionDetails = questionTableView.getSelectionModel().getSelectedItem();
		questioninexam.setQuestionID(questionDetails.getId());
		questioninexam.setTeacherUserName(questionDetails.getTeacherName());
		questioninexam.setQuestionContent(questionDetails.getQuestionContent());
		questioninexam.setPoints(0);
		setToQuestionInExamTableView();
		for (QuestionInExam item : questionInExamObservable) {
			if (item.getQuestionID().equals(questionDetails.getId()))
				flag = 1;
		}
		if (flag == 0) {
			questionObservableList.remove(questionTableView.getSelectionModel().getSelectedIndex());
			questionInExamObservable.add(questioninexam);
			questionsInExamTableView.getSortOrder().setAll(questionNameTableView);
		}

	}

	/**
	 * The setToQuestionInExamTableView function setting the question in the table
	 * view (question in exam)
	 * 
	 * @author Tom Zarhin
	 */
	private void setToQuestionInExamTableView() {
		questionPointsTableView.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
		questionNameTableView.setCellValueFactory(new PropertyValueFactory<>("questionID"));// display the id in the
																							// table view
		questionPointsTableView.setCellValueFactory(new PropertyValueFactory<>("points"));// display the points in table
																							// view // the
		questionsInExamTableView.setItems(questionInExamObservable);
	}

	/**
	 * The removeFromTableView function removing the question from the table view
	 * 
	 * @author Tom Zarhin/Or Edri
	 */
	@SuppressWarnings("unchecked")
	public void removeFromTableView(ActionEvent e) {
		ObservableList<QuestionInExam> questiontoremove;
		int flag = 0;
		try {
			questiontoremove = questionsInExamTableView.getSelectionModel().getSelectedItems();
			if (questionsInExamTableView.getSelectionModel().getSelectedItem() == null) {
				errorMsg("Please choose question to delete");
				return;
			}
			Question question = new Question();
			question.setQuestionContent(questiontoremove.get(0).getQuestionContent());
			question.setTeacherName(questiontoremove.get(0).getTeacherUserName());
			question.setId(questiontoremove.get(0).getQuestionID());
			for (Question item : questionObservableList) {
				if (item.getId().equals(question.getId()))
					flag = 1;
			}
			if (flag == 0) {
				questionObservableList.add(question);
				questionTableView.getSortOrder().setAll(qid);
			}

			questiontoremove.forEach(questionInExamObservable::remove);
			if (questionInExamObservable.isEmpty()) {
				subjectsComboBox.setDisable(false);
				coursesComboBox.setDisable(false);
			}
		} catch (RuntimeException exception) {

			return;
		}
		// add the question back to the tableview
	}

	/**
	 * The updateQuestionInExam function updating the question and the points of the
	 * exam
	 * 
	 * @author Tom Zarhin
	 */
	public void updateQuestionInExam(ActionEvent e) {
		int sumOfPoints = 0;
		for (QuestionInExam q : questionInExamObservable) {
			sumOfPoints += q.getPoints();
			if (q.getPoints() == 0) {
				errorMsg("You cant set a question with 0 points.");
				return;
			}
		}
		if (sumOfPoints != 100) {
			errorMsg("Points are not match to 100");
			return;
		}
		ArrayList<QuestionInExam> questioninexam = (ArrayList<QuestionInExam>) questionInExamObservable.stream()
				.collect(Collectors.toList());// making the observable a lis
		infoMsg("The question edited successfully");
		messageToServer[0] = "updateQuestionInExam";
		messageToServer[1] = questioninexam;
		messageToServer[2] = tempExamId;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer);// send the message to server
		try {
			chat.closeConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * The setPoints function set new points in the table view
	 * 
	 * @author Tom Zarhin
	 */
	@SuppressWarnings("unlikely-arg-type")
	public void setPoints(CellEditEvent<QuestionInExam, Float> edittedCell) {
		QuestionInExam questionSelected = questionsInExamTableView.getSelectionModel().getSelectedItem();
		if (!edittedCell.getNewValue().toString().equals(questionSelected.getPoints())) {
			questionSelected.setPoints(edittedCell.getNewValue());
			if (pageLabel.getText().equals("Update question in exam")) {
				updateBtn.setDisable(false);
			}
		}
		backButton.setDisable(false);
		passQuestionR.setDisable(false);
		passQuestionL.setDisable(false);
	}

	/**
	 * The blockBackButton function is for locking the back button when u editing
	 * points
	 * 
	 * @author Tom Zarhin
	 */
	public void blockBackButton() {
		backButton.setDisable(true);
		passQuestionR.setDisable(true);
		passQuestionL.setDisable(true);
	}

	/*********************************************
	 * Create exam code screen
	 ***********************************************************/

	/**
	 * The createExamCode function creating exam code
	 * 
	 * @author Or Edri
	 */
	public void createExamCode(ActionEvent e) {
		tempEvent = e;
		ExecutedExam exam;
		String examID = examComboBox.getValue();
		String executedExamId = examCode.getText();
		if (subjectsComboBox.getValue() == null) {
			errorMsg("Please choose subject");
			return;
		}
		if (coursesComboBox.getValue() == null) {
			errorMsg("Please choose course");
			return;
		}
		if (examComboBox.getValue() == null) {
			errorMsg("Please choose exam");
			return;
		}
		if (executedExamId.length() != 4) {
			errorMsg("You must enter exactly 4 letters & number");
			return;
		}
		for (int i = 0; i < executedExamId.length(); i++) {
			char ch = executedExamId.charAt(i);

			if (!((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9'))) {
				errorMsg("You must enter only letters and numbers.");
				return;
			}
		}
		exam = new ExecutedExam();
		exam.setExecutedExamID(executedExamId);
		exam.setTeacherName(getMyUser().getUsername());
		exam.setExam_id(examID);
		messageToServer[0] = "setExamCode";
		messageToServer[1] = exam;
		connect(this);
		chat.handleMessageFromClientUI(messageToServer);

	}

	/**
	 * The loadCourses function loading courses from database by subject
	 * 
	 * @author Or Edri
	 */
	public void loadCourses(ActionEvent e) throws IOException {
		try {
			if (pageLabel.getText().equals("Update question") && getMyUser().getRole().equals("director"))
				loadQuestions(e);
			else {
				/* ask for the courses name */
				String subject = subjectsComboBox.getValue(); // get the subject code
				String[] subjectSubString = subject.split("-");

				connect(this); // connecting to server
				messageToServer[0] = "getCourses";
				messageToServer[1] = subjectSubString[0].trim();
				if (getMyUser().getRole().equals("director")) {
					messageToServer[2] = null;
				} else {
					messageToServer[2] = getMyUser().getUsername();
					coursesComboBox.getSelectionModel().clearSelection();
				}
				chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject

			}
		} catch (NullPointerException exception) {
			return;
		}
	}

	/*********************************************
	 * Extend exam time screen
	 ***********************************************************/

	/**
	 * The createExtendTimeRequest function creating an extend time request
	 * 
	 * @author Tom Zarhin
	 */
	public void createExtendTimeRequest(ActionEvent e) throws IOException {
		if (timeForExamHours.getText().equals("") || timeForExamMinute.getText().equals("")) {
			errorMsg("Please fill the time you want to extend by");
			return;
		}
		if (reasonForChange.getText().trim().equals("")) {
			errorMsg("Please fill the reason for changing the time");
			return;
		}
		ExecutedExam executedexam = executedExamTableView.getSelectionModel().getSelectedItem();
		if (executedexam == null) {
			errorMsg("Please choose an exam");
			return;
		}
		RequestForChangingTimeAllocated request = new RequestForChangingTimeAllocated();
		request.setIDexecutedExam(executedexam.getExecutedExamID());
		request.setReason(reasonForChange.getText());
		request.setMenagerApprove("waiting");
		request.setTeacherName(getMyUser().getUsername());
		request.setTimeAdded(timeForExamHours.getText() + ":" + timeForExamMinute.getText() + ":00");
		connect(this); // connecting to server
		messageToServer[0] = "createChangingRequest";
		messageToServer[1] = request;
		chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
	}

	/***********************************************************
	 * TOM ????
	 ************************************************************/

	/**
	 * The lockSubject function locking the subject function (subject combobox)
	 * 
	 * @author Tom Zarhin
	 */
	public void lockSubject(ActionEvent e) {
		subjectsComboBox.setDisable(true);
	}

	/**
	 * The lockExam function locking the exam
	 * 
	 * @author Tom Zarhin
	 */
	public void lockExam(ActionEvent e) throws IOException {
		ExecutedExam executedexam = executedExamTableView.getSelectionModel().getSelectedItem();
		if (executedexam == null) {
			errorMsg("Please choose an exam");
			return;
		}
		executedExamTableView.getItems().remove(executedexam);
		executedExamTableView.refresh();
		connect(this); // connecting to server
		messageToServer[0] = "setExecutedExamLocked";
		messageToServer[1] = executedexam.getExecutedExamID();
		chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
	}

	/***********************************************************
	 * Check exam
	 ************************************************************/

	/**
	 * The setUnVisible function for showing to director information
	 * 
	 * @author Tom Zarhin
	 */
	public void setUnVisible() {
		try {
			if (getMyUser().getRole().equals("director")) {
				switch (pageLabel.getText()) {
				case "Update exam":
					btnDelete.setDisable(true);
					examsTableView.setEditable(false);
					break;
				case "Update question":
					coursesComboBox.setVisible(false);
					questionTableView.setEditable(false);
					btnDelete.setDisable(true);
					break;
				case "Update question in exam":
					questionsInExamTableView.setEditable(false);
					questionTableView.setDisable(true);
					passQuestionR.setDisable(true);
					passQuestionL.setDisable(true);
					break;
				}
				pageLabel.setVisible(false);
			}
		} catch (NullPointerException e) {

		}
	}

	/**
	 * The incHours function is for increase the hour by 1
	 * 
	 * @author Tom Zarhin
	 */
	public void incHours(ActionEvent e) {
		if (Integer.valueOf(timeForExamHours.getText()) < 60) {
			timeForExamHours.setText(String.valueOf(Integer.valueOf(timeForExamHours.getText()) + 1));
		}
	}

	/**
	 * The decHours function is for decreasing the hour by 1
	 * 
	 * @author Tom Zarhin
	 */
	public void decHours(ActionEvent e) {
		if (Integer.valueOf(timeForExamHours.getText()) > 0) {
			timeForExamHours.setText(String.valueOf(Integer.valueOf(timeForExamHours.getText()) - 1));
		}
	}

	/**
	 * The incMinutes function is for increase the minute by 1
	 * 
	 * @author Tom Zarhin
	 */
	public void incMinutes(ActionEvent e) {
		if (Integer.valueOf(timeForExamMinute.getText()) < 60) {
			timeForExamMinute.setText(String.valueOf(Integer.valueOf(timeForExamMinute.getText()) + 1));
		}
	}

	/**
	 * The decMinutes function is for increase the minute by 1
	 * 
	 * @author Tom Zarhin
	 */
	public void decMinutes(ActionEvent e) {
		if (Integer.valueOf(timeForExamMinute.getText()) > 0) {
			timeForExamMinute.setText(String.valueOf(Integer.valueOf(timeForExamMinute.getText()) - 1));
		}
	}
}
