package tiendagama.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Utilidades UI compartidas: look & feel, tamaños y helpers de componentes.
 */
public final class UIUtils {

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Dimension BTN_SIZE = new Dimension(120, 34);
    public static final Dimension FIELD_SIZE = new Dimension(260, 28);
    public static final Dimension SMALL_BTN_SIZE = new Dimension(90, 26);

    private UIUtils() {}

    public static void applyAppStyle() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        UIManager.put("Button.font", REGULAR_FONT);
        UIManager.put("Label.font", REGULAR_FONT);
        UIManager.put("TextField.font", REGULAR_FONT);
        UIManager.put("TextArea.font", REGULAR_FONT);
        UIManager.put("Table.font", REGULAR_FONT);
        UIManager.put("Table.rowHeight", 26);
    }

    public static JButton makePrimary(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(BTN_SIZE);
        b.setFocusPainted(false);
        b.setFont(REGULAR_FONT.deriveFont(Font.BOLD));
        return b;
    }

    public static JButton makeSecondary(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(BTN_SIZE);
        b.setFocusPainted(false);
        b.setFont(REGULAR_FONT);
        return b;
    }

    public static JButton makeSmall(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(SMALL_BTN_SIZE);
        b.setFocusPainted(false);
        b.setFont(REGULAR_FONT.deriveFont(12f));
        return b;
    }

    public static JTextField sizedField() {
        JTextField f = new JTextField();
        f.setPreferredSize(FIELD_SIZE);
        return f;
    }

    public static JLabel makeTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(TITLE_FONT);
        return l;
    }
}
