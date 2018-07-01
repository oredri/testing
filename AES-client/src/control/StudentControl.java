package control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import entity.Exam;
import entity.ExamDetailsMessage;
import entity.ExecutedExam;
import entity.MyFile;
import entity.Question;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * @author lior hammer
 *
 */
public class StudentControl extends UserControl implements Initializable {

	protected static ArrayList<Question> questioninexecutedexam;
	protected static HashMap<String, Integer> examAnswers;// saves the question id and the answers
	private static int remainTime;
	private static ExecutedExam exam;
	private static Timer timer;
	protected static Boolean copyFlag = false;
	private List<File> fileFromClient;
	private int index = -1;
	private static String timeToString;
	protected static MouseEvent tempEvent;
	private Boolean isLocked = false;
	
	/********************** AddTime Variables ***************************/
	private static ArrayList<String> requestId; 

	/********************* Variable declaration *************************/
	// *********for HomePage***********//

	@FXML
	private ToggleGroup answers;
	// move to user

	/************************* Manual logo ***********************************/
	@FXML
	private ImageView wordLogo;
	@FXML
	private ImageView uploadImage;
	@FXML
	private Button uploadManualExamButton;
	@FXML
	private Label fileName;
	@FXML
	private Text remarksForStudentText;
	@FXML
	private Label executedCode;
	ObservableList<ExamDetailsMessage> detailsList = FXCollections.observableArrayList();
	ObservableList<String> executeExamList = FXCollections.observableArrayList();

	// *******for student execute or download exam*********//
	@FXML
	private TextField codeTextField;
	@FXML
	private CheckBox correctExamCodeCB;
	@FXML
	private TextField userIDTextField;
	@FXML
	private Button finishButton;
	// ******************** student perform exam ************//

	@FXML
	private RadioButton correctRadioButton2;
	@FXML
	private RadioButton correctRadioButton1;
	@FXML
	private RadioButton correctRadioButton4;
	@FXML
	private RadioButton correctRadioButton3;
	@FXML
	private TextField answer3;
	@FXML
	private TextField answer2;
	@FXML
	private TextField answer4;
	@FXML
	private TextField answer1;
	@FXML
	private Label pageLabel;
	@FXML
	private TextField questionContent;
	@FXML
	private Label courseName;
	@FXML
	private Button nextBTN;
	@FXML
	private Button prevBTN;
	@FXML
	private TextField timerTextField;

	// ******************** Showing exam copy ************//
	@FXML
	protected ComboBox<String> examCodeCombo;

	/************************ Class Methods *************************/

	/**
	 * initialize(URL url, ResourceBundle rb) Arguments:URL url, RResourceBundle rb
	 * The method initialize the javaFX screens by their names
	 * 
	 * @author Lior Hammer
	 */
	public void initialize(URL url, ResourceBundle rb) {
		// connect(this);
		isPerformExam = false;
		messageToServer[4] = getMyUser().getUsername();
		switch (pageLabel.getText()) {
		case ("Perform exam"): {
			isPerformExam = true;
			correctRadioButton1.setVisible(true);
			correctRadioButton2.setVisible(true);
			correctRadioButton3.setVisible(true);
			correctRadioButton4.setVisible(true);
			answer1.setStyle("-fx-background-color: white;");
			answer2.setStyle("-fx-background-color: white;");
			answer3.setStyle("-fx-background-color: white;");
			answer4.setStyle("-fx-background-color: white;");
			answer1.setVisible(true);
			answer2.setVisible(true);
			answer3.setVisible(true);
			answer4.setVisible(true);
			if (copyFlag == true)
				nextQuestion(null);
			prevBTN.setVisible(false);
			if (copyFlag == false) {
				requestId = new ArrayList<String>();
				examAnswers = new HashMap<String, Integer>();
				nextQuestion(null);
				// timerTextField.setText("123");
				// s=solutionTime.toString();
				// timerTextField.setText(s);
				startTime();
				Platform.runLater(() -> {
					remarksForStudentText.setText(exam.getExam().getRemarksForStudent());
					courseName.setText(exam.getExecutedExamID());
				});
			}
			break;
		}
		case ("Manual exam"): {
			requestId = new ArrayList<String>();
			examAnswers = new HashMap<String, Integer>();
			isPerformExam = true;
			Platform.runLater(() -> {
				executedCode.setText(exam.getExecutedExamID());
				remarksForStudentText.setText(exam.getExam().getRemarksForStudent());
			});
			startTime();
			break;
		}
		case ("Home Screen"): {/* If its the home page */
			authorLabel.setText(getMyUser().getRole());
			userNameLabel.setText(getMyUser().getFullname());
			dateLabel.setText(dateFormat.format(currentTime));// Setting Current Date
			break;
		}
		default:
			return;
		}
	}

