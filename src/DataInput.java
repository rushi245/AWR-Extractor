import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class DataInput {

	public static void main(String agrs[]){
        String url = "jdbc:oracle:thin:@54.69.228.145:1521:XE"; 
    	Connection conn = null;
    	PreparedStatement stmt = null;      
        Properties props = new Properties();
        props.setProperty("user", "system");
        props.setProperty("password", "abcd123");
        
        String[] sessionids = {"888.281.57.59|1224071082|R4LSzTVIUwMJaHe92v5ItggS|888.281.57.59", "244.487.689.31|3732962495|zOPLxwmVgLiATDwsG79yhcuA|244.487.689","79.697.198.261|8540798042|oz62Rd9t2PEyVythtHRyVfQF|79.697.198.261","49.57.26.38|9169528258|Z5u2IdWj2A1KvR3GrKJkaDXT|49.57.26.38","377.326.74.89|9104827374|tlgqVZSMbozHZP3SAtZNvIsJ|377.326.74.89",
        		"895.72.617.97|6836078604|EizHM7msMPn4HAiR1oU2ykvp|895.72.617.97","197.38.147.57|9804190950|8c150v2St9QPEOYTvF2fUIUD|197.38.147.57","261.574.447.19|8353628542|KG7ZWM7enL39TLBujet7ZROo|261.574.447.19","73.334.631.29|9878558400|jlgkOFtqkrpHamAhZ1rMgv5m|73.334.631.29","373.56.75.36|8760004797|8YWyLhrdEG9xRByqVWZkJDiO|373.56.75.36"
        };
        
        try {
			conn = DriverManager.getConnection(url,props);
		} catch (SQLException e) {
			System.out.println("Connect Error  "+ e.toString());
		}	
        for(int i = 0 ; i < sessionids.length ; i++){
        	for(int j=0 ; j<1000 ; j++){
		        try{
					String sql ="insert into OD_AUDIT_TRIAL_MB (TRANID,SESSIONID) values(seq_transid.NEXTVAL,?)";
							stmt = conn.prepareStatement(sql);
		//					stmt.setString(1, "ffd1390134551");
							stmt.setString(1, sessionids[i]);
							stmt.executeUpdate();
							System.out.println("Inserted");
				} catch (SQLException e) {
					System.out.println("Connect Error 1 "+ e.toString());
				}	
        	}
        }
	}
	
}
