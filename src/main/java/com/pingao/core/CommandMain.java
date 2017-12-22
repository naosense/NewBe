package com.pingao.core;

import java.util.Scanner;


/**
 * Created by pingao on 2017/12/9.
 */
public class CommandMain {
    private static final Scanner CIN = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Hello, my name is NewBe, please choose game mode (1/2)");
        System.out.println("1: Computer vs Human   2: Computer vs Computer");
        int mode = CIN.nextInt();
        while (mode != 1 && mode != 2) {
            System.out.println("Game mode must be 1 or 2");
            mode = CIN.nextInt();
        }

        if (mode == 1) {
            System.out.println("Please choose game level (1~5)");
            int level = CIN.nextInt();
            while (level < 1 || level > 5) {
                System.out.println("Level must be between 1 and 5");
                level = CIN.nextInt();
            }

            System.out.println("Do you want to be first? (y/n)");
            String first = CIN.next();
            while (!first.equalsIgnoreCase("y") && !first.equalsIgnoreCase("n")) {
                System.out.println("Input 'y' or 'n' no");
                first = CIN.next();
            }

            System.out.println("Perfect! Remember player marker: You:'X' Computer:'O'. Ready? (y/n)");
            while (!CIN.next().equalsIgnoreCase("y")) {
                System.out.println("Input 'y' to start game");
            }

            Player computer = new ComputerPlayer('O', level);
            Player human = new HumanCommandPlayer('X');
            Board board;
            if (first.equalsIgnoreCase("y")) {
                board = new Board(human, computer);
            } else {
                board = new Board(computer, human);
            }

            board.start();
        } else {
            System.out.println("Please choose game level for Computer1 (1~5)");
            int level1 = CIN.nextInt();
            while (level1 < 1 || level1 > 5) {
                System.out.println("Level must be between 1 and 4");
                level1 = CIN.nextInt();
            }

            System.out.println("Please choose game level for Computer2 (1~5)");
            int level2 = CIN.nextInt();
            while (level2 < 1 || level2 > 5) {
                System.out.println("Level must be between 1 and 5");
                level2 = CIN.nextInt();
            }

            System.out.println("Perfect! Remember player marker: Computer1 'X' Computer2 'O'. Ready? (y/n)");
            while (!CIN.next().equalsIgnoreCase("y")) {
                System.out.println("Input 'y' to start game");
            }

            Player computer1 = new ComputerPlayer('X', level1);
            Player computer2 = new ComputerPlayer('O', level2);
            Board board = new Board(computer1, computer2);
            board.start();
        }
    }
}
