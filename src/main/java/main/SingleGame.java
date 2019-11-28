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
import javafx.scene.paint.Color;
import views.OptionController;
import views.RankingController;

public class SingleGame {
	private GraphicsContext gc;
	public Block[][] board;

	// 게임판의 너비와 높이를 저장
	private double width;
	private double height;

	private AnimationTimer mainLoop; // 게임의 메인 루프
	private long before;

	private Player player; // 지금 움직이는 블록
	private double blockDownTime = 0;

	public Integer score = 0;

	private Canvas nextBlockCanvas;
	private GraphicsContext nbgc; // 다음블록 캔버스

	private Canvas holdBlockCanvas;
	private GraphicsContext hbgc; // 홀드

	private double nbWidth; // 다음블록 캔버스의 너비
	private double nbHeight;// 다음블록 캔버스의 높이

	private double hbWidth;
	private double hbHeight; // 홀드 캔버스 너비, 높이

	// 게임오버 됐을때 다시시작 클릭 못 하게 하려고
	private Button resumeBtn;
	private Button reStartBtn;
	private Button exitBtn;
	private Button optionBtn;

	public VBox pauseBox;

	private Label scoreLabel;
	private Label readyLabel;
	// 게임 처음 시작
	public boolean start = false;

	public boolean gameOver = false;
	// 0일때가 정지 , 1일때가 카운트 , 2 일때가 게임 시작
	public int ready = 0;

	// bgm 다시 시작하는변수
	private double soundReStart = 0;
	// 싱글플레이 bgm 관리 변수
	private boolean startbgm = false;

