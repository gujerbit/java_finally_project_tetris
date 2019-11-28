package views;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import domain.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import main.MainApp;
import online.Server;

public class WaitingRoomController extends MasterController {
	@FXML
	private AnchorPane roomPage;
	@FXML
	private TextField IPText;
	@FXML
	private TextField portText;
	@FXML
	private TextArea txt;
	@FXML
	private Label hostLabel;;

	@FXML
	private TextField input;
	@FXML
	private Button connectionButton;
	@FXML
	private Button startBtn;
	@FXML
	private Button readyBtn;
	@FXML
	private Pane box1;
	@FXML
	private Label userName1;
	@FXML
	private Label userId1;
	@FXML
	private Pane box2;
	@FXML
	private Label userName2;
	@FXML
	private Label userId2;

	// 내 비번
	public String name;
	// 내 아이디
	public String id;

	private String enemyName;

	private boolean first = false;
	// 내가 방장인지 아닌지
	private boolean host = false;
	// 서버가 가득 찼는지
	private boolean pull = false;
	// 게임이 시작됐는지
	private boolean checkStart = false;

	Socket socket;
	Server server = new Server();
	Player player = new Player(MainApp.app.onGame.board);

	public void host() {
		readyBtn.setDisable(true);
		Server.userCnt = 0;
		host = true;
		first = false;
		checkStart = false;
		String ip = "127.0.0.1";
		try {
			DatagramSocket soc = new DatagramSocket();
			soc.connect(InetAddress.getByName("8.8.8.8"), 10002);
			ip = soc.getLocalAddress().getHostAddress();

		} catch (Exception e) {
			ip = "127.0.0.1";
		}
		String IP = ip;
		IPText.setText(IP);
		hostLabel.setText(name);
		box1.setOpacity(1);
		userName1.setText(name);
		userId1.setText(id);
		connection();
	}

	public void user() {
		host = false;
		first = false;
		checkStart = false;
	}

	public void exit() {
		clickSound();
		if (!pull) {
			connection();
		}
		MainApp.app.slideOut(roomPage);
		reset();

	}

	@FXML
	private void initialize() {
		txt.setEditable(false);
		input.setDisable(true);
		startBtn.setDisable(true);
		readyBtn.setDisable(true);
	}

	// 클라이언트 프로그램의 작동을 시작하는 메소드 입니다.
	public void startClient(String IP, int port) {
		Thread thread = new Thread() {
			public void run() {
				try {
					socket = new Socket(IP, port);
					receive();
				} catch (Exception e) {
//					if (!socket.isClosed()) {
//						stopClient();
//						System.out.println("서버접속 실패");
//						Platform.exit();
//					}
				}
			}
		};
		thread.start();
	}

