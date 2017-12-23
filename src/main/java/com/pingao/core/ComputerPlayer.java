package com.pingao.core;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * Created by pingao on 2017/12/9.
 */
public class ComputerPlayer extends Player {
    private static final Random RANDOM = new Random();
    private final int depth;
    private final int[][] history;
    private Move best;

    public ComputerPlayer(char marker, int depth) {
        super(marker);
        this.depth = depth;
        this.history = buildHistory();
    }

    private int[][] buildHistory() {
        int[][] history = new int[Board.N_ROW][Board.N_COL];
        for (int i = 0; i < Board.N_ROW; i++) {
            for (int j = 0; j < Board.N_COL; j++) {
                history[i][j] = 0;
            }
        }
        return history;
    }

    private Move first() {
        return new Move(0, new Board.Pos(
            Board.N_ROW / 4 + RANDOM.nextInt(Board.N_ROW) / 2,
            Board.N_COL / 4 + RANDOM.nextInt(Board.N_COL) / 2
        ));
    }

    @Override
    protected Move decide(Board board) {
        if (this.step() <= 0 && board.getEnemy(this).step() <= 0) {
            return first();
        } else {
            alphaBeta(board, this.depth, Integer.MIN_VALUE, Integer.MAX_VALUE, this);
            return this.best;
        }
    }

    private int alphaBeta(Board board, int depth, int alpha, int beta, Player player) {
        if (board.status().isGameOver() || depth <= 0) {
            return board.evaluate(this, this.depth - depth);
        }

        Board.Pos bestPos = null;
        int v = (this == player) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<Board.Pos> childPos = sortChildPos(board);
        for (Board.Pos pos : childPos) {
            Board bd = new Board(board);
            bd.mark(pos, player);
            int w = alphaBeta(bd, depth - 1, alpha, beta, bd.getEnemy(player));
            if (this == player) {
                if (v < w) {
                    v = w;
                    bestPos = pos;
                    if (depth == this.depth) {
                        this.best = new Move(v, pos);
                    }
                }
                alpha = Integer.max(alpha, w);
            } else {
                if (v > w) {
                    v = w;
                    bestPos = pos;
                }
                beta = Integer.min(beta, w);
            }

            if (beta <= alpha) {
                this.history[pos.getRow()][pos.getCol()] += 2 << depth;
                break;
            }
        }
        if (bestPos != null) {
            this.history[bestPos.getRow()][bestPos.getCol()] += 2 << depth;
        }
        return v;
    }

    private List<Board.Pos> sortChildPos(Board board) {
        return board.getChildPos()
                    .stream()
                    .sorted(
                        new Comparator<Board.Pos>() {
                            @Override
                            public int compare(Board.Pos o1, Board.Pos o2) {
                                return Integer.compare(
                                    ComputerPlayer.this.history[o2.getRow()][o2.getCol()],
                                    ComputerPlayer.this.history[o1.getRow()][o1.getCol()]);
                            }
                        })
                    .collect(Collectors.toList());
    }
}
