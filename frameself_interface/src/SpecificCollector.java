import java.util.Date;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;

import frameself.Collector;
import frameself.format.Event;
public class SpecificCollector {
	//Use this InterfaceKitPhidget to monitor and control phidgets devices
	public static InterfaceKitPhidget ik;
	// Create a collector that publishes events to FRAMSELF on 127.0.0.1:5000
	public static Collector collector = new Collector("127.0.0.1",5000);

	public static void start() {
		while(true){
			// Example of event (To be replaced by the real Phidgets event)
			Event event = new Event();
			event.setCategory("Luminosity");
			event.setValue("200");
			event.setSensor("LuminositySensor");
			event.setLocation("Home");
			event.setTimestamp(new Date());
			event.setExpiry(new Date(System.currentTimeMillis()+20000));
			// Send the event to FRAMSELF
			collector.send(event);
			try {
				// waiting for 1 second
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}