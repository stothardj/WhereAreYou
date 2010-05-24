import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class AndroidUser {
	public AndroidUser(String username) {
		this.username = username;
		jc = new JavaClient(username, "localhost", 79);
		try {
			jc.connect();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			jc.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void finalize() throws Throwable {
		try {
			jc.disconnect();
		} finally {
			super.finalize();
		}
	}
	
	// JSON API

	public void createUser(String first_name, String last_name, String password, String account_type) {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Create User", JSONType.STRING));
		f.add(new JSONField("First Name", first_name, JSONType.STRING));
		f.add(new JSONField("Last Name", last_name, JSONType.STRING));
		f.add(new JSONField("User Name", username, JSONType.STRING));
		f.add(new JSONField("Password", password, JSONType.STRING));
		f.add(new JSONField("Account Type", account_type, JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}

	public void removeUser() {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Remove User", JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}

	public void addZone(String zone_name, String lat, String lon, String radius, String action, String text) {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Add Zone", JSONType.STRING));
		f.add(new JSONField("Zone Name", zone_name, JSONType.STRING));
		f.add(new JSONField("Lat", lat, JSONType.FLOAT));
		f.add(new JSONField("Lon", lon, JSONType.FLOAT));
		f.add(new JSONField("Radius", radius, JSONType.FLOAT));
		f.add(new JSONField("Action", action, JSONType.STRING));
		f.add(new JSONField("Text", text, JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}

	public void removeZone(String zone_name) {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Remove Zone", JSONType.STRING));
		f.add(new JSONField("Zone Name", zone_name, JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}

	public void addFriend(String friend_name) {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Add Friend", JSONType.STRING));
		f.add(new JSONField("Friend Name", friend_name, JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}

	public void acceptFriend(String friend_name) {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Accept Friend", JSONType.STRING));
		f.add(new JSONField("Friend Name", friend_name, JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}

	public void removeFriend(String friend_name) {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Remove Friend", JSONType.STRING));
		f.add(new JSONField("Friend Name", friend_name, JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}

	public void updateCoordinate(String lat, String lon) {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Update Coordinate", JSONType.STRING));
		f.add(new JSONField("Lat", lat, JSONType.FLOAT));
		f.add(new JSONField("Lon", lon, JSONType.FLOAT));
		sendLine(AndroidUser.createJSONString(f));		
	}

	public void login(String password) {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Login", JSONType.STRING));
		f.add(new JSONField("User Name", username, JSONType.STRING));
		f.add(new JSONField("Password", password, JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}

	// JSON API (Testing only)
	public void showUsers() {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Show Users", JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}

	public void showFriends() {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Show Friends", JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}

	public void showZones() {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Show Zones", JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}
	
	public void emptyUsers() {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Empty Users", JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}

	public void emptyFriends() {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Empty Friends", JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}

	public void emptyZones() {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Empty Zones", JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}
	
	public void emptyAll() {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Empty All", JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));
	}	
	
	private void sendLine(String message) {
		System.out.println(username+": "+message);
		jc.sendLine(message);
	}
	
	private static String createJSONString(LinkedList<JSONField> fields) {
		String ret = "{";
		boolean first = true;
		for(JSONField field : fields) {
			if(!first)
				ret+=", ";
			first = false;
			ret += "\""+field.name+"\" : ";
			switch(field.type) {
			case FLOAT:
			case BOOL:
				ret += field.value;
				break;
			
			case STRING:
				ret += "\"" + field.value + "\"";
				break;
			}
		}
		ret += "}";
		return ret;
	}
	String username;
	JavaClient jc;
}
