package views;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.MainApp;
import main.OfflineMultiPlayGame;

public class OfflineMultiPlayController extends MasterController {
	@FXML
	private Canvas oneCanvas;
	@FXML
	private Canvas twoCanvas;
	@FXML
	private Canvas oneNextBlockCanvas;
	@FXML
	private Canvas twoNextBlockCanvas;
	@FXML
	private Canvas oneHoldCanvas;
	@FXML
	private Canvas twoHoldCanvas;
	@FXML
	private Label readyLabel;
	@FXML
	private Label oneLabel;
	@FXML
	private Label twoLabel;
	@FXML
	private VBox pauseBox;
	@FXML
	private Button resumeBtn;
	@FXML
	private Label lblLevel;
	@FXML
	private AnchorPane offlineMultiPlayPage;

	private boolean isKeySet;

	@FXML
	public void initialize() {
		MainApp.app.offGame = new OfflineMultiPlayGame(oneCanvas, twoCanvas, oneNextBlockCanvas, twoNextBlockCanvas,
				oneHoldCanvas, twoHoldCanvas, readyLabel, oneLabel, twoLabel, pauseBox, isKeySet, resumeBtn, lblLevel);
	}

	public void resume() {
		clickSound();
		MainApp.app.offGame.resume();

	}

	public void reStart() {
		clickSound();
		MainApp.app.offGame.gameStart();
	}

	public void exit() {
		clickSound();
		MainApp.app.slideOut(offlineMultiPlayPage);
		MainApp.app.offGame.isKeySet = false;
		MainApp.app.offGame.ready = 0;
	}
	public void option() {
		clickSound();
		MainApp.app.loadPane("option");
	}
	public void hoverSound() {
		OptionController opc = (OptionController) MainApp.app.getController("option");
		opc.hover.play();
		opc.resetHover();
	}

	public void clickSound() {
		OptionController opc = (OptionController) MainApp.app.getController("option");
		opc.click.play();
		opc.resetClick();
	}

	@Override
	public void reset() {
	}
}
