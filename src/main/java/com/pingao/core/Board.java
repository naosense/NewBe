package com.pingao.core;

import com.pingao.ui.BackGroundPanel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * Created by pingao on 2017/12/9.
 */
public class Board {
    public static final int N_ROW = 15;
    public static final int N_COL = 15;
    private static final char EMPTY_CHAR = '-';
    private static final int AVAILABLE_DISTANCE = 2;
    private static final Random RANDOM = new Random();
    private static final List<Pos> ALL_POS = buildAllPos();
    private static final List<List<Pos>> BANDS = buildBands();
    private static final long[][] RANDOM_TABLE = buildRandomTable();
    private static final Map<Player, Set<Set<Pos>>> GROUPS_CACHE = new HashMap<>();
    private static final int[][] SCORE_TABLE = {
        {1, 1, 1},
        {5, 10, 20},
        {10, 500, 1000},
        {25, 5000, 10000},
        {1000000, 1000000, 1000000}
    };

    private final GameStatus status;
    private final char[][] grid;
    public final Player player1;
    public final Player player2;
    private long hash;

    public Board(Player player1, Player player2) {
        this.grid = buildGrid();
        this.player1 = player1;
        this.player2 = player2;
        this.status = new GameStatus(Status.ONGOING, null, Collections.emptySet());
        this.hash = buildHash();
    }

    public Board(Board other) {
        this.player1 = other.player1;
        this.player2 = other.player2;
        this.grid = copyOf(other.grid);
        this.status = new GameStatus(other.status.status, other.status.winner, other.status.winningSet);
        this.hash = other.hash;
    }

    private static char[][] copyOf(char[][] src) {
        int length = src.length;
        char[][] target = new char[length][src[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[i].length);
        }
        return target;
    }

    private static long[][] buildRandomTable() {
        long[][] hash = new long[N_ROW * N_COL][3];
        for (int i = 0; i < N_ROW * N_COL; i++) {
            for (int j = 0; j < 3; j++) {
                hash[i][j] = RANDOM.nextLong();
            }
        }
        return hash;
    }

    private static List<Pos> buildAllPos() {
        List<Pos> poses = new ArrayList<>();
        for (int i = 0; i < N_ROW; i++) {
            for (int j = 0; j < N_COL; j++) {
                poses.add(new Pos(i, j));
            }
        }
        return poses;
    }

    private static List<List<Pos>> buildBands() {
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
        return basket.values().stream().filter(l -> l.size() > 1).collect(Collectors.toList());
    }

    private static void load(Map<Integer, List<Pos>> basket, int key, Pos pos) {
        List<Pos> band = basket.get(key);
        if (band == null) {
            basket.put(key, new ArrayList<>(Collections.singletonList(pos)));
        } else {
            band.add(pos);
        }
    }

    private char[][] buildGrid() {
        char[][] grid = new char[N_ROW][N_COL];
        for (int i = 0; i < N_ROW; i++) {
            for (int j = 0; j < N_COL; j++) {
                grid[i][j] = EMPTY_CHAR;
            }
        }
        return grid;
    }

    private long buildHash() {
        long hash = RANDOM.nextLong();
        for (int i = 0; i < N_ROW * N_COL; i++) {
            hash ^= RANDOM_TABLE[i][2];
        }
        return hash;
    }

    public boolean mark(Pos pos, Player player) {
        if ((pos.row < 0 || pos.row > N_ROW - 1) || (pos.col < 0 || pos.col > N_COL - 1)) {
            System.out.println("Row must between 1 and " + N_ROW + ", Col must between 1 and " + N_COL);
            return false;
        }
        if (this.grid[pos.row][pos.col] != EMPTY_CHAR) {
            System.out.println(pos + "=" + this.grid[pos.row][pos.col] + " is not empty");
            return false;
        }
        this.grid[pos.row][pos.col] = player.marker;
        this.hash ^= RANDOM_TABLE[pos.index][player == this.player1 ? 0 : 1];
        scanGroups();
        selfCheck();
        return true;
    }

