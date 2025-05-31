package com.example.connect4_minimax;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.*;

public class Connect4_AI_Controller implements Initializable {

    @FXML
    private GridPane gridPane;
    @FXML
    private Label label;

    private Circle[][] actualBoard;
    private Button[] buttons;

    private final String AI_PLAYER = "RED";
    private final String HUMAN_PLAYER = "BLUE";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.actualBoard = new Circle[7][6];
        buildBoard();
        this.buttons = new Button[7];
        buildButtons();
    }

    @FXML
    private void mouseClicked(ActionEvent event) {
        for (int i = 0; i < 7; i++) {
            if(event.getSource() == buttons[i]) {

                if(label.isVisible()) {
                    reset();
                }

                handleHumanPlayer(i);

                String result = checkWin(actualBoard);
                if (result != null) {
                    gameOver(result);
                } else {
                    handleAIPlayer();
                }
            }
        }
    }

    //Human ----------------------------------------------------------
    private void handleHumanPlayer(int column) {
        for (int i = 5; i > -1; i--) {
            if (actualBoard[column][i].getFill() == WHITE) {
                actualBoard[column][i].setFill(Color.valueOf(HUMAN_PLAYER));
                break;
            }
        }
    }

    //AI ----------------------------------------------------------
    private void handleAIPlayer() {
        int bestScore = Integer.MIN_VALUE;
        int rowPosition = -1;
        int columnPosition = -1;

        for (int column = 0; column < 7; column++) {
            for (int row = 5; row >= 0; row--) {
                if (actualBoard[column][row].getFill() == WHITE && checkPlaceable(actualBoard, column, row)) {
                    actualBoard[column][row].setFill(Color.valueOf(AI_PLAYER));
                    int score = minimax(actualBoard, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                    actualBoard[column][row].setFill(WHITE);
                    if(bestScore < score) {
                        bestScore = score;
                        rowPosition = row;
                        columnPosition = column;
                    }
                    break;
                }
            }
        }

        actualBoard[columnPosition][rowPosition].setFill(Color.valueOf(AI_PLAYER));

        String result = checkWin(actualBoard);
        if (result != null) {
            gameOver(result);
        }
    }

    private int minimax(Circle[][] board, int depth, int alpha, int beta, boolean isMaxing) {

        String result = checkWin(board);
        if(result != null) {
            if (result.equals(AI_PLAYER)) return 100 - depth;
            if (result.equals(HUMAN_PLAYER)) return depth - 100;
            return 0; //If draw
        }

        if (depth >= 7) { 
            return evaluateHeuristics(board);
        }

        if(isMaxing) {
            int bestScore = Integer.MIN_VALUE;
            for (int column = 0; column < 7; column++) {
                for (int row = 5; row >= 0; row--) {
                    if(board[column][row].getFill() == WHITE && checkPlaceable(board, column, row)) {
                        board[column][row].setFill(Color.valueOf(AI_PLAYER));
                        int score = minimax(board, depth + 1, alpha, beta, false);
                        board[column][row].setFill(WHITE);
                        bestScore = Math.max(score,bestScore);
                        alpha = Math.max(alpha, bestScore);
                        if(beta <= alpha) {
                            return bestScore;
                        }
                        break;
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int column = 0; column < 7; column++) {
                for (int row = 5; row >= 0; row--) {
                    if(board[column][row].getFill() == WHITE && checkPlaceable(board, column, row)) {
                        board[column][row].setFill(Color.valueOf(HUMAN_PLAYER));
                        int score = minimax(board, depth + 1, alpha, beta, true);
                        board[column][row].setFill(WHITE);
                        bestScore = Math.min(score, bestScore);
                        beta = Math.min(beta, bestScore);
                        if(beta <= alpha) {
                            return bestScore;
                        }
                        break;
                    }
                }
            }
            return bestScore;
        }
    }

    //Heuristics -----------------------------------------------------
    private int evaluateHeuristics(Circle[][] board) {
        int score = 0;
        Circle[] circles = new Circle[4];

        //Evaluating the Middle Column
        for (int row = 5; row > -1; row--) {
            if (board[3][row].getFill() == Color.valueOf(AI_PLAYER)) {
                score += 3;
            } else if (board[3][row].getFill() == Color.valueOf(AI_PLAYER)){
                score -= 3;
            }
        }

        //Evaluating the Columns
        for (int column = 0; column < 7; column++) {
            for (int j = 5; j > 2; j--) {
                for (int i = 0; i < 4; i++) {
                    circles[i] = board[column][j - i];
                }
                score += evaluate4Circles(circles);
            }
        }

        //Evaluating the Rows
        for (int row = 5; row > -1; row--) {
            for (int j = 0; j < 4; j++) {
                for (int i = 0; i < 4; i++) {
                    circles[i] = board[j + i][row];
                }
                score += evaluate4Circles(circles);
            }
        }

        //Evaluating the Diagonals
        for (int column = 0; column < 7; column++) {
            for (int row = 5; row > 2; row--) {
                if (column < 4) { //Diagonals going Left to Right
                    for (int i = 0; i < 4; i++) {
                        circles[i] = board[column + i][row - i];
                    }
                    score += evaluate4Circles(circles);
                }
                if (column > 2) { //Diagonals going Right to Left
                    for (int i = 0; i < 4; i++) {
                        circles[i] = board[column - i][row - i];
                    }
                    score += evaluate4Circles(circles);
                }
            }
        }
        return score;
    }
    private int evaluate4Circles(Circle[] circles) {
        int score = 0;
        int aiCount = 0;
        int humanCount = 0;
        int emptyCount = 0;

        for(Circle c: circles) {
            String type = getColorFromHex(c.getFill());
            if(type != null) {
                if (type.equals(AI_PLAYER)) {
                    aiCount++;
                } else if (type.equals(HUMAN_PLAYER)) {
                    humanCount++;
                }
            } else {
                emptyCount++;
            }
        }

        if(aiCount == 4) {
            score += 100;
        } else if (aiCount == 3 && emptyCount == 1) {
            score += 5;
        } else if (aiCount == emptyCount) {
            score += 2;
        }

        if(humanCount == 4) {
            score -= 100;
        } else if (humanCount == 3 && emptyCount == 1) {
            score -= 5;
        } else if (humanCount == emptyCount) {
            score -= 2;
        }

        return score;
    }

    private String checkWin(Circle[][] board) {
        for (int column = 0; column < 7; column++) { //Checking Columns (Left to Right, Bottom to top)
            for (int j = 5; j > 2; j--) {
                if(board[column][j].getFill() != WHITE && checkEqual(board[column][j], board[column][j-1], board[column][j-2], board[column][j-3])) {
                    return getColorFromHex(board[column][j].getFill());
                }
            }
        }

        for (int row = 5; row > -1; row--) { //Checking Rows (Bottom to top, Left to Right)
            for (int j = 0; j < 4; j++) {
                if(board[j][row].getFill() != WHITE && checkEqual(board[j][row], board[j+1][row], board[j+2][row], board[j+3][row])) {
                    return getColorFromHex(board[j][row].getFill());
                }
            }
        }

        for (int column = 0; column < 7; column++) { //Checking Diagonals
            for (int j = 5; j > 2; j--) {
                if (board[column][j].getFill() != WHITE) {
                    if (column < 4) { //Diagonals going Left to Right
                        if (checkEqual(board[column][j], board[column + 1][j - 1], board[column + 2][j - 2], board[column + 3][j - 3])) {
                            return getColorFromHex(board[column][j].getFill());
                        }
                    }
                    if (column > 2){ //Diagonals going Right to Left
                        if (checkEqual(board[column][j], board[column - 1][j - 1], board[column - 2][j - 2], board[column - 3][j - 3])) {
                            return getColorFromHex(board[column][j].getFill());
                        }
                    }
                }
            }
        }

        //Checking for draw
        if(Arrays.stream(board).flatMap(Arrays::stream).allMatch(circle -> circle.getFill() != WHITE)) {
            return "DRAW";
        }
        return null;
    }

    private boolean checkEqual(Circle c1, Circle c2, Circle c3, Circle c4) {
        return c1.getFill().equals(c2.getFill()) && c2.getFill().equals(c3.getFill()) && c3.getFill().equals(c4.getFill());
    }

    private boolean checkPlaceable(Circle[][] board, int column, int row) {
        if (row == 5) {
            return true;
        }
        return board[column][row + 1].getFill() != WHITE;
    }

    private void buildButtons() { //Creates a list of all the Buttons in the GridPane
        for (int i = 0; i < 7; i++) {
            buttons[i] = (Button) getNodeFromGridPane(gridPane, i, 0);
        }
    }

    private void buildBoard() { //Creates a 2D array of the Connect4s board
        for (int i = 0; i < 7; i++) {
            for (int j = 1; j < 7; j++) {
                actualBoard[i][j - 1] = (Circle) getNodeFromGridPane(gridPane, i, j);
            }
        }
    }
    private Node getNodeFromGridPane(GridPane gridPane, int column, int row) {
        for (Node node : gridPane.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            if (colIndex == column && rowIndex == row) {
                return node;
            }
        }
        return null;
    }
    private void gameOver(String result) {

        if(result.equals("DRAW")) {
            label.setText(result);
        } else {
            label.setText(result + ": WINS");
        }
        label.setTextFill(Color.valueOf(result));
        label.setVisible(true);
    }
    private void reset() {
        label.setVisible(false);

        Arrays.stream(actualBoard).flatMap(Arrays::stream)
                .filter(circle -> circle.getFill() != WHITE)
                .forEach(circle -> {
                    circle.setFill(WHITE);
                });
    }
    private String getColorFromHex(Paint color) {
        if(color == RED) return "RED";
        if(color == BLUE) return "BLUE";
        return null;
    }
}