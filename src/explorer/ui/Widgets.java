package explorer.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Function;

/** Reusable styled components. */
public final class Widgets {

    private Widgets() {}

    // ================= buttons =================

    /** Rounded flat button. Primary = filled blue; otherwise light with a border. */
    public static class FlatButton extends JButton {
        private final boolean primary;

        public FlatButton(String text, Icon icon, boolean primary) {
            super(text, icon);
            this.primary = primary;
            setFont(Theme.FONT_BOLD);
            setForeground(primary ? Color.WHITE : Theme.TEXT);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setIconTextGap(6);
            setBorder(BorderFactory.createEmptyBorder(7, text == null || text.isEmpty() ? 9 : 14,
                    7, text == null || text.isEmpty() ? 9 : 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Theme.smooth(g);
            ButtonModel m = getModel();
            int w = getWidth(), h = getHeight(), arc = 10;
            if (primary) {
                Color fill = !isEnabled() ? new Color(0x93B4F5)
                        : m.isPressed() ? Theme.ACCENT_DARK.darker()
                        : m.isRollover() ? Theme.ACCENT_DARK : Theme.ACCENT;
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, w, h, arc, arc);
            } else {
                g2.setColor(m.isPressed() ? Theme.BORDER : m.isRollover() && isEnabled() ? Theme.HOVER : Theme.BACKGROUND);
                g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
            }
            g2.dispose();
            setForeground(primary ? Color.WHITE : isEnabled() ? Theme.TEXT : new Color(0xB0B5BC));
            super.paintComponent(g);
        }
    }

    // ================= text field =================

    /** Rounded text field with a leading icon, placeholder text and a blue focus ring. */
    public static class SearchField extends JTextField {
        private final String placeholder;
        private final Icon icon = Icons.search(16, Theme.MUTED);

        public SearchField(String placeholder, int columns) {
            super(columns);
            this.placeholder = placeholder;
            setOpaque(false);
            setForeground(Theme.TEXT);
            setCaretColor(Theme.TEXT);
            setSelectionColor(Theme.SELECTION);
            setBorder(new RoundBorder(this));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { repaint(); }

                @Override public void focusLost(FocusEvent e) { repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Theme.smooth(g);
            g2.setColor(Theme.BACKGROUND);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            icon.paintIcon(this, g2, 10, (getHeight() - 16) / 2);
            g2.dispose();
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Graphics2D t = Theme.smooth(g);
                t.setColor(new Color(0x9CA3AF));
                t.setFont(getFont());
                Insets in = getInsets();
                FontMetrics fm = t.getFontMetrics();
                t.drawString(placeholder, in.left, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                t.dispose();
            }
        }

        private static class RoundBorder extends AbstractBorder {
            private final JComponent owner;

            RoundBorder(JComponent owner) { this.owner = owner; }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                Graphics2D g2 = Theme.smooth(g);
                boolean focused = owner.hasFocus();
                g2.setColor(focused ? Theme.ACCENT : Theme.BORDER);
                g2.setStroke(new BasicStroke(focused ? 1.6f : 1f));
                g2.drawRoundRect(x, y, w - 1, h - 1, 10, 10);
                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(7, 34, 7, 10); // left leaves room for the icon
            }
        }
    }

    // ================= labels =================

    /** Small uppercase gray heading, e.g. "RECENT". */
    public static JLabel sectionHeader(String text, Icon icon) {
        JLabel l = new JLabel(text.toUpperCase(), icon, SwingConstants.LEFT);
        l.setFont(Theme.FONT_SMALL_BOLD);
        l.setForeground(Theme.MUTED);
        l.setIconTextGap(6);
        l.setBorder(BorderFactory.createEmptyBorder(14, 12, 6, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(0x9CA3AF));
        l.setBorder(BorderFactory.createEmptyBorder(2, 16, 4, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // ================= lists =================

    /**
     * Styles a sidebar list: icon + text rows, roomy padding, hover highlight,
     * rounded selection, transparent background.
     */
    public static <T> void styleList(JList<T> list, Function<T, Icon> icon,
                                     Function<T, String> text, Function<T, String> tooltip) {
        final String HOVER_KEY = "hoverIndex";
        list.putClientProperty(HOVER_KEY, -1);
        list.setOpaque(false);
        list.setFixedCellHeight(30);
        list.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        list.setAlignmentX(Component.LEFT_ALIGNMENT);

        list.setCellRenderer(new ListCellRenderer<T>() {
            private final JLabel label = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Color bg = (Color) getClientProperty("bg");
                    if (bg != null) {
                        Graphics2D g2 = Theme.smooth(g);
                        g2.setColor(bg);
                        g2.fillRoundRect(6, 2, getWidth() - 12, getHeight() - 4, 8, 8);
                        g2.dispose();
                    }
                    super.paintComponent(g);
                }
            };

            {
                label.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 10));
                label.setIconTextGap(8);
            }

            @Override
            public Component getListCellRendererComponent(JList<? extends T> l, T value, int index,
                                                          boolean selected, boolean focus) {
                boolean hover = index == (Integer) l.getClientProperty(HOVER_KEY);
                label.putClientProperty("bg", selected ? Theme.SELECTION : hover ? Theme.HOVER : null);
                label.setIcon(icon.apply(value));
                label.setText(text.apply(value));
                label.setToolTipText(tooltip.apply(value));
                label.setForeground(Theme.TEXT);
                return label;
            }
        });

        MouseAdapter hover = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int i = list.locationToIndex(e.getPoint());
                if (i >= 0 && !list.getCellBounds(i, i).contains(e.getPoint())) i = -1;
                if (!Integer.valueOf(i).equals(list.getClientProperty(HOVER_KEY))) {
                    list.putClientProperty(HOVER_KEY, i);
                    list.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                list.putClientProperty(HOVER_KEY, -1);
                list.repaint();
            }
        };
        list.addMouseListener(hover);
        list.addMouseMotionListener(hover);
    }

    // ================= misc =================

    public static JComponent withPadding(JComponent c, int top, int left, int bottom, int right) {
        c.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        return c;
    }
}
