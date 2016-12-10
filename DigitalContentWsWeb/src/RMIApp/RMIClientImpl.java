package RMIApp;


import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;


public class RMIClientImpl extends UnicastRemoteObject implements RMIClientInterface{
        
        public RMIClientImpl() throws RemoteException{
            super();
        }
        public void receiveMessage(String msg) throws RemoteException{
            System.out.println(msg);
        }

}
