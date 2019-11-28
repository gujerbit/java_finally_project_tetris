package domain;

import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import main.MainApp;
import views.OptionController;
import views.WaitingRoomController;

public class Player {
	// 테트리스에는 총 7개의 블럭이 있어.
	// 각각의 블럭은 최대 4개의 변형을 가져
	// 각각의 블럭은 모두 4개의 블럭조각으로 구성되어 있어.
	private Point2D[][][] shape = new Point2D[7][][];

	public int current = 0; // 현재 블럭
	public int copyCurrent = 0;
	public int copyNowColor = 0;
	private int rotate = 0; // 현재 회전상태
	public int nowColor = 0;
	private Color[] colorSet = new Color[7];

	private boolean bxLeft = false;
	private boolean bxRight = false;
	private boolean byUp = false;
	private boolean byDown = false;

	private boolean shape0 = false;
	private boolean shape2 = false;
	private boolean shape3 = false;
	private boolean shape4 = false;
	private boolean shape5 = false;
	private boolean shape6 = false;

	public boolean host = false;

	private Random rnd;

	private int x = 5;
	private int y = 3;

	private int bx;
	private int by;

	private int preX;
	private int preY;

	private Block[][] board;

	private int nextBlock;
	private int nextColor;

	private int holdBlock;
	private int holdColor;
	private int holdCnt;
	private boolean checkhold;
	private int tempBlock;
	private int tempColor;

	public boolean online = false;

	public boolean ok = false;

	public int empty;

	private OptionController oc = new OptionController();

	public Player(Block[][] board) {
		this.board = board;
		// 작대기
		shape[0] = new Point2D[2][];
		shape[0][0] = getPointArray("0,-1:0,0:0,1:0,2");
		shape[0][1] = getPointArray("-1,0:0,0:1,0:2,0");
		// 네모
		shape[1] = new Point2D[1][];
		shape[1][0] = getPointArray("0,0:1,0:0,1:1,1");
		// ㄴ모양
		shape[2] = new Point2D[4][];
		shape[2][0] = getPointArray("0,-2:0,-1:0,0:1,0");
		shape[2][1] = getPointArray("0,1:0,0:1,0:2,0");
		shape[2][2] = getPointArray("-1,0:0,0:0,1:0,2");
		shape[2][3] = getPointArray("-2,0:-1,0:0,0:0,-1");

		// 역 ㄴ모양
		shape[3] = new Point2D[4][];
		shape[3][0] = getPointArray("0,-2:0,-1:0,0:-1,0");
		shape[3][1] = getPointArray("0,-1:0,0:1,0:2,0");
		shape[3][2] = getPointArray("0,0:1,0:0,1:0,2");
		shape[3][3] = getPointArray("-2,0:-1,0:0,0:0,1");

		shape[4] = new Point2D[2][];
		shape[4][0] = getPointArray("0,0:-1,0:0,-1:1,-1");
		shape[4][1] = getPointArray("0,0:0,-1:1,0:1,1");
		// 왼쪽무릎
		shape[5] = new Point2D[2][];
		shape[5][0] = getPointArray("0,0:0,-1:-1,-1:1,0");
		shape[5][1] = getPointArray("0,0:1,0:1,-1:0,1");
		// ㅗ 모양
		shape[6] = new Point2D[4][];
		shape[6][0] = getPointArray("0,0:0,-1:-1,0:1,0");
		shape[6][1] = getPointArray("0,0:0,-1:1,0:0,1");
		shape[6][2] = getPointArray("0,0:0,1:-1,0:1,0");
		shape[6][3] = getPointArray("0,0:-1,0:0,-1:0,1");

		// 색상 넣기
		colorSet[0] = Color.rgb(0, 187, 208);
		colorSet[1] = Color.rgb(232, 213, 0);
		colorSet[2] = Color.rgb(217, 146, 0);
		colorSet[3] = Color.rgb(0, 99, 196);
		colorSet[4] = Color.rgb(0, 232, 67);
		colorSet[5] = Color.rgb(219, 0, 0);
		colorSet[6] = Color.rgb(180, 0, 217);

		reSet();
		draw(false);
	}

