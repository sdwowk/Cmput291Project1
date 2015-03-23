import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;


public class DataManager {
	protected Connection con;

	private static DataManager instance;
	private String state;
	
	public static DataManager getInstance(String context) {
		if(instance == null){
			instance = new DataManager();
		}
		instance.state = context;

		return instance;
	}

	public void changeState(String st){
		state = st;
	}
	public void initDatabase(String username, String passw) throws Exception {
		boolean connectionNotMade = true;
		String driverName = "oracle.jdbc.driver.OracleDriver";
		Class drvClass = Class.forName(driverName);
		DriverManager.registerDriver((Driver)drvClass.newInstance());
		
		while(connectionNotMade){
			
			try{

				con = DriverManager.getConnection("jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS", username, passw);
				if(con.equals(null)){
					System.err.println("connection failed");
				}
				connectionNotMade = false;
			
			}catch(Exception e){
				System.err.println(e.toString());
				System.err.println("failed attempt to connect");
			}		
		}		
	}

	public void search(String searchRequest) {
		
	}

	public String personRegistered(String licOwner) {

		return null;
	}

	public void addPerson(String owner) {
		// TODO Auto-generated method stub
		
	}

	public void addVehicle(String vehicle) {
		// TODO Auto-generated method stub
		
	}

	public boolean isVehicleRegistered(String vehicleInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	public String[] getOwnershipInfo(String vehicleInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeOwners(String[] ownerInfo) {
		// TODO Auto-generated method stub
		
	}

	public void addTransaction(String transactionInfo) {
		// TODO Auto-generated method stub
		
	}

	public void addOwnership(String ownerInfo) {
		// TODO Auto-generated method stub
		
	}


}