	/**
	 * startTime() Arguments:No args The method start the time for the students that
	 * start executing exam
	 * 
	 * @author Lior Hammer
	 */
	@SuppressWarnings("deprecation")
	private void startTime() {
		final StudentControl sControl = this;
		try {
			chat = new ChatClient(ip, DEFAULT_PORT, sControl);
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		remainTime = Time.valueOf(exam.getSolutionTime()).getHours() * 3600
				+ Time.valueOf(exam.getSolutionTime()).getMinutes() * 60
				+ Time.valueOf(exam.getSolutionTime()).getSeconds();// remain
																	// is
																	// the
																	// time
																	// is
																	// seconds
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				try {
					chat.closeConnection();
				} catch (IOException e) {
					e.printStackTrace();
				}
				connect(sControl);
				int sec = setInterval();
				if (remainTime == 1) {
					endExam("time is over");
				}
				timeToString = intToTime(sec).toString();
				timerTextField.setText(timeToString);
			}
		}, 1000, 1000);
	}

	{

	}

	/**
	 * endExam(String message) Arguments:message The method called when the time is
	 * over or the exam has been locked by the teacher , the message is for the
	 * reason that explain why the exam has been finished
	 * 
	 * @author Lior Hammer
	 */
	public void endExam(String message) {
		timer.cancel();
		Platform.runLater(() -> errorMsg(message));
		try {
			questionContent.setText(message + "  click Finish");
			correctRadioButton1.setVisible(false);
			correctRadioButton2.setVisible(false);
			correctRadioButton3.setVisible(false);
			correctRadioButton4.setVisible(false);
			answer1.setVisible(false);
			answer2.setVisible(false);
			answer3.setVisible(false);
			answer4.setVisible(false);
		} catch (NullPointerException exception) {
			uploadManualExamButton.setVisible(false);
			try {
				uploadFileToServer(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * setInterval() Arguments: No args The method check if the exam executed time
	 * has over
	 * 
	 * @author Aviv Mahulya
	 */
	private static final int setInterval() {
		if (remainTime == 1)
			timer.cancel();
		return --remainTime;
	}

	/**
	 * Time intToTime(int seconds) Arguments: seconds The method transform from
	 * second to the relevant time units
	 * 
	 * @author Lior Hammer
	 */
	public static Time intToTime(int seconds) {
		int hours = seconds / 3600;
		int minutes = (seconds % 3600) / 60;
		int sec = seconds % 60;
		@SuppressWarnings("deprecation")
		Time t = new Time(hours, minutes, sec);
		return t;
	}

	/********************* general Functions *************************/

	/**
	 * goToHomePressed(ActionEvent e) Arguments: ActionEvent The method catch an
	 * click on the "go home" button
	 * 
	 * @author Lior Hammer
	 */
	@FXML
	public void goToHomePressed(ActionEvent e) throws Exception {
		closeScreen(e);
		openScreen("studentBoundary", "NewDesignHomeScreenStudent");
	}

	/********************* Student Home Screen listeners *************************/
	/**
	 * myGradesPressed(ActionEvent e) Arguments: ActionEvent The method catch an
	 * click on the "my grades" button
	 * 
	 * @author Lior Hammer
	 */
	public void myGradesPressed(ActionEvent e) {
		try {
			closeScreen(e);
			openScreen("studentBoundary", "MyGradesScreen");
		} catch (IOException e1) {

			e1.printStackTrace();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}
	}

	/**
	 * orderExamCopyPressed(ActionEvent e) Arguments: ActionEvent The method catch
	 * an click on the "order" button
	 * 
	 * @author Lior Hammer
	 */
	public void orderExamCopyPressed(ActionEvent e) {
		try {
			closeScreen(e);
			openScreen("studentBoundary", "OrderExamCopyScreen");
		} catch (IOException e1) {

			e1.printStackTrace();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

	}

	/**
	 * excecuteMorCExamPressed(ActionEvent e) Arguments: ActionEvent The method
	 * catch an click on the "execute exam" button
	 * 
	 * @author Lior Hammer
	 */
	public void excecuteMorCExamPressed(ActionEvent e) {
		try {
			closeScreen(e);
			openScreen("studentBoundary", "ManualAndComputerizeExamScreen");
		} catch (IOException e1) {

			e1.printStackTrace();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}
	}

	/***************************
	 * Student excecute exam
	 * 
	 * @throws SQLException
	 * @throws IOException
	 ****************************/
	public void excecuteExam(ActionEvent e) throws IOException, SQLException {// click on the button "execute exam"
		if (codeTextField.getText().equals("") || userIDTextField.getText().equals((""))) {
			errorMsg("Error in executed exam id");
			return;
		} else if (!userIDTextField.getText().equals((getMyUser().getUserID()))) {
			errorMsg("Your ID is incorrect"); // if user ID isn't correct
			return;
		}
		// everything fine
		copyFlag = false;
		exam = new ExecutedExam();
		exam.setExecutedExamID(codeTextField.getText());
		connect(this); // connecting to server
		messageToServer[0] = "checkExecutedExam";
		messageToServer[1] = exam.getExecutedExamID();
		messageToServer[2] = getMyUser().getUsername();
		chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
	}

	/************************* checking message ***********************************/
	// for all windows

	/**
	 * @see control.UserControl#checkMessage(java.lang.Object) checking the message
	 *      that sent by the server and call the relevant methods
	 * @author Lior Hammer
	 */
	@Override
	public void checkMessage(Object message) {
		try {
			chat.closeConnection();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		final Object[] msgFromServer = (Object[]) message;
		if (messagesRead.contains((int) msgFromServer[5])) {
			return;
		}
		messagesRead.add((int) msgFromServer[5]);
		if ((isPerformExam == true && msgFromServer[4].equals("all"))
				|| msgFromServer[4].equals(getMyUser().getUsername())) {

			try {
				chat.closeConnection();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println(msgFromServer[0].toString());
			Platform.runLater(() -> {
				try {
					switch (msgFromServer[0].toString()) {
					case "logoutProcess": {
						openScreen("boundary", "LoginGui");
						break;
					}

					case "checkExecutedExam": {
						if (msgFromServer[1] == null) {
							errorMsg("Can't perform this exam");
							return;
						}
						checkExecutedExam((Object[]) msgFromServer);
						break;
					}
					case "addTime": {
						// if(requestId.contains(arg0))
						addTimeToExam(msgFromServer);
						break;
					}

					case "setExecutedExamLocked": {
						if (((Boolean) msgFromServer[1] == true
								&& (((String) msgFromServer[2]).equals(exam.getExecutedExamID())))) {
							if (isLocked == false) {
								isLocked = true;
								endExam("Exam locked");
							}
						}
					}
					}
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	/**********************
	 * Handling message from server
	 * 
	 * @throws IOException
	 ***********************/

	/**
	 * checkExecutedExam(Object[] message)
	 * 
	 * @param message
	 * @throws IOException
	 *             The method check the type of executed exam that have been sent
	 *             from the server and execute block of code by the type of the
	 *             returned exam
	 * @author Tom Zarhin
	 */
	@SuppressWarnings("unchecked")
	private void checkExecutedExam(Object[] message) throws IOException {
		ArrayList<Question> questioninexam = (ArrayList<Question>) message[1];
		isLocked = false;
		Exam examToInsert = (Exam) message[2];
		exam.setSolutionTime(examToInsert.getSolutionTime());
		exam.getExam().setRemarksForStudent(examToInsert.getRemarksForStudent());
		questioninexecutedexam = questioninexam;
		if (examToInsert.getType().equals("manual")) {
			MyFile file = (MyFile) message[3];
			FileOutputStream fileOutputStream = null;
			BufferedOutputStream bufferedOutputStream = null;
			try {
				// receive file
				File diagFromClient = new File("Exams for student/" + file.getFileName());
				System.out.println("Please wait downloading file"); // reading file from socket
				fileOutputStream = new FileOutputStream(diagFromClient);
				bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				bufferedOutputStream.write(file.getMybytearray(), 0, file.getSize()); // writing byteArray to file
				bufferedOutputStream.flush(); // flushing buffers
				System.out.println("File " + diagFromClient + " downloaded ( size: " + file.getSize() + " bytes read)");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileOutputStream != null)
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				if (bufferedOutputStream != null)
					try {
						bufferedOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			Runtime.getRuntime()
					.exec("rundll32 url.dll,FileProtocolHandler " + "Exams for student\\" + file.getFileName());

			Platform.runLater(() -> openScreen("studentBoundary", "ManualExam"));
		} else {
			Platform.runLater(() -> openScreen("studentBoundary", "ComputerizedExam"));

		}
	}

	/**
	 * addTimeToExam
	 * 
	 * @param message
	 *            The method add time to a running exam
	 * @author Aviv Mahulya
	 */
	@SuppressWarnings("deprecation")
	public void addTimeToExam(Object[] message) {
		if (!requestId.contains((String) message[3])) {
			requestId.add((String) message[3]);
			int timeToAdd;
			Time timeFromMessage = (Time) message[2];
			if (exam.getExecutedExamID().equals((String) message[1])) {// if the student perform the relevant exam
				timeToAdd = timeFromMessage.getHours() * 3600 + timeFromMessage.getMinutes() * 60
						+ timeFromMessage.getSeconds();// reamin time is he time in secods
				remainTime += timeToAdd;
			}
		}
	}

	/************************ Student performing exam *************/

	/**
	 * nextQuestion(ActionEvent e) Arguments: ActionEvent The method catch an click
	 * on the "next" button and set the "next" button dis\enable according to the
	 * conditions
	 * 
	 * @author Lior Hammer
	 */
	@FXML
	private void nextQuestion(ActionEvent e) {
		if (index >= 0 && copyFlag == false)
			addAnswerToHashMap();
		index++;
		setQuestion();
		if (index + 1 == questioninexecutedexam.size()) {
			nextBTN.setVisible(false);
		} else if (index > 0)
			prevBTN.setVisible(true);

	}

	/**
	 * setQuestion()
	 *  setting the context of the relevant question on the screen
	 *  @author lior hammer
	 */
	private void setQuestion() {

		correctRadioButton1.setSelected(false);
		correctRadioButton2.setSelected(false);
		correctRadioButton3.setSelected(false);
		correctRadioButton4.setSelected(false);

		if (examAnswers.containsKey(questioninexecutedexam.get(index).getId())) {
			switch (examAnswers.get(questioninexecutedexam.get(index).getId())) {
			case 1:
				correctRadioButton1.setSelected(true);
				break;
			case 2:
				correctRadioButton2.setSelected(true);
				break;
			case 3:
				correctRadioButton3.setSelected(true);
				break;
			case 4:
				correctRadioButton4.setSelected(true);
				break;
			}
		}
		questionContent.setText(questioninexecutedexam.get(index).getQuestionContent());
		answer1.setText(questioninexecutedexam.get(index).getAnswer1());
		answer2.setText(questioninexecutedexam.get(index).getAnswer2());
		answer3.setText(questioninexecutedexam.get(index).getAnswer3());
		answer4.setText(questioninexecutedexam.get(index).getAnswer4());

		if (copyFlag == true) {
			Boolean correctAns = false;
			String qustionAnswer = questioninexecutedexam.get(index).getCorrectAnswer();
			answer1.setStyle("-fx-background-color: white;");
			answer2.setStyle("-fx-background-color: white;");
			answer3.setStyle("-fx-background-color: white;");
			answer4.setStyle("-fx-background-color: white;");
			String stdSelected;
			if (!examAnswers.isEmpty() && examAnswers.containsKey(questioninexecutedexam.get(index).getId()))
				stdSelected = examAnswers.get(questioninexecutedexam.get(index).getId()).toString();
			else if (examAnswers.size() < questioninexecutedexam.size() && examAnswers.size() > 0)
				stdSelected = "0";
			else
				stdSelected = "-1";
			try {
				switch (qustionAnswer) {
				case "1":
					if (stdSelected.equals("1")) {
						answer1.setStyle("-fx-background-color: green;");
						correctAns = true;
					}
					correctRadioButton1.setSelected(true);
					break;
				case "2":
					if (stdSelected.equals("2")) {
						answer2.setStyle("-fx-background-color: green;");
						correctAns = true;
					}
					correctRadioButton2.setSelected(true);
					break;
				case "3":
					if (stdSelected.equals("3")) {
						answer3.setStyle("-fx-background-color: green;");
						correctAns = true;
					}
					correctRadioButton3.setSelected(true);
					break;
				case "4":
					if (stdSelected.equals("4")) {
						answer4.setStyle("-fx-background-color: green;");
						correctAns = true;
					}
					correctRadioButton4.setSelected(true);
					break;
				}

				switch (stdSelected) {
				case "1":
					if (correctAns == false)
						answer1.setStyle("-fx-background-color: red;");
					break;
				case "2":
					if (correctAns == false)
						answer2.setStyle("-fx-background-color: red;");
					break;
				case "3":
					if (correctAns == false)
						answer3.setStyle("-fx-background-color: red;");
					break;
				case "4":
					if (correctAns == false)
						answer4.setStyle("-fx-background-color: red;");
					break;
				case "0":
					Alert alert = new Alert(AlertType.INFORMATION,
							"Please pay attention there is no answer for " + "the next question ", ButtonType.OK);
					alert.showAndWait();
				}
			} catch (NullPointerException e) {
				System.out.println("no answer for this question");
			}

		}

	}

	/**
	 * previousQuestion(ActionEvent e) Arguments: ActionEvent The method catch an
	 * click on the "previous" button and set the "previous" button dis\enable
	 * according to the conditions
	 * 
	 * @author Lior Hammer
	 */
	@FXML
	private void previousQuestion(ActionEvent e) {
		if (copyFlag == false)
			addAnswerToHashMap();
		index--;
		setQuestion();
		if (index == 0) {
			prevBTN.setVisible(false);
		}
		nextBTN.setVisible(true);
	}

	/**
	 * finishExam(ActionEvent e) Arguments: ActionEvent The method catch an click on
	 * the "Finish" button ans also send the data from the ckient to the server and
	 * go back to home screen
	 * 
	 * @author Lior Hammer
	 */
	@FXML
	private void finishExam(ActionEvent e) throws IOException {
		isPerformExam = false;
		if (timer != null)
			timer.cancel();
		if (!getMyUser().getRole().equals("student")) {
			((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
			((Stage) ((Node) tempEvent.getSource()).getScene().getWindow()).show();
			return;
		}
		boolean isFinish;
		if (copyFlag == false) {
			addAnswerToHashMap();
			if (questioninexecutedexam.size() != examAnswers.size()) {
				isFinish = false;
			} else {
				isFinish = true;
			}
			String details[] = new String[2];
			details[0] = exam.getExecutedExamID();
			details[1] = getMyUser().getUsername();
			connect(this);
			messageToServer[0] = "finishExam";
			messageToServer[1] = details;
			messageToServer[2] = examAnswers;
			messageToServer[3] = isFinish;
			chat.handleMessageFromClientUI(messageToServer);// send the message to server
			try {
				closeScreen(e);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		openScreen("studentBoundary", "NewDesignHomeScreenStudent");

		if (copyFlag == true) {
			index = -1;
			copyFlag = false; // when the user finish watching his exam copy flag should return be false
		}

	}

	/**
	 * addAnswerToHashMap() Arguments: No args The method add an answer to the
	 * HashMap if he answer on some question
	 * 
	 * @author Lior Hammer
	 */
	public void addAnswerToHashMap() {
		int selectedAnswer = 0;
		if (index <= -1)
			return;
		String q_id = questioninexecutedexam.get(index).getId();

		if (correctRadioButton1.isSelected()) {
			selectedAnswer = 1;
		} else if (correctRadioButton2.isSelected()) {
			selectedAnswer = 2;
		} else if (correctRadioButton3.isSelected()) {
			selectedAnswer = 3;
		} else if (correctRadioButton4.isSelected()) {
			selectedAnswer = 4;
		}
		if (selectedAnswer != 0) {
			if (examAnswers.containsKey(q_id)) {
				examAnswers.replace(q_id, selectedAnswer);
			} else {
				examAnswers.put(q_id, selectedAnswer);
			}
		}

	}

	/************************ Student perform manual exam *************/
	/**
	 * dragOver(DragEvent e) Arguments: DragEvent e The method handle a drag event
	 * when student want to set a file or take a file
	 * 
	 * @author Tom Zarhin
	 */
	public void dragOver(DragEvent e) {
		if (e.getDragboard().hasFiles()) {
			e.acceptTransferModes(TransferMode.ANY);
		}
	}

	/**
	 * dropFileToImage(DragEvent e) Arguments: DragEvent e The method handle a drop
	 * file
	 * 
	 * @author Tom Zarhin
	 */
	public void dropFileToImage(DragEvent e) {
		fileFromClient = e.getDragboard().getFiles();
		boolean wordFile = fileFromClient.get(0).getAbsolutePath().contains(".docx");
		if (wordFile) {
			wordLogo.setVisible(true);
			uploadManualExamButton.setDisable(false);
			fileName.setText(fileFromClient.get(0).getName());
			fileName.setVisible(true);
		} else {
			wordLogo.setVisible(false);
			uploadManualExamButton.setDisable(true);
			fileName.setVisible(false);
		}
	}

	/**
	 * uploadFileToServer(ActionEvent e) Arguments: ActionEvent e The method catch a
	 * click on upload and sent the file to the server
	 * 
	 * @author Tom Zarhin
	 */
	@SuppressWarnings("resource")
	public void uploadFileToServer(ActionEvent e) throws IOException {
		MyFile file = null;
		String LocalfilePath = null;
		try {
			file = new MyFile(fileFromClient.get(0).getName() + ".docx");
			LocalfilePath = fileFromClient.get(0).getAbsolutePath();
			try {
				File newFile = new File(LocalfilePath);
				byte[] mybytearray = new byte[(int) newFile.length()];
				FileInputStream fis = new FileInputStream(newFile);
				BufferedInputStream bis = new BufferedInputStream(fis);

				file.initArray(mybytearray.length);
				file.setSize(mybytearray.length);

				bis.read(file.getMybytearray(), 0, mybytearray.length);
			} catch (Exception exception) {
				System.out.println("Error send (Files)msg) to Server");
			}
		} catch (NullPointerException exception) {
			System.out.println("Student Didnt finish the manual exam");
		}
		String details[] = new String[2];
		details[0] = exam.getExecutedExamID();// the executed exam
		details[1] = getMyUser().getUsername();// name of the student
		connect(this);
		messageToServer[0] = "saveExamOfStudent";
		messageToServer[1] = details;
		messageToServer[2] = file;
		messageToServer[3] = e == null ? false : true;
		chat.handleMessageFromClientUI(messageToServer);
		Platform.runLater(() -> {
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/studentBoundary/NewDesignHomeScreenStudent.fxml"));
				Scene scene;
				scene = new Scene(loader.load());
				Stage stage = Main.getStage();
				stage.setScene(scene);
				stage.show();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

	}
	
	/**
	 * quitTheManualExam(ActionEvent e) Arguments: ActionEvent e The method catch a
	 * click on quit button that the student didnt finish the exam
	 * 
	 * @author Tom Zarhin
	 */
	public void quitTheManualExam(ActionEvent e) throws IOException {
		uploadFileToServer(null);
	}
	/**
	 * setDetails method set the details of the exam (executed exam id)
	 * 
	 * @author Tom Zarhin
	 */
	public void setDetails(String executedExamId) {
		exam.setExecutedExamID(executedExamId);
	}

}