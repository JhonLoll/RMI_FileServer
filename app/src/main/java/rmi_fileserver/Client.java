package main.java.rmi_fileserver;

import java.awt.*;
import java.io.*;
import java.rmi.Naming;
import java.util.List;
import javax.swing.*;

public class Client extends JFrame {

    private RMI server;
    private JTextArea fileListArea;
    private String currentUser = null;
    private JPanel topPanel;
    private JButton loginButton;
    private JButton uploadButton;
    private JButton downloadButton;
    private JButton deleteButton;

    public Client() {
        try {
            server = (RMI) Naming.lookup("rmi://192.168.1.201:1099/FileServer");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro conectando ao servidor.");
            System.exit(1);
        }

        setTitle("Cliente RMI - Servidor de Arquivos");
        setSize(500, 400);
        setLayout(new BorderLayout());

        topPanel = new JPanel();
        loginButton = new JButton("Login");
        uploadButton = new JButton("Upload");
        downloadButton = new JButton("Download");
        deleteButton = new JButton("Delete");

        loginButton.addActionListener(e -> login());
        uploadButton.addActionListener(e -> upload());
        downloadButton.addActionListener(e -> download());
        deleteButton.addActionListener(e -> delete());
        topPanel.add(loginButton); // Adiciona apenas o botão de login inicialmente

        add(topPanel, BorderLayout.NORTH);

        fileListArea = new JTextArea();
        fileListArea.setEditable(false);
        add(new JScrollPane(fileListArea), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void login() {
        String user = JOptionPane.showInputDialog("Usuário:");
        String pass = JOptionPane.showInputDialog("Senha:");
        try {
            if (server.login(user, pass)) {
                currentUser = user;
                // Atualiza a interface para mostrar os botões de ação e remover o de login
                topPanel.remove(loginButton);
                topPanel.add(uploadButton);
                topPanel.add(downloadButton);
                topPanel.add(deleteButton);
                topPanel.revalidate();
                topPanel.repaint();

                atualizarListaArquivos();
                JOptionPane.showMessageDialog(this, "Login bem-sucedido como " + user + "!");
            } else {
                JOptionPane.showMessageDialog(this, "Login inválido.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizarListaArquivos() throws Exception {
        List<String> files = server.listFiles();
        fileListArea.setText(String.join("\n", files));
    }

    private void upload() {
        if (!isLoggedIn()) return;

        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
                server.uploadFile(file.getName(), data);
                atualizarListaArquivos();
                JOptionPane.showMessageDialog(this, "Arquivo enviado!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void download() {
        if (!isLoggedIn()) return;

        String filename = JOptionPane.showInputDialog("Nome do arquivo para download:");
        try {
            byte[] data = server.downloadFile(filename);
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(filename));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile())) {
                    fos.write(data);
                }
                JOptionPane.showMessageDialog(this, "Download completo!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void delete() {
        if (!isLoggedIn()) return;

        String filename = JOptionPane.showInputDialog("Nome do arquivo para deletar:");
        try {
            server.deleteFile(filename);
            atualizarListaArquivos();
            JOptionPane.showMessageDialog(this, "Arquivo deletado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isLoggedIn() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Você precisa fazer login primeiro.");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        new Client();
    }
}
