package app;

import control.Controller3D;
import view.Window;
import javax.swing.*;
import java.io.IOException;

public class AppStart {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Window window = new Window();
            try {
                new Controller3D(window.getPanel());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            window.setVisible(true);
        });
        // https://www.google.com/search?q=SwingUtilities.invokeLater
        // https://www.javamex.com/tutorials/threads/invokelater.shtml
    }
}
