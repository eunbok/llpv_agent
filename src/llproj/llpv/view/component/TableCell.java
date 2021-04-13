package llproj.llpv.view.component;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import llproj.llpv.db.Database;

public class TableCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
	Database db;
	DefaultTableModel model;

	public TableCell(Database db, DefaultTableModel model) {
		this.db = db;
		this.model = model;
	}

	@Override
	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		ImageIcon icon = new ImageIcon("resources/delete.png");
		JLabel label1 = new JLabel("", icon, JLabel.CENTER);

		return label1;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		db.deleteLimit((String) value);
		model.removeRow(row);
		return null;
	}
}