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
	
	public void login(String password) {
		LinkedList<JSONField> f = new LinkedList<JSONField>();
		f.add(new JSONField("Request Type", "Login", JSONType.STRING));
		f.add(new JSONField("User Name", username, JSONType.STRING));
		f.add(new JSONField("Password", password, JSONType.STRING));
		sendLine(AndroidUser.createJSONString(f));		
	}
	
	//TODO: Following example of login, create functions for each thing a user can do
	
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
	private String username;
	JavaClient jc;
}
