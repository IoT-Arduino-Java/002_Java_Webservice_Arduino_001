import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;
 
/**
 * @author YoYo
 * 
 */
 
/****************************************************
 * Name: ExampleRESTServiceClient class
 * Description:  Web service client example
 * The main function receives temperature data from
 * the UART and sends it to the web service.11
 ****************************************************/
public class ExampleRESTServiceClient implements Observer{
	
	/****************************************************
	 * Name: Main function
	 ****************************************************/
	public static void main(String[] args) {
		String string = "";
		try {
 
			// Step1: Let's 1st read file from fileSystem
			// Change CrunchifyJSON.txt path here
			InputStream crunchifyInputStream = new FileInputStream("C:/CrunchifyJSON.txt");
			InputStreamReader crunchifyReader = new InputStreamReader(crunchifyInputStream);
			BufferedReader br = new BufferedReader(crunchifyReader);
			String line;
			while ((line = br.readLine()) != null) {
				string += line + "\n";
			}
 
			JSONObject jsonObject = new JSONObject(string);
			System.out.println(jsonObject);
 
			// Step2: Now pass JSON File Data to REST Service
			try {
				URL url = new URL("http://localhost:8080/WebServiceExample/api/WebService");
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(jsonObject.toString());
				out.close();
 
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
 
				while (in.readLine() != null) {
				}
				System.out.println("\nCrunchify REST Service Invoked Successfully..");
				in.close();
			} catch (Exception e) {
				System.out.println("\nError while calling Crunchify REST Service");
				System.out.println(e);
			}
 
			br.close();
			
			SerialComInterface main = new SerialComInterface();
			main.initialize();
			Thread t=new Thread() {
				public void run() {
					//the following line will keep this app alive for 1000 seconds,
					//waiting for events to occur and responding to them (printing incoming messages to console).
					try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
				}
			};
			t.start();
			System.out.println("Started");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/****************************************************
	 * Name: update (Observer update)
	 * Description: receive temperature data from UART
	 * and send it as JSON object to the webservice
	 ****************************************************/
    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof SerialComInterface){
        	SerialComInterface observable = (SerialComInterface) o;
        	String str = (String) arg;
            System.out.println("update() :" + str);
            
            if(str.contains("Temperature"))
            {
            	JSONObject jsonObject;

            	try {
            		
					jsonObject = new JSONObject("{" + str + "}");
					
					System.out.println(jsonObject);
		
	            	// Step2: Now pass JSON File Data to REST Service
			
	            	try {
                        // Create connection
						URL url = new URL("http://localhost:8080/WebServiceExample/api/WebService");
						URLConnection connection = url.openConnection();
						connection.setDoOutput(true);
						connection.setRequestProperty("Content-Type", "application/json");
						connection.setConnectTimeout(5000);
						connection.setReadTimeout(5000);
						OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
						out.write(jsonObject.toString());
						out.close();
		 
						BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		 
						while (in.readLine() != null) {
						
						}
						in.close();
					} catch (Exception e) {
						System.out.println("\nError while calling Crunchify REST Service");
						System.out.println(e);
					}
            	} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
            	}
            }
        }
    }
    
}