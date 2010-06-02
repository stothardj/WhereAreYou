package gogodeX.GUI;

public class Zone {
	private String name, action, text;
	private double lat, lon;
	private double radius; //in degree distance to be checked (ie. not miles)
	final double degPerMile = .0144569;
	
	//Can only leave something out if you do it explicitly when you create it.
	//Since you can't update zones in the database (right now), editting a zone
	//object is discouraged
	public Zone(String name, double lat, double lon, double radius, String action, String text) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.radius = radius;
		this.action = action;
		this.text = text;
	}
	
	public String getName() { return name; }
	public String getAction() { return action; }
	public String getText() { return text; }
	public double getLat() { return lat; }
	public double getLon() { return lon; }
	public double getRadiusInDegress() { return radius; }
	public double getRadiusInMiles() { return radius / degPerMile; }
	
}
