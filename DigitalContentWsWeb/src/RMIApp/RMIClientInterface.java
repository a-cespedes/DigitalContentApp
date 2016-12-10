package RMIApp;


import java.rmi.*;

public interface RMIClientInterface extends Remote {
    public void receiveMessage(String msg) throws RemoteException;
}
