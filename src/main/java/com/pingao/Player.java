package com.pingao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pingao on 2017/12/9.
 */
public abstract class Player {
    public final char marker;
    protected final List<Board.Pos> path;
    protected final Board board;
    public Player opponent;

    public Player(char marker, Board board) {
        this.marker = marker;
        this.board = board;
        this.path = new ArrayList<>();
    }

    public void next() {
        Move move = decide();
        path.add(move.getNext());
        board.mark(move.getNext(), this);
    }

    protected abstract Move decide();

    @Override
    public String toString() {
        return "Player " + marker;
    }
}
