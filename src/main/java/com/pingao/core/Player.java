package com.pingao.core;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by pingao on 2017/12/9.
 */
public abstract class Player {
    public final char marker;
    private final List<Long> times;
    private final List<Board.Pos> path;

    public Player(char marker) {
        this.marker = marker;
        this.times = new ArrayList<>();
        this.path = new ArrayList<>();
    }

    public long time() {
        return this.times.stream().mapToLong(t -> t).sum();
    }

    public long getLastTime() {
        if (this.times.size() <= 0) {
            return 0;
        }
        return this.times.get(this.times.size() - 1);
    }

    public int step() {
        return this.path.size();
    }

    public Board.Pos getLastPos() {
        if (this.path.size() <= 0) {
            return null;
        }
        return this.path.get(this.path.size() - 1);
    }

    public Board.Pos next(Board board) {
        long start = System.nanoTime();
        Move move = decide(board);
        while (!board.mark(move.getNext(), this)) {
            move = decide(board);
        }
        long end = System.nanoTime();
        this.times.add(end - start);
        this.path.add(move.getNext());
        return move.getNext();
    }

    protected abstract Move decide(Board board);

    @Override
    public String toString() {
        return "Player " + this.marker;
    }

    @Data
    protected class Move implements Comparable<Move> {
        private final Board.Pos next;
        private final int score;

        public Move(int score, Board.Pos next) {
            this.score = score;
            this.next = next;
        }

        @Override
        public int compareTo(Move o) {
            return Integer.compare(this.score, o.score);
        }

        @Override
        public String toString() {
            return this.score + ":-->" + this.next;
        }
    }
}
