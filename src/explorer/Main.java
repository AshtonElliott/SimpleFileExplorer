package explorer;

import explorer.error.ErrorHandler;
import explorer.file.FileController;
import explorer.gui.MainWindow;
import explorer.ui.Theme;

import javax.swing.SwingUtilities;

/** Entry point: sets up error handling first, then the theme, then builds the window. */
public class Main {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on"); // smooth text on Linux
        ErrorHandler.install();
        SwingUtilities.invokeLater(() -> {
            Theme.install();
            new MainWindow(new FileController()).show();
        });
    }
}
