import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;



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
	public void initDatabase() throws Exception {
		boolean connectionNotMade = true;
		String driverName = "oracle.jdbc.driver.OracleDriver";
		Class drvClass = Class.forName(driverName);
		DriverManager.registerDriver((Driver)drvClass.newInstance());

		
		while(connectionNotMade){
			
			try{
				String userInfo = RunRegistration.getUserInfo();
				String username = userInfo.split(",")[0];
				String passw = userInfo.split(",")[1];
				
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
		try{
			String[] stmtParts = owner.split(",");
			PreparedStatement stmt = con.prepareStatement("insert into people values( ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			stmt.clearParameters();
			
			stmt.setString(1, stmtParts[0].trim());
			stmt.setString(2, stmtParts[1].trim());
			stmt.setBigDecimal(3, BigDecimal.valueOf(Double.valueOf(stmtParts[2].trim())));
			stmt.setBigDecimal(4, BigDecimal.valueOf(Double.valueOf(stmtParts[3].trim())));
			stmt.setString(5, stmtParts[4].trim());
			stmt.setString(6, stmtParts[5].trim());
			stmt.setString(7, stmtParts[6].trim());
			stmt.setString(8, stmtParts[7].trim());
			
			String[] dateParts = stmtParts[8].split("/");
			if(dateParts.length != 3){
				throw new Exception("Date improperly input make sure it is in mm/dd/yyyy format and that the values are valid");
			}
			java.sql.Date date = new java.sql.Date(Integer.valueOf(dateParts[2]), Integer.valueOf(dateParts[0]), Integer.valueOf(dateParts[1]));
			
			stmt.setDate(9, date);
			
			System.out.println(stmt.toString());
			
		}catch(Exception e){
			System.err.println("Error while adding new Person, make sure values are properly added");
			System.err.println(e.toString());
		}
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

	public String licenseRegistered(String driverSIN) {
		// TODO Auto-generated method stub
		return null;
	}


}
