package arenaforge;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * Halaman Player: daftar player (JList) dengan tombol tambah dan hapus.
 * Tombol tambah membuka PlayerFormDialog.
 */
public class PlayerPanel extends JPanel {
    private final MainFrame frame;
    private final JList<Player> listPlayer = new JList<>(DataSementara.PLAYER);
    private final JButton btnHapus = new JButton("Hapus");
    private final JLabel lblJumlah = new JLabel();

    public PlayerPanel(MainFrame frame) {
        super(new BorderLayout(0, 12));
        this.frame = frame;
        setBorder(Tema.padding(20, 20, 20, 20));

        JButton btnTambah = new JButton("Tambah Player");
        btnTambah.addActionListener(e -> tambahPlayer());
        btnHapus.setEnabled(false);
        btnHapus.addActionListener(e -> hapusPlayer());

        // FlowLayout: tombol berjajar di kanan atas
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.add(btnHapus);
        tombol.add(btnTambah);

        JPanel atas = new JPanel(new BorderLayout());
        atas.add(Tema.label("Player", Font.BOLD, 20f), BorderLayout.WEST);
        atas.add(tombol, BorderLayout.EAST);

        listPlayer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Event: tombol Hapus hanya aktif kalau ada player yang dipilih
        listPlayer.addListSelectionListener(e -> btnHapus.setEnabled(!listPlayer.isSelectionEmpty()));

        add(atas, BorderLayout.NORTH);
        add(new JScrollPane(listPlayer), BorderLayout.CENTER);
        add(lblJumlah, BorderLayout.SOUTH);
        perbaruiJumlah();
    }

    private void tambahPlayer() {
        Player player = new PlayerFormDialog(frame).tampilkan();
        if (player != null) {
            DataSementara.PLAYER.addElement(player);
            perbaruiJumlah();
            frame.catatAktivitas("Player " + player.getNickname() + " (" + player.getId()
                    + ") terdaftar di " + player.getGame() + " sebagai " + player.getRole());
        }
    }

    private void hapusPlayer() {
        Player player = listPlayer.getSelectedValue();
        int pilihan = JOptionPane.showConfirmDialog(frame, "Hapus player " + player.getNickname() + "?",
                "Hapus Player", JOptionPane.YES_NO_OPTION);
        if (pilihan == JOptionPane.YES_OPTION) {
            DataSementara.PLAYER.removeElement(player);
            perbaruiJumlah();
            frame.catatAktivitas("Player " + player.getNickname() + " (" + player.getId() + ") dihapus");
        }
    }

    private void perbaruiJumlah() {
        int jumlah = DataSementara.PLAYER.getSize();
        lblJumlah.setText(jumlah == 0
                ? "Belum ada player. Klik \"Tambah Player\" untuk mendaftarkan."
                : "Jumlah player: " + jumlah);
    }
}
