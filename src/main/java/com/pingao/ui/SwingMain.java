package com.pingao.ui;

import com.pingao.core.Board;
import com.pingao.core.ComputerPlayer;
import com.pingao.core.HumanMousePlayer;
import com.pingao.core.Player;

import javax.swing.*;


/**
 * Created by pingao on 17-12-16.
 */
public class SwingMain extends JFrame {

    private static final long serialVersionUID = 8214339693742375000L;

    public static void main(String[] args) {
        Object[] levels = new String[]{"1", "2", "3", "4", "5"};
        boolean again = true;
        while (again) {
            SwingMain main = new SwingMain();
            main.setLayout(null);
            BackGroundPanel back = new BackGroundPanel();
            back.setBounds(0, 0, MarkerPanel.WIDTH * Board.N_COL, MarkerPanel.WIDTH * Board.N_ROW);
            main.add(back);
            run(main, MarkerPanel.WIDTH * Board.N_COL, MarkerPanel.WIDTH * Board.N_ROW + 23);

            Object mode =
                JOptionPane.showInputDialog(main, "Please choose game mode", "", JOptionPane.INFORMATION_MESSAGE, null, new Object[]{
                    "Computer vs Human", "Computer vs Computer"}, "Computer vs Human");
            while (mode == null) {
                mode =
                    JOptionPane.showInputDialog(main, "Please choose game mode", "", JOptionPane.INFORMATION_MESSAGE, null, new Object[]{
                        "Computer vs Human", "Computer vs Computer"}, "Computer vs Human");
            }

            if ("Computer vs Human".equals(mode)) {
                Object level =
                    JOptionPane.showInputDialog(main, "Please choose game level", "", JOptionPane.INFORMATION_MESSAGE, null, levels, "3");
                while (level == null) {
                    level =
                        JOptionPane.showInputDialog(main, "Please choose game level", "", JOptionPane.INFORMATION_MESSAGE, null, levels, "3");
                }
                int first =
                    JOptionPane.showConfirmDialog(main, "Do you want to be first", "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                while (first == -1) {
                    first =
                        JOptionPane.showConfirmDialog(main, "Do you want to be first", "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                }

                Player computer = new ComputerPlayer('O', Integer.parseInt(String.valueOf(level)));
                Player human = new HumanMousePlayer('X');
                Board board;

                if (first == 0) {
                    board = new Board(human, computer);
                } else {
                    board = new Board(computer, human);
                }
                board.start(back);
            } else {
                Object level1 =
                    JOptionPane.showInputDialog(main, "Please choose level for computer1", "", JOptionPane.INFORMATION_MESSAGE, null, levels, "3");
                while (level1 == null) {
                    level1 =
                        JOptionPane.showInputDialog(main, "Please choose level for computer1", "", JOptionPane.INFORMATION_MESSAGE, null, levels, "3");
                }

                Object level2 =
                    JOptionPane.showInputDialog(main, "Please choose level for computer2", "", JOptionPane.INFORMATION_MESSAGE, null, levels, "3");
                while (level2 == null) {
                    level2 =
                        JOptionPane.showInputDialog(main, "Please choose level for computer2", "", JOptionPane.INFORMATION_MESSAGE, null, levels, "3");
                }

                Player computer1 = new ComputerPlayer('O', Integer.parseInt(String.valueOf(level1)));
                Player computer2 = new ComputerPlayer('X', Integer.parseInt(String.valueOf(level2)));
                Board board = new Board(computer1, computer2);
                board.start(back);
            }

            again =
                JOptionPane.showConfirmDialog(main, "Do you want to play again", "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == 0;
            main.dispose();
        }
    }

    private static void run(JFrame f, int width, int height) {
        SwingUtilities.invokeLater(() -> {
            f.setTitle("NewBe, a new gomoku player");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setSize(width, height);
            f.setLocationRelativeTo(null);
            f.setResizable(false);
            f.setVisible(true);
        });
    }
}
