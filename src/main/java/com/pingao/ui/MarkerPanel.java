package com.pingao.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;


/**
 * Created by pingao on 17-12-16.
 */
public class MarkerPanel extends JPanel {

    private static final long serialVersionUID = 3198732980627375377L;
    public static final int WIDTH = 50;

    private final BackGroundPanel background;
    private final int index;
    private boolean isLast;
    private int player;

    public MarkerPanel(BackGroundPanel background, int index) {
        this.background = background;
        this.index = index;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                MarkerPanel.this.background.setClickedMarker(MarkerPanel.this.index);
            }
        });
        setOpaque(false);
    }

    public void mark(int player, boolean isLast) {
        this.player = player;
        this.isLast = isLast;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (this.player) {
            case 0:
                break;
            case 1: {
                RadialGradientPaint paint = new RadialGradientPaint(15, 15, 10, new float[]{0f, 1f}
                    , new Color[]{Color.LIGHT_GRAY, Color.BLACK});
                ((Graphics2D) g).setPaint(paint);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
                Ellipse2D e = new Ellipse2D.Float(5, 5, 40, 40);
                ((Graphics2D) g).fill(e);
                break;
            }
            case 2: {
                RadialGradientPaint paint = new RadialGradientPaint(15, 15, 50, new float[]{0f, 1f}
                    , new Color[]{Color.WHITE, Color.BLACK});
                ((Graphics2D) g).setPaint(paint);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
                Ellipse2D e = new Ellipse2D.Float(5, 5, 40, 40);
                ((Graphics2D) g).fill(e);
                break;
            }
        }

        if (this.isLast) {
            int x1 = 0, y1 = 0;
            int x2 = getSize().width - 1, y2 = getSize().height - 1;
            g.setColor(Color.RED);
            g.drawRect(x1, y1, x2, y2);
        }
    }
}