    private void selfCheck() {
        Set<Set<Pos>> groupsOfP1 = GROUPS_CACHE.get(this.player1);
        Set<Set<Pos>> groupsOfP2 = GROUPS_CACHE.get(this.player2);
        if (groupsOfP1.stream().anyMatch(g -> g.size() >= 5)) {
            this.status.status = Status.P1_WIN;
            this.status.winner = this.player1;
            this.status.winningSet =
                groupsOfP1.stream().filter(g -> g.size() >= 5).findFirst().orElse(Collections.emptySet());
        } else if (groupsOfP2.stream().anyMatch(g -> g.size() >= 5)) {
            this.status.status = Status.P2_WIN;
            this.status.winner = this.player2;
            this.status.winningSet =
                groupsOfP2.stream().filter(g -> g.size() >= 5).findFirst().orElse(Collections.emptySet());
        } else if (isDraw()) {
            this.status.status = Status.DRAW;
        }
    }

    private void scanGroups() {
        Set<Set<Pos>> groupsOfP1 = new HashSet<>();
        Set<Set<Pos>> groupsOfP2 = new HashSet<>();
        for (List<Pos> band : BANDS) {
            Set<Pos> group1 = new HashSet<>();
            Set<Pos> group2 = new HashSet<>();
            for (int i = 0; i < band.size(); i++) {
                Pos pos = band.get(i);
                if (this.grid[pos.row][pos.col] == this.player1.marker) {
                    group1.add(pos);
                    // last one trigger
                    if (i == band.size() - 1) {
                        groupsOfP1.add(group1);
                    }
                } else {
                    if (!group1.isEmpty()) {
                        groupsOfP1.add(group1);
                        group1 = new HashSet<>();
                    }
                }
                if (this.grid[pos.row][pos.col] == this.player2.marker) {
                    group2.add(pos);
                    // last one trigger
                    if (i == band.size() - 1) {
                        groupsOfP2.add(group2);
                    }
                } else {
                    if (!group2.isEmpty()) {
                        groupsOfP2.add(group2);
                        group2 = new HashSet<>();
                    }
                }
            }
        }
        GROUPS_CACHE.put(this.player1, groupsOfP1);
        GROUPS_CACHE.put(this.player2, groupsOfP2);
    }

    private boolean isDraw() {
        return ALL_POS.stream().noneMatch(p -> this.grid[p.row][p.col] == EMPTY_CHAR);
    }

    private boolean isEmpty(int index) {
        if (index < 0 || index >= N_ROW * N_COL) {
            return false;
        }
        Pos pos = new Pos(index);
        return this.grid[pos.row][pos.col] == EMPTY_CHAR;
    }

    public long hash() {
        return this.hash;
    }

    public GameStatus status() {
        return this.status;
    }

    public Set<Pos> getChildPos() {
        return ALL_POS.stream().filter(p -> this.grid[p.row][p.col] == EMPTY_CHAR && hasPlayerAdjacent(p)).collect(Collectors.toSet());
    }

    private boolean hasPlayerAdjacent(Pos pos) {
        int rowL = pos.row - AVAILABLE_DISTANCE < 0 ? 0 : pos.row - AVAILABLE_DISTANCE;
        int colL = pos.col - AVAILABLE_DISTANCE < 0 ? 0 : pos.col - AVAILABLE_DISTANCE;
        int rowH = pos.row + AVAILABLE_DISTANCE > N_ROW ? N_ROW : pos.row + AVAILABLE_DISTANCE;
        int colH = pos.col + AVAILABLE_DISTANCE > N_COL ? N_COL : pos.col + AVAILABLE_DISTANCE;

        for (int i = rowL; i < rowH; i++) {
            for (int j = colL; j < colH; j++) {
                if (this.grid[i][j] != EMPTY_CHAR) {
                    return true;
                }
            }
        }
        return false;
    }

