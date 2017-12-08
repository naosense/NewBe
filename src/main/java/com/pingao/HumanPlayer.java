package com.pingao;

import java.util.Scanner;

/**
 * Created by pingao on 2017/12/9.
 */
public class HumanPlayer extends Player {
    public HumanPlayer(char marker, Board board) {
        super(marker, board);
    }

    @Override
    protected Move decide() {
        Scanner cin = new Scanner(System.in);
        System.out.println("Input row and col:");
        int i = cin.nextInt();
        int j = cin.nextInt();
        return new Move(0, new Board.Pos(i - 1, j - 1));
    }
}
