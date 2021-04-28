package llproj.llpv.core;

import java.text.SimpleDateFormat;

import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import llproj.llpv.db.Database;

public class UpdateNoteThread implements Runnable {
	private static final Logger log = Logger.getLogger(UpdateNoteThread.class);
	Database db;
	JTextArea jTextArea5_1;
	JTextArea jTextArea5_2;
	SimpleDateFormat sdf = new SimpleDateFormat("ss");

	public UpdateNoteThread(Database db, JTextArea jTextArea5_1, JTextArea jTextArea5_2) {
		this.db = db;
		this.jTextArea5_1 = jTextArea5_1;
		this.jTextArea5_2 = jTextArea5_2;
	}

	public void run() {
		try {
			while (true) {
				log.trace("[UpdateNoteThread]");
				db.updateNote(jTextArea5_1.getText(), jTextArea5_2.getText());

				Thread.sleep(CmnVal.update_note_cycle_sec * 1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
