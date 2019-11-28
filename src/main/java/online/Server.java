package online;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.MainApp;
import views.WaitingRoomController;

public class Server {

	public static ExecutorService threadPool;
	public static Vector<ServerClient> clients = new Vector<ServerClient>();

	ServerSocket serverSocket;

	// 서버를 구동시켜 클라이언트의 연결을 기다리는 메소드

	public static int userCnt = 0;

	private String IP = "127.0.0.1";
	private int port = 9876;

	public void startServer() {
		String ip = "127.0.0.1";
		try {
			DatagramSocket soc = new DatagramSocket();
			soc.connect(InetAddress.getByName("8.8.8.8"), 10002);
			ip = soc.getLocalAddress().getHostAddress();

		} catch (Exception e) {
			ip = "127.0.0.1";
		}
		IP = ip;
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(IP, port));
		} catch (Exception e) {
			e.printStackTrace();
			if (!serverSocket.isClosed())
				stopServer();
			return;
		}
		WaitingRoomController wrc = (WaitingRoomController) MainApp.app.getController("room");
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socket = serverSocket.accept();
						userCnt++;

						clients.add(new ServerClient(socket));
						System.out.println("[클라이언트 접속]" + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName());
						if (userCnt > 2) {
							userCnt = 2;
						}
					} catch (Exception e) {
						if (!serverSocket.isClosed())
							stopServer();
						break;
					}
				}
			}
		};
		threadPool = Executors.newCachedThreadPool();
		threadPool.submit(thread);

	}

	// 서버의 작동을 중지시키는 메소드
	public void stopServer() {
		userCnt = 0;
		try {
			// 현재 작동중인 모든 소켓 닫기

			Iterator<ServerClient> iterator = clients.iterator();
			while (iterator.hasNext()) {
				ServerClient client = iterator.next();
				client.socket.close();
				iterator.remove();
			}
			// 서버 소켓 객체 닫기
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			// 쓰레드 풀 종료하기
			if (threadPool != null && !threadPool.isShutdown())
				threadPool.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
