package com.pingao.core;

/**
 * Created by pingao on 17-12-16.
 */
public class HumanMousePlayer extends Player {
    private Board.Pos next;

    public HumanMousePlayer(char marker) {
        super(marker);
    }

    @Override
    protected Move decide(Board board) {
        return new Move(0, this.next);
    }

    public void click(Board.Pos pos) {
        this.next = pos;
    }
}
