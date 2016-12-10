package RMIApp;


import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;



public class ContentClient{
	private static final String path = System.getProperty("user.home") 
			+ File.separator + "rmiapp" + File.separator + "cache" + File.separator;
        public static void main(String args[]){
            DigitalContent d;
            byte [] b;
            String key;
            File f = new File(path);
            try{
            	if(!f.mkdirs() && !f.exists())
            		System.out.println("The contents have no folder to be saved");
            }catch(Exception e){
            	e.printStackTrace();
            }
            if(args.length != 2){
                System.out.println("Correct call: java ContentClient host port");
                System.exit(-1);
            }
            int portNum = Integer.parseInt(args[1]);
            String host = args[0];
            try {
                System.out.println("Welcome to Digital Content Application");
                String registryURL = "rmi://"+ host + ":" + portNum + "/content";
                RMIServerInterface h = (RMIServerInterface) Naming.lookup(registryURL); 
                RMIClientInterface c = (RMIClientInterface) new RMIClientImpl();
                Scanner scn = new Scanner (System.in);
                String clientId = "";
                do{
                    System.out.println("Enter the nickname you want to use:");
                    String name = scn.nextLine();
                    clientId = h.registerClient(c, name);
                }while ("".equals(clientId));
                while(true){
                    showMenu();
                    String msg = scn.nextLine();
                    switch (msg){
                        case "u":
                            System.out.println("Enter description:");
                            String description = scn.nextLine();
                            System.out.println("Enter content path:");
                            b = getContent(scn.nextLine());
                            d = new DigitalContent(description, b);
                            h.upload(d, clientId);
                            break;
                        case "m":
                            System.out.println("Enter key:");
                            key = scn.nextLine();
                            System.out.println("Enter description:");
                            description = scn.nextLine();
                            h.modifyContent(key, description, clientId);
                            break;
                        case "g":
                            System.out.println("Enter key :");
                            key = scn.nextLine();
                            d = h.getContent(key,clientId);
                            if(d != null){
                            	saveContent(d.getContent(),key);
                            }
                            break;
                        case "d":
                            System.out.println("Enter key :");
                            h.delete(scn.nextLine(),clientId);
                            break;
                        case "q":
                        	scn.close();
                            System.exit(0);
                    }
                }

            }catch(NotBoundException | MalformedURLException | RemoteException e){
                 System.out.println("Error with the connection to the server.");
                 System.exit(-2);
            }
            
        }
        private static void showMenu(){
            System.out.println("Menu:");
            System.out.println("u:Upload content");
            System.out.println("m:Modify content");
            System.out.println("d:Delete content");
            System.out.println("g:Get content");
            System.out.println("q:Quit application");
        }
        
        private static byte[] getContent(String path){            
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
        
        private static void saveContent(byte [] digitalContent, String contentId){
            try {
                File nf = new File( path + contentId + ".mp4");
                FileOutputStream fw = new FileOutputStream(nf);
                fw.write(digitalContent);
                fw.flush();
                fw.close();
            } catch (IOException ex) {
                System.err.println("There has been an error saving content "+  contentId +".");
            }
        }
        
}
