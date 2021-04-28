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

public class CustomButtonBasic extends JButton {
	public CustomButtonBasic() {
		super();
		decorate();
	}

	public CustomButtonBasic(String text) {
		super(text);
		decorate();
	}

	public CustomButtonBasic(Action action) {
		super(action);
		decorate();
	}

	public CustomButtonBasic(Icon icon) {
		super(icon);
		decorate();
	}

	public CustomButtonBasic(String text, Icon icon) {
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
		if (getModel().isArmed()) {
			graphics.setColor(new Color(234,244,252));
		} else if (getModel().isRollover()) {
			graphics.setColor(new Color(234,250,252));
		} else {
			graphics.setColor(Color.white);
		}
		graphics.fillRoundRect(0, 0, width-1, height-1, 0, 0);
		graphics.setColor(new Color(122,138,153));
		graphics.drawRoundRect(0, 0, width-1, height-1, 0, 0);
		
		FontMetrics fontMetrics = graphics.getFontMetrics();
		Rectangle stringBounds = fontMetrics.getStringBounds(this.getText(), graphics).getBounds();
		int textX = (width - stringBounds.width) / 2;
		int textY = (height - stringBounds.height) / 2 + fontMetrics.getAscent();
		graphics.setColor(getForeground().black);
		graphics.setFont(getFont());
		graphics.drawString(getText(), textX, textY);
		graphics.dispose();
		
		super.paintComponent(g);
	}

}
