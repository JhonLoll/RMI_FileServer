package main.java.rmi_fileserver;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            RMI server = new FileServer();
            Naming.rebind("FileServer", server);
            System.out.println("Servidor de arquivos RMI iniciado com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