	public SingleGame(Canvas canvas, Canvas nextBlockCanvas, Canvas holdBlockCanvas, Label scoreLabel, Label readyLabel,
			VBox pauseBox, Button resumeBtn, Button reStartBtn, Button exitBtn, Button optionBtn) {
		width = canvas.getWidth();
		height = canvas.getHeight();
		this.pauseBox = pauseBox;
		this.nextBlockCanvas = nextBlockCanvas;
		this.holdBlockCanvas = holdBlockCanvas;

		this.scoreLabel = scoreLabel;
		this.readyLabel = readyLabel;

		this.nbgc = this.nextBlockCanvas.getGraphicsContext2D();
		this.hbgc = this.holdBlockCanvas.getGraphicsContext2D();

		this.nbWidth = this.nextBlockCanvas.getWidth();
		this.nbHeight = this.nextBlockCanvas.getHeight();

		this.hbWidth = this.holdBlockCanvas.getWidth();
		this.hbHeight = this.holdBlockCanvas.getHeight();
		this.resumeBtn = resumeBtn;
		this.reStartBtn = resumeBtn;
		this.optionBtn = optionBtn;
		this.exitBtn = exitBtn;

		double size = (width - 4) / 10;
		board = new Block[24][10]; // 게임판을 만들어주고

		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 10; j++) {
				board[i][j] = new Block(j * size + 2, i * size + 2, size);
			}
		}
		gc = canvas.getGraphicsContext2D();

		mainLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				update((now - before) / 1000000000d);
				before = now;
				render();
			}
		};

		before = System.nanoTime();
		player = new Player(board);
		gameOver = false;
		mainLoop.stop();
	}

	public void gameStart() {
		startbgm = true;
		gameOver = false;
		start = true;
		ready = 1;
		pauseBox.setOpacity(0);
		pauseBox.setDisable(true);
		readyLabel.setStyle("-fx-background-color: rgba(1,1,1,0)");
		score = 0;
		scoreLabel.setText("0");
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 10; j++) {
				board[i][j].setData(false, Color.WHITE);
				board[i][j].setPreData(false, Color.WHITE);
			}
		}
		hbgc.clearRect(0, 0, width, height);
		player.reSet();
		resumeBtn.setDisable(false);
		mainLoop.start();
		player.online = false;
	}

	// 매 프레임마다 실행되는 업데이트 매서드
	public void update(double delta) {
		OptionController oc = (OptionController) MainApp.app.getController("option");
		if (start) {
			delta = 0;
			start = false;
		}
		if (gameOver) {
			blockDownTime += delta;
			if (blockDownTime > 3) {
				readyLabel.setText("");
				mainLoop.stop();
				blockDownTime = 0;
				pauseBox.setOpacity(1);
				pauseBox.setDisable(false);
				resumeBtn.setDisable(true);
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

		double limit = 0.5 - score / 100d;

		if (limit < 0.1) {
			limit = 0.1;
		}

		if (ready == 2) {
			blockDownTime += delta;
			if (blockDownTime >= limit) {
				player.down();
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

	// 라인이 꽉 찼는지를 검사하는 매서드
	public void checkLineStatus() {
		for (int i = 23; i >= 0; i--) {
			boolean clear = true;
			for (int j = 0; j < 10; j++) {
				if (!board[i][j].getFill()) {
					clear = false; // 한칸이라도 비어있다면 clear를 false로 설정
					break;
				}
			}

			if (clear) { // 해당 줄이 꽉 찼다면
				score++;
				scoreLabel.setText(score.toString());
				for (int j = 0; j < 10; j++) {
					board[i][j].setData(false, Color.WHITE);
					board[i][j].setPreData(false, Color.WHITE);
				}

				for (int k = i - 1; k >= 0; k--) {
					for (int j = 0; j < 10; j++) {
						board[k + 1][j].copyData(board[k][j]);
					}
				}

				for (int j = 0; j < 10; j++) {
					board[0][j].setData(false, Color.WHITE);
					board[0][j].setPreData(false, Color.WHITE);
				}
				i++;
				OptionController opc = (OptionController) MainApp.app.getController("option");
				opc.boom.play();
				opc.resetBoom();
			}
		}
	}

	// 매 프레임마다 화면을 그려주는 매서드
	public void render() {
		gc.clearRect(0, 0, width, height);
		gc.setStroke(Color.rgb(0, 0, 0));
		gc.setLineWidth(2);
		gc.strokeRect(0, 0, width, height);

		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 10; j++) {
				board[i][j].render(gc);
			}
		}

		player.render(nbgc, nbWidth, nbHeight);
		player.holdRender(hbgc, hbWidth, hbHeight);
	}

	public void keyHandler(KeyEvent e) {
		OptionController oc = (OptionController) MainApp.app.getController("option");
		if (startbgm) {
			if (gameOver)
				return;
			if (ready == 2) {
				player.keyHandler(e);
			}
			if (!oc.setPause) {
				if (e.getCode() == KeyCode.ESCAPE) {
					if (ready == 0) {
						resume();
					} else if (ready == 2) {
						ready = 0;
						pauseBox.setOpacity(1);
						pauseBox.setDisable(false);
						readyLabel.setStyle("-fx-background-color: rgba(1,1,1,0.5)");
						oc.bgm.stop();
						resumeBtn.setDisable(false);
						reStartBtn.setDisable(false);
						optionBtn.setDisable(false);
						exitBtn.setDisable(false);
					}
				}
			} else {
				if (e.getCode() == KeyCode.END) {
					if (ready == 0) {
						resume();
					} else if (ready == 2) {
						ready = 0;
						pauseBox.setOpacity(1);
						pauseBox.setDisable(false);
						readyLabel.setStyle("-fx-background-color: rgba(1,1,1,0.5)");
						oc.bgm.stop();
						resumeBtn.setDisable(false);
						reStartBtn.setDisable(false);
						optionBtn.setDisable(false);
						exitBtn.setDisable(false);
					}
				}
			}
			if (ready == 2) {
				if (!oc.setFullDown) {
					if (e.getCode() == KeyCode.SPACE) {
						oc.drop.play();
						oc.resetDrop();
					}
				} else {
					if (e.getCode() == KeyCode.ENTER) {
						oc.drop.play();
						oc.resetDrop();
					}
				}

				if (!oc.setLeft) {
					if (e.getCode() == KeyCode.LEFT) {
						oc.move.play();
						oc.resetMove();
					}
				} else {
					if (e.getCode() == KeyCode.A) {
						oc.move.play();
						oc.resetMove();
					}
				}

				if (!oc.setRight) {
					if (e.getCode() == KeyCode.RIGHT) {
						oc.move.play();
						oc.resetMove();
					}
				} else {
					if (e.getCode() == KeyCode.D) {
						oc.move.play();
						oc.resetMove();
					}
				}

				if (!oc.setDown) {
					if (e.getCode() == KeyCode.DOWN) {
						oc.move.play();
						oc.resetMove();
					}
				} else {
					if (e.getCode() == KeyCode.S) {
						oc.move.play();
						oc.resetMove();
					}
				}

				if (!oc.setTurn) {
					if (e.getCode() == KeyCode.UP) {
						oc.rotate.play();
						oc.resetRotate();
					}
				} else {
					if (e.getCode() == KeyCode.W) {
						oc.rotate.play();
						oc.resetRotate();
					}
				}

				if (!oc.setHold) {
					if (e.getCode() == KeyCode.C) {
						oc.holdS.play();
						oc.resetHold();
					}
				} else {
					if (e.getCode() == KeyCode.DELETE) {
						oc.holdS.play();
						oc.resetHold();
					}
				}
			}
			
		}
	}

	public void resume() {
		ready = 1;
		pauseBox.setOpacity(0);
		pauseBox.setDisable(true);
		resumeBtn.setDisable(true);
		readyLabel.setStyle("-fx-background-color: rgba(1,1,1,0)");
	}

	public void setGameOver() {
		OptionController opc = (OptionController) MainApp.app.getController("option");
		gameOver = true;
		render();
		if (gameOver) {
			readyLabel.setText("GAME OVER");
			readyLabel.setStyle("-fx-background-color: rgba(1,1,1,0.5)");
			opc.bgm.stop();
			opc.gameoverS.play();
			opc.resetGameover();
		}
		RankingController rck = (RankingController)MainApp.app.getController("ranking");
		rck.record();
	}
}