    public int evaluate(Player player, int ply) {
        if (this.status.isWinning()) {
            return (player == this.status.winner) ? (Integer.MAX_VALUE - 1 - ply) : (Integer.MIN_VALUE + 1 + ply);
        } else if (this.status.isDraw()) {
            return 0;
        } else {
            int selfScore = GROUPS_CACHE.get(player).stream().mapToInt(g -> score(g, false)).sum();
            int enemyScore = GROUPS_CACHE.get(getEnemy(player)).stream().mapToInt(g -> score(g, true)).sum();
            return selfScore - enemyScore;
        }
    }

    private int score(Set<Pos> group, boolean isEnemy) {
        int size = group.size();
        int open = countOfOpen(group);
        int ratio = isEnemy && size > 2 ? 2 : 1;
        return ratio * SCORE_TABLE[size - 1][open];
    }

    public Player getEnemy(Player player) {
        return player == this.player1 ? this.player2 : this.player1;
    }

    private int countOfOpen(Set<Pos> group) {
        List<Pos> poses = new ArrayList<>(group);
        poses.sort(Comparator.comparing(Pos::getIndex));
        Pos min = poses.get(0);
        Pos max = poses.get(poses.size() - 1);
        if (min.row == max.row) {
            return (min.col > 0 && this.grid[min.row][min.col - 1] == EMPTY_CHAR ? 1 : 0)
                   + (max.col < N_COL - 1 && this.grid[min.row][max.col + 1] == EMPTY_CHAR ? 1 : 0);
        } else if (min.col == max.col) {
            return (min.row > 0 && this.grid[min.row - 1][min.col] == EMPTY_CHAR ? 1 : 0)
                   + (max.row < N_ROW - 1 && this.grid[max.row + 1][min.col] == EMPTY_CHAR ? 1 : 0);
        } else {
            if (min.col < max.col) {
                return (min.row > 0 && min.col > 0 && this.grid[min.row - 1][min.col - 1] == EMPTY_CHAR ? 1 : 0)
                       + (max.row < N_ROW - 1 && max.col < N_COL - 1 && this.grid[max.row + 1][max.col + 1] == EMPTY_CHAR ? 1 : 0);
            } else {
                return (min.row > 0 && min.col < N_COL - 1 && this.grid[min.row - 1][min.col + 1] == EMPTY_CHAR ? 1 : 0)
                       + (max.row < N_ROW - 1 && max.col > 0 && this.grid[max.row + 1][max.col - 1] == EMPTY_CHAR ? 1 : 0);
            }
        }
    }

    public void start() {
        print();
        while (!this.status.isGameOver()) {
            this.player1.next(this);
            print();
            if (this.status.isGameOver()) {
                break;
            }
            if (this.player1 instanceof ComputerPlayer && this.player2 instanceof ComputerPlayer) {
                pause();
            }
            this.player2.next(this);
            print();
            if (this.player1 instanceof ComputerPlayer && this.player2 instanceof ComputerPlayer) {
                pause();
            }
        }
    }

