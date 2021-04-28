package llproj.llpv.view;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

import org.apache.log4j.Logger;

import llproj.llpv.ServerStart;
import llproj.llpv.core.CmnVal;
import llproj.llpv.db.Database;
import llproj.llpv.util.MessageUt;
import llproj.llpv.view.panel.LimitPanel;
import llproj.llpv.view.panel.RankPanel;
import llproj.llpv.view.panel.SearchPanel;
import llproj.llpv.view.panel.SettingPanel;
import llproj.llpv.view.panel.notePanel;

public class MainView extends JFrame {
	private static final Logger log = Logger.getLogger(MainView.class);

	JTabbedPane tabbedPane = new JTabbedPane();

	public MainView(Database db) throws Exception {
		super(CmnVal.process_title);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image img = toolkit.getImage("resources/note.png");
		setIconImage(img);

		JPanel rankPanel = new RankPanel(db);
		JPanel searchPanel = new SearchPanel(db);
		JPanel limitPanel = new LimitPanel(db);
		JPanel settingPanel = new SettingPanel(db, tabbedPane);
		JPanel notePanel = new notePanel(db);

		UIManager.put("TabbedPane.border", BorderFactory.createLineBorder(Color.DARK_GRAY));
		UIManager.put("TabbedPane.selected", Color.white);
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(1, 0, 0, 0));
		UIManager.put("TabbedPane.tabsOverlapBorder", false);
		tabbedPane.setOpaque(true);
		tabbedPane.setUI(new MetalTabbedPaneUI() {
		});

		tabbedPane.add(MessageUt.getMessage("rank"), rankPanel);
		tabbedPane.add(MessageUt.getMessage("search"), searchPanel);
		tabbedPane.add(MessageUt.getMessage("limit"), limitPanel);
		tabbedPane.add(MessageUt.getMessage("setting"), settingPanel);
		tabbedPane.add(MessageUt.getMessage("note"), notePanel);
		tabbedPane.setFont(CmnVal.font);
		tabbedPane.setFocusable(false);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int index = tabbedPane.getSelectedIndex();
				if (index == 1) {
					((SearchPanel) searchPanel).doClickTab();
				} else if (index == 2) {
					((LimitPanel) limitPanel).doClickTab();
				}
			}
		});
		add(tabbedPane);

		// tray icon
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();

			ServerStart.trayIcon.setImageAutoSize(true);
			ServerStart.trayIcon.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(true);
				}
			});

			try {
				tray.add(ServerStart.trayIcon);

			} catch (AWTException e) {
				System.err.println("TrayIcon could not be added.");
			}

			final PopupMenu popup = new PopupMenu();
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			popup.add(exitItem);

			ServerStart.trayIcon.setPopupMenu(popup);
		}
		//

		setSize(900, 430);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}