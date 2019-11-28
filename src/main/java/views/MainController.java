package views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import main.MainApp;
import online.Server;

public class MainController extends MasterController {
	@FXML
	private Pane profileBox;
	@FXML
	private Label nameLabel;
	@FXML
	private Label IDLabel;
	@FXML
	private Button logoutbtn;
	@FXML
	private Button profilebtn;
	@FXML
	private Label mainLabel;
	@FXML
	private Button host;
	@FXML
	private Button enter;



	Server server = new Server();
	
	private OptionController oc = new OptionController();

	@FXML
	private void initialize() {
		logoutbtn.setDisable(true);
		profilebtn.setDisable(true);
		host.setDisable(true);
		enter.setDisable(true);
	}

	public void openSinglePlayGame() {
		MainApp.app.loadPane("singlePlay");
		MainApp.app.game.gameStart();
		click();
	}

	public void openOfflineMultiPlayGame() {
		MainApp.app.loadPane("offline");
		MainApp.app.offGame.gameStart();
		click();
	}
	
	public void openOptionPage() {
		MainApp.app.loadPane("option");
		oc = (OptionController)MainApp.app.getController("option");
		oc.soundBox.setOpacity(0);
		oc.soundBox.setDisable(true);
		click();
	}

	public void setProfile(String name, String id) {
		IDLabel.setText(id);
		nameLabel.setText(name);
		WaitingRoomController wrc = (WaitingRoomController) MainApp.app.getController("room");
		wrc.id = id;
		wrc.name = name;
	}

	public void openProfile() {
		profileBox.setOpacity(1);
		logoutbtn.setDisable(false);
		profilebtn.setDisable(false);
		click();
	}

	public void closeProfile() {
		profileBox.setOpacity(0);
		logoutbtn.setDisable(true);
		profilebtn.setDisable(true);
		mainLabel.setText("TETRIS");
		host.setDisable(true);
		enter.setDisable(true);
		host.setText("");
		enter.setText("");
	}
	
	public void openProfilePage() {
		click();
		ProfileController pfc = (ProfileController)MainApp.app.getController("profile");
		MainApp.app.loadPane("profile");
		pfc.initialize();
	}

	public void openLoginPage() {
		click();
		MainApp.app.loadPane("login");
		OptionController op = (OptionController)MainApp.app.getController("option");
		op.loginSound.play();
	}
	
	public void openRangking() {
		click();
		MainApp.app.loadPane("ranking");
		RankingController rck = (RankingController)MainApp.app.getController("ranking");
		rck.reloadTopScore();
	}

	public void online() {
		mainLabel.setText("");
		host.setDisable(false);
		enter.setDisable(false);
		host.setText("방만들기");
		enter.setText("입장하기");
		click();
	}

	public void openServer() {
		server.stopServer();
		server.startServer();
		MainApp.app.loadPane("room");
		WaitingRoomController wrc = (WaitingRoomController) MainApp.app.getController("room");
		wrc.host();
		click();
	}

	public void serverEnter() {
		WaitingRoomController wrc = (WaitingRoomController) MainApp.app.getController("room");
		wrc.user();
		MainApp.app.loadPane("room");
		click();
	}

	public void btnHover() {
		oc = (OptionController)MainApp.app.getController("option");
		oc.hover.play();
		oc.resetHover();
	}

	public void click() {
		oc = (OptionController)MainApp.app.getController("option");
		oc.click.play();
		oc.resetClick();
	}

	@Override
	public void reset() {
	}
}
