package llproj.llpv.view.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import llproj.llpv.core.CmnVal;
import llproj.llpv.db.Database;
import llproj.llpv.util.MessageUt;
import llproj.llpv.view.component.CustomButtonBasic;

public class SearchPanel extends JPanel {
	JFormattedTextField startTimeTextField;
	JFormattedTextField endTimeTextField;

	public SearchPanel(Database db) {
		JPanel searchTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
		JScrollPane searchBottom = new JScrollPane();

		searchTop.setPreferredSize(new Dimension(850, 50));
		searchBottom.setPreferredSize(new Dimension(800, 300));

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		JTextField searchTextField = new JTextField(10);
		JButton searchButton = new CustomButtonBasic(MessageUt.getMessage("search"));
		JLabel splitTimeText = new JLabel("~");
		startTimeTextField = new JFormattedTextField(sdf);
		endTimeTextField = new JFormattedTextField(sdf);

		Calendar startDt = Calendar.getInstance();
		startDt.add(Calendar.DATE, -1);
		Calendar endDt = Calendar.getInstance();
		endDt.add(Calendar.MINUTE, 1);
		startTimeTextField.setValue(startDt.getTime());
		endTimeTextField.setValue(endDt.getTime());
		startTimeTextField.setColumns(11);
		endTimeTextField.setColumns(11);

		String colNames[] = MessageUt.getMessage("search.col_names").split(",");
		DefaultTableModel model = new DefaultTableModel(colNames, 0) {
			public boolean isCellEditable(int row, int column) {
				switch (column) {
				case 0:
				case 1:
					return true;
				default:
					return false;
				}
			};
		};
		JTable searchTable = new JTable(model);

		searchTable.getColumnModel().getColumn(0).setPreferredWidth(80);
		searchTable.getColumnModel().getColumn(1).setPreferredWidth(320);
		searchTable.getColumnModel().getColumn(2).setPreferredWidth(80);
		searchTable.getColumnModel().getColumn(3).setPreferredWidth(20);
		DefaultTableCellRenderer tScheduleCellRendererCenter = new DefaultTableCellRenderer();
		tScheduleCellRendererCenter.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer tScheduleCellRendererRight = new DefaultTableCellRenderer();
		tScheduleCellRendererRight.setHorizontalAlignment(SwingConstants.RIGHT);
		searchTable.getColumnModel().getColumn(0).setCellRenderer(tScheduleCellRendererCenter);
		searchTable.getColumnModel().getColumn(2).setCellRenderer(tScheduleCellRendererCenter);
		searchTable.getColumnModel().getColumn(3).setCellRenderer(tScheduleCellRendererRight);
		searchTable.getTableHeader().setReorderingAllowed(false);

		searchButton.setFocusable(false);
		searchButton.setFont(CmnVal.font);
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setRowCount(0);
				String text = searchTextField.getText();
				String start_date = startTimeTextField.getText();
				String end_date = endTimeTextField.getText();
				db.getSearch(model, text, start_date, end_date);
			}
		});
		searchTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButton.doClick();
			}
		});
		searchButton.doClick();

		searchTop.add(searchTextField);
		searchTop.add(searchButton);
		searchTop.add(startTimeTextField);
		searchTop.add(splitTimeText);
		searchTop.add(endTimeTextField);
		searchBottom.setViewportView(searchTable);

		this.add(searchTop, BorderLayout.NORTH);
		this.add(searchBottom, BorderLayout.CENTER);
	}

	public void doClickTab() {
		Calendar startDt = Calendar.getInstance();
		startDt.add(Calendar.DATE, -1);
		Calendar endDt = Calendar.getInstance();
		endDt.add(Calendar.MINUTE, 1);
		startTimeTextField.setValue(startDt.getTime());
		endTimeTextField.setValue(endDt.getTime());
	}
}
