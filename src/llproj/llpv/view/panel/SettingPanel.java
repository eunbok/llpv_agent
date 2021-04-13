package llproj.llpv.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;

import llproj.llpv.ServerStart;
import llproj.llpv.core.CmnVal;
import llproj.llpv.core.UpdateAlarmThread;
import llproj.llpv.db.Database;
import llproj.llpv.view.component.CustomButtonBlue;

public class SettingPanel extends JPanel {
	public SettingPanel(Database db, JTabbedPane tabbedPane) throws Exception {
		JPanel settingGrid = new JPanel(new GridLayout(5, 1)); // JPanel ����
		settingGrid.setPreferredSize(new Dimension(850, 450));

		JPanel settingLimit = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15)); // JPanel ����
		JPanel settingSync = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15)); // JPanel ����
		JPanel settingAlarm = new JPanel(new GridLayout(2, 1)); // JPanel ����
		JPanel settingAlarmTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // JPanel ����
		JPanel settingAlarmBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // JPanel ����
		JPanel settingDelete = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15)); // JPanel ����

		Border settingLimitBorder = BorderFactory.createTitledBorder(null, "����", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, CmnVal.font_b, Color.black);
		Border settingSyncBorder = BorderFactory.createTitledBorder(null, "�ǽð� ����", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, CmnVal.font_b, Color.black);
		Border settingAlarmBorder = BorderFactory.createTitledBorder(null, "�˶�", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, CmnVal.font_b, Color.black);
		Border settingDeleteBorder = BorderFactory.createTitledBorder(null, "������", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, CmnVal.font_b, Color.black);

		JFormattedTextField alarmNoticeTextField;
		JFormattedTextField alarmRestTextField;

		JLabel limitText = new JLabel(
				"<html>���� ��� ��� (������ ���ѽð��� �ʰ��ϸ� �ش� ���α׷��� �������� �մϴ�. ���ð��� ���� �����Դϴ�)<br>* ������ �������� �����ؾ� ���� �۵��մϴ�.</html>");
		JLabel syncText = new JLabel("<html>* ���μ����� ������ ��Ÿ���ϴ�. (�ǽð� ���μ��� ���� ���� ����)</html>");
		JLabel alarmRunTimeText = new JLabel("���ð� : ");
		JLabel alarmRunTimerText = new JLabel("");
		JLabel alarmNoticeText = new JLabel("�˸�");
		JLabel alarmNoticeMinText = new JLabel("��");
		JLabel alarmRestText = new JLabel("�޽�");
		JLabel alarmDescText = new JLabel("��  (���ð��� ������ �ڵ����� ȭ����� ���°� �˴ϴ�.)");
		JLabel alarmTimerText = new JLabel("");
		JLabel deleteDescText = new JLabel("<html>����� ������ ����<br/>* ���� ��ư Ŭ�� �� �����Ͱ� �ٷ� �����˴ϴ�.</html>");
		JButton syncHomepageButton = new JButton();
		JButton deleteButton = new CustomButtonBlue("����");

		JCheckBox limitCheckBox = new JCheckBox();
		JCheckBox syncCheckBox = new JCheckBox();
		JCheckBox alarmCheckBox = new JCheckBox();

		// ���� ��� ��� ���� üũ�ڽ�
		if ("Y".equals(db.getConfig("is_use_limit"))) {
			limitCheckBox.setSelected(true);
			CmnVal.is_use_limit = true;
		}

		limitCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (1 == e.getStateChange()) { // üũ�Ѱ�
					db.setConfig("is_use_limit", "Y");
					CmnVal.is_use_limit = true;
					ServerStart.trayIcon.displayMessage("llpv �˸�", "���� ����� Ȱ��ȭ�մϴ�.", TrayIcon.MessageType.INFO);
				} else { // üũ���Ѱ�
					db.setConfig("is_use_limit", "N");
					CmnVal.is_use_limit = false;
					ServerStart.trayIcon.displayMessage("llpv �˸�", "���� ����� ��Ȱ��ȭ�մϴ�.", TrayIcon.MessageType.INFO);
				}
			}
		});

		// ������ ���� üũ�ڽ�
		if ("Y".equals(db.getConfig("is_send_data"))) {
			syncCheckBox.setSelected(true);
		}

		syncCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (1 == e.getStateChange()) { // üũ�Ѱ�
					ServerStart.trayIcon.displayMessage("llpv �˸�", "�ǽð� ���μ��� ���� ���踦 �����ϼ̽��ϴ�.",
							TrayIcon.MessageType.INFO);
					db.setConfig("is_send_data", "Y");
				} else { // üũ���Ѱ�
					ServerStart.trayIcon.displayMessage("llpv �˸�", "�ǽð� ���μ��� ���� ���踦 �����ϼ̽��ϴ�.",
							TrayIcon.MessageType.INFO);
					db.setConfig("is_send_data", "N");
				}
			}
		});

		settingLimit.add(limitText);
		settingLimit.add(limitCheckBox);
		settingLimit.setBorder(settingLimitBorder);

		settingGrid.add(settingLimit);

		final URI uri = new URI(CmnVal.homepage);
		syncHomepageButton.setFont(CmnVal.font);
		syncHomepageButton.setText("<HTML><a href='#'>" + CmnVal.homepage + "</a><br>�ǽð� ���μ��� ������ �з� �� ����</HTML>");
		syncHomepageButton.setHorizontalAlignment(SwingConstants.LEFT);
		syncHomepageButton.setBorderPainted(false);
		syncHomepageButton.setBorder(null);
		syncHomepageButton.setOpaque(false);
		syncHomepageButton.setBackground(Color.WHITE);
		syncHomepageButton.setToolTipText(uri.toString());
		syncHomepageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(uri);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		limitText.setFont(CmnVal.font);
		syncText.setFont(CmnVal.font);
		settingSync.add(syncHomepageButton);
		settingSync.add(syncText);
		settingSync.add(syncCheckBox);
		settingSync.setBorder(settingSyncBorder);
		settingGrid.add(settingSync);

		alarmRunTimeText.setFont(CmnVal.font);
		alarmRunTimerText.setFont(CmnVal.font);
		settingAlarmTop.add(alarmRunTimeText);
		settingAlarmTop.add(alarmRunTimerText);

		NumberFormat alarmNoticeTextFormat = NumberFormat.getInstance();
		NumberFormatter alarmNoticeTextFormatter = new NumberFormatter(alarmNoticeTextFormat);
		alarmNoticeTextFormatter.setValueClass(Integer.class);
		alarmNoticeTextFormatter.setMinimum(1);
		alarmNoticeTextFormatter.setMaximum(999);
		alarmNoticeTextFormatter.setCommitsOnValidEdit(true);

		NumberFormat alarmRestTextFormat = NumberFormat.getInstance();
		NumberFormatter alarmRestTextFormatter = new NumberFormatter(alarmRestTextFormat);
		alarmRestTextFormatter.setValueClass(Integer.class);
		alarmRestTextFormatter.setMinimum(0);
		alarmRestTextFormatter.setMaximum(999);
		alarmRestTextFormatter.setCommitsOnValidEdit(true);

		alarmNoticeTextField = new JFormattedTextField(alarmNoticeTextFormat);
		alarmNoticeTextField.setColumns(3);
		alarmNoticeTextField.setText(db.getConfig("alarm_run"));
		alarmNoticeTextField.setHorizontalAlignment(JTextField.CENTER);

		alarmRestTextField = new JFormattedTextField(alarmRestTextFormat);
		alarmRestTextField.setColumns(3);
		alarmRestTextField.setText(db.getConfig("alarm_rest"));
		alarmRestTextField.setHorizontalAlignment(JTextField.CENTER);

		// �˶� üũ�ڽ�
		if ("Y".equals(db.getConfig("is_use_alarm"))) {
			alarmCheckBox.setSelected(true);
			alarmNoticeTextField.setEditable(false);
			alarmRestTextField.setEditable(false);
			String alarm_run = db.getConfig("alarm_run");
			String alarm_rest = db.getConfig("alarm_rest");
			CmnVal.is_use_alarm = true;
			CmnVal.alarm_run = alarm_run;
			CmnVal.alarm_rest = alarm_rest;
		}

		alarmCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (1 == e.getStateChange()) { // üũ�Ѱ�
					db.setConfig("is_use_alarm", "Y");
					String alarm_run = alarmNoticeTextField.getText();
					String alarm_rest = alarmRestTextField.getText();
					db.setConfig("alarm_run", alarm_run);
					db.setConfig("alarm_rest", alarm_rest);

					CmnVal.is_use_alarm = true;
					CmnVal.alarm_run = alarm_run;
					CmnVal.alarm_rest = alarm_rest;

					alarmNoticeTextField.setEditable(false);
					alarmRestTextField.setEditable(false);
				} else { // üũ���Ѱ�
					db.setConfig("is_use_alarm", "N");
					CmnVal.is_use_alarm = false;
					alarmNoticeTextField.setEditable(true);
					alarmRestTextField.setEditable(true);
				}
			}
		});

		alarmNoticeText.setFont(CmnVal.font);
		alarmNoticeMinText.setFont(CmnVal.font);
		alarmRestText.setFont(CmnVal.font);
		alarmDescText.setFont(CmnVal.font);
		alarmTimerText.setFont(CmnVal.font);

		settingAlarmBottom.add(alarmNoticeText);
		settingAlarmBottom.add(alarmNoticeTextField);
		settingAlarmBottom.add(alarmNoticeMinText);

		settingAlarmBottom.add(alarmRestText);
		settingAlarmBottom.add(alarmRestTextField);
		settingAlarmBottom.add(alarmDescText);

		settingAlarmBottom.add(alarmCheckBox);
		settingAlarmBottom.add(alarmTimerText);

		settingAlarm.setBorder(settingAlarmBorder);
		settingAlarm.add(settingAlarmTop);
		settingAlarm.add(settingAlarmBottom);
		settingGrid.add(settingAlarm);

		deleteButton.setFocusable(false);
		deleteButton.setFont(CmnVal.font);
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ServerStart.trayIcon.displayMessage("llpv �˸�", "�����Ͱ� �����Ǿ����ϴ�.", TrayIcon.MessageType.INFO);
				db.deleteDB();
			}
		});
		deleteDescText.setFont(CmnVal.font);
		deleteButton.setFocusable(false);
		settingDelete.add(deleteDescText);
		settingDelete.add(deleteButton);
		settingDelete.setBorder(settingDeleteBorder);
		settingGrid.add(settingDelete);

		this.add(settingGrid, BorderLayout.CENTER);

		UpdateAlarmThread updateAlarmThread = new UpdateAlarmThread(tabbedPane, alarmRunTimerText, alarmTimerText);
		Thread thread = new Thread(updateAlarmThread);
		thread.start();
	}
}
