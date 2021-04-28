package llproj.llpv.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import llproj.llpv.core.CmnVal;
import llproj.llpv.core.UpdateNoteThread;
import llproj.llpv.db.Database;
import llproj.llpv.view.component.LimitedRowTextArea;

public class notePanel extends JPanel {
	protected static int line_check = 1;
	DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	JScrollPane datetimePane;
	JScrollPane notePane;

	JTextArea datetimeArea;
	JTextArea noteArea;

	public notePanel(Database db) {

		JPanel noteLayout = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));

		datetimePane = new JScrollPane();
		notePane = new JScrollPane();

		datetimeArea = new JTextArea();
		noteArea = new LimitedRowTextArea();

		noteLayout.setPreferredSize(new Dimension(850, 450));
		datetimePane.setPreferredSize(new Dimension(120, 330));
		notePane.setPreferredSize(new Dimension(690, 330));
		datetimePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		notePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		notePane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				Runnable doScroll = new Runnable() {
					public void run() {
						datetimePane.getVerticalScrollBar().setValue(notePane.getVerticalScrollBar().getValue());
					}
				};
				SwingUtilities.invokeLater(doScroll);
			}
		});

		datetimeArea.setEnabled(false);
		noteArea.setRows(CmnVal.note_rows);
		((LimitedRowTextArea) noteArea).setMaxRows(CmnVal.note_rows);
		datetimeArea.setDisabledTextColor(Color.black);
		KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
		KeyStroke redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);

		UndoManager undoManager = new UndoManager();
		noteArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
			}
		});

		// Map undo action
		noteArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(undoKeyStroke, "undoKeyStroke");
		noteArea.getActionMap().put("undoKeyStroke", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					undoManager.undo();
				} catch (CannotUndoException cue) {
				}
			}
		});
		// Map redo action
		noteArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(redoKeyStroke, "redoKeyStroke");
		noteArea.getActionMap().put("redoKeyStroke", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					undoManager.redo();
				} catch (CannotRedoException cre) {
				}
			}
		});
		noteArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateLineCount(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateLineCount(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateLineCount(e);
			}

			private void updateLineCount(DocumentEvent e) {
				try {
					String type = e.getType().toString();
					int totalLine = noteArea.getLineCount();
					boolean isAddLine = false;
					boolean isPaste = false;
					if (totalLine != line_check) {
						if (totalLine - line_check > 1) {
							isPaste = true;
						}
						line_check = totalLine;
						isAddLine = true;
					}
					int line = noteArea.getLineOfOffset(e.getOffset());
					int cursor = noteArea.getCaretPosition();
					int start = noteArea.getLineStartOffset(line);
					int end = noteArea.getLineEndOffset(line);
					int length = end - start;

					String text = noteArea.getText(start, length).trim();

					StringBuilder str;

					if ("INSERT".equals(type) && !"".equals(text) && !isAddLine) {
						CmnVal.dates[line] = sdf.format(new Date());
					}

					if ("INSERT".equals(type) && isAddLine) {
						String datesOrg[] = CmnVal.dates.clone();

						if (isPaste) {
							CmnVal.dates[line] = sdf.format(new Date());
						} else {
							CmnVal.dates[line + 1] = sdf.format(new Date());
						}

						for (int i = 2; line + i < CmnVal.dates.length; i++) {
							CmnVal.dates[line + i] = datesOrg[line + i - 1];
						}
					}

					if ("REMOVE".equals(type)) {
						CmnVal.dates[line] = sdf.format(new Date());
					}
					for (int i = 0; i < CmnVal.dates.length; i++) {
						String date = CmnVal.dates[i];
						if (totalLine <= i) {
							CmnVal.dates[i] = "\n";
						}
					}

					updateDateStr();
				} catch (Exception e2) {
					e2.printStackTrace();
				}

			}
		});

		datetimePane.setBorder(BorderFactory.createLineBorder(Color.black));
		notePane.setBorder(BorderFactory.createLineBorder(Color.black));
		datetimePane.setViewportView(datetimeArea);
		notePane.setViewportView(noteArea);

		db.getNote(noteArea);

		updateDateStr();

		noteLayout.add(datetimePane);
		noteLayout.add(notePane);

		this.add(noteLayout, BorderLayout.CENTER);

		UpdateNoteThread updateNoteThread = new UpdateNoteThread(db, datetimeArea, noteArea);
		Thread thread = new Thread(updateNoteThread);
		thread.start();
	}

	private void updateDateStr() {
		StringBuilder str = new StringBuilder();
		for (String date : CmnVal.dates) {
			if (date == null || "\n".equals(date)) {
				str.append("\n");
			} else {
				str.append(date + "\n");
			}

		}
		datetimeArea.setText(str.toString());
		Runnable doScroll = new Runnable() {
			public void run() {
				datetimePane.getVerticalScrollBar().setValue(notePane.getVerticalScrollBar().getValue());
			}
		};
		SwingUtilities.invokeLater(doScroll);
	}
}
