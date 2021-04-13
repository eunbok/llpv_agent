package llproj.llpv.view.component;

import javax.swing.JTextArea;

public class LimitedRowTextArea extends JTextArea {
	private int maxRows = 0;

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public void replaceSelection(String content) {
		if (getMaxRows() > 0 && getLineCount() >= getMaxRows()) {

			this.getToolkit().beep(); // or whatever warning
			return;
		}
		super.replaceSelection(content);
	}
}
