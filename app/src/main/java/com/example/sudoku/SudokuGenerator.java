package com.example.sudoku;

import java.util.Random;

public class SudokuGenerator {

    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int EMPTY_CELL = 0;
    private int[][] board;

    public int[][] getBoard() {
        return board;
    }

    public SudokuGenerator() {
        board = new int[SIZE][SIZE];
        generateSudoku();
    }

    public void generateSudoku() {
        fillValues();
        removeValues();
    }

    private void fillValues() {
        fillDiagonal();
        fillRemaining(0, SUBGRID_SIZE);
    }

    private void fillDiagonal() {
        for (int i = 0; i < SIZE; i = i + SUBGRID_SIZE) {
            fillSubgrid(i, i);
        }
    }

    private boolean fillSubgrid(int row, int col) {
        Random rand = new Random();
        int num;
        boolean[] used = new boolean[SIZE + 1];
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                do {
                    num = rand.nextInt(SIZE) + 1;
                } while (used[num]);
                used[num] = true;
                board[row + i][col + j] = num;
            }
        }
        return true;
    }

    private boolean fillRemaining(int row, int col) {
        if (row == SIZE - 1 && col == SIZE)
            return true;
        if (col == SIZE) {
            row++;
            col = 0;
        }
        if (board[row][col] != EMPTY_CELL)
            return fillRemaining(row, col + 1);

        for (int num = 1; num <= SIZE; num++) {
            if (isValid(row, col, num)) {
                board[row][col] = num;
                if (fillRemaining(row, col + 1))
                    return true;
                board[row][col] = EMPTY_CELL;
            }
        }
        return false;
    }

    private boolean isValid(int row, int col, int num) {
        return !usedInRow(row, num) && !usedInCol(col, num) && !usedInSubgrid(row - row % SUBGRID_SIZE, col - col % SUBGRID_SIZE, num);
    }

    private boolean usedInRow(int row, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num)
                return true;
        }
        return false;
    }

    private boolean usedInCol(int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == num)
                return true;
        }
        return false;
    }

    private boolean usedInSubgrid(int row, int col, int num) {
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                if (board[row + i][col + j] == num)
                    return true;
            }
        }
        return false;
    }

    private void removeValues() {
        Random rand = new Random();
        int cellsToRemove = SIZE * SIZE / 2; // Adjust the number of cells to remove as needed
        while (cellsToRemove > 0) {
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);
            if (board[row][col] != EMPTY_CELL) {
                board[row][col] = EMPTY_CELL;
                cellsToRemove--;
            }
        }
    }

    public void printSudoku() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

}