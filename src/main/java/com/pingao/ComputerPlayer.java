package com.pingao;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Created by pingao on 2017/12/9.
 */
public class ComputerPlayer extends Player {
    private static final Random RANDOM = new Random();
    private final Map<Integer, Map<Long, Move>> memory;
    private final int depth;


    public ComputerPlayer(char marker, int depth) {
        super(marker);
        this.depth = depth;
        this.memory = new HashMap<>();
        IntStream.rangeClosed(0, depth).forEach(i -> this.memory.put(i, new HashMap<>()));
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
            return alphaBeta(board, this.depth, Integer.MIN_VALUE, Integer.MAX_VALUE, this);
        }
    }


    private Move alphaBeta(Board board, int depth, int alpha, int beta, Player player) {
        if (board.status().isGameOver() || depth <= 0) {
            return new Move(board.evaluate(this), null);
        }

        if (this == player) {
            List<Board.Pos> childPos = sortChildPos(board, player);
            Move v = new Move(Integer.MIN_VALUE, null);
            for (Board.Pos pos : childPos) {
                Board bd = new Board(board);
                bd.mark(pos, player);
                Map<Long, Move> m = this.memory.get(depth);
                Move w = m.get(bd.hash());
                if (w == null) {
                    w = alphaBeta(bd, depth - 1, alpha, beta, bd.getEnemy(player));
                    m.put(bd.hash(), w);
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
            List<Board.Pos> childPos = sortChildPos(board, player);
            Move v = new Move(Integer.MAX_VALUE, null);
            for (Board.Pos pos : childPos) {
                Board bd = new Board(board);
                bd.mark(pos, player);
                Map<Long, Move> m = this.memory.get(depth);
                Move w = m.get(bd.hash());
                if (w == null) {
                    w = alphaBeta(bd, depth - 1, alpha, beta, bd.getEnemy(player));
                    m.put(bd.hash(), w);
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


    private List<Board.Pos> sortChildPos(Board board, Player player) {
        return board.getChildPos()
                    .stream()
                    .map(pos -> {
                        Board bd = new Board(board);
                        bd.mark(pos, player);
                        return new Move(bd.evaluate(this), pos);
                    })
                    .sorted((this == player) ? Comparator.comparing(Move::getScore).reversed() : Comparator.comparing(Move::getScore))
                    .map(Move::getNext)
                    .collect(Collectors.toList());
    }
}