	public void reSet() {
		// 게임이 처음시작, 재시작 할때 가장 처음 실행되는 메서드
		// 여기에 초기화 변수 넣어주면 됨
		ok = false;
		rnd = new Random();
		current = rnd.nextInt(shape.length);
		// current = 6;
		nowColor = current;
		nextBlock = rnd.nextInt(shape.length);
		// nextBlock = 6;
		nextColor = nextBlock;
		x = 5;
		y = 3;
		rotate = 0;
		holdCnt = 0;
		checkhold = true;
		if (shape[current] == shape[0]) {
			shape0 = true;
		} else if (shape[current] == shape[2]) {
			shape2 = true;
		} else if (shape[current] == shape[3]) {
			shape3 = true;
		} else if (shape[current] == shape[4]) {
			shape4 = true;
		} else if (shape[current] == shape[5]) {
			shape5 = true;
		} else if (shape[current] == shape[6]) {
			shape6 = true;
		}
		getPrePosition();
	}

	public void getPrePosition() {
		preX = x;
		preY = y;

		while (true) {
			preY += 1;
			if (!checkPossible(preX, preY)) {
				preY -= 1;
				break;
			}
		}
		// System.out.println(preX + ", " + preY);

	}

	public void draw(boolean remove) {
		for (int i = 0; i < shape[current][rotate].length; i++) {
			Point2D point = shape[current][rotate][i];
			// 예상 포지션 먼저 그려주고
			int px = (int) point.getX() + preX;
			int py = (int) point.getY() + preY;
			try {
				board[py][px].setPreData(!remove, colorSet[nowColor]);
			} catch (Exception e) {
				// System.out.println(py + ", " + px + ", " + preX + ", " + preY);
			}
			int bx = (int) point.getX() + x;
			int by = (int) point.getY() + y;
			board[by][bx].setData(!remove, colorSet[nowColor]);
		}
	}

	public void copyDraw(boolean remove) {
		for (int i = 0; i < shape[copyCurrent][rotate].length; i++) {
			Point2D point = shape[copyCurrent][rotate][i];
			// 예상 포지션 먼저 그려주고
//			int px = (int) point.getX() + preX;
//			int py = (int) point.getY() + preY;
//			try {
//				board[py][px].setPreData(!remove, colorSet[nowColor]);
//			} catch (Exception e) {
//				// System.out.println(py + ", " + px + ", " + preX + ", " + preY);
//			}
			int bx = (int) point.getX() + x;
			int by = (int) point.getY() + y;
			board[by][bx].setData(!remove, colorSet[copyNowColor]);
		}
	}

	public Point2D[] getPointArray(String pointStr) {
		// 0,-1:0,0:0,1:0,2 형식의 데이터 스트링이 넘어오면 이를 포인트 배열로 변환
		Point2D[] arr = new Point2D[4];
		String[] pointList = pointStr.split(":");
		for (int i = 0; i < pointList.length; i++) {
			String[] point = pointList[i].split(",");
			double x = Double.parseDouble(point[0]);
			double y = Double.parseDouble(point[1]);
			arr[i] = new Point2D(x, y);
		}
		return arr;
	}

	public void keyHandler(KeyEvent e) {
		int dx = 0, dy = 0; // 이동한 거리
		boolean rot = false; // 회전했는지 여부
		oc = (OptionController) MainApp.app.getController("option");
		if (!oc.setLeft) {
			if (e.getCode() == KeyCode.LEFT) {
				dx -= 1;
			}
		} else {
			if (e.getCode() == KeyCode.A) {
				dx -= 1;
			}
		}

		if (!oc.setRight) {
			if (e.getCode() == KeyCode.RIGHT) {
				dx += 1;
			}
		} else {
			if (e.getCode() == KeyCode.D) {
				dx += 1;
			}
		}

		if (!oc.setTurn) {
			if (e.getCode() == KeyCode.UP) {
				rot = true;
			}
		} else {
			if (e.getCode() == KeyCode.W) {
				rot = true;
			}
		}

		move(dx, dy, rot);

		// 내려가는 로직
		if (!oc.setDown) {
			if (e.getCode() == KeyCode.DOWN) { // 한칸 내리기
				down();
			}
		} else {
			if (e.getCode() == KeyCode.S) {
				down();
			}
		}

		if (!oc.setFullDown) {
			if (e.getCode() == KeyCode.SPACE) { // 한방에 내리기
				while (!down()) {
					// do nothing
				}
			}
		} else {
			if (e.getCode() == KeyCode.ENTER) {
				while (!down()) {

				}
			}
		}

		if (!oc.setHold) {
			if (e.getCode() == KeyCode.C) {
				hold();
			}
		} else {
			if (e.getCode() == KeyCode.DELETE) {
				hold();
			}
		}
	}

