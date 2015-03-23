import java.io.Console;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;


public class RunRegistraion {
	/**
	 * 
	 */
	protected static Connection con;
	private static Console console; 
	
	private static DataManager dataManager;
	private final static List<String> listCommands = new ArrayList<String>();
	public static void main(String args[]) throws Exception {
		System.out.println("Welcome to the CMPUT 291 Automobile Registration System");
				
		console = System.console();
		
		if(console == null){
			System.err.println("Console is null program will not work");
		}

		
		//connectToLab();
		
		listCommands.add("search");
		listCommands.add("new vehicle registration");
		listCommands.add("violation record");
		listCommands.add("auto transaction");
		listCommands.add("license registration");
		listCommands.add("init");
		
		dataManager = DataManager.getInstance("init");
		
		mainMenu();
		
	}


	private static void mainMenu() {
		while (true){
			try{
				System.out.println("You are in Main Menu.");
				System.out.println("Please choose one of the following: Search, New Vehicle Registration, Violation Record, Auto Transaction, or License Registration");
				
				String state = console.readLine();
				run(state);
			}catch(Exception e){
				System.err.println("Exception in Main menu");
			}


		}			
	}


	private static void run(String command) {
		command = command.toLowerCase();
		
		if(listCommands.contains(command)){
			switch (command){
			case "search":	dataManager.changeState("search");
				searchMenu();
				break;
				
			case "new vehicle registration":	dataManager.changeState("new vehicle registration");
				newVehicleRegistrationMenu();
				break;
			
			case "violation record": dataManager.changeState("violation record");
				violationRecordMenu();
				break;
			
			case "auto transaction": dataManager.changeState("auto transaction");
				autoTransactionMenu();
				break;
			
			case "license registration": dataManager.changeState("license registration");
				licenseRegistratoinMenu();
				break;
			}
				
		}else{
			System.out.println("Input not valid");
			return;
		}
		
	}


	private static void newVehicleRegistrationMenu() {
		while(true){		
			try{
				String ownerIsPrime;
				System.out.println("You are now in the New Vehicle Registration Menu: to return to Main Menu enter init");
				String Owner = console.readLine("Please enter in the New Vehicle Owner's SIN, name, height, weight, eyecolor, hair color, addr, gender, birthday (mm/dd/yyyy): ");
				
				if(Owner.toLowerCase().equals("init")){
					return;
				}
				
				if(Owner.split(",").length != 9){
					throw new Exception("Input invalid. Make sure to separate each entry with a comma!");
					
				}else if(dataManager.ownerInDatabase(Owner)== null){
					System.out.println("This owner is not in the Database, adding them now.");
					
					dataManager.addPerson(Owner);
					
				}else{
					System.out.println("This owner is already in the Database");
					
				}
				
				ownerIsPrime = console.readLine("Is the owner the primary owner? (y or n) ");
				
				if(ownerIsPrime.toLowerCase().contains("y")){
					ownerIsPrime = "y";
				}else if(ownerIsPrime.toLowerCase().equals("init")){
					return;
				}
				else{
					ownerIsPrime = "n";
				}
				
				String Vehicle = console.readLine("Please enter the vehicles Serial No., Maker, Model, Year, Color, and type_id ");
				
				if(Vehicle.toLowerCase().equals("init")){
					return;
				}else if(Vehicle.split(",").length != 6){
					throw new Exception("Input invalid. Make sure to separate each entry with a comma!");
				}else if(dataManager.isVehicleRegistered(Vehicle.split(",")[0])){
					throw new Exception("This Vehicle serial no. is already registered. Please enter correct data.");
				}
				else{
					dataManager.addVehicle(Vehicle);
					String ownerInfo = Owner.split(",")[0] + "," + Vehicle.split(",")[0] + "," + ownerIsPrime;
					dataManager.addOwnership(ownerInfo);
					
				}
				
			}catch(Exception e){
				System.err.println("Exception thrown in New Vehicle Registration");
				System.err.println(e.toString());
			}
		}
	}


	private static void violationRecordMenu() {
		// TODO Auto-generated method stub
		
	}


	private static void autoTransactionMenu() {
		while(true){
			try{
				
				System.out.println("You are now in the Auto Transaction Menu: to return enter init");
				String vehicleInfo = console.readLine("Please enter in the vehicle's Serial No. : ");
				String transactionInfo = console.readLine("Please enter the transaction info: date (mm/dd/yyyy), price :");
				String sellerInfo = console.readLine("Please enter seller's SIN: ");
				String buyerInfo = console.readLine("Please enter the buyer's SIN: ");
				
				
				if(vehicleInfo.equals("init")){
					return;
				}else if(!dataManager.isVehicleRegistered(vehicleInfo)){
					System.out.println("Vehicle is not registered, returning to Main Menu.");
					return;
				}else{
					String[] ownerInfo = dataManager.getOwnershipInfo(vehicleInfo);
					dataManager.removeOwners(ownerInfo);
					Integer transactionID = ((Double)(Math.random() * Math.pow(10, 15))).intValue();
					
					transactionInfo = transactionID.toString() + ", " + buyerInfo + ", " + sellerInfo;
					dataManager.addTransaction(transactionInfo);
				}
			}catch(Exception e){
				System.err.println("Exception raised in Auto Transaction");
				System.err.println(e.toString());
			}
		}
	}
	


	private static void licenseRegistratoinMenu() {
		// TODO Auto-generated method stub
		
	}


	private static void searchMenu() {
		while(true){
			try{
				System.out.println("You are now in the Search Menu: to return to Main Menu enter init; otherwise enter in a name or license number");
				String searchRequest = console.readLine();
				
				if(searchRequest.toLowerCase().equals("init")){
					return;
				}else{
					dataManager.search(searchRequest);
				}
				
			}catch(Exception e){
				System.err.println("exception raised in search menu");
			}
		}
		
		
	}


	private static void connectToLab() throws Exception {
		
		boolean connectionNotMade = true;
		String driverName = "oracle.jdbc.driver.OracleDriver";
		Class drvClass = Class.forName(driverName);
		DriverManager.registerDriver((Driver)drvClass.newInstance());
		
		while(connectionNotMade){
			
			try{
				String username = console.readLine("Please enter your Oracle username: ");
				char[] password = console.readPassword("Please enter your Oracle password: ");
				String passw = " ";
				for(int i = 0; i < password.length; i++){
					passw = passw + password[i];
				}
				
				passw = passw.trim();
	
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



}


