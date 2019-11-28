package views;

import javafx.scene.layout.Pane;

public abstract class MasterController {
	private Pane root;

	public Pane getRoot() {
		return root;
	}

	public void setRoot(Pane root) {
		this.root = root;
	}
	
	public abstract void reset();
}