	public void oneKeyHandler(KeyEvent e) {
		int dx = 0, dy = 0; // 이동한 거리
		boolean rot = false; // 회전했는지 여부
		if (e.getCode() == KeyCode.A) {
			dx -= 1;
		} else if (e.getCode() == KeyCode.D) {
			dx += 1;
		} else if (e.getCode() == KeyCode.W) {
			rot = true;
		}

		move(dx, dy, rot);

		// 내려가는 로직
		if (e.getCode() == KeyCode.S) { // 한칸 내리기
			down();
		} else if (e.getCode() == KeyCode.SPACE) { // 한방에 내리기
			while (!down()) {
				// do nothing
			}
//			System.out.println("1s");
		}

		if (e.getCode() == KeyCode.C) {
			hold();
		}
	}

	public void twoKeyHandler(KeyEvent e) {
		int dx = 0, dy = 0; // 이동한 거리
		boolean rot = false; // 회전했는지 여부
		if (e.getCode() == KeyCode.LEFT) {
			dx -= 1;
		} else if (e.getCode() == KeyCode.RIGHT) {
			dx += 1;
		} else if (e.getCode() == KeyCode.UP) {
			rot = true;
		}

		move(dx, dy, rot);

		// 내려가는 로직
		if (e.getCode() == KeyCode.DOWN) { // 한칸 내리기
			down();
		} else if (e.getCode() == KeyCode.ENTER) { // 한방에 내리기
			while (!down()) {
				// do nothing
			}
//			System.out.println("2s");
		}

		if (e.getCode() == KeyCode.DELETE) {
			hold();
		}
	}

	public void move(int dx, int dy, boolean rot) {
		draw(true);
		x += dx;
		y += dy;
		if (rot) {
			rotate = (rotate + 1) % shape[current].length;
		}

		if (!checkPossible(x, y)) {
			x -= dx;
			y -= dy;
			if (rot) {
				if (bxLeft == true || bxRight == true) {
					getTurnBlock();
					System.out.println("벽면 회전");
				} else {
//					if (!possibleError()) {
//						leftPossible();
//					rightPossible();
//					System.out.println("그냥 회전");
//				}
			}
			// upPossible();
			rotate = rotate - 1 < 0 ? shape[current].length - 1 : rotate - 1;
		}
	}

	getPrePosition();

	draw(false);
	}

	public void copyMove(int dx, int dy, int rot, int current) {
//		System.out.println("asd");
		copyDraw(true);
		x = dx;
		y = dy;
		rotate = rot;
		copyCurrent = current;
		copyNowColor = current;
		copyDraw(false);
	}

	public void tempMove() {
		x = 5;
		y = 3;
		rotate = 0;
		copyCurrent = 0;
		copyNowColor = 0;
		copyDraw(false);
		copyDraw(true);
	}

	public boolean down() {
		// 블럭을 한칸 아래로 내리는 매서드
		draw(true);
		y += 1;
		if (!checkPossible(x, y)) {
			y -= 1;
			draw(false); // 내려놓은 블럭을 원상복구 하기

			if (MainApp.app.game.ready == 2) {
				MainApp.app.game.checkLineStatus();
			} else if (MainApp.app.offGame.ready == 2) {
				MainApp.app.offGame.oneCheckLineStatus();
				MainApp.app.offGame.twoCheckLineStatus();
			} else if (MainApp.app.onGame.ready == 2) {
				MainApp.app.onGame.checkLineStatus();
			}
			ok = true;
			getNextBlock();
			draw(false); // 다음블럭 뽑은것을 그려주는거야
			return true;
		}
		draw(false);
		return false;
	}

