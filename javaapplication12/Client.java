
package javaapplication12;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameClientGUI gui = new GameClientGUI();
            gui.setVisible(true);
        });
    }
}
