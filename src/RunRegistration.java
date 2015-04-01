import java.io.Console;
import java.util.ArrayList;
import java.util.List;


public class RunRegistration {
	/**
	 * 
	 */
	private static Console console; 
	private static Integer intID;
	private static DataManager dataManager;
	private final static List<String> listCommands = new ArrayList<String>();
	
	public static void main(String args[]) throws Exception {
		
		System.out.println("Welcome to the CMPUT 291 Automobile Registration System");
				
		console = System.console();
		
		if(console == null){
			System.err.println("Console is null program will not work");
		}


		intID = ((Double)(Math.random() * Math.pow(10, 15))).intValue();



		//Initialize database and data manager
		dataManager = DataManager.getInstance("init");
		dataManager.initDatabase();
		
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
				System.out.println("You are now in the New Vehicle Registration Menu: to return to Main Menu enter init at any time.");
				String Owner = console.readLine("Please enter in the New Vehicle Owner's SIN, name, height, weight, eyecolor, hair color, address, gender, birthday (mm/dd/yyyy): ");
				
				if(Owner.toLowerCase().equals("init")){
					return;
				}
				
				System.out.println("SIN is" + Owner.split(",")[0].trim());
				
				if(Owner.split(",").length != 9){
					throw new Exception("Input invalid. Make sure to separate each entry with a comma!");
					
				}else if(dataManager.personRegistered(Owner.split(",")[0]) == null){
					System.out.println("This owner is not in the Database, adding them now.");
					
					boolean execute = dataManager.addPerson(Owner);
					if(execute){
						
					}else{
						throw new Exception("Error adding new Person to Database");
					}
					
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
				
				String Vehicle = console.readLine("Please enter the vehicles Serial No., Maker, Model, Year, Color, and type_id: ");
				if(Vehicle.toLowerCase().equals("init")){
					return;
				}
				
				String vType = console.readLine("Please enter the vehicle's type: ");
				if(vType.toLowerCase().trim().equals("init")){
					return;
					
				}else if(Vehicle.split(",").length != 6){
					throw new Exception("Input invalid. Make sure to separate each entry with a comma!");
					
				}else if(dataManager.isVehicleRegistered(Vehicle.split(",")[0])){
					throw new Exception("This Vehicle serial no. is already registered. Please enter correct data.");
					
				}
				else{
					boolean execute = dataManager.addVehicle(Vehicle);
					if(!execute){
						throw new Exception("Error adding vehicle to database");
					}
					String ownerInfo = Owner.split(",")[0] + "," + Vehicle.split(",")[0] + "," + ownerIsPrime;
					
					execute = dataManager.addOwnership(ownerInfo);
					if(!execute){
						throw new Exception("Error adding owner to database");
					}
					
					execute = dataManager.addVehicleType(Integer.valueOf(Vehicle.split(",")[5]), vType);
					if(!execute){
						throw new Exception("Error adding vehicle type to database");
					}else{
						System.out.println("Could not understand the input returning to New Vehicle Registration");
					}
					
					
					addSecondaryOwners(Vehicle.split(",")[0]);
					
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
				System.out.println("You are in the Violation Record Menu: to return to Main Menu enter init at any time.");
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
					ArrayList<String> ownerInfo = dataManager.getOwnershipInfo(vehicleInfo);
					for(int i = 0; i < ownerInfo.size(); i++){
						if(isPrimeOwner(ownerInfo.get(i))){
							accused = ownerInfo.get(i).split(",")[0];
						}
					}
				}else if(dataManager.personRegistered(accused) == null){
					throw new Exception("No person exists with this SIN!");
				}
				if(!dataManager.isVehicleRegistered(vehicleInfo)){
					throw new Exception("Vehicle is not registered please register vehicle in New Vehicle Registration Menu.");
				}
				intID = intID - 1;
				
				String ticketType = console.readLine("Please enter the ticket type: ");
				if(ticketType.toLowerCase().trim().equals("init")){
					return;
				}
				
				String ticketDate = console.readLine("Please enter the violation date (mm/dd/yyyy): ");
				if(ticketDate.toLowerCase().trim().equals("init")){
					return;
				}else if(ticketDate.split("/").length != 3){
					throw new Exception("ticket date entered improperly make sure to follow the format mm/dd/yyyy");
				}
				
				String place = console.readLine("Please enter the place of the violation: ");
				if(place.toLowerCase().trim().equals("init")){
					return;
				}
				
				String description = console.readLine("Please enter the ticket desciption: ");
				if(description.toLowerCase().trim().equals("init")){
					return;
				}
				
				String ticketInfo = accused + "," + vehicleInfo + "," + officerNo + "," + ticketType + "," + ticketDate + "," + place + "," + description;
				
				boolean execute = dataManager.addTicket(intID, ticketInfo);
				if(!execute){
					throw new Exception("Error adding ticket to database");
				}
				String fine = console.readLine("Please enter the ticket fine: ");
				if(fine.toLowerCase().trim().equals("init")){
					return;
				}else if(fine.split(".")[1].length() > 2 || fine.split(".").length != 2){
					throw new Exception("Fine improperly entered, make sure there is only two decimal places and that there is only one decima");
				}
				
				Double amount = Double.valueOf(fine);
				execute = dataManager.addTicketType(ticketType, amount);
				if(!execute){
					throw new Exception("Error adding ticket type to database");
				}
			}catch(Exception e){
				System.err.println("Exception raised in Violation Menu");
				System.err.println(e.toString());
			}
		}
	}
	
	private static void addSecondaryOwners(String vehicleInfo){
		boolean addSecondary = false;
		try{
			String addNewOwner = console.readLine("Would you like to add a new owner? (y or n): ");
			if(addNewOwner.equals("y")){
				addSecondary = true;
			}
			while(addSecondary){
				String secondOwner = console.readLine("Please enter the second owner's SIN number: ");
				if(dataManager.personRegistered(secondOwner) == null){
					throw new Exception("Second Owner is not registered");
				}
				String primary = console.readLine("Is this owner the primary owner? (y or n): ");
				
				String ownInfo = secondOwner.trim() + "," + vehicleInfo.trim() + "," + primary.trim();
				boolean execute = dataManager.addOwnership(ownInfo);
				if(!execute){
					throw new Exception("Error adding owner to database");
				}
				
				addNewOwner = console.readLine("Would you like to add a new owner? (y or n): ");
				if(addNewOwner.trim().toCharArray().equals("y")){
					addSecondary = true;
				}else{
					addSecondary = false;
				}
			}
		}catch(Exception e){
			System.err.println("Error adding second owner");
			System.err.println(e.toString());
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
	 * has not been registered or a vehicle for sale has not been registered. An
	 * error brings the person back to the start of the Auto Transaction Menu.
	 */
	private static void autoTransactionMenu() {
		while(true){
			try{
				
				System.out.println("You are now in the Auto Transaction Menu: to return enter init at any time.");
				String vehicleInfo = console.readLine("Please enter in the vehicle's Serial No. : ");
				if(vehicleInfo.toLowerCase().trim().equals("init")){
					return;
				}
				
				String transactionInfo = console.readLine("Please enter the following transaction information: date (mm/dd/yyyy), price: ");
				if(transactionInfo.toLowerCase().trim().equals("init")){
					return;
				}else if(transactionInfo.split(",").length != 2 || transactionInfo.split(",")[0].split("/").length != 3){
					throw new Exception("Error in entering transacion info. Make sure Information is in the following forma. date(mm/dd/yyyy) , price");
				}
				
				String sellerInfo = console.readLine("Please enter seller's SIN: ");
				if(sellerInfo.toLowerCase().trim().equals("init")){
					return;
				}
				
				String buyerInfo = console.readLine("Please enter the buyer's SIN: ");
				if(buyerInfo.toLowerCase().trim().equals("init")){
					return;
				}
				if(!dataManager.isVehicleRegistered(vehicleInfo)){
					System.out.println("Vehicle is not registered, returning to Main Menu.");
					return;
				}else if(dataManager.personRegistered(buyerInfo) == null){
					System.out.println("Buyer is not Registered");
					
					String person = console.readLine("Please enter in the New person's SIN, name, height, weight, eyecolor, hair color, address, gender, birthday (mm/dd/yyyy): ");
					
					if(person.toLowerCase().equals("init")){
						return;
					}
					
					if(person.split(",").length != 9){
						throw new Exception("Input invalid. Make sure to separate each entry with a comma!");
						
					}						
					
					boolean execute = dataManager.addPerson(person);
					if(execute){
						
					}else{
						throw new Exception("Error adding new person to Database");
					}
										
				}
				ArrayList<String> ownerInfo = dataManager.getOwnershipInfo(vehicleInfo);
				boolean sellerIsOwner = false;
				for(int i = 0; i < ownerInfo.size(); i++){
					if(ownerInfo.get(i).split(",")[0].trim().toLowerCase().equals(sellerInfo.toLowerCase().trim())){
						sellerIsOwner = true;
					}
				}
				
				if(!sellerIsOwner){
					throw new Exception("Seller is not currently an owner!");
				}
				
				boolean execute = dataManager.removeOwners(ownerInfo);
				if(!execute){
					throw new Exception("Error removing owner's from database");
				}
				
				intID = intID - 1;
					
				transactionInfo = intID.toString() + ", " + sellerInfo + ", " + buyerInfo + "," + vehicleInfo + "," + transactionInfo;
				execute = dataManager.addTransaction(transactionInfo);
				if(!execute){
					throw new Exception("Error in adding Auto Sale to database");
				}
				
				String primary = console.readLine("Is the buyer the primary owner? (y or n): ");
				if(primary.toLowerCase().trim().equals("init")){
					return;
				}
				
				String ownInfo = buyerInfo + "," + vehicleInfo + "," + primary;
				execute = dataManager.addOwnership(ownInfo);
				if(!execute){
					throw new Exception("Error adding owner to database");
				}
				
				addSecondaryOwners(vehicleInfo);
				
			}catch(Exception e){
				System.err.println("Exception raised in Auto Transaction");
				System.err.println(e.toString());
			}
		}
	}
	


	private static void licenseRegistrationMenu() {
		while(true){
			try{
				System.out.println("You are in the License Registration Menu. To return to Main Menu enter init at any time.");
				String driverSIN = console.readLine("Please enter the SIN number of the new driver: ");
				
				if(driverSIN.toLowerCase().equals("init")){
					return;
				}
				else if(dataManager.personRegistered(driverSIN.trim()) == null){
					System.out.println("Person not registered.");
									
					String person = console.readLine("Please enter in the New person's name, height, weight, eyecolor, hair color, address, gender, birthday (mm/dd/yyyy): ");
					
					if(person.toLowerCase().equals("init")){
						return;
					}
					
					person = driverSIN + "," + person;
					if(person.split(",").length != 9){
						throw new Exception("Input invalid. Make sure to separate each entry with a comma!");
						
					}						
					
					boolean execute = dataManager.addPerson(person);
					if(execute){
					
					}else{
						throw new Exception("Error adding new person to Database");
					}
										
				}else if(dataManager.licenseRegistered(driverSIN) == null){
					throw new Exception("Error occured quering database for this person");
					
				}else if(!(dataManager.licenseRegistered(driverSIN).equals(" "))){
					throw new Exception("This person has already registered for a license!");
				}
				
				String license_no = console.readLine("Please enter the license number: ");
				if(license_no.toLowerCase().trim().equals("init")){
					return;
				}
				
				String license_class = console.readLine("Please enter license class: ");
				if(license_class.toLowerCase().trim().equals("init")){
					return;
				}
				
				String pictureFile = console.readLine("Please enter the filename of the driver's photo: ");
				if(pictureFile.toLowerCase().trim().equals("init")){
					return;
				}
				
				String issueDate = console.readLine("Please enter the Issue Date (mm/dd/yyyy): ");
				if(issueDate.toLowerCase().trim().equals("init")){
					return;
				}
				
				String endDate = console.readLine("Please enter the expiry date (mm/dd/yyyy)");
				if(endDate.toLowerCase().trim().equals("init")){
					return;
				}
				
				String condition = console.readLine("Please enter the driver condition description (Type NA if not applicable): ");
				if(condition.toLowerCase().trim().equals("init")){
					return;
				}else if(condition.toLowerCase().trim().equals("na")){
				}
				else{
									
					Integer restrictionID = ((Double)(Math.random() * Math.pow(10, 8))).intValue();
					boolean execute = dataManager.addRestriction(license_no, restrictionID, condition);
					if(!execute){
						throw new Exception("Error adding restriction to database");
					}
				}
				
				if(issueDate.split("/").length != 3 || endDate.split("/").length != 3){
					throw new Exception("Error in date, make sure to format correctly (mm/dd/yyyy)");
				}
				
				String licenseInfo = license_no + "," + driverSIN + "," + license_class + ","+ pictureFile + "," + issueDate + "," + endDate;
				boolean execute = dataManager.addLicense(licenseInfo);
				if(!execute){
					throw new Exception("Error adding license to database");
				}
				
			}catch(Exception e){
				System.err.println("Error in license registration menu");
				System.err.println(e.toString());
			}
		}
		
	}


	private static void searchMenu() {
		while(true){
			try{
				System.out.println("You are now in the Search Menu: to return to Main Menu enter init any time.");
				String searchMenuRequest = console.readLine("To query about a person, enter person, To query about a vehicle, enter vehicle: ");
				
				if(searchMenuRequest.toLowerCase().trim().equals("init")){
					return;
				}else if(searchMenuRequest.toLowerCase().trim().equals("person")){
					personSearch();
				}else if(searchMenuRequest.toLowerCase().trim().equals("vehicle")){
					vehicleSearch();
				}else{
					throw new Exception("Input could not be understood");
				}
				
			}catch(Exception e){
				System.err.println("exception raised in search menu");
				System.err.println(e.toString());
			}
		}
		
		
	}


	private static void vehicleSearch() {
		try{
			System.out.println("You are in the Vehicle Search Menu. To return to Search Menu, enter search");
			String query = console.readLine("Please enter a vehicle's serial number: ");
			
			if(query.toLowerCase().trim().equals("search")){
				return;
			}
			
			ArrayList<String> results = dataManager.vehicleSearch(query);
			if(results.isEmpty()){
				throw new Exception("The vehicle searched does not exist");
			}else{
				for(String result : results){
					System.out.println(result);
				}
			}
		}catch(Exception e){
			System.err.println("exception raised in searching vehicles");
			System.err.println(e.toString());
		}
	}


	private static void personSearch() {
		try{
			System.out.println("You are in the Person Search Menu. To return to Search Menu, enter search");
			String query = console.readLine("Please enter a name, license number or SIN. ");
			
			if(query.toLowerCase().trim().equals("search")){
				return;
			}
			
			ArrayList<String> results = dataManager.personSearch(query);
			if(!results.isEmpty()){
				
				for(String result : results){
					System.out.println(result);
				}
			}
			else{
				throw new Exception("The person searched is not registered or is missing data from at least one of the following: Violation records, License records or Driver Condition records");
			}	
		}catch(Exception e){
			System.err.println("exception raised in searching people");
			System.err.println(e.toString());
		}
	}


}


