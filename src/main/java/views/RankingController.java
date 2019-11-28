package views;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import domain.ScoreVO;
import domain.UserVO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import main.MainApp;
import util.JDBCUtil;

public class RankingController extends MasterController {
	@FXML
	private AnchorPane RankingPage;
	@FXML
	private ListView<ScoreVO> topScoreList;

	private ObservableList<ScoreVO> list;

	private String id;
	private String name;

	private UserVO user;

	private int score;

	@FXML
	public void initialize() {
		list = FXCollections.observableArrayList();
		topScoreList.setItems(list);
		reloadTopScore();
	}

	public void reloadTopScore() {
		list.clear();
		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM tetris_score ORDER BY 'score' DESC LIMIT 0, 10";

		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(makeScoreVO(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("DB error");
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}

	private ScoreVO makeScoreVO(ResultSet rs) throws Exception {
		ScoreVO temp = new ScoreVO();
		temp.setId(rs.getString("id"));
		temp.setName(rs.getString("name"));
		temp.setScore(rs.getInt("score"));

		return temp;
	}

	public void record() {
		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;

		String sql = "INSERT INTO tetris_score (name, score) VALUES(?, ?)";

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setInt(2, MainApp.app.game.score);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("DB error");
		} finally {
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}

	public void getProfile(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public void closeRankingPage() {
		clickSound();
		MainApp.app.slideOut(RankingPage);
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
		// TODO Auto-generated method stub

	}
}
