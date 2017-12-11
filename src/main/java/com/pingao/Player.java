package com.pingao;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public long times() {
        return times.stream().mapToLong(t -> t).sum();
    }

    public long getLastTime() {
        if (times.size() <= 0) {
            return 0;
        }
        return times.get(times.size() - 1);
    }

    public int step() {
        return path.size();
    }

    public Board.Pos getLastPos() {
        if (path.size() <= 0) {
            return null;
        }
        return path.get(path.size() - 1);
    }

    public void next(Board board) {
        long start = System.nanoTime();
        Move move = decide(board);
        for (; !board.mark(move.getNext(), this); move = decide(board)) ;
        long end = System.nanoTime();
        times.add(TimeUnit.NANOSECONDS.toSeconds(end - start));
        path.add(move.getNext());
    }

    protected abstract Move decide(Board board);

    @Override
    public String toString() {
        return "Player " + marker;
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
            return Integer.compare(score, o.score);
        }        @Override
        public String toString() {
            return score + ":-->" + next;
        }


    }
}
