package com.pingao;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by pingao on 2017/12/9.
 */
public class ComputerPlayer extends Player {
    private static final Random RANDOM = new Random();
    private final int depth;
    private final Map<Integer, Map<Long, Move>> memory;

    public ComputerPlayer(char marker, int depth) {
        super(marker);
        if (depth < 1) {
            throw new IllegalArgumentException("Depth must be greater than 1");
        }
        this.depth = depth;
        this.memory = new HashMap<>();
        IntStream.rangeClosed(1, depth).forEach(i -> this.memory.put(i, new HashMap<>()));
    }

    private Move first() {
        return new Move(0, new Board.Pos(Board.N_ROW / 4 + RANDOM.nextInt(Board.N_ROW) / 2, Board.N_COL / 4 + RANDOM.nextInt(Board.N_COL) / 2 ));
    }

    @Override
    protected Move decide(Board board) {
        if (this.step() <= 0 && board.opponent(this).step() <= 0) {
            return first();
        } else {
            return alphaBeta(board, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, this);
        }
    }

    private Move alphaBeta(Board board, int depth, int alpha, int beta, Player player) {
        if (board.status().isGameOver() || this.depth <= depth) {
            return new Move(board.evaluate(this, depth), null);
        }

        if (this == player) {
            List<Board.Pos> sortPos = sortPosByScore(board, player);
            Move v = new Move(Integer.MIN_VALUE, null);
            for (Board.Pos pos : sortPos) {
                Board board1 = new Board(board);
                board1.mark(pos, player);
                Map<Long, Move> m = memory.get(depth);
                Move w = m.get(board1.hash());
                if (w == null) {
                    w = alphaBeta(board1, depth + 1, alpha, beta, board1.opponent(player));
                    m.put(board1.hash(), w);
                }
                if (v.compareTo(w) < 0) {
                    v = new Move(w.getScore(), pos);
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
                Board board1 = new Board(board);
                board1.mark(pos, player);
                Map<Long, Move> m = memory.get(depth);
                Move w = m.get(board1.hash());
                if (w == null) {
                    w = alphaBeta(board1, depth + 1, alpha, beta, board1.opponent(player));
                    m.put(board1.hash(), w);
                }
                if (v.compareTo(w) > 0) {
                    v = new Move(w.getScore(), pos);
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
                Board board1 = new Board(board);
                board1.mark(p, player);
                return new Move(board1.evaluate(this, 1), p);
            })
            .sorted(this == player ? Comparator.comparing(Move::getScore).reversed() : Comparator.comparing(Move::getScore))
            .map(Move::getNext)
            .collect(Collectors.toList());
    }
}
