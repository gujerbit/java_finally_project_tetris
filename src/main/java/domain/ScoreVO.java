package domain;

public class ScoreVO {
	private String id;
	private String name;
	private int score;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "ranking : " + id + " / " + "name : " + name + " / " + "score : " + score;
	}
}