	// 클라이언트 프로그램의 작동을 종료한느 메소드 입니다.
	public void stopClient() {
		first = false;
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 준비를 눌렀을때
	public void ready() {
		clickSound();
		send("SET_READY");
	}

	public void setBox() {
		box1.setOpacity(1);
		box2.setOpacity(1);
	}

	public void gameStart() {
		clickSound();
		send("GAME_START");
	}

//	11-22 한것
//	소리에 있는 버그 발견 -> 모든소리 주석처리
//	뿌요뿌요 버그 발견 -> 모든뿌요뿌요 주석처리
//	온라인할때 상대방 캔버스를 복사 하는데 WritableImage 를 String 로 받아서
//	애먹는중 어떻게 형변환 할지 고민중 아직 미완성 내일 최선한 쌤한테 물어볼 예정

//	11-23 한것
//	소리 설정들을 옵션 컨트롤러로 옮김 -> 하지만 렉걸리는 오류는 잡지 못 함 안진우 집 한에서 렉이나는 걸 수도
//	뿌요뿌요 버그 아직 고치지 못함
//	온라인캔버스 해결 못함
//	게임중 상대방이 나가면 자동으로 게임종료 구현 , 게임시작시 자신과 상대방의 이름이 나옴

	// 서버로부터 메시지를 전달받는 메소드 입니다.
	public void receive() {
		while (true) {
			if (first) {
				try {
					InputStream in = socket.getInputStream();
					byte[] buffer = new byte[512];
					int length = in.read(buffer);
					if (length == -1)
						throw new IOException();
					String message = new String(buffer, 0, length, "UTF-8");
					if (message.startsWith("HOST_COPY_PUYO")) {
						int idx = message.indexOf(" ");
						String cmd = message.substring(idx + 1);
						int temp = Integer.parseInt(cmd.split(" ")[0]);
						int empty = Integer.parseInt(cmd.split(" ")[1]);
						if (host) {
							Platform.runLater(() -> {
//								for (int i = 0; i < temp; i++) {
								MainApp.app.copyGame.setPuyopuyo(empty);
//								}
							});
						}
					} else if (message.startsWith("USER_COPY_PUYO")) {
						int idx = message.indexOf(" ");
						String cmd = message.substring(idx + 1);
						int temp = Integer.parseInt(cmd.split(" ")[0]) / 2;
						int empty = Integer.parseInt(cmd.split(" ")[1]);
						if (!host) {
							Platform.runLater(() -> {
//								for (int i = 0; i < temp; i++) {
								MainApp.app.copyGame.setPuyopuyo(empty);
//								}
							});
						}
					} else if (message.startsWith("HOST_SETBLOCK")) {
						if (host) {
							Platform.runLater(() -> {
								int idx = message.indexOf(" ");
								String cmd = message.substring(idx + 1);
								int x = Integer.valueOf(cmd.split(" ")[0]);
								int y = Integer.valueOf(cmd.split(" ")[1]);
								int rot = Integer.valueOf(cmd.split(" ")[2]);
								int current = Integer.valueOf(cmd.split(" ")[3]);
								MainApp.app.copyGame.setBlock(x, y, rot, current);
							});
						}
					} else if (message.startsWith("USER_SETBLOCK")) {
						if (!host) {
							Platform.runLater(() -> {
								int idx = message.indexOf(" ");
								String cmd = message.substring(idx + 1);
								int x = Integer.valueOf(cmd.split(" ")[0]);
								int y = Integer.valueOf(cmd.split(" ")[1]);
								int rot = Integer.valueOf(cmd.split(" ")[2]);
								int current = Integer.valueOf(cmd.split(" ")[3]);
								MainApp.app.copyGame.setBlock(x, y, rot, current);
							});
						}
					} else if (message.startsWith("HOST_OVER")) {// 방장이 게임오버 됐다면
						if (!host) {
							Platform.runLater(() -> {
								MainApp.app.onGame.setGameWin();
								txt.setText("");
								connectionButton.setDisable(false);
								input.setDisable(false);
								IPText.setDisable(false);
							});
						}
						Platform.runLater(() -> {
							connectionButton.setDisable(false);
							input.setDisable(false);
							IPText.setDisable(false);
						});
					} else if (message.startsWith("USER_OVER")) {// 유저가 게임오버 됐다면
						if (host) {
							Platform.runLater(() -> {
								MainApp.app.onGame.setGameWin();
								txt.setText("");
							});
						}
						Platform.runLater(() -> {
							connectionButton.setDisable(false);
							input.setDisable(false);
							IPText.setDisable(false);
						});
					} else if (message.startsWith("HOST_PUYO")) {// 방장에게 뿌요뿌요 하는거
						int idx = message.indexOf(" ");
						String cmd = message.substring(idx + 1);
						int abc = Integer.parseInt(cmd.split(" ")[0]);
						if (host) {
							Platform.runLater(() -> {
								for (int i = 0; i < abc; i++) {
									MainApp.app.onGame.puyopuyo(abc);
								}
							});
						}
					} else if (message.startsWith("USER_PUYO")) {// 유저에게 뿌요뿌요 하는거
						int idx = message.indexOf(" ");
						String cmd = message.substring(idx + 1);
						int abc = Integer.parseInt(cmd.split(" ")[0]);
						if (!host) {
							Platform.runLater(() -> {
								for (int i = 0; i < abc; i++) {
									MainApp.app.onGame.puyopuyo(abc);
								}
							});
						}
					} else if (message.startsWith("GAME_START")) {// 게임스타트 버튼을 눌렀을 때
						if (host) {
							MainApp.app.onGame.host = true;
						} else {
							MainApp.app.onGame.host = false;
						}
						checkStart = true;
						Platform.runLater(() -> {
							MainApp.app.onGame.gameStart();
							MainApp.app.loadPane("online");
							MainApp.app.onGame.nameSet(name, enemyName);
							connectionButton.setDisable(true);
							input.setDisable(true);
							IPText.setDisable(true);
						});

					} else if (message.startsWith("SET_READY")) {// 레디 버튼을 눌렀을 때
						if (readyBtn.getText().equals("준비") && startBtn.isDisable()) {
							if (host) {
								Platform.runLater(() -> {
									startBtn.setDisable(false);
								});
							} else {
								Platform.runLater(() -> {
									readyBtn.setText("준비해제");
								});
							}
							box2.setStyle("-fx-background-color: rgba(71,200,62,0.5)");
						} else if (readyBtn.getText().equals("준비해제") || !startBtn.isDisable()) {
							if (host) {
								Platform.runLater(() -> {
									startBtn.setDisable(true);
								});
							} else {// 그냥 메시지 전송하는거
								Platform.runLater(() -> {
									readyBtn.setText("준비");
								});
							}
							box2.setStyle("-fx-background-color:  rgba(0,0,0,0.3)");
						}
					} else if (message.startsWith("HOST_EXIT")) {
						// 방장이 나갔을 때
						box2.setStyle("-fx-background-color:  rgba(0,0,0,0.3)");
						if (!host) {
							Platform.runLater(() -> {
								connectionButton.setText("접속하기");
								input.setText("");
								txt.setText("");
								IPText.setText("");
								input.setDisable(true);
								hostLabel.setText("");
								box1.setOpacity(0);
								box2.setOpacity(0);
								readyBtn.setText("준비");
								if (checkStart) {
									OnlineMultiPlayController opc = (OnlineMultiPlayController) MainApp.app
											.getController("online");
									MainApp.app.slideOut(opc.onlineMultiPlayPage);
									txt.appendText("상대방이 게임을 종료했습니다.\n");
									MainApp.app.onGame.ready = 0;
									OptionController op = (OptionController) MainApp.app.getController("option");
									op.bgm.stop();
									connectionButton.setDisable(false);
									IPText.setDisable(false);
								} else {
									txt.appendText("방장이 나갔습니다.\n");
								}
								checkStart = false;
							});
							stopClient();
						} else {
							Server.userCnt = 0;
							server.stopServer();
						}
					} else if (message.startsWith("USER_EXIT")) {
						// 유저가 나갔을 때
						box2.setStyle("-fx-background-color:  rgba(0,0,0,0.3)");
						box2.setOpacity(0);
						Platform.runLater(() -> {
							startBtn.setDisable(true);
							if (checkStart && host) {
								OnlineMultiPlayController opc = (OnlineMultiPlayController) MainApp.app
										.getController("online");
								MainApp.app.slideOut(opc.onlineMultiPlayPage);
								txt.appendText("상대방이 게임을 종료했습니다.\n");
								MainApp.app.onGame.ready = 0;
								OptionController op = (OptionController) MainApp.app.getController("option");
								op.bgm.stop();
								connectionButton.setDisable(false);
								input.setDisable(false);
								IPText.setDisable(false);
							} else {
								if (host) {
									txt.appendText("상대방이 나갔습니다.\n");
								}
							}
							checkStart = false;
						});
						if (host) {
							Server.userCnt--;
							if (Server.userCnt < 0) {
								Server.userCnt = 0;
							}
						} else {
							stopClient();
						}
					} else if (message.startsWith("HOST_SET")) {
						// 호스트일때
						setBox();
						int idx = message.indexOf(" ");
						String cmd = message.substring(idx + 1);
						Platform.runLater(() -> {
							userName2.setText(cmd.split(" ")[0]);
							userId2.setText(cmd.split(" ")[1]);
							if (host) {
								enemyName = cmd.split(" ")[0];
							}
						});
						if (host) {
							// 방장이 유저의 이름을 받았으니까 방장도 유저에게 메시지 전송
							send("USER_SET " + name + " " + id);
						}
					} else if (message.startsWith("USER_SET")) {
						if (!host) {
							int idx = message.indexOf(" ");
							String cmd = message.substring(idx + 1);
							Platform.runLater(() -> {
								userName1.setText(cmd.split(" ")[0]);
								hostLabel.setText(cmd.split(" ")[0]);
								userId1.setText(cmd.split(" ")[1]);
								readyBtn.setDisable(false);
								enemyName = cmd.split(" ")[0];
							});
						}
					} else {
						Platform.runLater(() -> {
							txt.appendText(message);
						});
					}
				} catch (Exception e) {
					stopClient();
					break;
				}

			} else if (!first) {
				try {
					InputStream in = socket.getInputStream();
					byte[] buffer = new byte[512];
					int length = in.read(buffer);
					if (length == -1)
						throw new IOException();
					String message = new String(buffer, 0, length, "UTF-8");
					int abc = Integer.parseInt(message);
					pull = false;
					if (abc > 2) {
						pull = true;
						Platform.runLater(() -> {
							txt.appendText("해당 서버는 가득 찼습니다.\n");
							connectionButton.setText("접속하기");
							input.setDisable(true);
						});
						stopClient();
					}
					// abc = 플레이어수
					if (!host) {
						send("HOST_SET " + name + " " + id);
					}
					send(name + "님이 방에 접속하셨습니다.\n");
					first = true;

				} catch (Exception e) {
					stopClient();
					break;
				}
			}
		}

	}

	// 서버로 메시지를 전송하는 메소드 입니다.
	public void send(String message) {
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				} catch (Exception e) {
					stopClient();
				}
			}
		};
		thread.start();
	}

	// send버튼 눌렸을때
	public void inputAction() {
		send(name + " : " + input.getText() + "\n");
		input.setText("");
		input.requestFocus();
	}

	// 서버에 연결하는 부분
	public void connection() {
		clickSound();
		if (connectionButton.getText().equals("접속하기")) {

			int port = 9876;
			try {
				port = Integer.parseInt(portText.getText());
			} catch (Exception e) {
				e.printStackTrace();
			}
			startClient(IPText.getText(), port);
			connectionButton.setText("종료하기");
			input.setDisable(false);
		} else {
			if (!host) {
				send("USER_EXIT");
			} else {
				send("HOST_EXIT");
			}
			reset();
		}
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
		Server.userCnt = 0;
		connectionButton.setText("접속하기");
		input.setText("");
		txt.setText("");
		IPText.setText("");
		input.setDisable(true);
		hostLabel.setText("");
		box1.setOpacity(0);
		box2.setOpacity(0);
		first = false;
		host = false;
		checkStart = false;
		readyBtn.setText("준비");
		startBtn.setDisable(true);
	}
}
