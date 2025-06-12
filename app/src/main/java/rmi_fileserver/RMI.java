package main.java.rmi_fileserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMI extends Remote {
    boolean login(String username, String password) throws RemoteException;
    List<String> listFiles() throws RemoteException;
    void uploadFile(String filename, byte[] data) throws RemoteException;
    byte[] downloadFile(String filename) throws RemoteException;
    void deleteFile(String filename) throws RemoteException;
}