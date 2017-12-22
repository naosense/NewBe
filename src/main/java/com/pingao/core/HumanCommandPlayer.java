package com.pingao.core;

import java.util.Scanner;


/**
 * Created by pingao on 2017/12/9.
 */
public class HumanCommandPlayer extends Player {
    private static final String INTEGER_PATTERN = "\\d+";
    private static final Scanner CIN = new Scanner(System.in);

    public HumanCommandPlayer(char marker) {
        super(marker);
    }

    @Override
    protected Move decide(Board board) {
        System.out.println(this + " please input row and col");
        String row = CIN.next();
        String col = CIN.next();
        while (!row.matches(INTEGER_PATTERN) || !col.matches(INTEGER_PATTERN)) {
            System.out.println("Row and col must be integer");
            System.out.println(this + " please input row and col");
            row = CIN.next();
            col = CIN.next();
        }

        return new Move(0, new Board.Pos(Integer.parseInt(row) - 1, Integer.parseInt(col) - 1));
    }
}
