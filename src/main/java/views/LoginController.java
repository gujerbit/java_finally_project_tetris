package views;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import domain.UserVO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import main.MainApp;
import util.JDBCUtil;
import util.Util;

public class LoginController extends MasterController{
	@FXML
	private TextField txtId;
	@FXML
	private PasswordField passField;
	@FXML
	private AnchorPane loginPage;
	
	public UserVO user;
	
	public void loginProcess() {
		clickSound();
		String id = txtId.getText().trim();
		String pass = passField.getText().trim();
		
		if(id.isEmpty() || pass.isEmpty()) {
			Util.showAlert("오류!", "아이디나 비밀번호는 비어있을 수 없습니다!", AlertType.ERROR);
			return;
		}
		
		user = checkLogin(id, pass);
		
		if(user != null) {
			MainApp.app.slideOut(loginPage);
			MainController mcr = (MainController)MainApp.app.getController("main");
			mcr.setProfile(user.getName(), user.getId());
			OptionController op = (OptionController)MainApp.app.getController("option");
			op.loginSound.stop();
			ProfileController pfc = (ProfileController)MainApp.app.getController("profile");
			pfc.getProfile(user.getId());
			RankingController rck = (RankingController)MainApp.app.getController("ranking");
			rck.getProfile(user.getId(), user.getName());
			reset();
		} else {
			Util.showAlert("오류!", "존재하지 않는 아이디이거나 틀린 비밀번호 입니다!", AlertType.ERROR);
			return;
		}
	}
	
	public UserVO checkLogin(String id, String pass) {
		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM tetris_users WHERE id = ? AND password = PASSWORD(?)";
		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pass);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				user = new UserVO();
				user.setId(rs.getString("id"));
				user.setName(rs.getString("name"));
				return user;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("DB Error");
			return null;
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}
	
	public void openRegisterPage() {
		clickSound();
		MainApp.app.slideOut(loginPage);
		MainApp.app.loadPane("register");
		reset();
	}
	
	public void openFindIdPage() {
		clickSound();
		MainApp.app.slideOut(loginPage);
		
		MainApp.app.loadPane("findId");
		reset();
	}
	
	public void openFindPasswordPage() {
		clickSound();
		MainApp.app.slideOut(loginPage);
		MainApp.app.loadPane("findPassword");
		reset();
	}
	public void openOptionPage() {
		MainApp.app.loadPane("option");
		clickSound();
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
		txtId.setText("");
		passField.setText("");
	}
}
