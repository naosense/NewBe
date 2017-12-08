package com.pingao;

import lombok.Data;

/**
 * Created by pingao on 2017/12/9.
 */
@Data
public class Move implements Comparable<Move> {
    private Board.Pos next;
    private int score;

    public Move(int score, Board.Pos next) {
        this.score = score;
        this.next = next;
    }

    @Override
    public String toString() {
        return score + ":-->" + next;
    }

    @Override
    public int compareTo(Move o) {
        return Integer.compare(score, o.score);
    }
}