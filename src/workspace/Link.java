package workspace;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.Document;

/**
 * Links my game to my XMLStore class and several other static variables.
 * 
 * @author Mark Robinson
 */
public class Link {

	static XMLStore localDataStore = new XMLStore();
	static String activeGame = "0";
	static Document doc;
	static Date time = Calendar.getInstance().getTime();

	static Timer timer = new Timer();

	public static void main(String[] args) {

		localDataStore.Do("create");
		TimerTask dataSave = new TimerTask() {
			public void run() {
				time = Calendar.getInstance().getTime();
				localDataStore.Do("save");
			}
		};

		timer.scheduleAtFixedRate(dataSave, 0, 1000);

		new Thread() {
			public void run() {
				new RPSGame();
			}
		}.start();
	}
}