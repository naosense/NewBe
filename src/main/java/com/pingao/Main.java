package com.pingao;

import java.util.Scanner;

/**
 * Created by pingao on 2017/12/9.
 */
public class Main {
    private static final Scanner cin = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Hello, my name is NewBe, please choose game mode (1/2)");
        System.out.println("1: Computer vs Human   2: Computer vs Computer");
        int mode;
        for (mode = cin.nextInt(); mode != 1 && mode != 2; mode = cin.nextInt()) {
            System.out.println("Game mode must be 1 or 2");
        }

        if (mode == 1) {
            System.out.println("Please choose game level (1~4)");
            int level;
            for (level = cin.nextInt(); level < 1 || level > 4; level = cin.nextInt()) {
                System.out.println("Level must be between 1 and 4");
            }

            System.out.println("Do you want to be first? (y/n)");
            String first;
            for (first = cin.next(); !first.equalsIgnoreCase("y") && !first.equalsIgnoreCase("n"); first = cin.next()) {
                System.out.println("Input 'y' or 'n' no");
            }

            System.out.println("Perfect! Remember player marker: You:'X' Computer:'O'. Ready? (y/n)");
            for (; !cin.next().equalsIgnoreCase("y");) {
                System.out.println("Input 'y' to start game");
            }

            Player computer = new ComputerPlayer('O', level);
            Player human = new HumanPlayer('X');
            Board board;
            if (first.equalsIgnoreCase("y")) {
                board = new Board(human, computer);
            } else {
                board = new Board(computer, human);
            }

            board.start();
        } else {
            System.out.println("Please choose game level for Computer1 (1~4)");
            int level1;
            for (level1 = cin.nextInt(); level1 < 1 || level1 > 4; level1 = cin.nextInt()) {
                System.out.println("Level must be between 1 and 4");
            }

            System.out.println("Please choose game level for Computer2 (1~4)");
            int level2;
            for (level2 = cin.nextInt(); level2 < 1 || level2 > 4; level2 = cin.nextInt()) {
                System.out.println("Level must be between 1 and 4");
            }

            System.out.println("Perfect! Remember player marker: Computer1 'X' Computer2 'O'. Ready? (y/n)");
            for (; !cin.next().equalsIgnoreCase("y");) {
                System.out.println("Input 'y' to start game");
            }

            Player computer1 = new ComputerPlayer('X', level1);
            Player computer2 = new ComputerPlayer('O', level2);
            Board board = new Board(computer1, computer2);
            board.start();
        }
    }
}
