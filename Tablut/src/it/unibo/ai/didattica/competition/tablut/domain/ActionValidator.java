package it.unibo.ai.didattica.competition.tablut.domain;

import java.util.List;

public interface ActionValidator {
    /**
     * Validates if a move is legal based on the current game state.
     *
     * @param startRow    The starting row of the move.
     * @param startColumn The starting column of the move.
     * @param endRow      The target row of the move.
     * @param endColumn   The target column of the move.
     * @return true if the move is legal, false otherwise.
     */

    public boolean isActionLegal(int startRow, int startColumn, int endRow, int endColumn);

    public List<Action> getLegalActionsForPiece(int startRow, int startColumn);

    public List<Action> getLegalActions();
}
