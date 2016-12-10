package RMIApp;


import java.rmi.*;

public interface RMIServerInterface extends Remote {
    public String upload(DigitalContent d,String clientId) throws RemoteException ;
    public void modifyContent(String contentId,String description,String clientId) throws RemoteException;
    public DigitalContent getContent(String contentId,String clientId) throws RemoteException;
    public void delete(String contentId,String clientId) throws RemoteException ;
    public String registerClient(RMIClientInterface client, String name) throws RemoteException;
}
