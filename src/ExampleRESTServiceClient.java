import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

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

private static boolean    enableWebservice = false;
private static JTextField dayTextField;
private static JTextField temperatureTextField;
private static JTextField dateTextField;
private static JTextField timeTextField;
private static JLabel     ledLabel = new JLabel("•");

	/****************************************************
	 * Name: Main function
	 ****************************************************/
	public static void main(String[] args) {
		
		String string = "";
		
		try {
            // Start temperature GUI in EDT Task
	    	javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	              temperatureGUIrun();
	            }
	         });
	    	
	    	if (enableWebservice) {
	    		
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
	    	}
	    	
			// Start reading from UART port
			SerialComInterface main = new SerialComInterface();
			
			main.initialize();
			
			// Dummy thread to keep application alive
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
            

            // Check for day
            if(str.contains("Day:"))
            	
            	//Update the date text field in the EDT task
    	    	javax.swing.SwingUtilities.invokeLater(new Runnable() {
    	            public void run() {
    	            	dayTextField.setText(str.substring(5));
    	            }
    	         });
            
            // Check for date
            if(str.contains("Date:"))
            	
            	//Update the date text field in the EDT task
    	    	javax.swing.SwingUtilities.invokeLater(new Runnable() {
    	            public void run() {
    	            	dateTextField.setText(str.substring(6));
    	            }
    	         });
    	    	
             // Check for time
            if(str.contains("Time:"))
                	
                //Update the temperature text field in the EDT task
        	   	javax.swing.SwingUtilities.invokeLater(new Runnable() {
       	            public void run() {
        	           	timeTextField.setText(str.substring(6));
        	           }
                 });
        	    	
            // Check for temperature
            if(str.contains("Temperature"))
            	
            	//Update the temperature text field in the EDT task
    	    	javax.swing.SwingUtilities.invokeLater(new Runnable() {
    	            public void run() {
    	            	temperatureTextField.setText(str.substring(12));
    	            }
    	         });
            
            // Check for active LED
            if(str.contains("Active LED:")) {
            	
            	int activeLed = Integer.parseInt(str.substring(12));

            	//Update the temperature text field in the EDT task
    	    	javax.swing.SwingUtilities.invokeLater(new Runnable() {
    	            public void run() {

    	            	if (activeLed == 1) {
    		            	ledLabel.setForeground(Color.RED);            		
    	            	}
    	            	else if (activeLed == 2) {
    		            	ledLabel.setForeground(Color.BLUE);            		
    	            	}
    	            	else if (activeLed == 3) {
    		            	ledLabel.setForeground(Color.GREEN); 

    	            	}
    	            }
           		
            	});

            }
            
	    	if (enableWebservice) {    	    	
    	    	// Send temperature as JSON to the REST web service
            	JSONObject jsonObject;

            	try {
            		
      			   // Check for date
 /*                   if(str.contains("Time")) {
                    String	str1 = str.substring(0, 6) + "\"" + str.substring(6, str.length()) + "\"";
        			System.out.println(str1);
        			str = str1;
        			}
*/
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
   
    
    /****************************************************
	 * Name: temperatureGUIrun 
	 * Description: Swing GUI for temperature display
	 * 
	 ****************************************************/
    private static void temperatureGUIrun() {
    	
    	try {
            // Create monitor frame
    		JFrame monitorFrame = new JFrame("Arduino Monitor");
            int Yoffset = 10;
            
    		//Day
    		// Create day label
	        JLabel dayLabel;
	        dayLabel = new JLabel("Day:");  
	        dayLabel.setBounds(50,Yoffset, 100,20);
	        monitorFrame.add(dayLabel);
	        Yoffset +=20;
            
	        //Create date text field
	        dayTextField = new JTextField("No day yet...");
	        dayTextField.setEditable(false);
	        dayTextField.setBounds(50,Yoffset, 100,20);  
	        dayTextField.setBackground(Color.WHITE);
	        monitorFrame.add(dayTextField); 
	        Yoffset +=20;
	        
    		//Date
    		// Create date label
	        JLabel dateLabel;
	        dateLabel = new JLabel("Date:");  
	        dateLabel.setBounds(50,Yoffset, 100,20);   
	        monitorFrame.add(dateLabel);
	        Yoffset +=20;
	        
	        //Create date text field
	        dateTextField = new JTextField("No date yet...");
	        dateTextField.setEditable(false);
	        dateTextField.setBounds(50,Yoffset, 100,20);
	        dateTextField.setBackground(Color.WHITE);
	        monitorFrame.add(dateTextField); 
	        Yoffset +=20;
	        
    		//Time
    		// Create time label
	        JLabel timeLabel;
	        timeLabel = new JLabel("Time:");  
	        timeLabel.setBounds(50,Yoffset, 100,20);   
	        monitorFrame.add(timeLabel);
	        Yoffset +=20;
	        
	   		// Create time text field  
	        timeTextField = new JTextField("No time yet...");
	        timeTextField.setEditable(false);
	        timeTextField.setBounds(50,Yoffset, 100,20);
	        timeTextField.setBackground(Color.WHITE);
	        monitorFrame.add(timeTextField); 
	        Yoffset +=20;
	        
    		//Temperature
    		// Create temperature label
	        JLabel temperatureLabel;
	        temperatureLabel = new JLabel("Temperature:");  
	        temperatureLabel.setBounds(50,Yoffset, 100,20);   
	        monitorFrame.add(temperatureLabel);
	        Yoffset +=20;

	   		// Create temperature text field
	        temperatureTextField = new JTextField("No temp yet...");
	        temperatureTextField.setEditable(false);
	        temperatureTextField.setBounds(50,Yoffset, 100,20);  
	        temperatureTextField.setBackground(Color.WHITE);
	        monitorFrame.add(temperatureTextField);  
	        Yoffset +=20;
	        
    		//Temperature
    		// Create temperature text label
	        JLabel ledOnLabel;
	        ledOnLabel = new JLabel("Led on:");  
	        ledOnLabel.setBounds(50,Yoffset, 100,20);   
	        monitorFrame.add(ledOnLabel);
	        //Yoffset +=20;
	        
	   		// Create led button label
	        ledLabel.setBounds(120,Yoffset, 100,20);
	        ledLabel.setForeground(Color.GRAY);
	        monitorFrame.add(ledLabel);
	        
	        monitorFrame.setSize(300,250);
	        monitorFrame.setLayout(null);  
	        monitorFrame.setVisible(true);
	        monitorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        
    	}catch (Exception e) {
			e.printStackTrace();
    	}

    }    
}