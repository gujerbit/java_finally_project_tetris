package views;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.CopyGame;
import main.MainApp;
import main.OnlineMultiPlayGame;
import main.SingleGame;

public class OnlineMultiPlayController extends MasterController {
	@FXML
	public AnchorPane onlineMultiPlayPage;
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
	private Label player1lbl;
	@FXML
	private Label player2lbl;
	@FXML
	private Canvas copyCanvas;
	

	@FXML
	public void initialize() {
		MainApp.app.onGame = new OnlineMultiPlayGame(canvas, nextCanvas, holdCanvas, lblScore, readyLabel,copyCanvas,player1lbl,player2lbl,onlineMultiPlayPage);
		MainApp.app.copyGame = new CopyGame(copyCanvas);
	}

	public void resume() {
		MainApp.app.onGame.resume();

	}

	public void reStart() {
		MainApp.app.onGame.gameStart();
	}

	public void exit() {
		MainApp.app.slideOut(onlineMultiPlayPage);
		MainApp.app.onGame.ready = 0;
	}

	@Override
	public void reset() {
	}

}