	public boolean copyDown() {
		// 블럭을 한칸 아래로 내리는 매서드
		draw(true);
		y += 1;
		if (!checkPossible(x, y)) {
			y -= 1;
			draw(false); // 내려놓은 블럭을 원상복구 하기
//			getCopyNextBlock();
//			draw(false); // 다음블럭 뽑은것을 그려주는거야
			return true;
		}
		draw(false);
		return false;
	}

	private boolean leftBlock() {
		if (bxLeft == true) {
			if (shape0 == true && rotate == 1) {
				return true;
			} else if (shape2 == true && rotate == 2 || rotate == 3) {
				return true;
			} else if (shape3 == true && rotate == 3) {
				return true;
			} else if (shape4 == true && rotate == 0) {
				return true;
			} else if (shape5 == true && rotate == 0) {
				return true;
			} else if (shape6 == true && rotate == 2) {
				return true;
			}
		}
		return false;
	}

	private boolean rightBlock() {
		if (bxRight == true) {
			if (shape0 == true && rotate == 1) {
				return true;
			} else if (shape2 == true && rotate == 0 || rotate == 1) {
				return true;
			} else if (shape3 == true && rotate == 1) {
				return true;
			} else if (shape6 == true && rotate == 0) {
				return true;
			}
		}
		return false;
	}

	private boolean specialBlock() {
		if (shape0 == true && rotate == 1 && bxRight == true) {
			return true;
		} else if (shape3 == true && rotate == 3 && bxLeft == true) {
			return true;
		} else if (shape3 == true && rotate == 1 && bxRight == true) {
			return true;
		}
		return false;
	}

	public void getTurnBlock() {
		if (bxLeft == true) {
			if (leftBlock()) {
				if (specialBlock()) {
					x += 1;
				}
				x += 1;
				rotate += 1;
			}
		}

		if (bxRight == true) {
			if (rightBlock()) {
				if (specialBlock()) {
					x -= 1;
				}
				x -= 1;
				rotate += 1;
			}
		}
	}

	public void leftPossible() {
		if (!checkPossible(x, y)) {
			if (!checkPossible(x - 1, y)) {
				if (!checkPossible(x - 2, y)) {

				} else if (checkPossible(x - 2, y) && !possibleError()) {
					x -= 2;
					rotate += 1;
				} else if (checkPossible(x -= 2, y) && possibleError()) {
					x += 2;
					rotate -= 1;
				}
			} else if (!checkPossible(x - 1, y) && !possibleError()) {
				x -= 1;
				rotate += 1;
			} else if (checkPossible(x - 1, y) && possibleError()) {
				x += 1;
				rotate -= 1;
			}
		}
	}

	public void rightPossible() {
		if (!checkPossible(x, y)) {
			if (!checkPossible(x + 1, y)) {
				if (!checkPossible(x + 2, y)) {
//					if(!checkPossible(x+3, y) && shape0) {
//						x += 3;
//						rotate += 1;
//					}
				} else if (checkPossible(x + 2, y) && !possibleError()) {
					x += 2;
					rotate += 1;
				} else if (checkPossible(x + 2, y) && possibleError()) {
					x -= 2;
					rotate -= 1;
				}
			} else if (checkPossible(x + 1, y) && !possibleError()) {
				x += 1;
				rotate += 1;
			} else if (checkPossible(x + 1, y) && possibleError()) {
				x -= 1;
				rotate -= 1;
			}
		}
	}

	public void upPossible() {
		if (!checkPossible(x, y)) {
			if (!checkPossible(x, y - 1)) {
				if (!checkPossible(x, y - 2)) {
					if (!checkPossible(x, y - 3)) {

					} else {
						y -= 3;
						rotate += 1;
					}
				} else {
					y -= 2;
					rotate += 1;
				}
			} else {
				y -= 1;
				rotate += 1;
			}
		}
	}

