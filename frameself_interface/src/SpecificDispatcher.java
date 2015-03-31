import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import frameself.Dispatcher;
import frameself.format.Action;

public class SpecificDispatcher {
	// Create a dispatcher that listens to actions from FRAMESELF on port 7000 and
	// sends back results on 127.0.0.1:6000
	public static Dispatcher dispatcher = new Dispatcher("127.0.0.1",6000,7000);
	private static HttpURLConnection server;
	
	public static void start() {
		
	    try {
	    	// Envoi de la requête pour s'inscrire à la ressource SENSOR_O
			connect("http://127.0.0.1:8080/om2m/gscl/applications/SENSOR_0/containers/DATA/contentInstances/subscriptions", "POST");
			post("<om2m:subscription xmlns:om2m=\"http://uri.etsi.org/m2m\">"
					+ "<om2m:contact>http://127.0.0.1:6300/collector</om2m:contact>"
					+ "</om2m:subscription>");
			displayResponse();
			disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		while(true){
			// Receive an action from FRAMSELF
			Action action = dispatcher.receive();
			// TODO Execute action (To be replaced by a real Phidgets action) 
			System.out.println(action.getName()+" excuted");
//			switch(action.getName()) {
//				case "SwitchLampOn":
//					connect("http://127.0.0.1:8080/om2m/gscl/applications/OUTPUT_2/containers/DATA/contentInstances", "POST");
//					post("<obj><str name=\"type\" val=\"OUTPUT\"/>"
//							+ "<str name=\"location\" val=\"Home\"/>"
//							+ "<str name=\"appId\" val=\"OUTPUT_2\"/>"
//							+ "<int name=\"data\" val=\"1\"/>"
//							+ "</obj>");
//					displayResponse();
//					disconnect();
//					break;
//			}
//			
			// Update the action result and error attributes
			action.setResult("true");
			action.setError("No error");
			// Send back the action to FRAMSELF
			dispatcher.send(action);
		}
	}
	
	 public static void connect(String urlS, String method)
	  {
	    try
	    {
	      URL url = new URL(urlS);
	      server = (HttpURLConnection)url.openConnection();
	      server.setDoInput(true);
	      server.setDoOutput(true);
	      server.setRequestMethod(method);
          server.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
	      server.connect();
	    }
	    catch (Exception e)
	    {
	      System.out.println("Connection failed");
	    }
	  }

	  public static void disconnect()
	  {
	    server.disconnect();
	  }

	  public static void displayResponse()
	  {
	    String line;

	    try
	    {
	      BufferedReader s = new BufferedReader(
	                            new InputStreamReader(
	                                server.getInputStream()));
	      line = s.readLine();
	      while (line != null)
	      {
	        System.out.println(line);
	        line = s.readLine();
	      }
	      s.close();
	    }
	    catch(Exception e)
	    {
	      System.out.println("Unable to read input stream");
	    }
	  }

	  public static void post(String s)
	  {
	    try
	    {
	      BufferedWriter bw = new BufferedWriter(
	                                new OutputStreamWriter(
	                                    server.getOutputStream()));
	      bw.write(s, 0, s.length());
	      bw.flush();
	      bw.close();
	    }
	    catch(Exception e)
	    {
	      System.out.println("Unable to write to output stream");
	    }
	  }
}