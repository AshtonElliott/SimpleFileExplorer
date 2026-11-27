package explorer.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Small vector icons drawn in code, so they look sharp at any screen scaling
 * and need no image files.
 */
public final class Icons {

    private Icons() {}

    /** Draws into a size x size box. */
    private interface Painter {
        void paint(Graphics2D g, int s);
    }

    private static Icon icon(int size, Painter p) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = Theme.smooth(g);
                g2.translate(x, y);
                p.paint(g2, size);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }

            @Override public int getIconHeight() { return size; }
        };
    }

    // ---------------- file system ----------------

    public static Icon folder(int s) {
        return icon(s, (g, n) -> {
            double u = n / 16.0;
            g.setColor(Theme.FOLDER.darker());
            g.fill(new RoundRectangle2D.Double(1 * u, 2.5 * u, 6.5 * u, 4 * u, 2 * u, 2 * u));
            g.setColor(Theme.FOLDER);
            g.fill(new RoundRectangle2D.Double(1 * u, 4 * u, 14 * u, 10 * u, 2.5 * u, 2.5 * u));
        });
    }

    public static Icon file(int s, Color badge) {
        return icon(s, (g, n) -> {
            double u = n / 16.0;
            Path2D page = new Path2D.Double();
            page.moveTo(3 * u, 1 * u);
            page.lineTo(10 * u, 1 * u);
            page.lineTo(13.5 * u, 4.5 * u);
            page.lineTo(13.5 * u, 15 * u);
            page.lineTo(3 * u, 15 * u);
            page.closePath();
            g.setColor(Color.WHITE);
            g.fill(page);
            g.setColor(new Color(0xA0A6AF));
            g.setStroke(new BasicStroke((float) (1.1 * u)));
            g.draw(page);
            g.drawLine((int) (10 * u), (int) (1 * u), (int) (10 * u), (int) (4.5 * u));
            g.drawLine((int) (10 * u), (int) (4.5 * u), (int) (13.5 * u), (int) (4.5 * u));
            g.setColor(badge);
            g.fill(new RoundRectangle2D.Double(1.5 * u, 9 * u, 9 * u, 4.5 * u, 1.5 * u, 1.5 * u));
        });
    }

    public static Icon drive(int s) {
        return icon(s, (g, n) -> {
            double u = n / 16.0;
            g.setColor(new Color(0x6B7280));
            g.fill(new RoundRectangle2D.Double(1 * u, 4.5 * u, 14 * u, 7 * u, 2.5 * u, 2.5 * u));
            g.setColor(new Color(0x34D399));
            g.fillOval((int) (11 * u), (int) (7 * u), (int) Math.max(2, 2 * u), (int) Math.max(2, 2 * u));
        });
    }

    // ---------------- interface ----------------

    public static Icon search(int s, Color color) {
        return icon(s, (g, n) -> {
            double u = n / 16.0;
            g.setColor(color);
            g.setStroke(new BasicStroke((float) (1.7 * u), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawOval((int) (2 * u), (int) (2 * u), (int) (9 * u), (int) (9 * u));
            g.drawLine((int) (10 * u), (int) (10 * u), (int) (14 * u), (int) (14 * u));
        });
    }

    public static Icon arrowUp(int s, Color color) {
        return icon(s, (g, n) -> {
            double u = n / 16.0;
            g.setColor(color);
            g.setStroke(new BasicStroke((float) (1.8 * u), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine((int) (8 * u), (int) (13 * u), (int) (8 * u), (int) (3 * u));
            g.drawLine((int) (3.5 * u), (int) (7.5 * u), (int) (8 * u), (int) (3 * u));
            g.drawLine((int) (12.5 * u), (int) (7.5 * u), (int) (8 * u), (int) (3 * u));
        });
    }

    public static Icon stop(int s, Color color) {
        return icon(s, (g, n) -> {
            double u = n / 16.0;
            g.setColor(color);
            g.fill(new RoundRectangle2D.Double(3.5 * u, 3.5 * u, 9 * u, 9 * u, 2 * u, 2 * u));
        });
    }

    public static Icon clock(int s, Color color) {
        return icon(s, (g, n) -> {
            double u = n / 16.0;
            g.setColor(color);
            g.setStroke(new BasicStroke((float) (1.5 * u), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawOval((int) (1.5 * u), (int) (1.5 * u), (int) (13 * u), (int) (13 * u));
            g.drawLine((int) (8 * u), (int) (4.5 * u), (int) (8 * u), (int) (8 * u));
            g.drawLine((int) (8 * u), (int) (8 * u), (int) (10.5 * u), (int) (9.5 * u));
        });
    }

    /** Filled circle with "!" — for errors and warnings in the status bar. */
    public static Icon alert(int s, Color color) {
        return icon(s, (g, n) -> {
            double u = n / 16.0;
            g.setColor(color);
            g.fillOval(0, 0, n, n);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke((float) (1.8 * u), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine((int) (8 * u), (int) (4 * u), (int) (8 * u), (int) (8.5 * u));
            g.fillOval((int) (7 * u), (int) (10.5 * u), (int) Math.max(2, 2 * u), (int) Math.max(2, 2 * u));
        });
    }

    public static Icon sortArrow(boolean ascending) {
        return icon(8, (g, n) -> {
            g.setColor(Theme.MUTED);
            Path2D t = new Path2D.Double();
            if (ascending) {
                t.moveTo(0, 6); t.lineTo(8, 6); t.lineTo(4, 1);
            } else {
                t.moveTo(0, 2); t.lineTo(8, 2); t.lineTo(4, 7);
            }
            t.closePath();
            g.fill(t);
        });
    }

    /** Window/taskbar icon. */
    public static Image appImage() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = Theme.smooth(img.getGraphics());
        folder(64).paintIcon(null, g, 0, 0);
        search(30, Theme.ACCENT_DARK).paintIcon(null, g, 30, 30);
        g.dispose();
        return img;
    }

    // ---------------- type -> color ----------------

    private static final Map<Color, Set<String>> CATEGORIES = Map.of(
            new Color(0xDC2626), Set.of("PDF"),
            new Color(0x2563EB), Set.of("DOC", "DOCX", "TXT", "MD", "RTF", "ODT", "PPT", "PPTX"),
            new Color(0x16A34A), Set.of("XLS", "XLSX", "CSV", "ODS", "PNG", "JPG", "JPEG", "GIF",
                    "BMP", "SVG", "WEBP"),
            new Color(0x7C3AED), Set.of("ZIP", "RAR", "7Z", "TAR", "GZ", "JAR"),
            new Color(0xEA580C), Set.of("JAVA", "PY", "JS", "TS", "C", "CPP", "H", "CS", "HTML",
                    "CSS", "JSON", "XML", "PS1", "SH", "CLASS"),
            new Color(0xDB2777), Set.of("MP3", "WAV", "FLAC", "MP4", "MOV", "MKV", "AVI"));

    /** Color badge for a file based on its type text, e.g. "PDF file". */
    public static Color colorForType(String type) {
        String ext = type == null ? "" : type.replace(" file", "").toUpperCase(Locale.ROOT);
        for (Map.Entry<Color, Set<String>> e : CATEGORIES.entrySet()) {
            if (e.getValue().contains(ext)) return e.getKey();
        }
        return new Color(0x9CA3AF);
    }

    private static final Map<Color, Icon> FILE_ICON_CACHE = new java.util.concurrent.ConcurrentHashMap<>();
    private static final Icon FOLDER_16 = folder(16);

    /** Cached 16px icon for a table/list row. */
    public static Icon forEntry(boolean isFolder, String type) {
        if (isFolder) return FOLDER_16;
        return FILE_ICON_CACHE.computeIfAbsent(colorForType(type), c -> file(16, c));
    }
}
