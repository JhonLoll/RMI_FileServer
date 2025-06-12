package main.java.rmi_fileserver;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class FileServer extends UnicastRemoteObject implements RMI {
    private static final String DIR = "files/";
    private final Map<String, String> users;

    public FileServer() throws RemoteException {
        super();
        users = loadUsers();
        new File(DIR).mkdirs();
    }

    private Map<String, String> loadUsers() {
        Map<String, String> map = new HashMap<>();
        map.put("admin", "admin");
        map.put("Danilo", "123456");
        map.put("Jhon", "123456");
        return map;
    }

    public boolean login(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    public List<String> listFiles() {
        File folder = new File(DIR);
        return Arrays.asList(Objects.requireNonNull(folder.list()));
    }

    public void uploadFile(String filename, byte[] data) throws RemoteException {
        try (FileOutputStream fos = new FileOutputStream(DIR + filename)) {
            fos.write(data);
        } catch (IOException e) {
            throw new RemoteException("Erro ao salvar arquivo", e);
        }
    }

    public byte[] downloadFile(String filename) throws RemoteException {
        try {
            return java.nio.file.Files.readAllBytes(new File(DIR + filename).toPath());
        } catch (IOException e) {
            throw new RemoteException("Erro ao ler arquivo", e);
        }
    }

    public void deleteFile(String filename) throws RemoteException {
        new File(DIR + filename).delete();
    }
}