    public void start(BackGroundPanel back) {
        while (!this.status.isGameOver()) {
            if (this.player1.getLastPos() != null) {
                back.mark(this.player1.getLastPos().getIndex(), 1, false);
            }
            if (this.player1 instanceof HumanMousePlayer) {
                int index = back.getLastClicked();
                while (index < 0 || !isEmpty(index)) {
                    sleep(300);
                    index = back.getLastClicked();
                }
                ((HumanMousePlayer) this.player1).click(new Pos(index));
                back.mark(this.player1.next(this).getIndex(), 1, true);
            } else {
                back.mark(this.player1.next(this).getIndex(), 1, true);
            }

            if (this.status.isGameOver()) {
                break;
            }

            if (this.player2.getLastPos() != null) {
                back.mark(this.player2.getLastPos().getIndex(), 2, false);
            }

            if (this.player2 instanceof HumanMousePlayer) {
                int index = back.getLastClicked();

                while (index < 0 || !isEmpty(index)) {
                    sleep(300);
                    index = back.getLastClicked();
                }
                ((HumanMousePlayer) this.player2).click(new Pos(index));
                back.mark(this.player2.next(this).getIndex(), 2, true);
            } else {
                back.mark(this.player2.next(this).getIndex(), 2, true);
            }
        }

        // clear last pos of players
        back.mark(this.player1.getLastPos().getIndex(), 1, false);
        back.mark(this.player2.getLastPos().getIndex(), 2, false);
        // mark winning set
        String msg;
        switch (this.status.status) {
            case P1_WIN: {
                this.status().winningSet.forEach(p -> back.mark(p.getIndex(), 1, true));
                msg = "Black is win, congratulations!";
                break;
            }
            case P2_WIN: {
                this.status().winningSet.forEach(p -> back.mark(p.getIndex(), 2, true));
                msg = "White is win, congratulations!";
                break;
            }

            case DRAW: {
                msg = "You both are so good, but game is draw";
                break;
            }
            default: {
                msg = "This won't happen";
            }
        }

        JOptionPane.showMessageDialog(back, msg, "", JOptionPane.INFORMATION_MESSAGE);
    }

    private void sleep(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void print() {
        System.out.println();
        System.out.println();
        System.out.println("#" + (this.player1.step() + this.player2.step()));
        System.out.println((this.player1.step() == this.player2.step() ? "*" : " ") + buildPlayerInfo(this.player1));
        System.out.println((this.player1.step() == this.player2.step() ? " " : "*") + buildPlayerInfo(this.player2));
        System.out.println();
        System.out.print("    ");
        for (int i = 0; i < N_COL; i++) {
            System.out.print((i + 1) + "   ");
        }
        System.out.println();
        for (int i = 0; i < N_ROW; i++) {
            System.out.print((i + 1) + (i == 9 ? "  " : "   "));
            for (int j = 0; j < N_COL; j++) {
                System.out.print(this.grid[i][j] + "   ");
            }
            System.out.println();
            System.out.println();
        }
        System.out.println();
        if (this.status.isGameOver()) {
            if (this.status.isWinning()) {
                System.out
                    .println(this.status.winner + " is the WINNER(" + this.status.winningSet + "), congratulations!");
            } else {
                System.out.println("You both are so good, but game is draw!");
            }
            System.out.println("Summary:");
            double timesOfP1 = this.player1.time() * 1.0 / 1E9;
            double timesOfP2 = this.player2.time() * 1.0 / 1E9;
            System.out.printf("%s   Step: %d   Total Time: %3.1fs   Avg Time: %.1fs\n", "*" + this.player1,
                              this.player1.step(), timesOfP1, timesOfP1 / this.player1.step());
            System.out.printf("%s   Step: %d   Total Time: %3.1fs   Avg Time: %.1fs\n", " " + this.player2,
                              this.player2.step(), timesOfP2, timesOfP2 / this.player2.step());
        }
    }

    private String buildPlayerInfo(Player player) {
        return player + "  Step: " + player.step() + "  Last Pos: " + player.getLastPos();
    }

    private void pause() {
        System.out.println("[Print any key to continue]");
        final Scanner cin = new Scanner(System.in);
        cin.useDelimiter("\n");
        cin.nextLine();
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

        Pos(int index) {
            this.index = index;
            this.row = index / N_ROW;
            this.col = index - this.row * N_ROW;
        }

        @Override
        public String toString() {
            return "(" + (this.row + 1) + ", " + (this.col + 1) + ")";
        }
    }


    @Getter
    @AllArgsConstructor
    @ToString
    public static class GameStatus {
        private Status status;
        private Player winner;
        private Set<Pos> winningSet;

        public boolean isGameOver() {
            return this.status != Status.ONGOING;
        }

        public boolean isDraw() {
            return this.status == Status.DRAW;
        }

        public boolean isWinning() {
            return this.status == Status.P1_WIN || this.status == Status.P2_WIN;
        }
    }
}
