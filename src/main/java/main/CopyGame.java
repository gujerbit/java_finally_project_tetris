package main;

import java.util.Random;

import domain.Block;
import domain.Player;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class CopyGame {
	private GraphicsContext gc;
	public Block[][] board;

	private double width;
	private double height;

	private Player player;

	public Label label; // 메인컨트롤러에서 받아온 라벨, 이 라벨에 setText 하면 잘 써짐
	public Label over;
	public Button btn;
	public Canvas sCanvas;
	
//	private KeyEvent e;

	public CopyGame(Canvas canvas) {
		// 캔버스의 너비와 높이를 가져온다.

		width = canvas.getWidth();
		height = canvas.getHeight();

		double size = (width - 4) / 10;

		board = new Block[24][10]; // 게임 판 만들고

		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 10; j++) {
				board[i][j] = new Block(j * size + 2, i * size + 2, size);
			}
		}

		this.gc = canvas.getGraphicsContext2D();

		player = new Player(board);
		render();
	}

	public void reset() {
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 10; j++) {
				board[i][j].setData(false, Color.WHITE);
				board[i][j].setPreData(false, Color.WHITE);
			}
		}
		render();
	}

	public void setBlock(int x, int y, int rot, int current) {
//		System.out.println("여기 들어옴");
		player.copyMove(x, y, rot, current);
//		System.out.println("x : " + x + "\ny : " + y + "\nrot : " + rot + "\ncurrent : " + current);
		checkLineStatus();
		render();
		player.tempMove();
	}

	public void setPuyopuyo(int empty) {
		// 한 번 실행 될 때 마다 여기를 호출
		for (int i = 0; i < 23; i++) {
			for (int j = 0; j < 10; j++) {
				board[i][j].copyData(board[i + 1][j]);
			}
		}

		for (int j = 0; j < 10; j++) {
			board[23][j].setData(true, Color.GRAY);
			board[23][j].getPre(false);
		}
		board[23][empty].setData(false, null);
		render();
	}


	// 업데이트 매서드

	public void checkLineStatus() {
		// 라인이 꽉 찼는지 체크해주는 매서드
		for (int i = 23; i >= 0; i--) {
			boolean clear = true;
			for (int j = 0; j < 10; j++) {
				if (!board[i][j].getFill()) {
					clear = false;// 한칸이라도 비어있다면 clear를 false 로 설정
					break;
				}
			}
			if (clear) {// 해당 줄이 꽉 찼다면
				for (int j = 0; j < 10; j++) {
					board[i][j].setData(false, Color.WHITE);
				}
				for (int k = i - 1; k >= 0; k--) {
					for (int j = 0; j < 10; j++) {
						board[k + 1][j].copyData(board[k][j]);
					}
				}
				for (int j = 0; j < 10; j++) {
					board[0][j].setData(false, Color.WHITE);
				}
				i++;
			}
		}
	}

	public void puyopuyo() {
		int empty = 0;
		for (int i = 0; i < 23; i++) {
			for (int j = 0; j < 10; j++) {
				player.draw(true);
				board[i][j].copyData(board[i + 1][j]);
			}
		}

		for (int j = 0; j < 10; j++) {
			Random rnd = new Random();
			empty = rnd.nextInt(9);
			board[23][j].setData(true, Color.GRAY);
			board[23][j].getPre(false);
		}

		board[23][empty].setData(false, null);
		player.draw(false);
	}

	public void moveBlock(String move) {
//		int dx = 0, dy = 0; // 이동한 거리
//		boolean rot = false; // 회전했는지 여부
//		if (move.equals("LEFT")) {
//			dx -= 1;
//		} else if (move.equals("RIGTH")) {
//			dx += 1;
//		} else if (move.equals("UP")) {
//			rot = true;
//		}
//
//		player.copyMove(dx, dy, rot);
//
//		// 내려가는 로직
//		if (move.equals("DOWN")) {
//			player.copyDown();
//		} else if (move.equals("ENTER")) {
//			while (!player.copyDown()) {
//				// do nothing
//			}
//		}
//		render();
	}

	// 렌더 메서드
	public void render() {
//		if(e.getCode() == KeyCode.C) {
//			return;
//		}
		// 매 프레임마다 화면을 그려주는 메서드
		gc.clearRect(0, 0, width, height);
		gc.setStroke(Color.rgb(0, 0, 0));
		gc.setLineWidth(2);
		gc.strokeRect(0, 0, width, height);

		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 10; j++) {
				board[i][j].render(gc);
			}
		}
	}

}
