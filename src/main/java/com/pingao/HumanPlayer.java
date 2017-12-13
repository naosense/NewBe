package com.pingao;

import java.util.Scanner;

/**
 * Created by pingao on 2017/12/9.
 */
public class HumanPlayer extends Player {
    private static final String INTEGER_PATTERN = "\\d+";
    private static final Scanner CIN = new Scanner(System.in);

    public HumanPlayer(char marker) {
        super(marker);
    }

    @Override
    protected Move decide(Board board) {
        System.out.println(this + " please input row and col");
        String row = CIN.next();
        String col = CIN.next();
        for (; !row.matches(INTEGER_PATTERN) || !col.matches(INTEGER_PATTERN); row = CIN.next(), col = CIN.next()) {
            System.out.println("Row and col must be integer");
            System.out.println(this + " please input row and col");
        }
        return new Move(0, new Board.Pos(Integer.parseInt(row) - 1, Integer.parseInt(col) - 1));
    }
}
