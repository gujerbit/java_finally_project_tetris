package views;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import main.MainApp;
import util.JDBCUtil;
import util.Util;

public class ProfileController extends MasterController {
	@FXML
	private AnchorPane profilePage;
	@FXML
	private VBox mainBox;
	@FXML
	private VBox passBox;
	@FXML
	private VBox nameBox;
	@FXML
	private VBox deleteBox;
	@FXML
	private PasswordField txtPass;
	@FXML
	private PasswordField cPass;
	@FXML
	private TextField txtName;
	@FXML
	private TextField cName;
	@FXML
	private TextField dId;
	@FXML
	private TextField dCheck;
	@FXML
	private ComboBox<String> checkBox;

	private ObservableList<String> checkList;

	public String checkId;
	public String check;

	private String newS;

	@FXML
	public void initialize() {
		passBox.setOpacity(0);
		passBox.setDisable(true);
		nameBox.setOpacity(0);
		nameBox.setDisable(true);
		deleteBox.setOpacity(0);
		deleteBox.setDisable(true);

		checkList = FXCollections.observableArrayList("출생지는?", "가장 재미있게 플레이한 게임은?", "가장 좋아하는 음식은?", "졸업한 초등학교는?");
		checkBox.setItems(checkList);

		checkBox.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends String> observ, String oldS, String newS) -> {
					this.newS = newS;
				});
	}

	public void openPass() {
		clickSound();
		mainBox.setOpacity(0);
		passBox.setOpacity(1);
		passBox.setDisable(false);
		nameBox.setOpacity(0);
		nameBox.setDisable(true);
		deleteBox.setOpacity(0);
		deleteBox.setDisable(true);
	}

	public void closePass() {
		mainBox.setOpacity(1);
		clickSound();
		passBox.setOpacity(0);
		passBox.setDisable(true);
	}

	public void openName() {
		clickSound();
		mainBox.setOpacity(0);
		nameBox.setOpacity(1);
		nameBox.setDisable(false);
		passBox.setOpacity(0);
		passBox.setDisable(true);
		deleteBox.setOpacity(0);
		deleteBox.setDisable(true);
	}

	public void closeName() {
		mainBox.setOpacity(1);
		clickSound();
		nameBox.setOpacity(0);
		nameBox.setDisable(true);
	}

	public void openDelete() {
		clickSound();
		deleteBox.setOpacity(1);
		mainBox.setOpacity(0);
		deleteBox.setDisable(false);
		passBox.setOpacity(0);
		passBox.setDisable(true);
		nameBox.setOpacity(0);
		nameBox.setDisable(true);
	}

	public void closeDelete() {
		mainBox.setOpacity(1);
		clickSound();
		deleteBox.setOpacity(0);
		deleteBox.setDisable(true);
	}

	public void passChange() {
		clickSound();
		if (txtPass.getText().isEmpty()) {
			Util.showAlert("알림!", "바꿀 비밀번호를 입력해주세요!", AlertType.INFORMATION);
			return;
		}

		String pass = txtPass.getText().trim();
		String check = cPass.getText().trim();

		if (!check.equals(pass)) {
			Util.showAlert("오류!", "바꿀 비밀번호와 확인 비밀번호가 일치하지 않습니다!", AlertType.ERROR);
			return;
		}

		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;

		String sql = "UPDATE tetris_users SET password = PASSWORD(?) WHERE id = ?";

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, pass);
			pstmt.setString(2, checkId);

			pstmt.executeUpdate();

			if (pstmt.executeUpdate() != 1) {
				Util.showAlert("오류!", "비밀번호가 올바르지 않습니다!", AlertType.ERROR);
			}
			Util.showAlert("성공!", "비밀번호가 성공적으로 변경되었습니다!", AlertType.INFORMATION);
			closePass();
			reset();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("DB error");
		} finally {
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}

	public void nameChange() {
		clickSound();
		if (txtName.getText().isEmpty()) {
			Util.showAlert("알림!", "바꿀 닉네임을 입력해주세요!", AlertType.INFORMATION);
			return;
		}

		String name = txtName.getText().trim();
		String check = cName.getText().trim();

		if (!check.equals(name)) {
			Util.showAlert("오류!", "바꿀 닉네임과 확인 닉네임이 일치하지 않습니다!", AlertType.ERROR);
			return;
		}

		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "UPDATE tetris_users SET name = ? WHERE id = ?";
		String sqlExist = "SELECT * FROM tetris_users WHERE name = ?";

		try {
			pstmt = con.prepareStatement(sqlExist);
			pstmt.setString(1, name);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				Util.showAlert("알림!", "중복된 닉네임은 사용할 수 없습니다!", AlertType.INFORMATION);
				return;
			}
			
			JDBCUtil.close(pstmt);
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, checkId);

			pstmt.executeUpdate();

			if (pstmt.executeUpdate() != 1) {
				Util.showAlert("에러!", "올바르지 않은 닉네임입니다!", AlertType.ERROR);
			}

			Util.showAlert("성공!", "성공적으로 닉네임을 변경하였습니다!", AlertType.INFORMATION);

			closeName();
			MainController mc = (MainController) MainApp.app.getController("main");
			mc.setProfile(name, checkId);
			reset();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("DB error");
		} finally {
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}

	public void deleteProfile() {
		clickSound();
		if (dId.getText().isEmpty() || dCheck.getText().isEmpty()) {
			Util.showAlert("알림!", "필수값을 입력하지 않았습니다!", AlertType.INFORMATION);
			return;
		}

		if (newS == null) {
			Util.showAlert("알림!", "본인 확인 질문을 선택해주세요!", AlertType.INFORMATION);
			return;
		}

		String id = dId.getText().trim();
		String myCheck = dCheck.getText();
		String checking = "";

		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sqlExist = "SELECT myCheck FROM tetris_users WHERE id = ?";
		String sql = "DELETE FROM tetris_users WHERE id = ? AND myCheck = ?";

		if (!checkId.equals(id)) {
			Util.showAlert("오류!", "잘못된 ID입니다!", AlertType.ERROR);
			return;
		}

		try {
			pstmt = con.prepareStatement(sqlExist);
			pstmt.setString(1, id);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				checking = rs.getString("myCheck");
			}

			if (!checking.equals(myCheck)) {
				Util.showAlert("오류!", "본인 확인 질문이 맞지 않습니다!", AlertType.ERROR);
				return;
			}

			JDBCUtil.close(pstmt);

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, myCheck);

			pstmt.executeUpdate();

			Util.showAlert("성공!", "계정을 삭제하였습니다!", AlertType.INFORMATION);
			closeDelete();
			reset();
			MainApp.app.slideOut(profilePage);
			MainController mc = (MainController) MainApp.app.getController("main");
			mc.closeProfile();
			MainApp.app.loadPane("login");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("DB error");
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}

	public void getProfile(String checkId) {
		this.checkId = checkId;
	}

	public void closeProfilePage() {
		clickSound();
		mainBox.setOpacity(1);
		MainApp.app.slideOut(profilePage);
		reset();
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
		txtPass.setText("");
		cPass.setText("");
		txtName.setText("");
		cName.setText("");
		dId.setText("");
		dCheck.setText("");
	}
}
