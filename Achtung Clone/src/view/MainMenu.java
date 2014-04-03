package view;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JPanel;


public class MainMenu extends JPanel {
	private static final Color fontColor = Color.black;
	private static final Font font = new Font("Helvetica", Font.BOLD, 30);
	
	private final JButton play = new JButton();
	//private final JButton options;
	public MainMenu() {
		super();
		this.setOpaque(false);
		setBackground(Color.red);
		initButton(play, "Play");
		this.add(play);
	}
	
	private static void initButton(JButton btn, String text) {
		btn.setFont(font);
		btn.setText(text);
		btn.setForeground(fontColor);
	}
	
	//TODO
	private class MenuButton extends JButton {
		protected void paintComponent(Graphics g) {
			g.drawOval(0, 0, 10, 10);
		}
	}
}
