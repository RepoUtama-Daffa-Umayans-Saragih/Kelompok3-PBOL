package arenaforge.user;

import arenaforge.Main;
import arenaforge.Tema;
import arenaforge.ui.Pesan;
import arenaforge.ui.Tombol;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Jendela untuk peran Player: header di atas, lalu PortalPlayer sebagai isinya. Semuanya Swing. */
public class UserFrame extends JFrame {

    public UserFrame() {
        super("ArenaForge - Portal Player");

        // Event: minta konfirmasi sebelum jendela ditutup
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                konfirmasiTutup();
            }
        });

        getContentPane().setBackground(Tema.HITAM);
        add(buatHeader(), BorderLayout.NORTH);
        add(new PortalPlayer(DataPortal.NAMA_PLAYER, DataPortal.turnamenDiikuti()), BorderLayout.CENTER);

        setSize(1120, 760);
        setMinimumSize(new Dimension(920, 600));
        setLocationRelativeTo(null);
    }

    private JPanel buatHeader() {
        JLabel lblPlayer = Tema.label("Masuk sebagai: " + DataPortal.NAMA_PLAYER + " (" + DataPortal.ID_PLAYER + ")",
                java.awt.Font.PLAIN, 13f);
        Tombol btnKeluarAkun = new Tombol("Keluar Akun", Tombol.Jenis.KEDUA);
        btnKeluarAkun.addActionListener(e -> keluarAkun());

        JPanel kanan = new JPanel();
        kanan.setLayout(new BoxLayout(kanan, BoxLayout.X_AXIS));
        kanan.setOpaque(false);
        kanan.add(lblPlayer);
        kanan.add(Box.createHorizontalStrut(12));
        kanan.add(btnKeluarAkun);

        JPanel header = Tema.header("ArenaForge", "Portal Player");
        header.add(kanan, BorderLayout.EAST);
        return header;
    }

    private void keluarAkun() {
        if (Pesan.konfirmasi(this, "Keluar Akun", "Keluar dari akun player?")) {
            dispose();
            Main.mulai();
        }
    }

    private void konfirmasiTutup() {
        if (Pesan.konfirmasi(this, "Tutup Aplikasi", "Tutup aplikasi ArenaForge?")) {
            System.exit(0);
        }
    }
}