	public boolean possibleError() {
		leftPossible();
		rightPossible();
		upPossible();

		if (!checkPossible(x, y)) {
			return true;
		}
		return false;
	}

	public void hold() {
		// holdCnt == 0 처음 시작
		// holdCnt == 1 홀드
		// holdCnt == 2 홀드 금지

		if (!checkhold) {
			// 홀드하고 다시 홀드 못하게 하는 부분
			return;
		} else if (holdCnt == 1) {
			// 홀드 한걸 꺼낼때
			tempBlock = current;
			tempColor = nowColor; // 임시 저장소에 현재 블록하고 색 넣어주고
			draw(true); // 현재 블록 없애주고
			current = holdBlock;
			nowColor = holdColor; // 현재 블록에 홀드 블록과 색 넣어주고
			holdBlock = tempBlock;
			holdColor = tempColor; // 홀드에 임시 저장소에 넣은 블록하고 색 넣어주기
			x = 5;
			y = 3;
			holdCnt = 2;
			rotate = 0;
		} else if (holdCnt == 0) {
			// 처음으로 홀드할때
			holdBlock = current;
			holdColor = nowColor; // 홀드에 현재 블록하고 색 넣어주고
			draw(true); // 현재 블록 없애주고
			getNextBlock(); // 다음 블록 호출
			holdCnt = 1;
		}
		checkhold = false;
		getPrePosition(); // 홀드블럭 꺼낼때 프리 오류 있어서 이거 넣어야 됨
	}

	// 다음블럭을 생성하는 매서드
	public void getNextBlock() {
		WaitingRoomController wrc = (WaitingRoomController) MainApp.app.getController("room");
		if (online) {
			if (ok) {
				if (host) {
					// 만약 내가 방장일때 내 블럭을 유저로 넘겨줌
					wrc.send("USER_SETBLOCK " + x + " " + y + " " + rotate + " " + current);
					ok = false;
				} else {
					// 만약 내가 유저일때 내 블럭을 방장에게 넘겨줌
					wrc.send("HOST_SETBLOCK " + x + " " + y + " " + rotate + " " + current);
					ok = false;
				}
			}
		}
		x = 5;
		y = 3;
		rotate = 0;
		checkhold = true;
		current = nextBlock;
		nowColor = current;
		nextBlock = rnd.nextInt(shape.length);
		nextColor = nextBlock;
		shape0 = false;
		shape2 = false;
		shape3 = false;
		shape4 = false;
		shape5 = false;
		shape6 = false;
		if (shape[current] == shape[0]) {
			shape0 = true;
		} else if (shape[current] == shape[2]) {
			shape2 = true;
		} else if (shape[current] == shape[3]) {
			shape3 = true;
		} else if (shape[current] == shape[4]) {
			shape4 = true;
		} else if (shape[current] == shape[5]) {
			shape5 = true;
		} else if (shape[current] == shape[6]) {
			shape6 = true;
		}

		if (!checkPossible(x, y)) {
			draw(true);
			if (MainApp.app.game.ready == 2) {
				MainApp.app.game.setGameOver();
			} else if (MainApp.app.offGame.ready == 2) {
				if (!checkOne()) {
					MainApp.app.offGame.setOneGameOver();
				} else if (!checkTwo()) {
					MainApp.app.offGame.setTwoGameOver();
				}
			} else if (MainApp.app.onGame.ready == 2) {
				MainApp.app.onGame.setGameOver();
			}
		}
		getPrePosition();

		if (holdCnt != 0) {
			holdCnt = 1;
		}
	}

