import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;



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

	public ArrayList<String> personRegistered(String licOwner) {
		try{
			PreparedStatement peopleStmt = con.prepareStatement("SELECT name, addr, birthday FROM people WHERE sin = ?");
			
			peopleStmt.clearParameters();
			
			peopleStmt.setString(1, licOwner);
			
			ResultSet registered = peopleStmt.executeQuery();
			
			ArrayList<String> result = new ArrayList<String>();
			while(registered.next()){
				result.add(registered.getString(1) + "," + registered.getString(2) + "," + registered.getDate(3).toString());
			}
			if(result.isEmpty()){
				return null;
			}
			return result;
		}catch(Exception e){
			System.err.println("Error finding registered person");
			System.err.println(e.toString());
			return null;
		}
		
	}

	public boolean addPerson(String owner) {
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
			stmt.executeUpdate();
			
			return true;
		}catch(Exception e){
			System.err.println("Error while adding new Person, make sure values are properly added");
			System.err.println(e.toString());
			return false;
		}
	}

	public boolean addVehicle(String vehicle) {
		try{
			String[] stmtParts = vehicle.split(",");
			PreparedStatement stmt = con.prepareStatement("insert into vehicle values( ?, ?, ?, ?, ?, ?)");		
			
			stmt.clearParameters();
			
			stmt.setString(1, stmtParts[0].trim());
			stmt.setString(2, stmtParts[1].trim());
			stmt.setString(3, stmtParts[2].trim());
			stmt.setBigDecimal(4, BigDecimal.valueOf(Double.valueOf(stmtParts[3].trim())));
			stmt.setString(5, stmtParts[4].trim());
			stmt.setInt(6, Integer.valueOf(stmtParts[5].trim()));
			
			stmt.executeUpdate();
			
			return true;
		}catch(Exception e){
			System.err.println("Error while adding Vehicle");
			System.err.println(e.toString());
			return false;
		}
	}

	public boolean isVehicleRegistered(String vehicleInfo) {
		try{
			PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) FROM vehicle WHERE serial_no = ?");
			
			stmt.clearParameters();
			
			stmt.setString(1, vehicleInfo);
			
			ResultSet result = stmt.executeQuery();
			while(result.next()){
				return true;
			}
			
			return false;
		}catch(Exception e){
			return false;
		}
	}

	public ArrayList<String> getOwnershipInfo(String vehicleInfo) {
		try{
			ArrayList<String> result = new ArrayList<String>();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM owner WHERE vehicle_id = ?");
			
			stmt.clearParameters();
			
			stmt.setString(1, vehicleInfo);
			
			ResultSet response = stmt.executeQuery();
			while(response.next()){
				result.add(response.getString(1) + "," + response.getString(2) + "," + response.getString(3));
			}
			
			if(result.isEmpty()){
				return null;
			}
			
			return result;
		}catch(Exception e){
			System.err.println("Error while getting owner information from database");
			System.err.println(e.toString());
			return null;
		}
	}

	public boolean removeOwners(ArrayList<String> ownerInfo) {
		try{
			
			PreparedStatement stmt = con.prepareStatement("delete from owner where owner_id = ? AND vehicle_id = ?");
			String[] stmtParts;
			for(String owner : ownerInfo){
				stmtParts = owner.split(",");
				stmt.clearParameters();
				stmt.setString(1, stmtParts[0].trim());
				stmt.setString(2, stmtParts[1].trim());
				
				stmt.executeUpdate();
			
			}
			return true;
		}catch(Exception e){
			System.err.println("Error while removing owner's");
			System.err.println(e.toString());
			return false;
		}
		
	}

	public boolean addTransaction(String transactionInfo) {
		try{
			PreparedStatement stmt = con.prepareStatement("insert into auot_sale values (?, ?, ?, ?, ?, ?)");
			String[] stmtParts = transactionInfo.split(",");
			
			stmt.clearParameters();
			
			stmt.setInt(1, Integer.valueOf(stmtParts[0].trim()));
			stmt.setString(2, stmtParts[1]);
			stmt.setString(3, stmtParts[2]);
			stmt.setString(4, stmtParts[3]);
			
			String[] dateParts = stmtParts[4].trim().split("/");
			if(dateParts.length != 3){
				throw new Exception("Date improperly input make sure it is in mm/dd/yyyy format and that the values are valid");
			}
			java.sql.Date date = new java.sql.Date(Integer.valueOf(dateParts[2]), Integer.valueOf(dateParts[0]), Integer.valueOf(dateParts[1]));
			stmt.setDate(5, date);
			
			stmt.setBigDecimal(6, BigDecimal.valueOf(Double.valueOf(stmtParts[5].trim())));
			
			stmt.executeUpdate();
			
			return true;
		}catch(Exception e){
			System.err.println("Error while adding auto sale");
			System.err.println(e.toString());
			return false;
		}
		
	}

	public boolean addOwnership(String ownerInfo) {
		try{
			String[] ownerParts = ownerInfo.split(",");
			PreparedStatement stmt = con.prepareStatement("insert into owner values(?, ?, ?");
			
			stmt.clearParameters();
			
			stmt.setString(1, ownerParts[0]);
			stmt.setString(2, ownerParts[1]);
			stmt.setString(3, ownerParts[2]);
			
			stmt.executeUpdate();
			return true;
		}catch(Exception e){
			System.err.println("Error while adding Owner");
			System.err.println(e.toString());
			return false;
		}
		
	}

	public String licenseRegistered(String driverSIN) {
		try{
			PreparedStatement stmt = con.prepareStatement("SELECT license_no, class FROM drive_license WHERE sin = ?");
			
			stmt.clearParameters();
			
			stmt.setString(1, driverSIN);
			
			String result = " ";

			ResultSet response = stmt.executeQuery();
			if(response.next()){
				result = response.getString(1) + "," + response.getString(2);
			}

			return result;
		}catch(Exception e){
			System.err.println("Error while retrieving license information from database");
			System.err.println(e.toString());
			return null;
		}
	}

	public boolean addLicense(String licenseInfo) {
		try{
			String[] stmtParts = licenseInfo.split(",");
			PreparedStatement stmt = con.prepareStatement("Insert into drive_license values(?, ?, ?, ?, ?, ?)");
			
			stmt.clearParameters();
			
			stmt.setString(1, stmtParts[0]);
			stmt.setString(2, stmtParts[1]);
			stmt.setString(3, stmtParts[2]);
			
			File pictureFile = new File(stmtParts[3]);
			
			stmt.setBinaryStream(4, new FileInputStream(pictureFile), (int)pictureFile.length());
			
			String[] iDateParts = stmtParts[4].trim().split("/");
			if(iDateParts.length != 3){
				throw new Exception("Date improperly input make sure it is in mm/dd/yyyy format and that the values are valid");
			}
			String[] eDateParts = stmtParts[5].trim().split("/");
			if(eDateParts.length != 3){
				throw new Exception("Date improperly input make sure it is in mm/dd/yyyy format and that the values are valid");
			}
			
			java.sql.Date issueDate = new Date(Integer.valueOf(iDateParts[2]), Integer.valueOf(iDateParts[0]), Integer.valueOf(iDateParts[1]));
			java.sql.Date expiryDate = new Date(Integer.valueOf(eDateParts[2]), Integer.valueOf(eDateParts[0]), Integer.valueOf(eDateParts[1]));
			
			stmt.setDate(5, issueDate);
			stmt.setDate(6, expiryDate);
			
			stmt.executeUpdate();
			return true;
		}catch(Exception e){
			System.err.println("Error adding license");
			System.err.println(e.toString());
			return false;
		}
	}

	public boolean addRestriction(String restrictionInfo, Integer restrictionID, String condition) {
		try{
			PreparedStatement conditionStmt = con.prepareStatement("insert into driving_condition values(?,?)");
			PreparedStatement restrictionStmt = con.prepareStatement("insert into restriction values(?,?");
			
			conditionStmt.clearParameters();
			restrictionStmt.clearParameters();
			
			conditionStmt.setInt(1, restrictionID);
			restrictionStmt.setString(1, restrictionInfo);
			
			conditionStmt.setString(2, condition);
			restrictionStmt.setInt(2, restrictionID);
			
			restrictionStmt.executeUpdate();
			conditionStmt.executeUpdate();
			return true;
		}catch(Exception e){
			System.err.println("Error adding restricion");
			System.err.println(e.toString());
			return false;
		}
		
	}

	public boolean addVehicleType(Integer integer, String vType) {
		try{
			PreparedStatement stmt = con.prepareStatement("insert into vehicle_type values (?, ?)");
			
			stmt.clearParameters();
			
			stmt.setInt(1, integer);
			stmt.setString(2, vType);
			
			stmt.executeUpdate();
			return true;
		}catch(Exception e){
			System.err.println("Error adding vehicle type");
			System.err.println(e.toString());
			return false;
		}
	}

	public boolean addTicket(Integer ticketNo, String ticketInfo) {
		try{
			String[] stmtParts = ticketInfo.split(",");
			PreparedStatement stmt = con.prepareStatement("insert into ticket values(?, ?, ?, ?, ?, ?, ?, ?)");
			
			stmt.clearParameters();
			
			stmt.setInt(1, ticketNo);
			stmt.setString(2, stmtParts[0].trim());
			stmt.setString(3, stmtParts[1].trim());
			stmt.setString(4, stmtParts[2].trim());
			stmt.setString(5, stmtParts[3].trim());
			
			String[] dateParts = stmtParts[4].trim().split("/");
			if(dateParts.length != 3){
				throw new Exception("Date improperly input make sure it is in mm/dd/yyyy format and that the values are valid");
			}
			
			java.sql.Date date = new Date(Integer.valueOf(dateParts[2].trim()), Integer.valueOf(dateParts[0].trim()), Integer.valueOf(dateParts[1].trim()));
			stmt.setDate(6, date);
			
			stmt.setString(7, stmtParts[5].trim());
			stmt.setString(8, stmtParts[6].trim());
			
			stmt.executeUpdate();
			
			return true;
		}catch(Exception e){
			System.err.println("Error adding ticket");
			System.err.println(e.toString());
			return false;
		}		
	}

	public boolean addTicketType(String ticketType, Double amount) {
		try{
			PreparedStatement stmt = con.prepareStatement("insert into ticket_type values(?,?)");
			
			stmt.clearParameters();
			
			stmt.setString(1, ticketType);
			stmt.setBigDecimal(2, BigDecimal.valueOf(amount));
			
			stmt.executeUpdate();
			return true;
		}catch(Exception e){
			System.err.println("Error adding ticket type");
			System.err.println(e.toString());
			return false;
		}		
	}

	public ArrayList<String> personSearch(String query) {
		// TODO Auto-generated method stub
		return null;
	}


}
