package views;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import main.MainApp;
import util.JDBCUtil;
import util.Util;

public class FindPasswordController extends MasterController {
	@FXML
	private TextField txtId;
	@FXML
	private PasswordField password;
	@FXML
	private PasswordField cPassword;
	@FXML
	private Pane pane;
	@FXML
	AnchorPane findPasswordPage;
	
	private String id;

	public void findPassword() {
		clickSound();
		id = txtId.getText().trim();
		
		if(id.isEmpty()) {
			Util.showAlert("알림!", "ID를 입력하세요!", AlertType.INFORMATION);
			return;
		}

		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM tetris_users WHERE id = ? AND password = PASSWORD";
		String sqlExist = "SELECT * FROM tetris_users WHERE id = ?";

		try {
			pstmt = con.prepareStatement(sqlExist);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			if(!rs.next()) {
				Util.showAlert("알림!", "존재하지 않는 ID입니다!", AlertType.INFORMATION);
			}
			
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				pane.setOpacity(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("DB Error");
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}
	
	public void changePass() {
		clickSound();
		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		
		String sqlUpdate = "UPDATE tetris_users SET password = PASSWORD(?) WHERE id = ?";
		
		try {
			String pass = password.getText().trim();
			String cPass = cPassword.getText().trim();
			
			if(pass.isEmpty()) {
				Util.showAlert("알림!", "새 비밀번호를 입력해주세요!", AlertType.INFORMATION);
				return;
			}
			
			if(!cPass.equals(pass)) {
				Util.showAlert("오류!", "비밀번호와 확인 비밀번호가 일치하지 않습니다!", AlertType.ERROR);
				return;
			}
			
			pstmt = con.prepareStatement(sqlUpdate);
			pstmt.setString(1, pass);
			pstmt.setString(2, id);
			
			pstmt.executeUpdate();
			
			if(pstmt.executeUpdate() != 1) {
				Util.showAlert("오류!", "비밀번호가 올바르지 않습니다!", AlertType.ERROR);
			}
			Util.showAlert("알림!", "성공적으로 비밀번호가 바뀌었습니다!", AlertType.INFORMATION);
			pane.setOpacity(0);
			reset();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("DB error");
		} finally {
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}

	public void close() {
		clickSound();
		MainApp.app.slideOut(findPasswordPage);
		MainApp.app.loadPane("login");
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
		txtId.setText("");
		password.setText("");
		cPassword.setText("");
	}
}
