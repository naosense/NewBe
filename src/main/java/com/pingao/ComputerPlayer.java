package com.pingao;

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

    public ComputerPlayer(char marker, int depth, Board board) {
        super(marker, board);
        this.depth = depth;
    }

    private Move first() {

        return new Move(0, new Board.Pos(Board.N_ROW / 4 + RANDOM.nextInt(Board.N_ROW) / 2, Board.N_COL / 4 + RANDOM.nextInt(Board.N_COL) / 2 ));
    }

    @Override
    protected Move decide() {
        if (this.path.isEmpty() && opponent.path.isEmpty()) {
            return first();
        } else {
            return alphaBeta(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, this);
        }
    }

    private Move alphaBeta(Board board, int depth, int alpha, int beta, Player player) {
        Board.Status status = board.getStatus();
        if (status != Board.Status.ONGOING) {
            return new Move(board.evaluate(this, depth), null);
        } else {
            if (depth <= 0) {
                return new Move(board.evaluate(this, depth), null);
            }
        }

        if (this == player) {
            List<Board.Pos> sortPos = sortPosByScore(board, player);
            Move v = new Move(Integer.MIN_VALUE, null);
            for (Board.Pos pos : sortPos) {
                board.mark(pos, player);
                Move w = alphaBeta(board, depth - 1, alpha, beta, player.opponent);
                board.reset(pos);
                if (v.compareTo(w) < 0) {
                    v = w;
                    v.setNext(pos);
                }
                alpha = Integer.max(alpha, v.getScore());
                if (beta <= alpha) {
                    break;
                }
            }
            return v;
        } else {
            List<Board.Pos> sortPos = sortPosByScore(board, player);
            Move v = new Move(Integer.MAX_VALUE, null);
            for (Board.Pos pos : sortPos) {
                board.mark(pos, player);
                Move w = alphaBeta(board, depth - 1, alpha, beta, player.opponent);
                board.reset(pos);
                if (v.compareTo(w) > 0) {
                    v = w;
                    v.setNext(pos);
                }
                beta = Integer.min(beta, v.getScore());
                if (beta <= alpha) {
                    break;
                }
            }
            return v;
        }
    }

    private List<Board.Pos> sortPosByScore(Board board, Player player) {
        return board.getAvailablePos().stream()
            .map(p -> {
                board.mark(p, player);
                Move m = new Move(board.evaluate(this, 1), p);
                board.reset(p);
                return m;
            })
            .sorted(this == player ? Comparator.comparing(Move::getScore).reversed() : Comparator.comparing(Move::getScore))
            .map(Move::getNext)
            .collect(Collectors.toList());
    }
}
