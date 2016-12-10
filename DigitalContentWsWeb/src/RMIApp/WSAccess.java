package RMIApp;

import java.io.IOException;

public interface WSAccess {
	public ContentInfo getContent(String key) throws IOException;
	public String upload(ContentInfo ci) throws IOException;
	public void delete(String key) throws IOException;
	public void modifyContent(String key,String description) throws IOException;
}
