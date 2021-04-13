package llproj.llpv.view.component;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class CustomButtonBlue extends JButton {
	public CustomButtonBlue() {
		super();
		decorate();
	}

	public CustomButtonBlue(String text) {
		super(text);
		decorate();
	}

	public CustomButtonBlue(Action action) {
		super(action);
		decorate();
	}

	public CustomButtonBlue(Icon icon) {
		super(icon);
		decorate();
	}

	public CustomButtonBlue(String text, Icon icon) {
		super(text, icon);
		decorate();
	}

	protected void decorate() {
		setBorderPainted(false);
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight()-2;
		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (getModel().isArmed()) { //눌린상태
			graphics.setColor(new Color(36,51,65));
		} else if (getModel().isRollover()) { //올린상태
			graphics.setColor(new Color(52,73,94));
		} else { //기본상태
			graphics.setColor(new Color(40,104,174));
		}
		graphics.fillRoundRect(0, 0, width-1, height-1, 0, 0);
		graphics.setColor(new Color(99,130,192));
		graphics.drawRoundRect(0, 0, width-1, height-1, 0, 0);
		
		FontMetrics fontMetrics = graphics.getFontMetrics();
		Rectangle stringBounds = fontMetrics.getStringBounds(this.getText(), graphics).getBounds();
		int textX = (width - stringBounds.width) / 2;
		int textY = (height - stringBounds.height) / 2 + fontMetrics.getAscent();
		graphics.setColor(getForeground().white); // 글자색
		graphics.setFont(getFont());
		graphics.drawString(getText(), textX, textY);
		graphics.dispose();
		
		super.paintComponent(g);
	}

}
