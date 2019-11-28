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
import main.MainApp;
import util.JDBCUtil;
import util.Util;

public class RegisterController extends MasterController{
	@FXML
	private TextField txtId;
	@FXML
	private PasswordField passField;
	@FXML
	private PasswordField cPassField;
	@FXML
	private TextField txtName;
	@FXML
	private ComboBox<String> checkBox;
	@FXML
	private TextField txtCheck;
	@FXML
	private AnchorPane registerPage;
	
	private ObservableList<String> checkList;
	
	private String newS;
	
	@FXML
	public void initialize() {
		checkList = FXCollections.observableArrayList("출생지는?", "가장 재미있게 플레이한 게임은?", "가장 좋아하는 음식은?", "졸업한 초등학교는?");
		checkBox.setItems(checkList);
		
		checkBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue <? extends String> observ, String oldS, String newS)-> {
			this.newS = newS;
		});
	}
	
	public void register() {
		clickSound();
		String id = txtId.getText().trim();
		String pass = passField.getText().trim();
		String cPass = cPassField.getText().trim();
		String name = txtName.getText().trim();
		String myCheck = txtCheck.getText();
		
		if(id.isEmpty() || name.isEmpty() || pass.isEmpty() || cPass.isEmpty() || myCheck.isEmpty()) {
			Util.showAlert("알림!", "필수적으로 입력해야 하는 칸이 비어있습니다!", AlertType.INFORMATION);
			return;
		}
		
		if(!cPass.equals(pass)) {
			Util.showAlert("알림!", "비밀번호와 확인 비밀번호가 일치하지 않습니다!", AlertType.INFORMATION);
			return;
		}
		
		if(newS == null) {
			Util.showAlert("알림!", "본인 확인 질문을 선택해주세요!", AlertType.INFORMATION);
			return;
		}
		
		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sqlExist = "SELECT * FROM tetris_users WHERE id = ?";
		String sqlDistinct = "SELECT * FROM tetris_users WHERE name = ?";
		String sqlInsert = "INSERT INTO tetris_users(id, password, name, myCheck) VALUES(?, PASSWORD(?), ?, ?)";
		
		try {
			pstmt = con.prepareStatement(sqlExist);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				Util.showAlert("알림!", "중복된 ID입니다!", AlertType.INFORMATION);
				return;
			}
			
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			
			pstmt = con.prepareStatement(sqlDistinct);
			pstmt.setString(1, name);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				Util.showAlert("알림!", "중복된 닉네임입니다!", AlertType.INFORMATION);
				return;
			}
			
			JDBCUtil.close(pstmt);
			
			pstmt = con.prepareStatement(sqlInsert);
			pstmt.setString(1, id);
			pstmt.setString(2, pass);
			pstmt.setString(3, name);
			pstmt.setString(4, myCheck);
			
			if(pstmt.executeUpdate() != 1) {
				Util.showAlert("오류!", "회원정보가 올바르게 입력되지 않았습니다!", AlertType.ERROR);
				return;
			}
			
			Util.showAlert("알림!", "회원가입이 성공적으로 완료되었습니다!", AlertType.INFORMATION);
			
			MainApp.app.slideOut(getRoot());
			MainApp.app.loadPane("login");
			reset();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("DB Error");
			return;
		} finally {
			JDBCUtil.close(rs);
 			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}
	
	public void cancel() {
		clickSound();
		MainApp.app.slideOut(getRoot());
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
		passField.setText("");
		cPassField.setText("");
		txtName.setText("");
		txtCheck.setText("");
		
	}
}
