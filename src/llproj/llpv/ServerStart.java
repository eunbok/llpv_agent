package llproj.llpv;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import llproj.llpv.core.CmnVal;
import llproj.llpv.core.GetProcessThread;
import llproj.llpv.core.SendDataThread;
import llproj.llpv.db.Database;
import llproj.llpv.util.MessageUt;
import llproj.llpv.view.MainView;

public class ServerStart {
	private static final Logger log = Logger.getLogger(ServerStart.class);

	public static Image image = Toolkit.getDefaultToolkit().getImage("resources/note.png");
	public static TrayIcon trayIcon = new TrayIcon(image, CmnVal.process_title);
	public static final Date TODAY = new Date();
	public static final String TODAY_STR = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TODAY);

	public static void main(String[] args) throws Exception {
		FileInputStream log4jRead = new FileInputStream("log4j.properties");
		Properties log4jProperty = new Properties();
		log4jProperty.load(log4jRead);
		PropertyConfigurator.configure(log4jProperty);
		log.info("[llprocessview " + CmnVal.Version + " Start ]");
		
		FileInputStream fileInputStream = new FileInputStream("llpv.properties");
		Properties property = new Properties();
		property.load(fileInputStream);
		
		MessageUt.setLocale(property.getProperty("lang"));
		
		//TODO 나중에 사용자 입력받기
		boolean local_mode = true;
		String url = "./data/llpv";
		String id = "root";
		String pass = "";

		
		Database db = null;
		if (local_mode) {
			db = new Database(url, id, pass);
			db.createDB();
		}
		
		new MainView(db);
		
		GetProcessThread getProcessRunnable = new GetProcessThread(db);
		Thread getProcessThread = new Thread(getProcessRunnable);
		getProcessThread.start();

		SendDataThread sendDataRunnable = new SendDataThread(db);
		Thread sendDataThread = new Thread(sendDataRunnable);
		sendDataThread.start();
	}
}
