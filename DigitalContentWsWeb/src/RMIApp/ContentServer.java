package RMIApp;

import java.net.MalformedURLException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.Naming;


public class ContentServer{

        public static void main(String args[]) throws MalformedURLException{
                if(args.length != 2){
                    System.out.println("Correct call: java ContentServer host port");
                    System.exit(-1);
                }
                int portNum = Integer.parseInt(args[1]);
                String host = args[0];
                try{
                    RMIServerImpl exportedObj = new RMIServerImpl();
                    startRegistry(portNum);
                    String registryURL = "rmi://"+ host +":" + portNum + "/content";
                    Naming.rebind(registryURL,exportedObj);
                    System.out.println("ContentServer is ready.");
                }catch(RemoteException ex) {
                    System.out.println("Remote Exception error.");
                }
        }

        private static void startRegistry(int RMIPortNum) throws RemoteException{
                try {
                        Registry registry = LocateRegistry.getRegistry(RMIPortNum);
                        registry.list();
                }
                catch(RemoteException ex) {
                        Registry registry = LocateRegistry.createRegistry(RMIPortNum);
                        System.out.println("RMI registry created at port " + RMIPortNum);
                }
        }
}
