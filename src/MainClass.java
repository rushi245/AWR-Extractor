import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;


public class MainClass {
	public static void main(String args[]) throws SQLException, IOException, URISyntaxException{
		JavaConnect jc = new JavaConnect();
		jc.connectWithDB();
	}
}
