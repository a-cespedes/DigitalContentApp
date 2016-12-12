package RMIApp;


import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.util.Map.Entry;





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
    		if(ci == null){
    			user.client.receiveMessage("There is no content with key: " + contentId);
    			return;
    		}
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
    		if(ci == null){
    			user.client.receiveMessage("There is no content with key: " + contentId);
    		}
    		else{
    			user.client.receiveMessage("The content :" + contentId +  "has description:" + ci.getDescription());
    			return new DigitalContent(ci.getDescription(),getFileContent(ci.getPath()));
    		}
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
    		if(ci == null){
    			user.client.receiveMessage("There is no content with key: " + contentId);
    			return;
    		}
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
    		user.client.receiveMessage(printListOfContents(contents,false));
    	}  	
    	catch (IOException e) { 
    		user.client.receiveMessage("There has been a problem getting the list of contents.");
    		e.printStackTrace();
    	}
    	
    }
    
    @Override
    public void search(String word, String clientId) throws RemoteException {
    	User user = users.get(clientId);
    	try{
    		Set<ContentInfo> contents = webService.search(word);	
    		user.client.receiveMessage(printListOfContents(contents,true));
    	}  	
    	catch (IOException e) { 
    		user.client.receiveMessage("There has been a problem getting content from the search.");
    		e.printStackTrace();
    	}
    	
    }
    
    private String printListOfContents(Set<ContentInfo> results,boolean owner){
    	String resultsNumber = results.size() + " results found.\n\n";
    	if (results.size() == 0){
    		return resultsNumber;
    	}
    	String rs ="";
		for (ContentInfo c : results){
			rs += "key: " + c.getKey() + "\n" + "description: " + c.getDescription() + "\n";
			if (owner){
				rs += "owner:" + c.getOwner() + "\n";
			}
			rs += "\n";
		}
		return resultsNumber + rs;
    }
    

    @Override
    public String registerClient(RMIClientInterface client, String name) throws RemoteException {
        User user = new User(name,client);
        String id = UUID.randomUUID().toString();
        if(nicks.contains(user.name)){
        	for(Entry<String,User> row : users.entrySet()){
        		if(row.getValue().name.equals(user.name)){
        			id = row.getKey();       			
        			user.connect();
        			users.put(id, user);
        			user.client.receiveMessage("Weclome back: " + user.name);   
        			System.out.println("Client " + id + " has log in in the system.");
        		}
        		
        	}
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
    
    @Override
    public void logout(RMIClientInterface client,String clientId) throws RemoteException {
    	User user = users.get(clientId);
    	user.disconnect();
    	users.put(clientId, user);
    	
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
        public boolean connected;

        public User(String name, RMIClientInterface client) {
            this.name = name;
            this.client = client;
        }
        public void connect(){
        	this.connected = true;
        }
        public void disconnect(){
        	this.connected = false;
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
