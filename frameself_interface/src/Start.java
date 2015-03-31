
public class Start {
	public static void main(String[] args) {
		new Thread(){
			public void run(){
				SpecificCollector.start();
			}
		}.start();
		new Thread(){
			public void run(){
				SpecificDispatcher.start();
			}
		}.start();

	}

}
