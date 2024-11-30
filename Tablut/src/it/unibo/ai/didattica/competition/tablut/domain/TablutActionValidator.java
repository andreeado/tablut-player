package it.unibo.ai.didattica.competition.tablut.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TablutActionValidator implements ActionValidator {

    private final State state;
    private static final int THRONE_ROW = 4;
    private static final int THRONE_COL = 4;

    public TablutActionValidator(State state) {
        this.state = state;
    }

    @Override
    public boolean isActionLegal(int startRow, int startColumn, int endRow, int endColumn) {
        // controllo estremi
        if (!isWithinBounds(startRow, startColumn) || !isWithinBounds(endRow, endColumn))
            return false;

        // controllo che sia un mio pezzo
        if (!isYourPiece(startRow, startColumn))
            return false;
        // no mosse diagonali
        if (startRow != endRow && startColumn != endColumn)
            return false;
        // controllo se mi muovo
        if (startRow == endRow && startColumn == endColumn)
            return false;
        // controllo ostacoli
        if (!isPathClear(startRow, startColumn, endRow, endColumn))
            return false;

        return true;
    }

    private boolean isYourPiece(int startRow, int startColumn) {
        State.Turn turn = state.getTurn();
        State.Pawn pawn = state.getPawn(startRow, startColumn);
        return (turn == State.Turn.WHITE && pawn == State.Pawn.WHITE) ||
                (turn == State.Turn.WHITE && pawn == State.Pawn.KING) ||
                (turn == State.Turn.BLACK && pawn == State.Pawn.BLACK);
    }

    @Override
    public List<Action> getLegalActionsForPiece(int startRow, int startColumn) {
        List<Action> actions = new ArrayList<>();
        // convert origin to string
        String from = state.getBox(startRow, startColumn);
        String to;
        if (!isYourPiece(startRow, startColumn)) {
            System.out.println("Warning: Attempting to get actions for non-owned piece at " + from);
            return actions;
        }
        // check di tutte le direzioni
        // verso su
        for (int row = startRow - 1; row >= 0; row--) {
            if (isActionLegal(startRow, startColumn, row, startColumn)) {
                to = state.getBox(row, startColumn); // Convert end position to string
                try {
                    actions.add(new Action(from, to, state.getTurn()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        // verso giù
        for (int row = startRow + 1; row < state.getBoard().length; row++) {
            if (isActionLegal(startRow, startColumn, row, startColumn)) {
                to = state.getBox(row, startColumn);
                try {
                    actions.add(new Action(from, to, state.getTurn()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        // verso sinistra
        for (int col = startColumn - 1; col >= 0; col--) {
            if (isActionLegal(startRow, startColumn, startRow, col)) {
                to = state.getBox(startRow, col);
                try {
                    actions.add(new Action(from, to, state.getTurn()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        // verso destra
        for (int col = startColumn + 1; col < state.getBoard()[0].length; col++) {
            if (isActionLegal(startRow, startColumn, startRow, col)) {
                to = state.getBox(startRow, col);
                try {
                    actions.add(new Action(from, to, state.getTurn()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        return actions;
    }

    @Override
    public List<Action> getLegalActions() {
        List<Action> actions = new ArrayList<>();
        State.Pawn[][] board = state.getBoard();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (isYourPiece(row, col)) {
                    // controlla le azioni per quel pezzo
                    List<Action> pieceActions = getLegalActionsForPiece(row, col);
                    // Debug log
                    // for (Action action : pieceActions) {
                    //     System.out.println("Validator generated move: " + action.toString());
                    // }
                    actions.addAll(pieceActions);
                }
            }
        }

        return actions;
    }

    private boolean isWithinBounds(int row, int column) {
        int boardSize = state.getBoard().length;
        return row >= 0 && row < boardSize && column >= 0 && column < boardSize;
    }
    private boolean isPathClear(int startRow, int startColumn, int endRow, int endColumn) {
        State.Pawn movingPiece = state.getPawn(startRow, startColumn);
        /*------CONTROLLO CELLA DI ARRIVO------*/
        // controllo trono
        if (endRow == THRONE_ROW && endColumn == THRONE_COL) {
            return false;
        }
        // controllo casella occupata
        if (!state.getPawn(endRow, endColumn).equalsPawn(State.Pawn.EMPTY.toString()))
            return false;
        // controllo cittadelle
        if (!CitadelManager.canEnterCitadel(movingPiece, startRow, startColumn, endRow, endColumn)) {
            return false;
        }

        /*------CONTROLLO PERCORSO------*/
        //fai condizioni che controllano se sto scavalcando qualsiasi cosa
        if (startRow == endRow) {  // orizzontale
            int start = Math.min(startColumn, endColumn);
            int end = Math.max(startColumn, endColumn);
            for (int col = start + 1; col < end; col++) {
                if (isObstacle(startRow, col)) {
                    return false;
                }
            }
        } else {  // verticale
            int start = Math.min(startRow, endRow);
            int end = Math.max(startRow, endRow);
            for (int row = start + 1; row < end; row++) {
                if (isObstacle(row, startColumn)) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean isObstacle(int row, int col) {
        // Check if space is occupied
        if (!state.getPawn(row, col).equalsPawn(State.Pawn.EMPTY.toString())) {
            return true;
        }
        // check throne
        if (row == THRONE_ROW && col == THRONE_COL) {
            return true;
        }
        // Check if it's a citadel that can't be crossed
        if (CitadelManager.isCitadel(row, col)) {
            State.Pawn movingPiece = state.getPawn(row, col);
            if (movingPiece != State.Pawn.BLACK || !CitadelManager.canEnterCitadel(movingPiece, row, col, row, col)) {
                return true;
            }
        }
        return false;
    }
}
