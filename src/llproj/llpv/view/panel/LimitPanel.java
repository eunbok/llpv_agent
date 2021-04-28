package llproj.llpv.view.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

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
import javax.swing.text.NumberFormatter;

import llproj.llpv.ServerStart;
import llproj.llpv.core.CmnVal;
import llproj.llpv.db.Database;
import llproj.llpv.util.MessageUt;
import llproj.llpv.view.component.CustomButtonBasic;
import llproj.llpv.view.component.CustomButtonBlue;
import llproj.llpv.view.component.TableCell;

public class LimitPanel extends JPanel {
  DefaultTableModel model;
  Database db;

  public LimitPanel(Database db) {
    this.db = db;

    JPanel limitTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
    JScrollPane limitBottom = new JScrollPane();

    limitTop.setPreferredSize(new Dimension(850, 50));
    limitBottom.setPreferredSize(new Dimension(800, 300));

    JLabel processText = new JLabel(MessageUt.getMessage("limit.process"));
    JLabel programText = new JLabel(MessageUt.getMessage("limit.program"));
    JLabel limitTimeText = new JLabel(MessageUt.getMessage("limit.limit_time"));

    JTextField processTextField = new JTextField(10);
    JTextField programTextField = new JTextField(10);
    JFormattedTextField limitTimeTextField;
    NumberFormat limitTimeFormat = NumberFormat.getInstance();
    NumberFormatter limitTimeFormatter = new NumberFormatter(limitTimeFormat);
    limitTimeFormatter.setValueClass(Integer.class);
    limitTimeFormatter.setMinimum(1);
    limitTimeFormatter.setMaximum(999);
    limitTimeFormatter.setCommitsOnValidEdit(true);

    limitTimeTextField = new JFormattedTextField(limitTimeFormatter);
    limitTimeTextField.setColumns(3);
    limitTimeTextField.setHorizontalAlignment(JTextField.CENTER);

    JButton addButton = new CustomButtonBlue(MessageUt.getMessage("limit.add"));
    JButton lookupButton = new CustomButtonBasic(MessageUt.getMessage("lookup"));

    String col_names[] = MessageUt.getMessage("limit.col_names").split(",");
    model = new DefaultTableModel(col_names, 0) {
      public boolean isCellEditable(int row, int column) {
        switch (column) {
          case 5:
            return true;
          default:
            return false;
        }
      };
    };
    JTable limitTable = new JTable(model);

    limitTable.getColumnModel().getColumn(0).setPreferredWidth(180);
    limitTable.getColumnModel().getColumn(1).setPreferredWidth(180);
    limitTable.getColumnModel().getColumn(2).setPreferredWidth(60);
    limitTable.getColumnModel().getColumn(3).setPreferredWidth(60);
    limitTable.getColumnModel().getColumn(4).setPreferredWidth(100);
    limitTable.getColumnModel().getColumn(5).setPreferredWidth(30);
    DefaultTableCellRenderer tScheduleCellRendererCenter = new DefaultTableCellRenderer();
    tScheduleCellRendererCenter.setHorizontalAlignment(SwingConstants.CENTER);
    DefaultTableCellRenderer tScheduleCellRendererRight = new DefaultTableCellRenderer();
    tScheduleCellRendererRight.setHorizontalAlignment(SwingConstants.RIGHT);
    limitTable.getColumnModel().getColumn(0).setCellRenderer(tScheduleCellRendererCenter);
    limitTable.getColumnModel().getColumn(1).setCellRenderer(tScheduleCellRendererCenter);
    limitTable.getColumnModel().getColumn(2).setCellRenderer(tScheduleCellRendererCenter);
    limitTable.getColumnModel().getColumn(3).setCellRenderer(tScheduleCellRendererCenter);
    limitTable.getColumnModel().getColumn(4).setCellRenderer(tScheduleCellRendererCenter);
    limitTable.getColumnModel().getColumn(5).setCellRenderer(tScheduleCellRendererCenter);
    limitTable.getTableHeader().setReorderingAllowed(false);

    limitTable.getColumnModel().getColumn(5).setCellRenderer(new TableCell(db, model));
    limitTable.getColumnModel().getColumn(5).setCellEditor(new TableCell(db, model));

    processText.setFont(CmnVal.font);
    programText.setFont(CmnVal.font);
    limitTimeText.setFont(CmnVal.font);

    addButton.setFont(CmnVal.font);
    lookupButton.setFont(CmnVal.font);
    addButton.setFocusable(false);
    lookupButton.setFocusable(false);

    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        model.setRowCount(0);
        boolean check = true;
        String msg = "";
        String run_file = processTextField.getText();
        String run_title = programTextField.getText();
        String limit_min = limitTimeTextField.getText();

        if (!limit_min.matches("^[0-9]+$")) {
          check = false;
          msg = MessageUt.getMessage("limit.check_min");
          limitTimeTextField.grabFocus();
        }

        if ("".equals(limit_min) || "0".equals(limit_min)) {
          check = false;
          msg = MessageUt.getMessage("limit.check_min");
          limitTimeTextField.grabFocus();
        }

        if ("".equals(run_file)) {
          check = false;
          msg = MessageUt.getMessage("limit.check_process");
          processTextField.grabFocus();
        }

        if (check) {
          db.saveLimit(run_file, run_title, limit_min);
        } else {
          ServerStart.trayIcon.displayMessage(MessageUt.getMessage("tray"), msg,
              TrayIcon.MessageType.WARNING);
        }
        db.getLimitList(model);
      }
    });
    lookupButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        model.setRowCount(0);
        db.getLimitList(model);
      }
    });
    model.setRowCount(0);
    db.getLimitList(model);

    limitTop.add(processText);
    limitTop.add(processTextField);
    limitTop.add(programText);
    limitTop.add(programTextField);
    limitTop.add(limitTimeText);
    limitTop.add(limitTimeTextField);
    limitTop.add(addButton);
    limitTop.add(lookupButton);
    limitBottom.setViewportView(limitTable);

    this.add(limitTop, BorderLayout.NORTH);
    this.add(limitBottom, BorderLayout.CENTER);
  }

  public void doClickTab() {
    model.setRowCount(0);
    db.getLimitList(model);
  }
}
