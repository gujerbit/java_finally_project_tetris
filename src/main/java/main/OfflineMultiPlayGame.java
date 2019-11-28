package main;

import domain.Block;
import domain.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import views.OptionController;

public class OfflineMultiPlayGame {
	private GraphicsContext oneGc; // 플레이어 1
	private GraphicsContext twoGc; // 플레이어 2

	public Block[][] oneBoard; // 플레이어 1 게임판
	public Block[][] twoBoard; // 플레이어 2 게임판

	private double width; // 게임판 너비
	private double height; // 게임판 높이

	private AnimationTimer mainLoop;
	private long before;

	private Player onePlayer; // 플레이어 1의 현재 블록
	private Player twoPlayer; // 플레이어 2의 현재 블록

	private double blockDownTime = 0;

	private Canvas oneNextBlockCanvas;
	private GraphicsContext onbGc; // 플레이어 1의 다음 블록 캔버스

	private Canvas twoNextBlockCanvas;
	private GraphicsContext tnbGc; // 플레이어 2의 다음 블록 캔버스

	private double nbWidth; // 다음 블록 캔버스의 너비
	private double nbHeight; // 다음 블록 캔버스의 높이

	private Canvas oneHoldCanvas;
	private GraphicsContext ohGc; // 플레이어 1의 홀드 캔버스

	private Canvas twoHoldCanvas;
	private GraphicsContext thGc; // 플레이어 2의 홀드 캔버스

	private double hWidth; // 홀드 캔버스의 너비
	private double hHeight; // 홀드 캔버스의 높이

	public boolean start = false; // 처음 게임 시작
	public boolean oneGameOver = false; // 플레이어 1의 게임 오버
	public boolean twoGameOver = false; // 플레이어 2의 게임 오버

	public int ready = 0; // 0일 때가 정지, 1일 때가 카운트, 2일 때가 게임 시작

	private Label readyLabel;
	private Label oneLabel;
	private Label twoLabel;
	private VBox pauseBox; // 일시정지 화면

	private int oneScore = 0; // 플레이어 1의 점수
	private int twoScore = 0; // 플레이어 2의 점수
	private int sumScore = 0; // 합계 점수 (속도 증가용)

	// 게임 브금
	private MediaPlayer bgm;
	// 게임오버 사운드
	private MediaPlayer gameoverS;
	// 홀드 사운드
	private MediaPlayer hold;
	// 스페이스바
	private MediaPlayer drop;
	// 움직일 때
	private MediaPlayer move;
	// 돌릴때
	private MediaPlayer rotate;
	// 터질때
	private MediaPlayer boom;
	// bgm 다시 시작하는변수
	private double soundReStart = 0;

	public boolean isKeySet = false;
	// 게임오버 됐을때 다시시작 클릭 못 하게 하려고
	private Button resumeBtn;

	private Label lblLevel;

