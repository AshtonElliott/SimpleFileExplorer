package explorer.ui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/** Slim, modern scrollbar: rounded gray thumb, no arrow buttons. */
public class ThinScrollBarUI extends BasicScrollBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new ThinScrollBarUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        scrollbar.setPreferredSize(new Dimension(10, 10));
        scrollbar.setOpaque(false);
    }

    @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }

    @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }

    private static JButton zeroButton() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        return b;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        // transparent track
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        if (r.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g2 = Theme.smooth(g);
        g2.setColor(isDragging || isThumbRollover() ? new Color(0x9CA3AF) : new Color(0xC9CDD3));
        int pad = 2;
        g2.fillRoundRect(r.x + pad, r.y + pad, r.width - pad * 2, r.height - pad * 2, 8, 8);
        g2.dispose();
    }
}
