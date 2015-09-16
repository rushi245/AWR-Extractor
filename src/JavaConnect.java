import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import org.json.*;

public class JavaConnect {
	Connection conn;
	PreparedStatement preStatement;
	ResultSet result,result2;
	Scanner sc;
	long DBID;
	int instance_id;
	String startTime, endTime;
	String beginsnap ,endsnap,output,html;
	File file;
	String username, password, databasename;
	String server,port;
	JSONObject obj;
	
	JavaConnect(){
		try{
			//Code for taking input values from JSON file
			sc = new Scanner(System.in);
			
			URL location = MainClass.class.getProtectionDomain().getCodeSource().getLocation();
			
			String filepath = new File(".").getAbsolutePath();
			filepath = filepath.replaceFirst("\\.", "\\ServerConfig.txt");
	        System.out.println(filepath);
	        
			File file = new File(filepath);
			FileInputStream input = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			input.read(data);
			input.close();
	
			String str = new String(data, "UTF-8");		
			obj = new JSONObject(str);
			
	        server = obj.getString("server");
	        port = obj.getString("port");
	        databasename = obj.getString("databasename");
	        username = obj.getString("username");
	        password = obj.getString("password"); 
	        
	        System.out.println("Server  : " + server);
	        System.out.println("Port  : " + port);
	        System.out.println("databasename  : " + databasename);
	        System.out.println("username  : " + username);
	        System.out.println("password  : " + password);
		}
		catch(Exception e){
			System.out.println(e.getStackTrace());
			System.out.println("Problem with JSON file");
		}
	}
	
	public void connectWithDB(){
		//URL of Oracle database server
			
		//Connection to Database
        String url = "jdbc:oracle:thin:@"+ server +":"+ port +":" + databasename + ""; 
            
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        
        try {
			conn = DriverManager.getConnection(url,props);
		} catch (SQLException e) {
			System.out.println("Connect Error");
		}
        
        
        //Query to fetch DBID and instance number
        String sql ="select d.dbid  dbid , d.name db_name , i.instance_number inst_num ";
        sql = sql + ", i.instance_name   inst_name  from v$database d, v$instance i";
        
        try{

		preStatement = conn.prepareStatement(sql);
		result = preStatement.executeQuery();
	
        
        while(result.next()){
            DBID = result.getLong("dbid");
            instance_id = result.getInt("inst_num");
            System.out.println("DBID  " + DBID + "  Instance ID  : "+instance_id);
        }    
        
        }
        catch(Exception e){
        	System.out.println(e.getStackTrace());
        	System.out.println("Issue in Instance Query");
        }
        
        System.out.println("done");
        
        System.out.println("Enter the start time (DD/MM/YYYY HH:MM:SS) :  ");
        startTime = sc.nextLine();

        System.out.println("Enter the end   time (DD/MM/YYYY HH:MM:SS) :  ");
        endTime = sc.nextLine();      
        
        //Query to fetch snapshot ids
        sql = "select snap_id, begin_interval_time, end_interval_time, dbid,";
        sql = sql + " instance_number from dba_hist_snapshot where end_interval_time > to_date";
        sql = sql + "('" + startTime + "','dd/mm/yyyy HH24:MI:SS') and end_interval_time < ";
        sql = sql + "to_date('" + endTime + "','dd/mm/yyyy HH24:MI:SS') order by snap_id";
        try{
		preStatement = conn.prepareStatement(sql);
		result = preStatement.executeQuery();
		
		int count = 1;
		
		while(result.next()){
			if (count == 1)
			{beginsnap = result.getString("snap_id");count++;}
			else{
				try{
				endsnap = result.getString("snap_id");
				System.out.println("Begin Snap  : "+ beginsnap + " End Snap  :  "+ endsnap + " Time " + result.getString("end_interval_time"));
				
				//Query to fetch generated html code
				sql = "select output from table(dbms_workload_repository.awr_report_html( "+ DBID +",  " + instance_id + ",  " + beginsnap +", " + endsnap +", 0 ))";
				System.out.println("SQL : " + sql);
				preStatement = conn.prepareStatement(sql);
				result2 = preStatement.executeQuery();
				
				file = new File(".//AWRReport_"+beginsnap + "_"+ endsnap+".html");
				FileWriter fw = new FileWriter(file.getAbsoluteFile());

				html = "";
				while(result2.next()){
					output = result2.getString("output");
					if(output==null)
						output="";
					html = html + output;
//				
					
				}
				
				//Output written to file
				fw.write(html);
				fw.flush();
				fw.close();
				System.out.println("File Created .//AWRReport_"+beginsnap + "_"+ endsnap+".html");
				}catch(SQLException s){
					System.out.println(s.toString());
				}
				
				beginsnap = endsnap;
				System.out.println("Done 1 report  " + beginsnap );
			}
		}
	}catch(Exception e){
		System.out.println(e.getStackTrace());
		System.out.println("Problem with Snapshot and Report Extraction");
	}
	}
}
