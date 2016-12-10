package RMIApp;

public class ContentInfo {
	private String key;
	private String path;
	private String owner;
	private String description;
		
	public ContentInfo() {
		
	}
	public String getOwner() {
		return owner;
	}public String getPath() {
		return path;
	}public void setPath(String path) {
		this.path = path;
	}public void setOwner(String owner) {
		this.owner = owner;
	}public void setDescription(String description) {
		this.description = description;
	}public String getDescription() {
		return description;
	}public String getKey() {
		return key;
	}public void setKey(String key) {
		this.key = key;
	}

}