	private boolean checkPossible(int x, int y) {
		// 블럭의 이동이 가능한지 체크하는 매서드
		for (int i = 0; i < shape[current][rotate].length; i++) {
			bx = (int) shape[current][rotate][i].getX() + x;
			by = (int) shape[current][rotate][i].getY() + y;

			if (bx < 0) {
				bxLeft = true;
				return false;
			} else if (bx >= 10) {
				bxRight = true;
				return false;
			} else if (by < 1) {
				byUp = true;
				return false;
			} else if (by >= 24) {
				byDown = true;
				return false;
			} else {
				bxLeft = false;
				bxRight = false;
				byUp = false;
				byDown = false;
			}

			if (board[by][bx].getFill()) {
				return false;
			}
		}
		return true;
	}

	public boolean checkOne() {
		boolean empty = true;
		for (int i = 0; i < 10; i++) {
			if (MainApp.app.offGame.oneBoard[3][i].getFill()) {
				empty = false;
			}
		}

		if (!empty) {
			if (shape0) {
				if (MainApp.app.offGame.oneBoard[y + 4][x].getFill()) {
					return false;
				}
			} else if (shape2 || shape3) {
				if (MainApp.app.offGame.oneBoard[y + 3][x].getFill()) {
					return false;
				}
			} else {
				if (MainApp.app.offGame.oneBoard[y + 2][x].getFill()) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean checkTwo() {
		boolean empty = true;
		for (int i = 0; i < 10; i++) {
			if (MainApp.app.offGame.twoBoard[3][i].getFill()) {
				empty = false;
			}
		}

		if (!empty) {
			if (shape0) {
				if (MainApp.app.offGame.twoBoard[y + 4][x].getFill()) {
					return false;
				}
			} else if (shape2 || shape3) {
				if (MainApp.app.offGame.twoBoard[y + 3][x].getFill()) {
					return false;
				}
			} else {
				if (MainApp.app.offGame.twoBoard[y + 2][x].getFill()) {
					return false;
				}
			}
		}
		return true;
	}

	public void render(GraphicsContext gc, double width, double height) {
		Color color = colorSet[nextColor];
		Point2D[] block = shape[nextBlock][0]; // 다음 블럭 모양

		gc.clearRect(0, 0, width, height);
		double x = width / 2;
		double y = height / 2;
		double size = width / 4 - 10;
		if (nextBlock == 0) {
			y -= size;
		}

		for (int i = 0; i < block.length; i++) {
			double dx = x + block[i].getX() * size;
			double dy = y + block[i].getY() * size;
			gc.setFill(color.darker());
			gc.fillRoundRect(dx, dy, size, size, 4, 4);

			gc.setFill(color);
			gc.fillRoundRect(dx + 2, dy + 2, size - 4, size - 4, 4, 4);
		}

	}

	public void holdRender(GraphicsContext gc, double width, double height) {
		if (holdCnt == 0) {
			return;
		}
		Color color = colorSet[holdColor];
		Point2D[] block = shape[holdBlock][0];

		gc.clearRect(0, 0, width, height);
		double x = width / 2;
		double y = height / 2;
		double size = width / 4 - 10;
		if (holdBlock == 0) {
			y -= size;
		}
		for (int i = 0; i < block.length; i++) {
			double dx = x + block[i].getX() * size;
			double dy = y + block[i].getY() * size;
			gc.setFill(color.darker());
			gc.fillRoundRect(dx, dy, size, size, 4, 4);

			gc.setFill(color);
			gc.fillRoundRect(dx + 2, dy + 2, size - 4, size - 4, 4, 4);
		}
	}

	public void puyopuyo() {
		empty = 0;
		for (int i = 0; i < 23; i++) {
			for (int j = 0; j < 10; j++) {
				draw(true);
				board[i][j].setPreData(false, null);
				board[i][j].copyData(board[i + 1][j]);
				getPrePosition();
			}
		}
		for (int j = 0; j < 10; j++) {
			Random rnd = new Random();
			empty = rnd.nextInt(9);
			board[23][j].getPre(false);
			board[23][j].setData(true, Color.GRAY);
			getPrePosition();
		}
		board[23][empty].setData(false, null);
		board[23][empty].setPreData(false, null);
		draw(false);
	}

	public void clearBoard() {
		for (int i = 23; i >= 0; i--) {
			for (int j = 0; j < 10; j++) {
				board[i][j].setData(false, null);
			}
		}
	}
}
