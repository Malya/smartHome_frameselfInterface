import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.phidgets.InterfaceKitPhidget;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import frameself.Collector;
import frameself.format.Event;


public class SpecificCollector {
	//Use this InterfaceKitPhidget to monitor and control phidgets devices
	public static InterfaceKitPhidget ik;
	// Create a collector that publishes events to FRAMSELF on 127.0.0.1:5000
	public static Collector collector = new Collector("127.0.0.1", 5000);

	private static int port = 6300;
    private static String context = "/collector";
	
	public static void start() {

		System.out.println("Starting server..");
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			server.createContext(context, new MyHandler());
			server.start();
			System.out.println("The server is now listening on\nPort: "+ port+"\nContext: "+context+"\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static class MyHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {

			String body = "";
			int i;
			char c;
			try {
				InputStream is = t.getRequestBody();


				while ((i = is.read()) != -1) {
					c = (char) i;
					body = (String) (body + c);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Received notification:");
			System.out.println(body);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder dBuilder = dbf.newDocumentBuilder();

				Document notifyDoc = dBuilder.parse(new InputSource(new ByteArrayInputStream(body.getBytes("utf-8"))));

				String contentInstance64 = notifyDoc.getElementsByTagName("om2m:representation").item(0).getTextContent();
				System.out.println("ContentInstance (Base64-encoded):\n"+contentInstance64+"\n");

				String contentInstance = new String(DatatypeConverter.parseBase64Binary(contentInstance64));
				System.out.println("ContentInstance:\n"+contentInstance+"\n");

				Document instanceDoc = dBuilder.parse(new InputSource(new ByteArrayInputStream(contentInstance.getBytes("utf-8"))));
				String content64 = instanceDoc.getElementsByTagName("om2m:content").item(0).getTextContent();;
				System.out.println("Content (Base64-encoded):\n"+content64+"\n");

				final String content = new String(DatatypeConverter.parseBase64Binary(content64));
				System.out.println("Content:\n"+content+"\n");

				t.sendResponseHeaders(204, -1);
				
				Document finalDoc = dBuilder.parse(new ByteArrayInputStream(content.getBytes("utf-8")));
				finalDoc.getDocumentElement().normalize();
				Event event = new Event();
				
				System.out.println("Root element :" + finalDoc.getDocumentElement().getNodeName());
				NodeList nList = finalDoc.getDocumentElement().getChildNodes();
				
				for (int temp = 0; temp < nList.getLength(); temp++) {
				 
					Node node = nList.item(temp);			 
					System.out.println("\nCurrent Element :" + node.getNodeName());
			 
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) node;
						switch(eElement.getAttribute("name")) {
							case "type":  event.setCategory("Luminosity"); System.out.println("setCategory"); break;
							case "location": event.setLocation(eElement.getAttribute("val")); System.out.println("setLcoation");break;
							case "appId": event.setSensor("LuminositySensor"); System.out.println("setSensor");break;
							case "data": event.setValue(eElement.getAttribute("val"));System.out.println("setValue");break;
							default: break;
						}					
					}
				}
				
				event.setTimestamp(new Date());
				event.setExpiry(new Date(System.currentTimeMillis()+20000));
				
				// Send the event to FRAMSELF
				collector.send(event);

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				t.sendResponseHeaders(501, -1);
			} catch (SAXException e) {
				e.printStackTrace();
				t.sendResponseHeaders(501, -1);
			} catch (IOException e) {
				e.printStackTrace();
				t.sendResponseHeaders(501, -1);
			}
		}
	}
}