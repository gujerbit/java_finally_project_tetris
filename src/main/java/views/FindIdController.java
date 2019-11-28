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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import main.MainApp;
import util.JDBCUtil;
import util.Util;

public class FindIdController extends MasterController {
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtCheck;
	@FXML
	private ComboBox<String> checkBox;
	@FXML
	private Label lblFind;
	@FXML
	private AnchorPane findIdPage;

	private ObservableList<String> checkList;
	private String newS;
	private String id;

	@FXML
	public void initialize() {
		checkList = FXCollections.observableArrayList("출생지는?", "가장 재미있게 플레이한 게임은?", "가장 좋아하는 음식은?", "졸업한 초등학교는?");
		checkBox.setItems(checkList);

		checkBox.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends String> observ, String oldS, String newS) -> {
					this.newS = newS;
				});
	}

	public void findId() {
		clickSound();
		String name = txtName.getText();
		String myCheck = txtCheck.getText();

		if (name.isEmpty() || myCheck.isEmpty()) {
			Util.showAlert("알림!", "필수적으로 입력해야 하는 칸이 비어있습니다!", AlertType.INFORMATION);
			return;
		}

		if (newS == null) {
			Util.showAlert("알림!", "본인 확인 질문을 선택해주세요!", AlertType.INFORMATION);
			return;
		}

		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sqlExist = "SELECT id FROM tetris_users WHERE name = ? AND myCheck = ?";
		
		try {
			pstmt = con.prepareStatement(sqlExist);
			pstmt.setString(1, name);
			pstmt.setString(2, myCheck);			
			rs = pstmt.executeQuery();
			
			if (!rs.next()) {
				Util.showAlert("알림!", "닉네임이나 본인확인 질문을 잘못 입력하셨거나 없는 닉네임입니다!", AlertType.INFORMATION);
				return;
			} else {
				id = rs.getString("id");
				lblFind.setText("ID : " + id + " 입니다.");
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
	
	public void close() {
		clickSound();
		MainApp.app.slideOut(findIdPage);
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
		txtName.setText("");
		txtCheck.setText("");
		lblFind.setText("");
	}
}
