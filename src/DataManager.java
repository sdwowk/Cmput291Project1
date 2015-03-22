
public class DataManager {
	private static DataManager instance;
	private String state;
	
	public static DataManager getInstance(String context) {
		if(instance == null){
			instance = new DataManager();
			instance.initDatabase();
		}
		instance.state = context;

		return instance;
	}

	public void changeState(String st){
		state = st;
	}
	private void initDatabase() {
		// TODO Auto-generated method stub
		
	}

	public void search(String searchRequest) {
		
	}

	public String ownerInDatabase(String licOwner) {

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

}
