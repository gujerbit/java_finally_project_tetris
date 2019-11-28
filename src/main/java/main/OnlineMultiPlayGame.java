package main;

import java.util.Random;

import domain.Block;
import domain.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import views.OptionController;
import views.WaitingRoomController;

public class OnlineMultiPlayGame {
	private GraphicsContext gc;

	public Block[][] board;

	// 게임판의 너비와 높이를 저장
	private double width;
	private double height;

	private AnimationTimer mainLoop; // 게임의 메인 루프
	private long before;

	private Player player; // 지금 움직이는 블록
	private double blockDownTime = 0;

	private Integer score = 0;

	private Canvas nextBlockCanvas;
	private GraphicsContext nbgc; // 다음블록 캔버스

	// 메인 캔버스
	private Canvas canvas;

	private Canvas holdBlockCanvas;
	private GraphicsContext hbgc; // 홀드

	private double nbWidth; // 다음블록 캔버스의 너비
	private double nbHeight;// 다음블록 캔버스의 높이

	private double hbWidth;
	private double hbHeight; // 홀드 캔버스 너비, 높이

	private Label scoreLabel;
	private Label readyLabel;

	// 플레이어 1 라벨 (왼쪽)
	private Label player1lbl;
	// 플레이어 2 라벨 (오른쪽)
	private Label player2lbl;
	// 게임 처음 시작
	public boolean start = false;

	public boolean gameOver = false;
	// 0일때가 정지 , 1일때가 카운트 , 2 일때가 게임 시작
	public int ready = 0;
	// bgm 다시 시작하는변수
	private double soundReStart = 0;
	// 싱글플레이 bgm 관리 변수
	private boolean startbgm = false;

	public boolean host = false;

	public boolean ok = true;

	public AnchorPane onlineMultiPlayPage;

