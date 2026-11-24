package arenaforge.ui;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/** Panel yang lebarnya mengikuti JScrollPane, jadi hanya tinggi yang bisa digulir. */
public class PanelGulir extends JPanel implements Scrollable {

    public PanelGulir(LayoutManager layout) {
        super(layout);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle tampak, int arah, int jurusan) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle tampak, int arah, int jurusan) {
        return Math.max(16, tampak.height - 32);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
