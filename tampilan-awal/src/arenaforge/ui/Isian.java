package arenaforge.ui;

import arenaforge.Tema;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

/** Pabrik kolom isian bertema gelap. Bingkai marun, berubah merah saat sedang diisi. */
public final class Isian {
    private Isian() {
    }

    public static JTextField teks(int kolom) {
        return gayai(new JTextField(kolom));
    }

    public static JPasswordField sandi(int kolom) {
        return gayai(new JPasswordField(kolom));
    }

    public static <T extends JTextComponent> T gayai(T isian) {
        isian.setBackground(Tema.PERMUKAAN);
        isian.setForeground(Tema.TEKS);
        isian.setCaretColor(Tema.TEKS);
        isian.setSelectionColor(Tema.MARUN);
        isian.setSelectedTextColor(Tema.TEKS);
        isian.setFont(Tema.font(java.awt.Font.PLAIN, 14f));
        isian.setDisabledTextColor(Tema.TEKS_TBD);
        isian.setBorder(bingkai(Tema.MARUN));
        isian.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isian.setBorder(bingkai(Tema.MERAH));
            }

            @Override
            public void focusLost(FocusEvent e) {
                isian.setBorder(bingkai(Tema.MARUN));
            }
        });
        return isian;
    }

    private static Border bingkai(Color warna) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(warna),
                BorderFactory.createEmptyBorder(6, 8, 6, 8));
    }
}
