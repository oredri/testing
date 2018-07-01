package control;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import entity.ExecutedExam;
import entity.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class UserControl implements Initializable {
	/* FXML Variables */
	@FXML
	private TextField userName;
	@FXML
	protected Label userNameLabel;
	@FXML
	protected Label authorLabel;
	@FXML
	protected Label dateLabel;
	@FXML
	private PasswordField password;
	@FXML
	private Label errorMsg;
	@FXML
	private ImageView errorImg;
	@FXML
	private ImageView errorImg1;
	@FXML
	private Button LoginBtn;
	@FXML
	private ImageView LoginButton;
	@FXML
	public Text userText;/// user name
	@FXML
	private Label userText1;
	@FXML
	private Button closeButton;
	@FXML
	public Button logoutBtn;
	protected ComboBox<String> subjectsComboBox;
	protected ComboBox<String> coursesComboBox;

	// class variables
	// date and author variables
	private Calendar currentCalendar = Calendar.getInstance();
	protected Date currentTime = currentCalendar.getTime();

	protected SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy ");
	protected Boolean isPerformExam;
	private Parent home_page_parent;
	private Scene home_page_scene;
	static Thread th;
	protected String userNameFromDB;
	protected  static ArrayList<Integer> messagesRead = new ArrayList<Integer>();

	protected Object[] messageToServer = new Object[5];// The message the user send to the message
	/* connections variables */
	static String ip;// server ip
	final  UserControl uc = this;
	final public static int DEFAULT_PORT = 5555;
	protected ChatClient chat; 
	private static User myUser;
	/**
	 * variables to show statisticRepot to teacher And Director 
	 */
	@FXML
	public BarChart<?, ?> barChart;
	@SuppressWarnings("rawtypes")
	public XYChart.Series histogram = null;
	public int[] sumGradeRanges;
	public ArrayList<ExecutedExam> GradeList;

	/* this method connected between client and server */
	/**
	 * connect(UserControl user) Arguments:(UserControl user The method connect the
	 * user as client to the server
	 * 
	 * @author Aviv Mahulya
	 */
	public Boolean connect(UserControl user) {
		try {
			chat = new ChatClient(ip, DEFAULT_PORT, user);
			return true;
		} catch (IOException exception) {
			System.out.println("Error: Can't setup connection!" + " Terminating client.");
			return false;
			// System.exit(1);
		}
	}

	@SuppressWarnings("static-access")
	public void setServerIp(String ip) {
		this.ip = ip;
	}

	public void initialize(URL arg0, ResourceBundle arg1) {

		myUser = new User();
		errorMsg.setVisible(false);
		errorImg.setVisible(false);
		errorImg1.setVisible(false);
		LoginBtn.setDefaultButton(true);
	}

	/**
	 * checkMessage(Object message) Arguments:Object message The method handle the
	 * message from server
	 * 
	 * @author Aviv Mahulya
	 */
	public void checkMessage(Object message) {
		try {
			chat.closeConnection();/* close the connection with the server */
			UserControl uc = this;
			Object[] msg = (Object[]) message;
		if (messagesRead.contains((int) msg[5])) {/* Check if the user already read this message */
			return;
		}
			messagesRead.add((int) msg[5]);/* Save the serial number of the message */
			User user = (User) msg[1];
			if (user == null) {
			
			Platform.runLater(() -> {
				String strError = (String)msg[2];
				if(strError.equals("wrong"))
					errorMsg("not valid details");
				if(strError.equals("connected"))
					errorMsg("The user is already connected");
				openScreen("boundary", "LoginGui");

			});

				return;
			}
			if (msg[0].toString().equals("checkUserDetails")) {/* check if the message contains the user details */
				if (user != null) {  
					switch (user.getRole()
							.toLowerCase()) {/* check the role of the user and set the permisiion according to that */
					case "teacher": {
						System.out.println("teacher screen request");
						Platform.runLater(new Runnable() {
							/* open the home screen of teacher */
							@Override
							public void run() {
								try {
									FXMLLoader loader = new FXMLLoader();
									loader.setLocation(getClass().getResource("/boundary/HomeScreenTeacher.fxml"));
									home_page_parent = loader.load();
									TeacherControl tController = loader.getController();
									String userName = user.getFullname().toLowerCase();
									tController.setUserText(userName);/* send the name to the controller */
									setMyUser(user);
									getMyUser().setFullname(user.getFullname());
									getMyUser().setRole(user.getRole());
									getMyUser().setUsername(user.getUsername());
									tController.setStudentAuthor_Date_name();/* send the name to the controller */
									home_page_parent.getStylesheets()
											.add(getClass().getResource("/style.css").toExternalForm());
									home_page_scene = new Scene(home_page_parent);
									Main.getStage().setTitle("HomeScreenTeacher");
									Main.getStage().setScene(home_page_scene);
									Main.getStage().setOnCloseRequest(event -> {
										uc.connect(uc);
										messageToServer[0] = "logoutProcess";
										messageToServer[1] = getMyUser().getUsername();
										messageToServer[2] = null;
										messageToServer[4] = getMyUser().getUsername();
										chat.handleMessageFromClientUI(messageToServer);
										Platform.exit();

									});
								} catch (IOException e) {
									e.printStackTrace();
								}
							}

						});
						break;
					}
					case "student": {
						System.out.println("student screen request");
						Platform.runLater(new Runnable() {
							/* open the home screen of student */
							@Override
							public void run() {
								try {
									FXMLLoader loader = new FXMLLoader();
									loader.setLocation(
											getClass().getResource("/studentBoundary/NewDesignHomeScreenStudent.fxml"));
									home_page_parent = loader.load();
									StudentControl sController = loader.getController();
									setMyUser(user);
									getMyUser().setFullname(user.getFullname());
									getMyUser().setUsername(user.getUsername());   
									getMyUser().setRole(user.getRole());
									sController.setStudentAuthor_Date_name();/* send the name to the controller */
									home_page_scene = new Scene(home_page_parent);
									home_page_parent.getStylesheets()
											.add(getClass().getResource("/style.css").toExternalForm());
									Main.getStage().setTitle("HomeScreenStudent");
									Main.getStage().setScene(home_page_scene);
									Main.getStage().setOnCloseRequest(event -> {
										uc.connect(uc);
										messageToServer[0] = "logoutProcess";
										messageToServer[1] = getMyUser().getUsername();
										messageToServer[2] = null;
										messageToServer[4] = getMyUser().getUsername();
										chat.handleMessageFromClientUI(messageToServer);
										Platform.exit();

									});
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
						break;
					}
					case "director": {
						System.out.println("director screen request");
						Platform.runLater(new Runnable() {
							/* open the home screen of director */
							@Override
							public void run() {
								try {
									FXMLLoader loader = new FXMLLoader();
									loader.setLocation(
											getClass().getResource("/directorBoundary/HomeScreenDirector.fxml"));
									home_page_parent = loader.load();
									home_page_parent.getStylesheets()
											.add(getClass().getResource("/style.css").toExternalForm());
									setMyUser(user);
									getMyUser().setFullname(user.getFullname());
									getMyUser().setUsername(user.getUsername());
									getMyUser().setRole(user.getRole());
									DirectorControl dController = loader.getController();
									String userName = user.getFullname().toLowerCase();/* get the name of the user */
									dController.setUserText(userName);/* send the name to the controller */
									dController.setStudentAuthor_Date_name();/* send the name to the controller */
									home_page_scene = new Scene(home_page_parent);
									Main.getStage().setTitle("HomeScreenDirector");
									Main.getStage().setScene(home_page_scene);
									Main.getStage().setOnCloseRequest(event -> {
										uc.connect(uc);
										messageToServer[0] = "logoutProcess";
										messageToServer[1] = getMyUser().getUsername();
										messageToServer[2] = null;
										messageToServer[4] = getMyUser().getUsername();
										chat.handleMessageFromClientUI(messageToServer);
										Platform.exit();

									});
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
						break;
					}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * loginPressed(ActionEvent e) *Arguments:Object message The method handle
	 * loginButtonPressed The method shall send the server message with the details
	 * of the user as written in the form
	 * 
	 * @author Aviv Mahulya
	 */
	public void loginPressed(ActionEvent e) throws IOException { 
		
		
		
		if (userName.getText().equals("") || password.getText().equals("")) {/* if one of the fields is empty */
			errorMsg.setVisible(true);
			errorImg.setVisible(true);
			errorImg1.setVisible(true);
		} else {
			/* send message to server */
			connect(this);
			messageToServer[0] = "checkUserDetails";
			messageToServer[1] = userName.getText();
			messageToServer[2] = password.getText();
			messageToServer[4] = userName.getText();
			chat.handleMessageFromClientUI(messageToServer);
		}
	}
	
	public void loginPressed(String userName,String password) throws IOException { //for testing
			/* send message to server */
			connect(this);
			messageToServer[0] = "checkUserDetails";
			messageToServer[1] = userName;
			messageToServer[2] = password;
			messageToServer[4] = userName;
			chat.handleMessageFromClientUI(messageToServer);
	}

	/**
	 * logoutPressed(ActionEvent e) Arguments:ActionEvent e The method handle logout
	 * Button Pressed The method shall send the server message with the details of
	 * the user. The method shall open the login screen
	 * 
	 * @author Aviv Mahulya
	 */
	public void logoutPressed(ActionEvent e) throws IOException {

		connect(this);
		messageToServer[0] = "logoutProcess";
		messageToServer[1] = getMyUser().getUsername();
		messageToServer[2] = null;
		messageToServer[4] = getMyUser().getUsername();
		chat.handleMessageFromClientUI(messageToServer);
		openScreen("boundary", "LoginGui");
	}

	/**
	 * setUserText(String userNameFromDB) Arguments:String userNameFromDB The method
	 * shall set the userName of USER to userNameFromDB
	 * 
	 * @author Aviv Mahulya
	 */
	public void setUserText(String userNameFromDB) {/* set the user name text in the "hello user" text */
		this.userNameFromDB = userNameFromDB;
		getMyUser().setFullname(userNameFromDB);
		if (this instanceof DirectorControl) {
			userText1.setText(userNameFromDB);
			return;
		}
		userText.setText(userNameFromDB);
	}

	/**
	 * closeButtonAction(ActionEvent e) Arguments:ActionEvent e The method handle
	 * the press on close button
	 * 
	 * @author Aviv Mahulya
	 */
	public void closeButtonAction(ActionEvent e) throws IOException {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	/**
	 * loadCourses(String typeList, String subject) Arguments:String typeList,
	 * String subject The method send to the server request of all the courses of
	 * the relevant teacher and relevant subject
	 * 
	 * @author Orit Hammer
	 */
	public void loadCourses(String typeList, String subject) throws IOException {

		if (subject == null)
			return;
		String[] subjectSubString = subject.split("-");
		connect(this); /* connecting to server */
		messageToServer[0] = "getCourses";
		messageToServer[1] = subjectSubString[0].trim();
		if (typeList == "All")
			messageToServer[2] = null;
		else
			messageToServer[2] = getMyUser().getUsername();
		chat.handleMessageFromClientUI(messageToServer); // ask from server the list of question of this subject
	}

	/**
	 * getMyUser() Arguments: The method return the current User details(entity)
	 * 
	 * @author Aviv Mahulya
	 */
	public static User getMyUser() {
		return myUser;
	}

	/**
	 * setMyUser() Arguments: The method set the current User details(entity)
	 * 
	 * @author Aviv Mahulya
	 */
	public static void setMyUser(User myUser) {
		UserControl.myUser = myUser;
	}

	/**************** methods To Use Of all users *******************************/
	/**
	 * openScreen(String boundary, String screen) Arguments:String boundary, String
	 * screen the method open the 'screen' from the directory 'boundary'
	 * 
	 * @author Or Edri
	 */
	public void openScreen(String boundary, String screen) {// open windows
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(
					getClass().getResource("/" + boundary + "/" + screen + ".fxml"));/* Get the relevant FXL file */
			Scene scene = new Scene(loader.load());
			scene.getStylesheets().add(getClass().getResource("/style.css")
					.toExternalForm());/* select the style sheet for the scene(css) */
			Stage stage = Main.getStage();
			stage.setOnCloseRequest(event -> {/* set listener for x button, perforn logout */
				if (isPerformExam != null && isPerformExam == false) {
					connect(this);
					messageToServer[0] = "logoutProcess";
					messageToServer[1] = getMyUser().getUsername();
					messageToServer[2] = null;
					messageToServer[4] = getMyUser().getUsername();
					chat.handleMessageFromClientUI(messageToServer);
					System.exit(0);
				}
			});
			stage.setTitle(screen);
			stage.setScene(scene);
			stage.show();
			  Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
			  stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
			  stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in opening the page");
		}
	}

	/**
	 * errorMsg(String message) Arguments:String message the method open the error
	 * message with the string 'message'
	 * 
	 * @author Aviv Mahulya
	 */
	public void errorMsg(String message) {// for error message
		new Alert(Alert.AlertType.ERROR, message).showAndWait();
	}

	public void infoMsg(String message) {
		new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
	}

	/**
	 * closeScreen(ActionEvent e) Arguments:ActionEvent e the method close the
	 * source screen of e
	 * 
	 * @author Tom Zarhin
	 */
	public void closeScreen(ActionEvent e) throws IOException, SQLException {
		final Node source = (Node) e.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

	/**
	 * setStudentAuthor_Date_name()
	 * 
	 * @author LeeOrr hammer
	 */
	public void setStudentAuthor_Date_name() {// *** move to userControl rename userDetails
		// userNameLabel.setText(getMyUser().getFullname());
		dateLabel.setText(dateFormat.format(currentTime));// Setting Current Date
		// authorLabel.setText("" + myUser.getRole());
	}

	/**
	 * ShowHistogramInBarChart()
	 * 
	 * @throws NullPointerException
	 * @author orit Hammer
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void ShowHistogramInBarChart() throws NullPointerException {
		if (myUser.getRole().equals("student") == false) {
			// set values in the bar chart
			for (int i = 0, j = 9; i < sumGradeRanges.length * 10 - 10; i += 10, j += 10) {
				histogram.getData().add(new XYChart.Data(i + "-" + j, sumGradeRanges[i / 10]));
			}
			histogram.getData().add(new XYChart.Data("90-100", sumGradeRanges[9]));
			barChart.getData().add(histogram);
		}
	}

	
	/**
	  * sumRangGrades(float grade)
	  * 
	  * @param grade
	  * @throws IndexOutOfBoundsException
	  * @author orit Hammer
	  */
	 public void sumRangGrades(float grade) throws IndexOutOfBoundsException {
	  if (myUser.getRole().equals("student") == false) {
	   if (grade >= 0 && grade <= 9)// grade between0to9
	    sumGradeRanges[0]++;
	   else if (grade >= 10 && grade <= 19)// grade between10to19
	    sumGradeRanges[1]++;
	   else if (grade >= 20 && grade <= 29)// grade between 20to29
	    sumGradeRanges[2]++;
	   else if (grade >= 30 && grade <= 39)// grade between 30to39
	    sumGradeRanges[3]++;
	   else if (grade >= 40 && grade <= 49)// grade between 40to49
	    sumGradeRanges[4]++;
	   else if (grade >= 50 && grade <= 59)// grade between 50to59
	    sumGradeRanges[5]++;
	   else if (grade >= 60 && grade <= 69)// grade between60to69
	    sumGradeRanges[6]++;
	   else if (grade >= 70 && grade <= 79)// grade between70to79
	    sumGradeRanges[7]++;
	   else if (grade >= 80 && grade <= 89)// grade between80to89
	    sumGradeRanges[8]++;
	   else if (grade >= 90 && grade <= 100)// grade between90to100
	    sumGradeRanges[9]++;
	  }
	 }
	  /**
	   * sumRangGrades(ExecutedExam eExam)
	   * 
	   * @param eExam
	    @throws IndexOutOfBoundsException
	    @author orit Hammer
	   */
	  public void sumRangGrades(ExecutedExam eExam) throws IndexOutOfBoundsException {
	   if (myUser.getRole().equals("student") == false) {
	    for (int i = 0; i < sumGradeRanges.length; i++)
	     sumGradeRanges[i] += eExam.getGradeRang()[i];
	   }
	  }
}
