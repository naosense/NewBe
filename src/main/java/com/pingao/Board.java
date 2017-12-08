package com.pingao;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by pingao on 2017/12/9.
 */
public class Board {
    public static final int N_ROW = 10;
    public static final int N_COL = 10;
    private static final int AVAILABLE_DISTANCE = 2;
    private final char[][] grid = {
        {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
        {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
        {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
        {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
        {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
        {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
        {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
        {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
        {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
        {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
    };
    private final List<Pos> allPos;
    private final List<List<Pos>> bands;
    private Player player1;
    private Player player2;
    private Status status;

    public Board() {
        this.allPos = IntStream.range(0, N_ROW).boxed().flatMap(i -> IntStream.range(0, N_COL).mapToObj(j -> new Pos(i, j))).collect(Collectors.toList());
        this.bands = getBands();
        this.status = Status.ONGOING;
    }

    //public Board(Board old) {
    //    this.allPos = new ArrayList<>(old.allPos);
    //    this.bands = new ArrayList<>(old.bands);
    //    this.player1 = old.player1;
    //    this.player2 = old.player2;
    //    this.status = old.status;
    //    this
    //
    //}

    private List<List<Pos>> getBands() {
        Map<Integer, List<Pos>> basket = new HashMap<>();
        int offset = 2 * (N_ROW + N_COL);
        for (int i = 0; i < N_ROW; i++) {
            for (int j = 0; j < N_COL; j++) {
                // row
                load(basket, i, new Pos(i, j));

                // col
                load(basket, j + offset, new Pos(i, j));

                // diagonal
                load(basket, i + j + 2 * offset, new Pos(i, j));
                load(basket, i - j + 3 * offset, new Pos(i, j));
            }
        }
        return new ArrayList<>(basket.values());
    }

    private void load(Map<Integer, List<Pos>> basket, int key, Pos pos) {
        List<Pos> band = basket.get(key);
        if (band == null) {
            basket.put(key, new ArrayList<>(Collections.singletonList(pos)));
        } else {
            band.add(pos);
        }
    }

    public void setPlayers(Player player1, Player player2) {
        if (player1.marker == player2.marker) {
            throw new IllegalArgumentException("Marker of player must be different");
        }
        this.player1 = player1;
        this.player2 = player2;
        player1.opponent = player2;
        player2.opponent = player1;
    }

    public void mark(Pos pos, Player player) {
        if (grid[pos.row][pos.col] != '.') {
            throw new IllegalArgumentException("Move " + pos + "=" + grid[pos.row][pos.col] + " is not empty");
        }
        grid[pos.row][pos.col] = player.marker;
        status = updateStatus();
    }

    private Status updateStatus() {
        Set<Set<Pos>> groupsOfP1 = combine(grid, player1);
        Set<Set<Pos>> groupsOfP2 = combine(grid, player2);
        //System.out.println("groupsOfO = " + groupsOfP1);
        //System.out.println("groupsOfX = " + groupsOfP2);
        if (groupsOfP1.stream().anyMatch(g -> g.size() >= 5)) {
            return Status.P1_WIN;
        } else if (groupsOfP2.stream().anyMatch(g -> g.size() >= 5)) {
            return Status.P2_WIN;
        } else if (isDraw(grid)) {
            return Status.DRAW;
        } else {
            return Status.ONGOING;
        }
    }

    public Set<Set<Pos>> combine(char[][] board, Player player) {
        Set<Set<Pos>> groups = new HashSet<>();
        for (List<Pos> band : bands) {
            Set<Pos> group = new HashSet<>();
            for (int i = 0; i < band.size(); i++) {
                Pos pos = band.get(i);
                if (board[pos.row][pos.col] == player.marker) {
                    group.add(pos);
                    // last one trigger
                    if (i == band.size() - 1) {
                        groups.add(group);
                    }
                } else {
                    if (!group.isEmpty()) {
                        groups.add(group);
                        group = new HashSet<>();
                    }
                }
            }
        }
        return groups.stream().filter(g -> g.size() > 1).collect(Collectors.toSet());
    }

    private boolean isDraw(char[][] board) {
        return allPos.stream().noneMatch(p -> board[p.row][p.col] == '.');
    }

    public void reset(Pos pos) {
        grid[pos.row][pos.col] = '.';
    }

    public Set<Pos> getAvailablePos() {
        return allPos.stream().filter(p -> isPosValid(grid, p)).collect(Collectors.toSet());
    }

    private boolean isPosValid(char[][] board, Pos pos) {
        if (board[pos.row][pos.col] != '.') {
            return false;
        }
        int rowL = pos.row - AVAILABLE_DISTANCE < 0 ? 0 : pos.row - AVAILABLE_DISTANCE;
        int colL = pos.col - AVAILABLE_DISTANCE < 0 ? 0 : pos.col - AVAILABLE_DISTANCE;
        int rowH = pos.row + AVAILABLE_DISTANCE > N_ROW - 1 ? N_ROW - 1 : pos.row + AVAILABLE_DISTANCE;
        int colH = pos.col + AVAILABLE_DISTANCE > N_COL - 1 ? N_COL - 1 : pos.col + AVAILABLE_DISTANCE;
        return IntStream.range(rowL, rowH).boxed().flatMap(i -> IntStream.range(colL, colH).mapToObj(j -> board[i][j] != '.')).anyMatch(b -> b);
    }

    public void print() {

        System.out.println(getPlayerInfo(player1));
        System.out.println(getPlayerInfo(player2));
        System.out.print("\t");
        for (int i = 0; i < N_COL; i++) {
            System.out.print((i + 1) + "\t");
        }
        System.out.println();
        for (int i = 0; i < N_ROW; i++) {
            System.out.print((i + 1) + "\t");
            for (int j = 0; j < N_COL; j++) {
                System.out.print(grid[i][j] + "\t");
            }
            System.out.println();
        }
    }

    private String getPlayerInfo(Player player) {
        if (player.path.isEmpty()) {
            return player + "\tStep: 0" + "\tLast Pos: None";
        } else {
            return player + "\tStep: " + player.path.size() + "\tLast Pos: " + player.path.get(player.path.size() - 1);
        }
    }

    public Status getStatus() {
        return status;
    }

    public int evaluate(Player player, int depth) {
        if (status == Status.P1_WIN) {
            return player.marker == player1.marker ? Integer.MAX_VALUE - depth : Integer.MIN_VALUE + depth;
        } else if (status == Status.P2_WIN) {
            return player.marker == player2.marker ? Integer.MAX_VALUE - depth : Integer.MIN_VALUE + depth;
        } else if (status == Status.DRAW) {
            return 0;
        } else {
            Set<Set<Board.Pos>> groupsOfPlayer = combine(grid, player);
            Set<Set<Board.Pos>> groupsOfOpponent = combine(grid, player.opponent);
            return groupsOfPlayer.stream().mapToInt(g -> score(g, false)).sum() - groupsOfOpponent.stream().mapToInt(g -> score(g, true)).sum();
        }
    }

    private int score(Set<Pos> group, boolean isO) {
        int res = 0;
        if (group.size() == 1) {
            res = 1;
        } else if (group.size() == 2) {
            int open = getOpenNumber(group);
            if (open == 2) {
                res = 2;
            } else if (open == 1) {
                res = 1;
            } else {
                res = 1;
            }
        } else if (group.size() == 3) {
            int open = getOpenNumber(group);
            if (open == 2) {
                res = 2000;
            } else if (open == 1) {
                res = isO ? 200 : 20;
            } else {
                res = isO ? 20 : 2;
            }
        } else if (group.size() == 4) {
            int open = getOpenNumber(group);
            if (open == 2) {
                res = isO ? 20000 : 2000;
            } else if (open == 1) {
                res = isO ? 10000 : 1000;
            } else {
                res = isO ? 2000 : 200;
            }
        } else if (group.size() == 5) {
            res = 1000000;
        }
        return res;
    }

    private int getOpenNumber(Set<Pos> group) {
        List<Pos> poses = new ArrayList<>(group);
        poses.sort(Comparator.comparing(Pos::getIndex));
        Pos min = poses.get(0);
        Pos max = poses.get(poses.size() - 1);
        if (min.row == max.row) {
            return (min.col > 0 && grid[min.row][min.col - 1] == '.' ? 1 : 0) + (max.col < N_COL - 1 && grid[min.row][max.col + 1] == '.' ? 1 : 0);
        } else if (min.col == max.col) {
            return (min.row > 0 && grid[min.row - 1][min.col] == '.' ? 1 : 0) + (max.row < N_ROW - 1 && grid[max.row + 1][min.col] == '.' ? 1 : 0);
        } else {
            if (min.col < max.col) {
                return (min.row > 0 && min.col > 0 && grid[min.row - 1][min.col - 1] == '.' ? 1 : 0) + (max.row < N_ROW - 1 && max.col < N_COL - 1 && grid[max.row + 1][max.col + 1] == '.' ? 1 : 0);
            } else {
                return (min.row > 0 && min.col < N_COL - 1 && grid[min.row - 1][min.col + 1] == '.' ? 1 : 0) + (max.row < N_ROW - 1 && max.col > 0 && grid[max.row + 1][max.col - 1] == '.' ? 1 : 0);
            }
        }
    }

    public boolean isEnd() {
        if (status == Board.Status.P1_WIN) {
            System.out.println(player1 + " win");
            return true;
        } else if (status == Board.Status.P2_WIN) {
            System.out.println(player2 + " win");
            return true;
        } else if (status == Board.Status.DRAW) {
            System.out.println("Game is draw");
            return true;
        } else {
            return false;
        }
    }

    public enum Status {P1_WIN, P2_WIN, DRAW, ONGOING}

    @Data
    public static class Pos {
        private final int row;
        private final int col;
        private final int index;

        Pos(int row, int col) {
            this.row = row;
            this.col = col;
            this.index = row * N_ROW + col;
        }

        @Override
        public String toString() {
            return "(" + (row + 1) + ", " + (col + 1) + ")";
        }
    }
}
