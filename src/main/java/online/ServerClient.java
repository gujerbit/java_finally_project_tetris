package online;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import main.MainApp;
import views.WaitingRoomController;

public class ServerClient {

	Socket socket;

	public ServerClient(Socket socket) {
		this.socket = socket;
		send(Server.userCnt + "");
		receive();
		
	}

	// 반복적으로 클러이언트로부터 메시지 받는 메소드.
	WaitingRoomController wrc = (WaitingRoomController) MainApp.app.getController("room");

	public void receive() {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						InputStream in = socket.getInputStream();
						byte[] buffer = new byte[512];

						int length = in.read(buffer);
						System.out.println(
								"[메시지 수신성공]" + socket.getRemoteSocketAddress() + ": " + Thread.currentThread());
						
						String message = new String(buffer, 0, length, "UTF-8");
						for (ServerClient client : Server.clients) {
							client.send(message);
						}
					}
				} catch (Exception e) {
					try {
						System.out.println("[메시지수신오류]" + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName());
//						Server.userCnt --;
						Server.clients.remove(ServerClient.this);
						socket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}

				}
			}
		};
		Server.threadPool.submit(thread);
	}

	// 해당 클라이언트에게 메시지 전송
	public void send(String message) {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				} catch (Exception e) {
					try {
						System.out.println("[메시지수신오류]" + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName());
						Server.clients.remove(ServerClient.this);
					} catch (Exception e2) {
						e2.printStackTrace();
					}

				}
			}
		};
		Server.threadPool.submit(thread);
	}


}
