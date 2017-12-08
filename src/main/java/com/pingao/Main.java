package com.pingao;

/**
 * Created by pingao on 2017/12/9.
 */
public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        Player player1 = new ComputerPlayer('O', 4, board);
        Player player2 = new ComputerPlayer('X', 1, board);
        board.setPlayers(player1, player2);
        while (!board.isEnd()) {
            player1.next();
            board.print();
            if (board.isEnd()) {
                break;
            }
            player2.next();
            board.print();
        }
    }
}
