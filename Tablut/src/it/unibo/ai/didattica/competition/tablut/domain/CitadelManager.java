package it.unibo.ai.didattica.competition.tablut.domain;

public class CitadelManager {
    private static final boolean[][] CITADELS = new boolean[9][9];
    private static final int[][] CITADEL_COORDINATES = {
            {0, 3}, {0, 4}, {0, 5}, {1, 4},  // Top
            {3, 0}, {4, 0}, {5, 0}, {4, 1},  // Left
            {3, 8}, {4, 8}, {5, 8}, {4, 7},  // Right
            {8, 3}, {8, 4}, {8, 5}, {7, 4}   // Bottom
    };

    // Initialize citadels map
    static {
        for (int[] coord : CITADEL_COORDINATES) {
            CITADELS[coord[0]][coord[1]] = true;
        }
    }

    public static boolean isCitadel(int row, int col) {
        return CITADELS[row][col];
    }

    public static boolean canEnterCitadel(State.Pawn piece, int fromRow, int fromCol, int toRow, int toCol) {
        // Black pieces can only move between connected citadels
        if (piece == State.Pawn.BLACK) {
            if (isCitadel(toRow, toCol)) {
                if (!isCitadel(fromRow, fromCol)) {
                    return false;
                }
                // Check if citadels are in the same group (adjacent)
                return areCitadelsConnected(fromRow, fromCol, toRow, toCol);
            }
        }
        // White pieces and king cannot enter citadels
        else if (piece == State.Pawn.WHITE || piece == State.Pawn.KING) {
            return !isCitadel(toRow, toCol);
        }

        return true;
    }

    private static boolean areCitadelsConnected(int fromRow, int fromCol, int toRow, int toCol) {
        // Check if citadels are in the same group (top, bottom, left, or right)
        if (fromRow == 0 && toRow == 0) return true; // Top citadels
        if (fromRow == 8 && toRow == 8) return true; // Bottom citadels
        if (fromCol == 0 && toCol == 0) return true; // Left citadels
        if (fromCol == 8 && toCol == 8) return true; // Right citadels

        // Check adjacent citadels
        return Math.abs(fromRow - toRow) <= 1 && Math.abs(fromCol - toCol) <= 1;
    }

    public static boolean canCaptureNearCitadel(int row, int col) {
        // Special cases where captures near citadels are not allowed
        return !(row == 4 && col == 8) && // Right center
                !(row == 4 && col == 0) && // Left center
                !(row == 0 && col == 4) && // Top center
                !(row == 8 && col == 4);   // Bottom center
    }
}
