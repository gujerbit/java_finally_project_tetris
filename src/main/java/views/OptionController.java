package views;

import java.net.URISyntaxException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import main.MainApp;

public class OptionController extends MasterController {
	@FXML
	private AnchorPane optionPage;
	@FXML
	private VBox keyBox;
	@FXML
	public VBox soundBox;
	@FXML
	private Button left;
	@FXML
	private Button right;
	@FXML
	private Button turn;
	@FXML
	private Button down;
	@FXML
	private Button fullDown;
	@FXML
	private Button hold;
	@FXML
	private Button pause;
	@FXML
	private Slider background;
	@FXML
	private Slider effect;

	// 게임 브금
	public MediaPlayer bgm;
	// 게임오버 사운드
	public MediaPlayer gameoverS;
	// 홀드 사운드
	public MediaPlayer holdS;
	// 스페이스바
	public MediaPlayer drop;
	// 움직일 때
	public MediaPlayer move;
	// 돌릴때
	public MediaPlayer rotate;
	// 터질때
	public MediaPlayer boom;
	// 버튼들 클릭할때
	public MediaPlayer click;
	// 버튼들 호버할때
	public MediaPlayer hover;
	// 로그인 화면
	public MediaPlayer loginSound;
	// 승리 효과음
	public MediaPlayer winSound;

	public boolean setLeft;
	public boolean setRight;
	public boolean setTurn;
	public boolean setDown;
	public boolean setFullDown;
	public boolean setHold;
	public boolean setPause;

	@FXML
	public void initialize() {
		setLeft = false;
		setRight = false;
		setTurn = false;
		setDown = false;
		setFullDown = false;
		setHold = false;
		setPause = false;
		setMedia();
		soundBox.setDisable(true);
		loginSound.play();
	}

	public void changeLeft() {
		clickSound();
		if (setLeft) {
			left.setText("LEFT BUTTON");
		} else {
			left.setText("A");
		}

		if (setLeft) {
			setLeft = false;
		} else {
			setLeft = true;
		}
	}

	public void changeRight() {
		clickSound();
		if (setRight) {
			right.setText("RIGHT BUTTON");
		} else {
			right.setText("D");
		}

		if (setRight) {
			setRight = false;
		} else {
			setRight = true;
		}
	}

	public void changeTurn() {
		clickSound();
		if (setTurn) {
			turn.setText("UP BUTTON");
		} else {
			turn.setText("W");
		}

		if (setTurn) {
			setTurn = false;
		} else {
			setTurn = true;
		}
	}

	public void changeDown() {
		clickSound();
		if (setDown) {
			down.setText("DOWN BUTTON");
		} else {
			down.setText("S");
		}

		if (setDown) {
			setDown = false;
		} else {
			setDown = true;
		}
	}

	public void changeFullDown() {
		clickSound();
		if (setFullDown) {
			fullDown.setText("SPACE BAR");
		} else {
			fullDown.setText("ENTER");
		}

		if (setFullDown) {
			setFullDown = false;
		} else {
			setFullDown = true;
		}
	}

	public void changeHold() {
		clickSound();
		if (setHold) {
			hold.setText("C");
		} else {
			hold.setText("DELETE");
		}

		if (setHold) {
			setHold = false;
		} else {
			setHold = true;
		}
	}

	public void changePause() {
		clickSound();
		if (setPause) {
			pause.setText("ESC");
		} else {
			pause.setText("END");
		}

		if (setPause) {
			setPause = false;
		} else {
			setPause = true;
		}
	}

	public void setBackground() {
		double bgmSize = background.getValue() / 100d;
		bgm.setVolume(bgmSize);
		gameoverS.setVolume(bgmSize);
		loginSound.setVolume(bgmSize);
	}

	public void setEffect() {
		double effectSize = effect.getValue() / 100d;
		drop.setVolume(effectSize);
		holdS.setVolume(effectSize);
		move.setVolume(effectSize);
		rotate.setVolume(effectSize);
		boom.setVolume(effectSize);
		click.setVolume(effectSize);
		hover.setVolume(effectSize);
	}

	public void setMedia() {
		Media mp3;
		try {
			mp3 = new Media(getClass().getResource("/resources/harddrop.mp3").toURI().toString());
			drop = new MediaPlayer(mp3);
			drop.setVolume(0.5);
			mp3 = new Media(getClass().getResource("/resources/hold.mp3").toURI().toString());
			holdS = new MediaPlayer(mp3);
			holdS.setVolume(0.5);
			mp3 = new Media(getClass().getResource("/resources/move.mp3").toURI().toString());
			move = new MediaPlayer(mp3);
			move.setVolume(0.5);
			mp3 = new Media(getClass().getResource("/resources/rotate.mp3").toURI().toString());
			rotate = new MediaPlayer(mp3);
			rotate.setVolume(0.5);
			mp3 = new Media(getClass().getResource("/resources/boom.mp3").toURI().toString());
			boom = new MediaPlayer(mp3);
			boom.setVolume(0.5);
			mp3 = new Media(getClass().getResource("/resources/bgm.mp3").toURI().toString());
			bgm = new MediaPlayer(mp3);
			bgm.setVolume(0.5);
			mp3 = new Media(getClass().getResource("/resources/gameover.mp3").toURI().toString());
			gameoverS = new MediaPlayer(mp3);
			gameoverS.setVolume(0.5);
			mp3 = new Media(getClass().getResource("/resources/click.mp3").toURI().toString());
			click = new MediaPlayer(mp3);
			click.setVolume(0.5);
			mp3 = new Media(getClass().getResource("/resources/ui.mp3").toURI().toString());
			hover = new MediaPlayer(mp3);
			hover.setVolume(0.5);
			mp3 = new Media(getClass().getResource("/resources/login.mp3").toURI().toString());
			loginSound = new MediaPlayer(mp3);
			loginSound.setOnEndOfMedia(()->{
				loginSound.seek(Duration.ZERO);
			});
			loginSound.setVolume(0.5);
			mp3 = new Media(getClass().getResource("/resources/win.mp3").toURI().toString());
			winSound = new MediaPlayer(mp3);
			winSound.setVolume(0.5);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.out.println("효과음오류 발생");
		}
	}

	public void resetMove() {
		move.seek(Duration.millis(0));
	}

	public void resetBoom() {
		boom.seek(Duration.millis(0));
	}

	public void resetDrop() {
		drop.seek(Duration.millis(0));
	}

	public void resetHold() {
		holdS.seek(Duration.millis(0));
	}

	public void resetRotate() {
		rotate.seek(Duration.millis(0));
	}

	public void resetHover() {
		hover.seek(Duration.millis(0));
	}

	public void resetClick() {
		click.seek(Duration.millis(0));
	}

	public void resetBGM() {
		bgm.seek(Duration.millis(0));
	}

	public void click() {
		click.seek(Duration.millis(0));
	}

	public void resetGameover() {
		gameoverS.seek(Duration.millis(0));
	}
	public void resetLogin() {
		loginSound.seek(Duration.millis(0));
	}
	public void resetWin() {
		winSound.seek(Duration.millis(0));
	}

	public void openSoundBox() {
		clickSound();
		soundBox.setOpacity(1);
		soundBox.setDisable(false);
		keyBox.setOpacity(0);
	}

	public void closeSoundBox() {
		clickSound();
		soundBox.setOpacity(0);
		soundBox.setDisable(true);
		keyBox.setOpacity(1);
	}

	public void closeOptionPage() {
		clickSound();
		MainApp.app.slideOut(optionPage);
		soundBox.setOpacity(0);
		soundBox.setDisable(true);
		keyBox.setOpacity(1);
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
