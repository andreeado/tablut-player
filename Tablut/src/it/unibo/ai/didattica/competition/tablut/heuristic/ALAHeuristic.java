package it.unibo.ai.didattica.competition.tablut.heuristic;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALAHeuristic {


    private static final char[][] lookupTable;
    private static final Map<String, Integer> features;
    private static final List<int[]> escapes;

    public ALAHeuristic() {
            }
        
            static {
        // Inizializza lookupTable
        lookupTable = new char[][] {
            {'-', 'E', 'E', 'C', 'C', 'C', 'E', 'E', '-'},
            {'E', '-', '-', '-', 'C', '-', '-', '-', 'E'},
            {'E', '-', '-', '-', '-', '-', '-', '-', 'E'},
            {'C', '-', '-', '-', 'N', '-', '-', '-', 'C'},
            {'C', 'C', '-', 'N', 'T', 'N', '-', 'C', 'C'},
            {'C', '-', '-', '-', 'N', '-', '-', '-', 'C'},
            {'E', '-', '-', '-', '-', '-', '-', '-', 'E'},
            {'E', '-', '-', '-', 'C', '-', '-', '-', 'E'},
            {'-', 'E', 'E', 'C', 'C', 'C', 'E', 'E', '-'}
        };

        // Inizializza features
        features = new HashMap<>();
        features.put("0,0", 1);
        features.put("0,1", 0);
        features.put("0,2", 0);
        features.put("0,3", 1);
        features.put("0,4", 2);
        features.put("0,5", 1);
        features.put("0,6", 0);
        features.put("0,7", 0);
        features.put("0,8", 1);
        features.put("1,0", 0);
        features.put("1,1", 1);
        features.put("1,2", 1);
        features.put("1,3", 2);
        features.put("1,4", 3);
        features.put("1,5", 2);
        features.put("1,6", 1);
        features.put("1,7", 1);
        features.put("1,8", 0);
        features.put("2,0", 0);
        features.put("2,1", 1);
        features.put("2,2", 2);
        features.put("2,3", 3);
        features.put("2,4", 4);
        features.put("2,5", 3);
        features.put("2,6", 2);
        features.put("2,7", 1);
        features.put("2,8", 0);
        features.put("3,0", 1);
        features.put("3,1", 2);
        features.put("3,2", 3);
        features.put("3,3", 4);
        features.put("3,4", 5);
        features.put("3,5", 4);
        features.put("3,6", 3);
        features.put("3,7", 2);
        features.put("3,8", 1);
        features.put("4,0", 2);
        features.put("4,1", 3);
        features.put("4,2", 4);
        features.put("4,3", 5);
        features.put("4,4", 6);
        features.put("4,5", 5);
        features.put("4,6", 4);
        features.put("4,7", 3);
        features.put("4,8", 2);
        features.put("5,0", 1);
        features.put("5,1", 2);
        features.put("5,2", 3);
        features.put("5,3", 4);
        features.put("5,4", 5);
        features.put("5,5", 4);
        features.put("5,6", 3);
        features.put("5,7", 2);
        features.put("5,8", 1);
        features.put("6,0", 0);
        features.put("6,1", 1);
        features.put("6,2", 2);
        features.put("6,3", 3);
        features.put("6,4", 4);
        features.put("6,5", 3);
        features.put("6,6", 2);
        features.put("6,7", 1);
        features.put("6,8", 0);
        features.put("7,0", 0);
        features.put("7,1", 1);
        features.put("7,2", 1);
        features.put("7,3", 2);
        features.put("7,4", 3);
        features.put("7,5", 2);
        features.put("7,6", 1);
        features.put("7,7", 1);
        features.put("7,8", 0);
        features.put("8,0", 1);
        features.put("8,1", 0);
        features.put("8,2", 0);
        features.put("8,3", 1);
        features.put("8,4", 2);
        features.put("8,5", 1);
        features.put("8,6", 0);
        features.put("8,7", 0);
        features.put("8,8", 1);


        // Inizializza escapes
        escapes = new ArrayList<>();
        escapes.add(new int[] {0, 1});
        escapes.add(new int[] {0, 2});
        escapes.add(new int[] {0, 6});
        escapes.add(new int[] {0, 7});
        escapes.add(new int[] {1, 0});
        escapes.add(new int[] {1, 8});
        escapes.add(new int[] {2, 0});
        escapes.add(new int[] {2, 8});
        escapes.add(new int[] {6, 0});
        escapes.add(new int[] {6, 8});
        escapes.add(new int[] {7, 0});
        escapes.add(new int[] {7, 8});
        escapes.add(new int[] {8, 1});
        escapes.add(new int[] {8, 2});
        escapes.add(new int[] {8, 6});
        escapes.add(new int[] {8, 7});
    }

    private static int[] getKingPosition(State state) {
        //where I saved the int position of the king
        int[] king= new int[2];
        //obtain the board
        State.Pawn[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (state.getPawn(i, j).equalsPawn("K")) {
                    king[0] = i;
                    king[1] = j;
                }
            }
        }
        return king;
    }

    /**
     * Calcola la differenza tra i pezzi bianchi e neri.
     * @param whitePieces Numero di pezzi bianchi.
     * @param blackPieces Numero di pezzi neri.
     * @return Differenza calcolata.
     */
    private static int calculatePieceDifference(int whitePieces, int blackPieces) {
        return 2 * whitePieces - blackPieces;
    }

    /**
     * Restituisce la distanza di Manhattan del re dalla posizione di fuga più vicina.
     * @param kingPos Posizione attuale del re.
     * @return Distanza di Manhattan.
     */
    private static int calculateKingEscapeDistance(int[] kingPos) {
        return lookupTable[kingPos[0]][kingPos[1]] == '-' ? 0 : 1; // Placeholder logico
    }

    /**
     * Calcola il pericolo attorno al re, cioè quante caselle adiacenti sono occupate da nemici o edifici.
     * @param kingPos Posizione del re.
     * @param board La matrice del gioco.
     * @return Metriche di pericolo.
     */
    private static int calculateDangerMetric(int[] kingPos, Pawn[][] board) {
        int dangerMetric = 0;
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} }; // Direzioni (nord, sud, ovest, est)

        for (int[] dir : directions) {
            int newX = kingPos[0] + dir[0];
            int newY = kingPos[1] + dir[1];

            // Controlla se le coordinate sono valide
            if (newX >= 0 && newX <= 8 && newY >= 0 && newY <= 8) {
                if (board[newX][newY] == Pawn.BLACK || lookupTable[newX][newY] == 'C') {
                    dangerMetric++;
                }
            }
        }

        if (lookupTable[kingPos[0]][kingPos[1]] == 'N' || lookupTable[kingPos[0]][kingPos[1]] == 'T'){
            dangerMetric--;
        }

        return dangerMetric;
    }

    /**
     * Trova le vie di fuga disponibili per il re.
     * @param kingPos Posizione del re.
     * @param board La matrice del gioco.
     * @return Lista delle posizioni di fuga disponibili.
     */
    private static int findAvailableEscapes(int[] kingPos, Pawn[][] board) {
        // List<int[]> availableEscapes = new ArrayList<>();
        int counterResult = 0;

        for (int[] escape : escapes) {
            int x = escape[0];
            int y = escape[1];

            // Controlla se la posizione di fuga è vuota
            if (board[x][y] == Pawn.EMPTY) {
                // Controlla il lato opposto rispetto al bordo
                if (!((x == 0 && (board[1][y] == Pawn.BLACK || board[0][y] == Pawn.BLACK)) ||  // Fuga sul bordo superiore
                      (x == 8 && (board[7][y] == Pawn.BLACK || board[8][y] == Pawn.BLACK)) ||  // Fuga sul bordo inferiore
                      (y == 0 && (board[x][1] == Pawn.BLACK || board[x][0] == Pawn.BLACK)) ||  // Fuga sul bordo sinistro
                      (y == 8 && (board[x][7] == Pawn.BLACK || board[x][8] == Pawn.BLACK)))) { // Fuga sul bordo destro
                    counterResult++;
                    // availableEscapes.add(escape);
                }
            }
        }

        return counterResult;
    }

    /**
     * Trova i percorsi liberi dal re alle posizioni di fuga.
     * @param kingPos Posizione del re.
     * @param board La matrice del gioco.
     * @return Lista delle posizioni accessibili.
     */
    private static int findFreePaths(int[] kingPos, Pawn[][] board) {
    
        int x = kingPos[0];
        int y = kingPos[1];
        int freePathsCount = 0;
    
        for (int[] escape : escapes) {
            int ex = escape[0];
            int ey = escape[1];
    
            if (x == ex) { // Stessa riga
                boolean freePath = true;
                for (int col = Math.min(y, ey) + 1; col < Math.max(y, ey); col++) {
                    if (board[x][col] != Pawn.EMPTY || features.get(x + "," + col) == 'C' || features.get(x + "," + col) == 'T') {
                        freePath = false;
                        break;
                    }
                }
                if (freePath) {
                    freePathsCount++;
                }
            } else if (y == ey) { // Stessa colonna
                boolean freePath = true;
                for (int row = Math.min(x, ex) + 1; row < Math.max(x, ex); row++) {
                    if (board[row][y] != Pawn.EMPTY || features.get(row + "," + y) == 'C' || features.get(row + "," + y) == 'T') {
                        freePath = false;
                        break;
                    }
                }
                if (freePath) {
                    freePathsCount++;
                }
            }
        }
    
        return freePathsCount;
    }

    /**
     * Calculates the mobility of the king (number of valid moves).
     * 
     * @param kingPos The position of the king as an array [row, column].
     * @param board   The game board represented as a 2D array of Pawns.
     * @return The number of valid moves for the king.
     */
    private static int kingmobility(int[] kingPos, Pawn[][] board) {
        int mobility = 0;
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} }; // Up, down, left, right

        for (int[] dir : directions) {
            int newX = kingPos[0];
            int newY = kingPos[1];

            while (true) {
                newX += dir[0];
                newY += dir[1];

                // Check if the new position is within bounds
                if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) {
                    break;
                }

                // Stop if the square is occupied
                if (board[newX][newY] != Pawn.EMPTY) {
                    break;
                }

                // Stop if the king reaches an invalid area (camps or throne)
                if (lookupTable[newX][newY] == 'C' || lookupTable[newX][newY] == 'T') {
                    break;
                }

                // Valid move
                mobility++;
            }
        }

        return mobility;
    }


    public float evaluate(State state) {
        int[] kingPos = getKingPosition(state);
        // Check terminal states first
        if (state.getTurn().equals(State.Turn.BLACKWIN)) {
            return Float.NEGATIVE_INFINITY;
        } else if (state.getTurn().equals(State.Turn.WHITEWIN)) {
            return Float.POSITIVE_INFINITY;
        } else if (state.getTurn().equals(State.Turn.DRAW)) {
            return 0;
        }
        // Get king position and check if king is captured
        if (kingPos == null || (kingPos[0] == -1 && kingPos[1] == -1)) {
            return Float.NEGATIVE_INFINITY; // King is captured
        }
        Pawn[][] board = state.getBoard();
        int whitePieces = state.getNumberOf(Pawn.WHITE);
        int blackPieces= state.getNumberOf(Pawn.BLACK);
        float[] input = new float[] {
                calculatePieceDifference(state.getNumberOf(Pawn.WHITE), state.getNumberOf(Pawn.BLACK)),
                calculateKingEscapeDistance(kingPos),
                calculateDangerMetric(kingPos, state.getBoard()),
                findAvailableEscapes(kingPos, state.getBoard()),
                findFreePaths(kingPos, state.getBoard()),
                kingmobility(kingPos, state.getBoard())
        };
        float heuristic;
        heuristic =  NeuralNetwork.getInstance().predict(input);
        return heuristic;
    }
}