	public OfflineMultiPlayGame(Canvas oneCanvas, Canvas twoCanvas, Canvas oneNextBlockCanvas,
			Canvas twoNextBlockCanvas, Canvas oneHoldCanvas, Canvas twoHoldCanvas, Label readyLabel, Label oneLabel,
			Label twoLabel, VBox pauseBox, boolean isKeySet, Button resumeBtn, Label lblLevel) {
		this.oneNextBlockCanvas = oneNextBlockCanvas;
		this.twoNextBlockCanvas = twoNextBlockCanvas;

		this.oneHoldCanvas = oneHoldCanvas;
		this.twoHoldCanvas = twoHoldCanvas;

		this.isKeySet = isKeySet;
		this.resumeBtn = resumeBtn;

		width = oneCanvas.getWidth();
		height = oneCanvas.getHeight();

		this.nbWidth = this.oneNextBlockCanvas.getWidth();
		this.nbHeight = this.oneNextBlockCanvas.getHeight();

		this.hWidth = this.oneHoldCanvas.getWidth();
		this.hHeight = this.oneHoldCanvas.getHeight();

		this.readyLabel = readyLabel;
		this.oneLabel = oneLabel;
		this.twoLabel = twoLabel;
		this.pauseBox = pauseBox;
		this.lblLevel = lblLevel;

		double size = (width - 4) / 10;

		oneBoard = new Block[24][10];
		twoBoard = new Block[24][10];

		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 10; j++) {
				oneBoard[i][j] = new Block(j * size + 2, i * size + 2, size);
				twoBoard[i][j] = new Block(j * size + 2, i * size + 2, size);
			}
		}

		oneGc = oneCanvas.getGraphicsContext2D();
		twoGc = twoCanvas.getGraphicsContext2D();

		this.onbGc = this.oneNextBlockCanvas.getGraphicsContext2D();
		this.tnbGc = this.twoNextBlockCanvas.getGraphicsContext2D();

		this.ohGc = this.oneHoldCanvas.getGraphicsContext2D();
		this.thGc = this.twoHoldCanvas.getGraphicsContext2D();

		mainLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				update((now - before) / 1000000000d);
				before = now;
				oneRender();
				twoRender();
			}
		};

		before = System.nanoTime();
		onePlayer = new Player(oneBoard);
		twoPlayer = new Player(twoBoard);

		oneGameOver = false;
		twoGameOver = false;
		mainLoop.stop();

	}

	public void gameStart() {
		oneGameOver = false;
		twoGameOver = false;
		start = true;
		ready = 1;
		oneScore = 0;
		twoScore = 0;
		sumScore = 0;
		pauseBox.setOpacity(0);
		readyLabel.setStyle("-fx-background-color: rgba(1,1,1,0)");
		oneLabel.setStyle("-fx-background-color: rgba(1,1,1,0)");
		twoLabel.setStyle("-fx-background-color: rgba(1,1,1,0)");
		oneLabel.setText("");
		twoLabel.setText("");
		lblLevel.setText("1");
		resumeBtn.setDisable(false);

		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 10; j++) {
				oneBoard[i][j].setData(false, Color.WHITE);
				twoBoard[i][j].setData(false, Color.WHITE);

				oneBoard[i][j].setPreData(false, Color.WHITE);
				twoBoard[i][j].setPreData(false, Color.WHITE);
			}
		}
		isKeySet = true;
		ohGc.clearRect(0, 0, hWidth, hHeight);
		thGc.clearRect(0, 0, width, height);
		onePlayer.reSet();
		twoPlayer.reSet();
		mainLoop.start();
		pauseBox.setDisable(true);
		onePlayer.online = false;
		twoPlayer.online = false;
	}

	public void update(double delta) {
		OptionController oc = (OptionController) MainApp.app.getController("option");
		if (start) {
			delta = 0;
			start = false;
		}
		if (oneGameOver || twoGameOver) {
			blockDownTime += delta;
			if (blockDownTime > 3) {
				mainLoop.stop();
				blockDownTime = 0;
				pauseBox.setOpacity(1);
				resumeBtn.setDisable(true);
				oneLabel.setText("");
				twoLabel.setText("");
			}
			return;
		}

		if (ready == 1) {
			blockDownTime += delta;

			if (blockDownTime <= 1) {
				readyLabel.setText("3");
			} else if (blockDownTime <= 2) {
				readyLabel.setText("2");
			} else if (blockDownTime <= 3) {
				readyLabel.setText("1");
			} else if (blockDownTime > 3) {
				ready = 2;
				readyLabel.setText("");
				soundReStart = 0;
				oc.resetBGM();
				oc.bgm.play();
			}
		}
		double limit = 0.5;

		if (limit > 0.1) {
			limit = 0.5 - sumScore / 400d;
		}
//		System.out.println(limit);

		if (limit <= 0.4 && limit > 0.3) {
			lblLevel.setText("2");
		} else if (limit <= 0.3 && limit > 0.2) {
			lblLevel.setText("3");
		} else if (limit <= 0.2 && limit > 0.1) {
			lblLevel.setText("4");
		} else if (limit == 0.1) {
			lblLevel.setText("5");
		}

		if (limit <= 0.1) {
			limit = 0.1;
		}

		if (ready == 2) {
			blockDownTime += delta;
			if (blockDownTime >= limit) {
				onePlayer.down();
				twoPlayer.down();
				blockDownTime = 0;
			}
			soundReStart += delta;
			if (soundReStart >= 143) {
				oc.resetBGM();
				oc.bgm.play();
				soundReStart = 0;
			}
		}
	}

	public void oneCheckLineStatus() {
		OptionController oc = (OptionController) MainApp.app.getController("option");
		for (int i = 23; i >= 0; i--) {
			boolean clear = true;
			for (int j = 0; j < 10; j++) {
				if (!oneBoard[i][j].getFill()) {
					clear = false;
					break;
				}
			}
			if (clear) {
				oneScore++;
				sumScore += oneScore;
				twoPlayer.puyopuyo();
				for (int j = 0; j < 10; j++) {
					oneBoard[i][j].setData(false, Color.WHITE);
				}

				for (int k = i - 1; k >= 0; k--) {
					for (int j = 0; j < 10; j++) {
						oneBoard[k + 1][j].copyData(oneBoard[k][j]);
					}
				}

				for (int j = 0; j < 10; j++) {
					oneBoard[0][j].setData(false, Color.WHITE);
				}
				i++;
				oc.boom.play();
				oc.resetBoom();
			}
		}
	}

	public void twoCheckLineStatus() {
		OptionController oc = (OptionController) MainApp.app.getController("option");
		for (int i = 23; i >= 0; i--) {
			boolean clear = true;
			for (int j = 0; j < 10; j++) {
				if (!twoBoard[i][j].getFill()) {
					clear = false;
					break;
				}
			}

			if (clear) {
				twoScore++;
				sumScore += twoScore;
				onePlayer.puyopuyo();
				for (int j = 0; j < 10; j++) {
					twoBoard[i][j].setData(false, Color.WHITE);
				}

				for (int k = i - 1; k >= 0; k--) {
					for (int j = 0; j < 10; j++) {
						twoBoard[k + 1][j].copyData(twoBoard[k][j]);
					}
				}

				for (int j = 0; j < 10; j++) {
					twoBoard[0][j].setData(false, Color.WHITE);
				}
				i++;
				oc.boom.play();
				oc.resetBoom();
			}
		}
	}

	public void oneRender() {
		OptionController oc = (OptionController) MainApp.app.getController("option");
		oneGc.clearRect(0, 0, width, height);
		oneGc.setStroke(Color.rgb(0, 0, 0));
		oneGc.setLineWidth(2);
		oneGc.strokeRect(0, 0, width, height);

		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 10; j++) {
				oneBoard[i][j].render(oneGc);
			}
		}

		onePlayer.render(onbGc, nbWidth, nbHeight);
		onePlayer.holdRender(ohGc, hWidth, hHeight);

		if (oneGameOver) {
			oneLabel.setText("YOU LOSE...");
			oneLabel.setStyle("-fx-background-color: rgba(1,1,1,0.5)");
			oc.bgm.stop();
			oc.gameoverS.play();
			pauseBox.setDisable(false);
		} else if (twoGameOver) {
			oneLabel.setText("YOU WIN!!");
			oneLabel.setStyle("-fx-background-color: rgba(1,1,1,0.5)");
			oc.bgm.stop();
			oc.gameoverS.play();
			pauseBox.setDisable(false);
		}
	}

	public void twoRender() {
		OptionController oc = (OptionController) MainApp.app.getController("option");
		twoGc.clearRect(0, 0, width, height);
		twoGc.setStroke(Color.rgb(0, 0, 0));
		twoGc.setLineWidth(2);
		twoGc.strokeRect(0, 0, width, height);

		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 10; j++) {
				twoBoard[i][j].render(twoGc);
			}
		}

		twoPlayer.render(tnbGc, nbWidth, nbHeight);
		twoPlayer.holdRender(thGc, hWidth, hHeight);

		if (twoGameOver) {
			twoLabel.setText("YOU LOSE...");
			twoLabel.setStyle("-fx-background-color: rgba(1,1,1,0.5)");
			oc.bgm.stop();
			oc.gameoverS.play();
			pauseBox.setDisable(false);
		} else if (oneGameOver) {
			twoLabel.setText("YOU WIN!!");
			twoLabel.setStyle("-fx-background-color: rgba(1,1,1,0.5)");
			oc.bgm.stop();
			oc.gameoverS.play();
			pauseBox.setDisable(false);
		}
	}

	public void oneKeyHandler(KeyEvent e) {
		OptionController oc = (OptionController) MainApp.app.getController("option");
		if (isKeySet) {
			if (oneGameOver || twoGameOver) {
				return;
			}

			if (ready == 2) {
				onePlayer.oneKeyHandler(e);
			}

			if (e.getCode() == KeyCode.ESCAPE) {
				if (ready == 0) {
					resume();
				} else if (ready == 2) {
					ready = 0;
					pauseBox.setOpacity(1);
					readyLabel.setStyle("-fx-background-color: rgba(1,1,1,0.5)");
					oc.bgm.stop();
					pauseBox.setDisable(false);
				}
			}
			if (ready == 2) {
				if (e.getCode() == KeyCode.SPACE) {
					oc.drop.play();
					oc.resetDrop();
				}
				if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.D || e.getCode() == KeyCode.S) {
					oc.move.play();
					oc.resetMove();
				}
				if (e.getCode() == KeyCode.W) {
					oc.rotate.play();
					oc.resetRotate();
				}
				if (e.getCode() == KeyCode.C) {
					oc.holdS.play();
					oc.resetHold();
				}
			}

		}
	}

	public void twoKeyHandler(KeyEvent e) {
		OptionController oc = (OptionController) MainApp.app.getController("option");
		if (isKeySet) {
			if (oneGameOver || twoGameOver) {
				return;
			}

			if (ready == 2) {
				twoPlayer.twoKeyHandler(e);
			}

			if (e.getCode() == KeyCode.END) {
				if (ready == 0) {
					resume();
				} else if (ready == 2) {
					ready = 0;
					pauseBox.setOpacity(1);
					readyLabel.setStyle("-fx-background-color: rgba(1,1,1,0.5)");
					oc.bgm.stop();
					pauseBox.setDisable(false);
				}
			}
			if (ready == 2) {
				if (e.getCode() == KeyCode.ENTER) {
					oc.drop.play();
					oc.resetDrop();
				}
				if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.DOWN) {
					oc.move.play();
					oc.resetMove();
				}
				if (e.getCode() == KeyCode.UP) {
					oc.rotate.play();
					oc.resetRotate();
				}
				if (e.getCode() == KeyCode.DELETE) {
					oc.holdS.play();
					oc.resetHold();
				}
			}

		}
	}


	public void resume() {
		pauseBox.setDisable(true);
		ready = 1;
		pauseBox.setOpacity(0);
		readyLabel.setStyle("-fx-background-color: rgba(1,1,1,0)");

	}

	public void setOneGameOver() {
		oneGameOver = true;
		twoRender();
		oneRender();
	}

	public void setTwoGameOver() {
		twoGameOver = true;
		oneRender();
		twoRender();
	}
}
