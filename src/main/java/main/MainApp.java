package main;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import online.Server;
import views.FindIdController;
import views.FindPasswordController;
import views.LoginController;
import views.MasterController;
import views.OfflineMultiPlayController;
import views.OnlineMultiPlayController;
import views.OptionController;
import views.ProfileController;
import views.RankingController;
import views.RegisterController;
import views.SinglePlayController;
import views.WaitingRoomController;

public class MainApp extends Application {

	public static MainApp app;

	private StackPane mainPage;

	private Map<String, MasterController> controllerMap = new HashMap<>();

	public SingleGame game = null;
	public OfflineMultiPlayGame offGame = null;
	public OnlineMultiPlayGame onGame = null;
	public CopyGame copyGame = null;

	Server server = new Server();

	public MasterController getController(String name) {
		return controllerMap.get(name);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		app = this;
		try {
			// 메인 페이지
			FXMLLoader mainLoader = new FXMLLoader();
			mainLoader.setLocation(getClass().getResource("/views/MainLayout.fxml"));
			mainPage = mainLoader.load();
			// 메인 페이지 컨트롤러 넣기
			MasterController mc = mainLoader.getController();
			controllerMap.put("main", mc);

			// 로그인 페이지
			FXMLLoader loginLoader = new FXMLLoader();
			loginLoader.setLocation(getClass().getResource("/views/LoginLayout.fxml"));
			AnchorPane loginPage = loginLoader.load();
			// 맵에 로그인 컨트롤러 넣어주기
			LoginController lc = loginLoader.getController();
			lc.setRoot(loginPage);
			controllerMap.put("login", lc);
			// 회원가입 페이지
			FXMLLoader registerLoader = new FXMLLoader();
			registerLoader.setLocation(getClass().getResource("/views/RegisterLayout.fxml"));
			AnchorPane registerPage = registerLoader.load();
			// 맵에 회원가입 컨트롤러 넣어주기
			RegisterController rc = registerLoader.getController();
			rc.setRoot(registerPage);
			controllerMap.put("register", rc);

			// 싱글 플레이 페이지
			FXMLLoader singlePlayLoader = new FXMLLoader();
			singlePlayLoader.setLocation(getClass().getResource("/views/SinglePlayLayout.fxml"));
			AnchorPane singlePlayPage = singlePlayLoader.load();
			// 맵에 싱글 플레이 컨트롤러 넣어주기
			SinglePlayController spc = singlePlayLoader.getController();
			spc.setRoot(singlePlayPage);
			controllerMap.put("singlePlay", spc);
			// 계정 아이디 찾기 페이지
			FXMLLoader findIdLoader = new FXMLLoader();
			findIdLoader.setLocation(getClass().getResource("/views/FindIdLayout.fxml"));
			AnchorPane findIdPage = findIdLoader.load();
			// 맵에 계정 찾기 컨트롤러 넣어주기
			FindIdController fic = findIdLoader.getController();
			fic.setRoot(findIdPage);
			controllerMap.put("findId", fic);
			// 계정 비밀번호 찾기 페이지
			FXMLLoader findPasswordLoader = new FXMLLoader();
			findPasswordLoader.setLocation(getClass().getResource("/views/FindPasswordLayout.fxml"));
			AnchorPane findPasswordPage = findPasswordLoader.load();
			// 맵에 계정 비밀번호 찾기 컨트롤러 넣어주기
			FindPasswordController fpc = findPasswordLoader.getController();
			fpc.setRoot(findPasswordPage);
			controllerMap.put("findPassword", fpc);

			// 오프라인 멀티플레이 페이지
			FXMLLoader offlineLoader = new FXMLLoader();
			offlineLoader.setLocation(getClass().getResource("/views/OfflineMultiPlayLayout.fxml"));
			AnchorPane offlinePage = offlineLoader.load();
			// 맵에 오프라인 멀티플레이 컨트롤러 넣어주기
			OfflineMultiPlayController ofmpc = offlineLoader.getController();
			ofmpc.setRoot(offlinePage);
			controllerMap.put("offline", ofmpc);

			// 옵션 페이지
			FXMLLoader optionLoader = new FXMLLoader();
			optionLoader.setLocation(getClass().getResource("/views/OptionLayout.fxml"));
			AnchorPane optionPage = optionLoader.load();
			// 맵에 옵션 컨트롤러 넣어주기
			OptionController opc = optionLoader.getController();
			opc.setRoot(optionPage);
			controllerMap.put("option", opc);

			// 온라인 멀티플레이 페이지
			FXMLLoader onlineLoader = new FXMLLoader();
			onlineLoader.setLocation(getClass().getResource("/views/OnlineMultiPlayLayout.fxml"));
			AnchorPane onlineMultiPlayPage = onlineLoader.load();
			// 맵에 온라인 멀티플레이 컨트롤러 넣어주기
			OnlineMultiPlayController onmpc = onlineLoader.getController();
			onmpc.setRoot(onlineMultiPlayPage);
			controllerMap.put("online", onmpc);

			// 대기방 페이지
			FXMLLoader roomLoader = new FXMLLoader();
			roomLoader.setLocation(getClass().getResource("/views/WaitingRoomLayout.fxml"));
			AnchorPane roomPage = roomLoader.load();
			// 맵에 대기방 컨트롤러 넣어주기
			WaitingRoomController wrc = roomLoader.getController();
			wrc.setRoot(roomPage);
			controllerMap.put("room", wrc);
			
			// 프로필 페이지
			FXMLLoader profileLoader = new FXMLLoader();
			profileLoader.setLocation(getClass().getResource("/views/ProfileLayout.fxml"));
			AnchorPane profilePage = profileLoader.load();
			// 맵에 프로필 컨트롤러 넣어주기
			ProfileController pfc = profileLoader.getController();
			pfc.setRoot(profilePage);
			controllerMap.put("profile", pfc);
			
			// 랭킹 페이지
			FXMLLoader rankingLoader = new FXMLLoader();
			rankingLoader.setLocation(getClass().getResource("/views/RankingLayout.fxml"));
			AnchorPane rankingPage = rankingLoader.load();
			// 맵에 랭킹 컨트롤러 넣어주기
			RankingController rkc = rankingLoader.getController();
			rkc.setRoot(rankingPage);
			controllerMap.put("ranking", rkc);

			Scene scene = new Scene(mainPage);

			scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
			mainPage.getChildren().add(loginPage);

			scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
				if (game != null) {
					game.keyHandler(e);
				}
			});

			scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
				if (offGame != null) {
					offGame.oneKeyHandler(e);
					offGame.twoKeyHandler(e);
				}
			});

			scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
				if (onGame != null) {
					onGame.keyHandler(e);
				}
			});

			WaitingRoomController wrc1 = (WaitingRoomController) MainApp.app.getController("room");
			primaryStage.getIcons().add(new Image("/resources/icons.jpg"));
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setOnCloseRequest((e) -> {
				wrc.exit();
			});
			primaryStage.setResizable(false);
			primaryStage.setTitle("TETRIS");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FXML Error");
		}
	}

	public void slideOut(Pane pane) {
		Timeline timeline = new Timeline();
		KeyValue toRight = new KeyValue(pane.translateXProperty(), 1300);
		// KeyValue fadeOut = new KeyValue(pane.opacityProperty(), 0);

		KeyFrame keyFrame = new KeyFrame(Duration.millis(500), (e) -> {
			mainPage.getChildren().remove(pane);
		}, toRight/* , fadeOut */);

		timeline.getKeyFrames().add(keyFrame);
		timeline.play();
	}

	public void loadPane(String name) {
		MasterController mc = controllerMap.get(name);
		Pane pane = mc.getRoot();
		mainPage.getChildren().add(pane);

		pane.setTranslateX(-800);
		// pane.setOpacity(0);

		Timeline tl = new Timeline();
		KeyValue toRight = new KeyValue(pane.translateXProperty(), 0);
		KeyValue fadeOut = new KeyValue(pane.opacityProperty(), 1);
		KeyFrame keyFrame = new KeyFrame(Duration.millis(500), toRight, fadeOut);

		tl.getKeyFrames().add(keyFrame);
		tl.play();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
