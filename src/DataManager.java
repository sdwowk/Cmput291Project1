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
			PreparedStatement peopleStmt = con.prepareStatement("SELECT name, addr, birthday FROM people WHERE LTRIM(RTRIM(sin)) = ?");
			
			peopleStmt.clearParameters();
			
			peopleStmt.setString(1, licOwner.trim());
			
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
			java.sql.Date date = new java.sql.Date(Integer.valueOf(dateParts[2].trim()), Integer.valueOf(dateParts[0].trim()), Integer.valueOf(dateParts[1].trim()));
			
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
			PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) FROM vehicle WHERE LTRIM(RTRIM(serial_no)) = ?");
			
			stmt.clearParameters();
			
			stmt.setString(1, vehicleInfo.trim());
			
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
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM owner WHERE LTRIM(RTRIM(vehicle_id)) = ?");
			
			stmt.clearParameters();
			
			stmt.setString(1, vehicleInfo.trim());
			
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
			
			PreparedStatement stmt = con.prepareStatement("delete from owner where LTRIM(RTRIM(owner_id = ?)) AND LTRIM(RTRIM(vehicle_id)) = ?");
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
			stmt.setString(2, stmtParts[1].trim());
			stmt.setString(3, stmtParts[2].trim());
			stmt.setString(4, stmtParts[3].trim());
			
			String[] dateParts = stmtParts[4].trim().split("/");
			if(dateParts.length != 3){
				throw new Exception("Date improperly input make sure it is in mm/dd/yyyy format and that the values are valid");
			}
			java.sql.Date date = new java.sql.Date(Integer.valueOf(dateParts[2].trim()), Integer.valueOf(dateParts[0].trim()), Integer.valueOf(dateParts[1].trim()));
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
			PreparedStatement stmt = con.prepareStatement("SELECT licence_no, class, expiring_date FROM drive_licence WHERE LTRIM(RTRIM(sin)) = ?");
			
			stmt.clearParameters();
			
			stmt.setString(1, driverSIN.trim());
			
			String result = " ";

			ResultSet response = stmt.executeQuery();
			if(response.next()){
				result = response.getString(1).trim() + "," + response.getString(2) + "," + response.getDate(3).toString();
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
			PreparedStatement stmt = con.prepareStatement("Insert into drive_licence values(?, ?, ?, ?, ?, ?)");
			
			stmt.clearParameters();
			
			stmt.setString(1, stmtParts[0]);
			stmt.setString(2, stmtParts[1]);
			stmt.setString(3, stmtParts[2]);
			if(stmtParts[3].trim().equals("") || stmtParts[3].toLowerCase().trim().equals("null")){
				stmt.setNull(4, java.sql.Types.BLOB);
			}else{
				File pictureFile = new File(stmtParts[3]);
				
				stmt.setBinaryStream(4, new FileInputStream(pictureFile), (int)pictureFile.length());
			}
			
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
		try{
			
			ArrayList<String> result = new ArrayList<String>();
			
			//Query should bring back all the SIN numbers of either a driver's license entered or a name entered
			PreparedStatement personStatement = con.prepareStatement("SELECT sin FROM people WHERE (LTRIM(RTRIM(name)) = ?)");
			PreparedStatement licensSmt = con.prepareStatement("SELECT sin FROM drive_licence WHERE (LTRIM(RTRIM(licence_no)) = ?)");
			
			personStatement.clearParameters();
			
			personStatement.setString(1, query.trim());
			licensSmt.setString(1, query.trim());
			
			ResultSet personResults = personStatement.executeQuery();
			ResultSet licenceResults = licensSmt.executeQuery();
			
			ArrayList<String> persons = new ArrayList<String>();
			while(personResults.next()){
				persons.add(personResults.getString(1));
			}
			
			while(licenceResults.next()){
				persons.add(licenceResults.getString(1));
			}
			
			//If a SIN was entered this should be true, need to grab violation information from person
			if(persons.isEmpty()){
				//Add the license numbers for the SIN
				persons.add(licenseRegistered(query).split(",")[0].trim());				
				
				PreparedStatement sinStmt = con.prepareStatement("SELECT * FROM ticket WHERE LTRIM(RTRIM(violator_no)) = ?");
				
				sinStmt.clearParameters();
				
				sinStmt.setString(1, persons.get(0).trim());
				
				ResultSet results = sinStmt.executeQuery();
				while(results.next()){
					result.add(String.valueOf(results.getInt(1)) + "," + results.getString(2) + "," + results.getString(3) + "," + results.getString(4) + "," + results.getString(5) + "," + results.getDate(6).toString() + "," + results.getString(7) + "," + results.getString(8));
				}
			
				return result;
			}
			
			PreparedStatement violationStmt = con.prepareStatement("SELECT * FROM ticket WHERE LTRIM(RTRIM(violator_no)) = ?");
			
			violationStmt.clearParameters();
			
			violationStmt.setString(1, query.trim());
			
			ResultSet results = violationStmt.executeQuery();
		
			
			for(String person : persons){
				result.add(personRegistered(person).get(0) + "," + licenseRegistered(person) + "," + getCondition(licenseRegistered(person).split(",")[0]));
			
			}
			//If there are multiple names we just return the information in result
			if(persons.size() > 1){
				return result;
			}else{
				//Even if one name exists there will be nothing in results and the proper information is returned
				
				ArrayList<String> licenseResult = new ArrayList<String>();
				
				//If a driver license was entered only one person is in list result
				String licenseString = result.get(0);
				while(results.next()){
					licenseString = licenseString + "," + "\n" +  "Violation:" + String.valueOf(results.getInt(1)) + "," + results.getString(2) + "," + results.getString(3) + "," + results.getString(4) + "," + results.getString(5) + "," + results.getDate(6).toString() + "," + results.getString(7) + "," + results.getString(8);
				}
				licenseResult.add(licenseString);
				return licenseResult;
			}
			
		}catch(Exception e){
			System.err.println("Error while searching people.");
			System.err.println(e.toString());
			
			return null;
		}
	}

	private String getCondition(String string) {
		try{
			PreparedStatement conditionStmt = con.prepareStatement("SELECT r_id FROM restriction WHERE LTRIM(RTRIM(licence_no)) = ?");
			
			conditionStmt.clearParameters();
			
			conditionStmt.setString(1, string.trim());
			
			Integer rID = null;
			ResultSet restrictionID = conditionStmt.executeQuery();
			if(restrictionID.next()){
				rID = restrictionID.getInt(1);
			}
			
			PreparedStatement descStmt = con.prepareStatement("SELECT description FROM condition WHERE LTRIM(RTRIM(c_id)) = ?");
			
			descStmt.clearParameters();
			
			descStmt.setInt(1, rID);
			
			String description = " ";
			ResultSet desc = descStmt.executeQuery();
			if(desc.next()){
				description = desc.getString(1);
			}
			return description;
		}catch(Exception e){
			System.err.println("Error getting the condition from database. Condition may not exist");
			System.err.println(e.toString());
			
			return null;
		}
	}

	public ArrayList<String> vehicleSearch(String query) {
		// TODO Auto-generated method stub
		return null;
	}


}