	public OnlineMultiPlayGame(Canvas canvas, Canvas nextBlockCanvas, Canvas holdBlockCanvas, Label scoreLabel,
			Label readyLabel, Canvas copyCanvas, Label player1lbl, Label player2lbl, AnchorPane onlineMultiPlayPage) {
		width = canvas.getWidth();
		height = canvas.getHeight();
		this.nextBlockCanvas = nextBlockCanvas;
		this.holdBlockCanvas = holdBlockCanvas;
		this.canvas = canvas;

		this.onlineMultiPlayPage = onlineMultiPlayPage;

		this.scoreLabel = scoreLabel;
		this.readyLabel = readyLabel;

		this.player1lbl = player1lbl;
		this.player2lbl = player2lbl;

		this.nbgc = this.nextBlockCanvas.getGraphicsContext2D();
		this.hbgc = this.holdBlockCanvas.getGraphicsContext2D();

		this.nbWidth = this.nextBlockCanvas.getWidth();
		this.nbHeight = this.nextBlockCanvas.getHeight();

		this.hbWidth = this.holdBlockCanvas.getWidth();
		this.hbHeight = this.holdBlockCanvas.getHeight();

		double size = (width - 4) / 10;
		board = new Block[24][10]; // 게임판을 만들어주고

		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 10; j++) {
				board[i][j] = new Block(j * size + 2, i * size + 2, size);
			}
		}
		gc = this.canvas.getGraphicsContext2D();

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
		OptionController oc = (OptionController) MainApp.app.getController("option");
		MainApp.app.copyGame.reset();
		startbgm = true;
		gameOver = false;
		start = true;
		ready = 1;
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
		mainLoop.start();
		WaitingRoomController wrc = (WaitingRoomController) MainApp.app.getController("room");
		if (host) {
			// 만약 내가 방장일때 내 블럭을 유저로 넘겨줌
			wrc.send("USER_BLOCK " + player.current);
		} else {
			// 만약 내가 유저일때 내 블럭을 방장에게 넘겨줌
			wrc.send("HOST_BLOCK " + player.current);
		}
		player.online = true;
		player.host = host;
	}

	public void nameSet(String user1, String user2) {
		player1lbl.setText(user1);
		player2lbl.setText(user2);
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
				MainApp.app.slideOut(onlineMultiPlayPage);
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
				WaitingRoomController wrc = (WaitingRoomController) MainApp.app.getController("room");
//				if (ok) {
//					if (host) {
//						// 만약 내가 방장일때 유저미리보기를 다운
//						wrc.send("USER_MOVE " + "DOWN");
//					} else {
//						// 만약 내가 유저일때 방장미리보기를 다운
//						wrc.send("HOST_MOVE " + "DOWN");
//					}
//					ok = false;
//				}
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
		OptionController oc = (OptionController) MainApp.app.getController("option");
		int temp = 0;
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
				temp++;
				scoreLabel.setText(score.toString());
				for (int j = 0; j < 10; j++) {
					board[i][j].setData(false, Color.WHITE);
//					board[i][j].setPreData(false, Color.WHITE);
				}

				for (int k = i - 1; k >= 0; k--) {
					for (int j = 0; j < 10; j++) {
						board[k + 1][j].copyData(board[k][j]);
					}
				}

				for (int j = 0; j < 10; j++) {
					board[0][j].setData(false, Color.WHITE);
//					board[0][j].setPreData(false, Color.WHITE);
				}
				i++;

				oc.boom.play();
				oc.resetBoom();
			}
		}
		if (temp != 0) {
			WaitingRoomController wrc = (WaitingRoomController) MainApp.app.getController("room");
			if (host) {
				// 만약 내가 호스트라면 유저에 뿌요뿌요 하기
				wrc.send("USER_PUYO " + temp + " ");
			} else {
				// 만약 내가 유저라면 호스트에 뿌요뿌요 하기
				wrc.send("HOST_PUYO " + temp + " ");
			}
		}
		temp = 0;
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
		String move = "";
		if (startbgm) {
			if (gameOver)
				return;
			if (ready == 2) {
				player.keyHandler(e);
			}
			if (ready == 2) {
				if (e.getCode() == KeyCode.SPACE) {
					oc.drop.play();
					oc.resetDrop();
					move = "ENTER";
				}
				if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.DOWN) {
					oc.move.play();
					oc.resetMove();
					if (e.getCode() == KeyCode.LEFT) {
						move = "LEFT";
					} else if (e.getCode() == KeyCode.RIGHT) {
						move = "RIGTH";
					} else if (e.getCode() == KeyCode.DOWN) {
						move = "DOWN";
					}
				}
				if (e.getCode() == KeyCode.UP) {
					oc.rotate.play();
					oc.resetRotate();
					move = "UP";
				}
				if (e.getCode() == KeyCode.C) {
					oc.holdS.play();
					oc.resetHold();
				}
//				WaitingRoomController wrc = (WaitingRoomController) MainApp.app.getController("room");
//				if (ok) {
//					if (host) {
//						// 만약 내가 방장일때 내 움직임을 유저로 넘겨줌
//						wrc.send("USER_MOVE " + move);
//					} else {
//						// 만약 내가 유저일때 내 움직임을 방장에게 넘겨
//						wrc.send("HOST_MOVE " + move);
//					}
//					ok = false;
//				}
			}

		}
	}

	public void resume() {
		ready = 1;
		readyLabel.setStyle("-fx-background-color: rgba(1,1,1,0)");

	}


	public void setGameOver() {
		OptionController oc = (OptionController) MainApp.app.getController("option");
		gameOver = true;
		render();
		if (gameOver) {
			readyLabel.setText("LOSE");
			readyLabel.setStyle("-fx-background-color: rgba(1,1,1,0.5)");
			oc.bgm.stop();
			oc.gameoverS.play();
			oc.resetGameover();
			WaitingRoomController wrc = (WaitingRoomController) MainApp.app.getController("room");
			if (host) {
				// 호스트가 게임오버 됐을때
				wrc.send("HOST_OVER");
			} else {
				// 유저가 게임오버 됐을때
				wrc.send("USER_OVER");
			}
		}
	}

	public void setGameWin() {
		OptionController oc = (OptionController) MainApp.app.getController("option");
		readyLabel.setText("WIN");
		ready = 0;
		readyLabel.setStyle("-fx-background-color: rgba(1,1,1,0.5)");
		oc.bgm.stop();
		oc.winSound.play();
		oc.resetWin();
		gameOver = true;
	}

	public void puyopuyo(int temp) {
		int empty = 0;
		for (int i = 0; i < 23; i++) {
			for (int j = 0; j < 10; j++) {
				player.draw(true);
				board[i][j].setPreData(false, null);
				board[i][j].copyData(board[i + 1][j]);
				player.getPrePosition();
			}
		}
		for (int j = 0; j < 10; j++) {
			Random rnd = new Random();
			empty = rnd.nextInt(9);
			board[23][j].getPre(false);
			board[23][j].setData(true, Color.GRAY);
			player.getPrePosition();
		}
		board[23][empty].setData(false, null);
		board[23][empty].setPreData(false, null);
		player.draw(false);
		WaitingRoomController wrc = (WaitingRoomController) MainApp.app.getController("room");
		if (host) {
			// 만약 내가 호스트라면 유저에 뿌요뿌요 하기
			wrc.send("USER_COPY_PUYO " + temp + " " + empty);
		} else {
			// 만약 내가 유저라면 호스트에 뿌요뿌요 하기
			wrc.send("HOST_COPY_PUYO " + temp + " " + empty);
		}
	}

}
