package explorer.ui;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * One place for the app's look: colors, fonts and global Swing defaults.
 * Shared by the gui and sidebar modules (this package has no app logic).
 *
 * Uses Swing's built-in cross-platform look and feel and restyles it as a flat,
 * light theme, so the app looks the same on Windows, macOS and Linux and needs
 * no extra libraries.
 */
public final class Theme {

    // ---- colors ----
    public static final Color BACKGROUND   = new Color(0xFFFFFF);
    public static final Color SIDEBAR      = new Color(0xF6F7F9);
    public static final Color BORDER       = new Color(0xE3E5E8);
    public static final Color TEXT         = new Color(0x1F2328);
    public static final Color MUTED        = new Color(0x6B7280);
    public static final Color ACCENT       = new Color(0x2563EB);
    public static final Color ACCENT_DARK  = new Color(0x1D4ED8);
    public static final Color SELECTION    = new Color(0xDBEAFE);
    public static final Color HOVER        = new Color(0xEEF2F7);
    public static final Color ROW_STRIPE   = new Color(0xFAFBFC);
    public static final Color ERROR        = new Color(0xB42318);
    public static final Color WARNING      = new Color(0xB54708);
    public static final Color FOLDER       = new Color(0xF2B33D);

    // ---- fonts ----
    public static final Font FONT;
    public static final Font FONT_BOLD;
    public static final Font FONT_SMALL_BOLD;

    static {
        String family = pickFontFamily("Segoe UI", "Helvetica Neue", "Inter", "Noto Sans",
                "Fira Sans", "DejaVu Sans");
        FONT = new Font(family, Font.PLAIN, 13);
        FONT_BOLD = FONT.deriveFont(Font.BOLD);
        FONT_SMALL_BOLD = FONT.deriveFont(Font.BOLD, 11f);
    }

    private Theme() {}

    /** Call once, on the Swing thread, before creating any component. */
    public static void install() {
        try {
            MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException ignored) {
            // keep whatever is active; the styling below still applies
        }

        // Same font everywhere
        FontUIResource font = new FontUIResource(FONT);
        for (Object key : UIManager.getLookAndFeelDefaults().keySet().toArray()) {
            if (key.toString().endsWith(".font")) UIManager.put(key, font);
        }

        put("Panel.background", BACKGROUND);
        put("Viewport.background", BACKGROUND);
        put("CheckBox.background", BACKGROUND);
        put("Label.foreground", TEXT);
        put("Table.selectionBackground", SELECTION);
        put("Table.selectionForeground", TEXT);
        put("List.selectionBackground", SELECTION);
        put("List.selectionForeground", TEXT);
        put("ToolTip.background", new Color(0x111827));
        put("ToolTip.foreground", Color.WHITE);
        put("OptionPane.background", BACKGROUND);
        put("ProgressBar.foreground", ACCENT);
        put("ProgressBar.background", BORDER);
        UIManager.put("ToolTip.border", BorderFactory.createEmptyBorder(4, 8, 4, 8));
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
        UIManager.put("List.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
        UIManager.put("ProgressBar.border", BorderFactory.createEmptyBorder());
        UIManager.put("Button.gradient", null);
        UIManager.put("CheckBox.focus", BACKGROUND);
        UIManager.put("ScrollBarUI", ThinScrollBarUI.class.getName());
    }

    private static void put(String key, Color c) {
        UIManager.put(key, new ColorUIResource(c));
    }

    private static String pickFontFamily(String... preferred) {
        List<String> installed = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String f : preferred) {
            if (installed.contains(f)) return f;
        }
        return Font.SANS_SERIF;
    }

    /** Turns on smooth edges for custom painting. */
    public static Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }
}
