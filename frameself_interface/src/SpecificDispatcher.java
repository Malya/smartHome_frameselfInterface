import frameself.Dispatcher;
import frameself.format.Action;
public class SpecificDispatcher {
	// Create a dispatcher that listens to actions from FRAMESELF on port 7000 and
	// sends back results on 127.0.0.1:6000
	public static Dispatcher dispatcher = new Dispatcher("127.0.0.1",6000,7000);
	
	public static void start() {
		
		while(true){
			// Receive an action from FRAMSELF
			Action action = dispatcher.receive();
			// TODO Execute action (To be replaced by a real Phidgets action) 
			System.out.println(action.getName()+" excuted");
			// Update the action result and error attributes
			action.setResult("true");
			action.setError("No error");
			// Send back the action to FRAMSELF
			dispatcher.send(action);
		}
	}
}