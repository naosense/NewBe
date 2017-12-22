package com.pingao.ui;

import com.pingao.core.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by pingao on 17-12-16.
 */
public class BackGroundPanel extends JPanel {

    private static final long serialVersionUID = -6870656490608404947L;
    private static final Image img = readImg(BackGroundPanel.class.getClassLoader().getResourceAsStream("board.jpg"));
    private final List<MarkerPanel> markers;
    private int lastClicked = -1;

    public BackGroundPanel() {
        this.markers = new ArrayList<>();
        setLayout(new GridLayout(Board.N_ROW, Board.N_COL));
        for (int i = 0; i < Board.N_ROW * Board.N_COL; i++) {
            MarkerPanel marker = new MarkerPanel(this, i);
            add(marker);
            this.markers.add(marker);
        }
    }

    private static Image readImg(InputStream in) {
        Image img = null;
        try {
            img = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }

    public void mark(int index, int player, boolean isLast) {
        this.markers.get(index).mark(player, isLast);
    }

    public int getLastClicked() {
        return this.lastClicked;
    }

    public void setClickedMarker(int index) {
        this.lastClicked = index;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i <= Board.N_ROW; i++) {//画横线
            g.drawLine(0, MarkerPanel.WIDTH / 2 + i * MarkerPanel.WIDTH, MarkerPanel.WIDTH * Board.N_COL, MarkerPanel.WIDTH / 2 + i * MarkerPanel.WIDTH);
        }
        for (int i = 0; i <= Board.N_COL; i++) {//画竖线
            g.drawLine(MarkerPanel.WIDTH / 2 + i * MarkerPanel.WIDTH, 0, MarkerPanel.WIDTH / 2 + i * MarkerPanel.WIDTH, MarkerPanel.WIDTH * Board.N_ROW);
        }
    }
}
