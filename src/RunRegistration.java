import java.io.Console;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RunRegistration {
	/**
	 * 
	 */
	private static Console console; 
	
	private static DataManager dataManager;
	private final static List<String> listCommands = new ArrayList<String>();
	
	public static void main(String args[]) throws Exception {
		
		System.out.println("Welcome to the CMPUT 291 Automobile Registration System");
				
		console = System.console();
		
		if(console == null){
			System.err.println("Console is null program will not work");
		}





		//Initialize database and data manager
		dataManager = DataManager.getInstance("init");
		//dataManager.initDatabase();
		
		//List of available menu options
		listCommands.add("search");
		listCommands.add("new vehicle registration");
		listCommands.add("violation record");
		listCommands.add("auto transaction");
		listCommands.add("license registration");
		listCommands.add("init");
		
		//Start in the main menu
		mainMenu();
		
	}


	public static String getUserInfo() {
		//Gather info required to connect to database and cs server
		String username = console.readLine("Please enter your Oracle username: ");
		char[] password = console.readPassword("Please enter your Oracle password: ");
		String passw = " ";
		for(int i = 0; i < password.length; i++){
			passw = passw + password[i];
		}
		
		passw = passw.trim();
		
		return username + " , " + passw;
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
			if(command.equals("search")) {
				dataManager.changeState("search");
				searchMenu();
				
			}else if(command.equals("new vehicle registration")){
				dataManager.changeState("new vehicle registration");
				newVehicleRegistrationMenu();
			
			}else if(command.equals("violation record")){
				dataManager.changeState("violation record");
				violationRecordMenu();
			
			}else if((command.equals("auto transaction"))){
				dataManager.changeState("auto transaction");
				autoTransactionMenu();
				
			}else if(command.equals("license registration")){
				dataManager.changeState("license registration");
				licenseRegistrationMenu();
				
			}
				
		}else{
			System.out.println("Input not valid");
			return;
		}
		
	}

	/*	Allows user to enter information about a new owner, and 
	 *  their vehicle. This produces a string so DataManager can
	 *  add them to the database.
	 */
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
					
				}else if(dataManager.personRegistered(Owner.split(",")[0])== null){
					System.out.println("This owner is not in the Database, adding them now.");
					
					dataManager.addPerson(Owner);
					
				}else{
					System.out.println("This owner is already in the Database");
					
				}
				
				ownerIsPrime = console.readLine("Is the owner the primary owner? (y or n): ");
				
				if(ownerIsPrime.toLowerCase().contains("y")){
					ownerIsPrime = "y";
				}else if(ownerIsPrime.toLowerCase().equals("init")){
					return;
				}
				else{
					ownerIsPrime = "n";
				}
				
				String Vehicle = console.readLine("Please enter the vehicles Serial No., Maker, Model, Year, Color, and type_id ");
				String vType = console.readLine("Please enter the vehicle's type");
				
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
					dataManager.addVehicleType(Owner.split(",")[5], vType);
					
				}
				
			}catch(Exception e){
				System.err.println("Exception thrown in New Vehicle Registration");
				System.err.println(e.toString());
			}
		}
	}


	private static void violationRecordMenu() {
		while(true){
			try{
				System.out.println("You are in the Violation Record Menu: to return to Main Menu enter init");
				String accused = console.readLine("Enter in the violator's SIN, if not enter NA: ");
				if(accused.toLowerCase().trim().equals("init")){
					return;
				}
				String vehicleInfo = console.readLine("Enter the vehicle's Serial No.: ");
				if(vehicleInfo.toLowerCase().trim().equals("init")){
					return;
				}
				String officerNo = console.readLine("Please enter the Officer's id: ");
				if(officerNo.toLowerCase().trim().equals("init")){
					return;
				}
				
				if(accused.toLowerCase().equals("na")){
					String[] ownerInfo = dataManager.getOwnershipInfo(vehicleInfo);
					for(int i = 0; i < ownerInfo.length; i++){
						if(isPrimeOwner(ownerInfo[i])){
							accused = ownerInfo[i].split(",")[0];
						}
					}
				}else if(!dataManager.isVehicleRegistered(vehicleInfo)){
					throw new Exception("Vehicle is not registered please register vehicle in New Vehicle Registration Menu.");
					
				}
				
				
			}catch(Exception e){
				System.err.println("Exception raised in Violation Menu");
				System.err.println(e.toString());
			}
		}
	}

	private static boolean isPrimeOwner(String string) {
		if(string.split(",")[2].toLowerCase().contains("y")){
			return true;
		}
		return false;
	}


	/*
	 * Allows user to enter information about a vehicle transaction, a buyer that
	 * hasn't been registered in the database and produces errors when a seller
	 * has not been registered or a vehicle for sale has not been registered.
	 */
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
				}else if(dataManager.personRegistered(buyerInfo) == null){
					System.out.println("Buyer is not Registered");
					
					String person = console.readLine("Please enter in the New person's SIN, name, height, weight, eyecolor, hair color, addr, gender, birthday (mm/dd/yyyy): ");
					
					if(person.toLowerCase().equals("init")){
						return;
					}
					
					if(person.split(",").length != 9){
						throw new Exception("Input invalid. Make sure to separate each entry with a comma!");
						
					}						
					
					dataManager.addPerson(person);
										
				}
						
				String[] ownerInfo = dataManager.getOwnershipInfo(vehicleInfo);
				
				boolean sellerIsOwner = false;
				for(int i = 0; i < ownerInfo.length; i++){
					if(ownerInfo[i].split(",")[0].trim().toLowerCase().equals(sellerInfo.toLowerCase().trim())){
						sellerIsOwner = true;
					}
				}
				
				if(!sellerIsOwner){
					throw new Exception("Seller is not currently an owner!");
				}
				
				dataManager.removeOwners(ownerInfo);
				Integer transactionID = ((Double)(Math.random() * Math.pow(10, 15))).intValue();
					
				transactionInfo = transactionID.toString() + ", " + buyerInfo + ", " + sellerInfo;
				dataManager.addTransaction(transactionInfo);
				
				String primary = console.readLine("Is the buyer the primary owner? (y or n): ");
				String ownInfo = buyerInfo + "," + vehicleInfo + "," + primary;
				dataManager.addOwnership(ownInfo);
				
			}catch(Exception e){
				System.err.println("Exception raised in Auto Transaction");
				System.err.println(e.toString());
			}
		}
	}
	


	private static void licenseRegistrationMenu() {
		while(true){
			try{
				String driverSIN = console.readLine("Please enter the SIN number of the new driver: ");
				
				if(driverSIN.toLowerCase().equals("init")){
					return;
				}
				else if(dataManager.personRegistered(driverSIN) == null){
					System.out.println("Person not registered.");
									
					String person = console.readLine("Please enter in the New person's name, height, weight, eyecolor, hair color, addr, gender, birthday (mm/dd/yyyy): ");
					
					if(person.toLowerCase().equals("init")){
						return;
					}
					
					person = driverSIN + "," + person;
					if(person.split(",").length != 9){
						throw new Exception("Input invalid. Make sure to separate each entry with a comma!");
						
					}						
					
					dataManager.addPerson(person);
										
				}else if(dataManager.licenseRegistered(driverSIN) != null){
					throw new Exception("Driver's license already issued to this person");
				}
				
				String license_no = console.readLine("Please enter the license number: ");
				
				String license_class = console.readLine("Please enter license class: ");
				String pictureFile = console.readLine("Please enter the filename of the driver's photo: ");
				String issueDate = console.readLine("Please enter the Issue Date (mm/dd/yyyy): ");
				String endDate = console.readLine("Please enter the expiry date (mm/dd/yyyy)");
				
				String restrictionInfo = console.readLine("Please enter in the driver's restriction info description: ");
				String condition = console.readLine("Please enter the driver condition info: ");
				Double restrictID = ((Double)Math.random() * Math.pow(10, 8));
				Integer restrictionID = restrictID.intValue();
				
				String licenseInfo = license_no + "," + driverSIN + "," + license_class + ","+ pictureFile + "," + issueDate + "," + endDate;
				dataManager.addLicense(licenseInfo);
				dataManager.addRestriction(restrictionID, restrictionInfo, condition);
			}catch(Exception e){
				System.err.println("Error in license registration menu");
				System.err.println(e.toString());
			}
		}
		
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


}


