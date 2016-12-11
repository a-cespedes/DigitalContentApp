package RMIApp;


import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;





public class RMIServerImpl extends UnicastRemoteObject implements RMIServerInterface{
        
    private Set<String> nicks;
    private Map<String,User> users;
    private final String path = System.getProperty("user.home") + File.separator + "DigitalContent" + File.separator;
    private WSAccess webService;
    
    public RMIServerImpl() throws RemoteException{
        super();
        nicks = new HashSet<>();
        users = new HashMap<>();
        webService = new WSAccessImpl();
        File f = new File(path);
        try{
        	if(!f.mkdirs() && !f.exists())
        		System.out.println("The contents have no folder to be saved");
        }catch(Exception e){
        	e.printStackTrace();
        }
    }

    @Override
    public void modifyContent(String contentId,String description,String clientId) throws RemoteException {
        User user = users.get(clientId);
        try{
    		ContentInfo ci = webService.getContent(contentId);	
    		if(!ci.getOwner().equals(user.name)){
    			user.client.receiveMessage("You don't have permission to modify this content.");
    			return;
    		}
    		webService.modifyContent(contentId, description);
    		user.client.receiveMessage("The content " + contentId + " was modified correctly.");
            System.out.println("Content " + contentId + " has been modified.");
    	}  	
    	catch (IOException e) { 
    		user.client.receiveMessage("There has been a problem modifying the content.");
    		e.printStackTrace();
    	}        
    }

    @Override
    public DigitalContent getContent(String contentId,String clientId) throws RemoteException{
    	User user = users.get(clientId);
    	try{
    		ContentInfo ci = webService.getContent(contentId);	
    		return new DigitalContent(ci.getDescription(),getFileContent(ci.getPath()));
    	}  	
    	catch (IOException e) { 
    		user.client.receiveMessage("There has been a problem getting the file.");
    		e.printStackTrace();
    	}
    	return null;
    }
    
    

    @Override
    public void delete(String contentId,String clientId) throws RemoteException {
        User user = users.get(clientId);
        try{
    		ContentInfo ci = webService.getContent(contentId);	
    		if(!ci.getOwner().equals(user.name)){
    			user.client.receiveMessage("You don't have permission to delete this content.");
    			return;
    		}
    		webService.delete(contentId);
    		deleteFileContent(path + contentId + ".mp4");
    		user.client.receiveMessage("The content " + contentId + " was deleted correctly.");
            System.out.println("Content " + contentId + " has been deleted.");
    	}  	
    	catch (IOException e) { 
    		user.client.receiveMessage("There has been a problem deleting the file.");
    		e.printStackTrace();
    	}
    }

    @Override
    public String upload(DigitalContent d,String clientId) throws RemoteException {
    	User user = users.get(clientId);
    	try{	        
	        String key = UUID.randomUUID().toString().replaceAll("-", "");
	        String path = this.path + key;
	        ContentInfo ci = new ContentInfo();
	        ci.setDescription(d.getDescription());
	        ci.setPath(path + ".mp4"); 
	        ci.setOwner(user.name);
	        ci.setKey(key);
	        webService.upload(ci);
	        saveContent(d.getContent(), key);
	        user.client.receiveMessage("The content " + key + " has been uploaded: " + d.getDescription());
	        System.out.println("New content added: " + key + " is now on system.");
	        return key;
    	}
	
		catch (IOException e) { 
			user.client.receiveMessage("There has been a problem uploading the file.");
			e.printStackTrace();
		}
		return null;
    }
    
    @Override
    public void list(String owner, String clientId) throws RemoteException {
    	User user = users.get(clientId);
    	try{
    		Set<ContentInfo> contents = webService.list(owner);	
    		user.client.receiveMessage(printListOfContents(contents));
    	}  	
    	catch (IOException e) { 
    		user.client.receiveMessage("There has been a problem getting the file.");
    		e.printStackTrace();
    	}
    	
    }
    
    private String printListOfContents(Set<ContentInfo> results){
    	String resultsNumber = results.size() + " results found.\n";
		String header = "";
		for (int i = 0; i < 15; i++)
			header += " ";
		header += "key";
		for (int i = 0; i < 16; i++)
			header += " ";
		header += "|";
		String line = "";
		for (int i = 0; i < 34; i++)
			line += "-";
		line += "+";		
		String rs = "";
		int maxSize = 12;
		for (ContentInfo c : results){
			rs += " " + c.getKey() + " " + "|" + " " + c.getDescription() +"\n";
			if(c.getDescription().length() + 1 > maxSize){
				maxSize = c.getDescription().length() + 1;
			}
		}
		String d = "description";
		int spacesNum = (maxSize - d.length()) / 2;
		for (int i = 0; i < spacesNum; i++)
			header += " ";
		header+= d + "\n";
		for (int i = 0; i < 2 * spacesNum + d.length(); i++)
			line += "-";
		line += "\n";
		return resultsNumber + header + line + rs;
    }
    

    @Override
    public String registerClient(RMIClientInterface client, String name) throws RemoteException {
        User user = new User (name,client);
        String id = "";
        if(nicks.contains(user.name)){
            user.client.receiveMessage("User already in use. Try another nick.");
        }
        else{
            id = UUID.randomUUID().toString();
            users.put(id, user);
            nicks.add(user.name);
            user.client.receiveMessage("Correct registration.");
            System.out.println("Client " + id + " has registered the system.");
        }
        return id;
    }
    
    private void saveContent(byte [] digitalContent, String contentId){
        try {
            File nf = new File( path + contentId + ".mp4");
            FileOutputStream fw = new FileOutputStream(nf);
            fw.write(digitalContent);
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            System.err.println("There has been an error saving content"+ contentId +".");
        }
    }
        
    public class User{
        public String name;
        public RMIClientInterface client;

        public User(String name, RMIClientInterface client) {
            this.name = name;
            this.client = client;
        }
            
    }
    
    private static byte[] getFileContent(String path){            
        try{
            File file = new File(path);
            FileInputStream fin = new FileInputStream(file);
            byte [] b = new byte [(int)file.length()];
            fin.read(b);   
            fin.close();
            return b;
        }
        catch(IOException e){
            System.err.println("Error while handling the file");
        }
        return new byte[0];
    }
    
    private static boolean deleteFileContent(String path){            
        File file = new File(path);
        return file.delete();
    }

}
