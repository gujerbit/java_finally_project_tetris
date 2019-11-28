package views;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.MainApp;
import main.SingleGame;

public class SinglePlayController extends MasterController {
	@FXML
	private AnchorPane singlePlayPage;
	@FXML
	private Canvas canvas;
	@FXML
	private Canvas nextCanvas;
	@FXML
	private Canvas holdCanvas;
	@FXML
	private Label lblScore;
	@FXML
	private Label readyLabel;
	@FXML
	public VBox pauseBox;
	@FXML
	private Button resumeBtn;
	@FXML
	private Button reStartBtn;
	@FXML
	private Button exitBtn;
	@FXML
	private Button optionBtn;

	@FXML
	public void initialize() {
		MainApp.app.game = new SingleGame(canvas, nextCanvas, holdCanvas, lblScore, readyLabel, pauseBox, resumeBtn,
				reStartBtn, exitBtn, optionBtn);
	}

	public void resume() {
		clickSound();
		MainApp.app.game.resume();

	}

	public void reStart() {
		clickSound();
		MainApp.app.game.gameStart();
	}

	public void exit() {
		clickSound();
		MainApp.app.slideOut(singlePlayPage);
		MainApp.app.game.ready = 0;
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
