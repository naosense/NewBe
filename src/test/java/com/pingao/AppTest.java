package com.pingao;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest
    extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {

        Map<Integer, List<Board.Status>> map = new HashMap<>();
        for (int level = 1; level < 4; level++) {
            List<Board.Status> statuses = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                Player computer1 = new ComputerPlayer('X', level);
                Player computer2 = new ComputerPlayer('O', level);
                Board board = new Board(computer1, computer2);
                board.start();
                statuses.add(board.status().getStatus());
            }
            map.put(level, statuses);
        }
        System.out.println("Level   p1   p2   draw");
        for (Map.Entry<Integer, List<Board.Status>> entry : map.entrySet()) {
            List<Board.Status> statuses = entry.getValue();
            long p1winCount = statuses.stream().filter(s -> s == Board.Status.P1_WIN).count();
            long drawCount = statuses.stream().filter(s -> s == Board.Status.DRAW).count();
            System.out.println(entry.getKey() + "   " + p1winCount + "   " + (50 - p1winCount - drawCount) + "   " + drawCount);
        }
        assertTrue(true);
    }
}
