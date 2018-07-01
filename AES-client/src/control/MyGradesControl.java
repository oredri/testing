package control;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import entity.ExamDetailsMessage;
import entity.Question;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MyGradesControl extends StudentControl implements Initializable {
	// *********for student see his grades AND can order exam***********//
	@FXML
	private TableView<ExamDetailsMessage> examGradesTable;
	@FXML
	private TableColumn<ExamDetailsMessage, String> examCodeColumn;
	@FXML
	private TableColumn<ExamDetailsMessage, String> courseCodeColumn;
	@FXML
	private TableColumn<ExamDetailsMessage, String> gradeColumn;
	@FXML
	private TableColumn<ExamDetailsMessage, String> dateColumn;
	@FXML
	private TableColumn<ExamDetailsMessage, String> executedIDCol;

	/**
	 * initialize(URL arg0, ResourceBundle arg1) Arguments:URL arg0, ResourceBundle
	 * arg1 The method initialize the javaFX screens
	 * 
	 * @author Aviv Mahulya
	 */
	public void initialize(URL url, ResourceBundle rb) {
		connect(this);
		messageToServer[0] = "getExamsByUserName";
		messageToServer[1] = getMyUser().getUsername();/* send the user name of the current user */
		messageToServer[2] = null;
		chat.handleMessageFromClientUI(messageToServer);// send the message to server
	}

	/**
	 * showingCopy(ArrayList<Question> ques, HashMap<String, Integer> ans)
	 * Arguments:ArrayList<Question> ques, HashMap<String, Integer> ans The method
	 * shows copy of exam that student performed
	 * 
	 * @author Orit Aharon
	 */
	public void showingCopy(ArrayList<Question> ques, HashMap<String, Integer> ans) {
		if (ans.isEmpty())/* if the exam is empty */
		{
			Alert emptyStdExam = new Alert(AlertType.INFORMATION, "Your exam has no answers please pay attention",
					ButtonType.OK);
			emptyStdExam.showAndWait();
		}
		examAnswers = ans;
		questioninexecutedexam = ques;
		copyFlag = true;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (!getMyUser().getRole().equals("student")) {
					((Stage) ((Node) tempEvent.getSource()).getScene().getWindow()).hide();
					Stage primaryStage = new Stage();
					FXMLLoader loader = new FXMLLoader();
					Pane root;
					try {
						root = loader
								.load(getClass().getResource("/studentBoundary/ComputerizedExam.fxml").openStream());
						Scene scene = new Scene(root);
						scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
						primaryStage.setScene(scene);
						primaryStage.show();
					} catch (IOException e) {

						e.printStackTrace();
					}
				} else {
					openScreen("studentBoundary", "ComputerizedExam");
				}
			}
		});
	}

	/**
	 * checkMessage(Object message) Arguments:Object message The method handle the
	 * message from server
	 * 
	 * @author Aviv Mahulya
	 */
	@SuppressWarnings("unchecked")
	public void checkMessage(Object message) {
		try {
			chat.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		final Object[] msgFromServer = (Object[]) message;
		Platform.runLater(() -> {
			switch (msgFromServer[0].toString()) {
			case "getExamsByUserName": {
				showGradesOnTable((ArrayList<ExamDetailsMessage>) msgFromServer[1]);
				break;
			}
			case "showingCopy": {/* get copy of exam and display it */
				showingCopy((ArrayList<Question>) msgFromServer[1], (HashMap<String, Integer>) msgFromServer[2]);
				break;
			}
			}
		});
	}

	/**
	 * showGradesOnTable(ArrayList<ExamDetailsMessage> detailsFromS)
	 * Arguments:ArrayList<ExamDetailsMessage> detailsFromS The method shows the
	 * grades of students, by getting all his exams
	 * 
	 * @author Aviv Mahulya
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void showGradesOnTable(ArrayList<ExamDetailsMessage> detailsFromS) {

		for (ExamDetailsMessage edM : detailsFromS) {
			detailsList.add(edM);
			executeExamList.add(edM.getExcecutedExamID());
		}

		if (examGradesTable != null && examGradesTable.getColumns() != null)
			examGradesTable.getColumns().clear();
		if (examCodeCombo != null && examCodeCombo.getItems() != null)
			examCodeCombo.getItems().clear();
		examCodeCombo.setItems(executeExamList);
		examGradesTable.setItems(detailsList);
		examCodeColumn.setCellValueFactory(new PropertyValueFactory("examID"));
		dateColumn.setCellValueFactory(new PropertyValueFactory("examDate"));
		gradeColumn.setCellValueFactory(new PropertyValueFactory("examGrade"));
		courseCodeColumn.setCellValueFactory(new PropertyValueFactory("examCourse"));
		executedIDCol.setCellValueFactory(new PropertyValueFactory<>("excecutedExamID"));
		examGradesTable.getColumns().addAll(examCodeColumn, courseCodeColumn, gradeColumn, dateColumn, executedIDCol);
		/*
		 * also need to take from detailsFromS the exam_id's and insert them to
		 * observeable list into the relevante combobox .
		 */
	}

	/**
	 * rderExamPressed(ActionEvent e) ActionEvent e send request for the questions
	 * and answers of specific exam(of user)
	 * 
	 * @author Aviv Mahulya
	 */
	public void orderExamPressed(ActionEvent e) {

		if (examCodeCombo.getValue() == null) {
			errorMsg("Please select exam first");
			return;
		}
		connect(this);
		messageToServer[0] = "getStudentAnswers";
		messageToServer[1] = examCodeCombo.getValue(); // sending executed exam id
		messageToServer[2] = getMyUser().getUsername(); // sending the user name
		chat.handleMessageFromClientUI(messageToServer);
	}

}
