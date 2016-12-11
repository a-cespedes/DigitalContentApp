package RMIApp;

import java.io.IOException;
import java.util.Set;

public interface WSAccess {
	public ContentInfo getContent(String key) throws IOException;
	public String upload(ContentInfo ci) throws IOException;
	public void delete(String key) throws IOException;
	public void modifyContent(String key,String description) throws IOException;
	public Set<ContentInfo> list(String owner) throws IOException;
	public Set<ContentInfo> search(String word) throws IOException;
}
