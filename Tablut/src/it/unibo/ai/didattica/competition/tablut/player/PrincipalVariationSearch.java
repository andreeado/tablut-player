package it.unibo.ai.didattica.competition.tablut.player;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.heuristic.ALAHeuristic;
import java.util.List;


public class PrincipalVariationSearch {
    private static final float INFINITY = 1000000;
    private static final float NEG_INFINITY = -INFINITY;
    private final int maxDepth;
    private final ALAHeuristic heuristic;
    private Game game;
    private static final long TIME_BUFFER_MS = 2000; // 2 second buffer for safety

    public PrincipalVariationSearch(Game game) {
        this.maxDepth = 5;
        this.heuristic = new ALAHeuristic();
        this.game = game;
    }

    /**
     * Finds the best move using Iterative Deepening and Principal Variation Search.
     *
     * @param  currentState      The current game state.
     * @param validMoves A list of valid moves from the current state.
     * @param isMaxPlayer Indicates whether the current player is the maximizing player.
     * @return The best move determined by the search.
     */
    public Action findBestMove(State currentState, List<Action> validMoves, boolean isMaxPlayer){
        long startTime = System.currentTimeMillis();
        long timeLimit = startTime + 58000; // 58 seconds (leaving 2s buffer)
        Action bestMove = validMoves.get(0);
        Action currentBestMove = bestMove;
        int currentDepth = 1;

        try {
            // Iterative deepening
            while (System.currentTimeMillis() < timeLimit) {
                currentBestMove = findMoveAtDepth(currentState, validMoves, isMaxPlayer, currentDepth, timeLimit);
                if (currentBestMove != null) {
                    bestMove = currentBestMove; // Update best move if search completed
                    System.out.println("Completed search at depth: " + currentDepth);
                    currentDepth++;
                } else {
                    // Search at this depth didn't complete in time
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Search interrupted, using best move found so far");
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Search took " + (endTime - startTime) + "ms, reached depth " + (currentDepth - 1));

        return bestMove;
    }

    private Action findMoveAtDepth(State currentState, List<Action> validMoves, boolean isMaxPlayer,
                                   int depth, long timeLimit) {
        float bestScore = NEG_INFINITY;
        Action bestMove = null;
        float alpha = NEG_INFINITY;
        float beta = INFINITY;

        for (Action move : validMoves) {
            System.out.println("MOVE: " + move);
            if (System.currentTimeMillis() >= timeLimit) {
                return null; // Time is running out, abort this depth
            }

            try {
                State newState = game.checkMove(currentState, move);
                float score = -pvs(newState, depth - 1, -beta, -alpha, false, !isMaxPlayer, timeLimit);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                    alpha = Math.max(alpha, score);
                }
            } catch (Exception e) {
                System.err.println("Error evaluating move: " + move);
            }
        }

        return bestMove;
    }
    /**
     * Principal Variation Search (PVS) recursive function.
     *
     * @param state       The current game state.
     * @param depth       The remaining depth of the search.
     * @param alpha       The alpha bound for alpha-beta pruning.
     * @param beta        The beta bound for alpha-beta pruning.
     * @param isFirstNode Indicates whether this is the first node in the subtree.
     * @param isMaxPlayer Indicates whether the current player is the maximizing player.
     * @return The best score for the current player.
     */
    private float pvs(State state, int depth, float alpha, float beta, boolean isFirstNode,
                      boolean isMaxPlayer, long timeLimit) {
        if (System.currentTimeMillis() >= timeLimit - TIME_BUFFER_MS) {
            throw new RuntimeException("Time limit reached");
        }

        if (depth == 0 || isTerminalState(state)) {
            return isMaxPlayer ? heuristic.evaluate(state) : -heuristic.evaluate(state);
        }

        List<Action> possibleMoves = game.getValidator().getLegalActions();
        if (possibleMoves.isEmpty()) {
            return isMaxPlayer ? NEG_INFINITY : INFINITY;
        }

        float bestScore = NEG_INFINITY;
        boolean isFirstMove = true;

        for (Action move : possibleMoves) {
            if (System.currentTimeMillis() >= timeLimit - TIME_BUFFER_MS) {
                throw new RuntimeException("Time limit reached");
            }

            try {
                State newState = game.checkMove(state, move);
                float score;

                if (isFirstMove) {
                    score = -pvs(newState, depth - 1, -beta, -alpha, false, !isMaxPlayer, timeLimit);
                    isFirstMove = false;
                } else {
                    score = -pvs(newState, depth - 1, -alpha - 1, -alpha, false, !isMaxPlayer, timeLimit);
                    if (score > alpha && score < beta) {
                        score = -pvs(newState, depth - 1, -beta, -alpha, false, !isMaxPlayer, timeLimit);
                    }
                }

                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, score);

                if (alpha >= beta) {
                    break;
                }
            } catch (RuntimeException e) {
                throw e; // Propagate time limit exceptions
            } catch (Exception e) {
                System.err.println("Error in PVS search");
            }
        }

        return bestScore;
    }

    private boolean isTerminalState(State state) {
        return state.getTurn().equals(State.Turn.BLACKWIN) ||
                state.getTurn().equals(State.Turn.WHITEWIN) ||
                state.getTurn().equals(State.Turn.DRAW);
    }
    /**
     * Reorders the moves to prioritize those in the principal variation.
     *
     * @param moves              The list of valid moves to reorder.
     * @param principalVariation The principal variation from the previous iteration.
     */
    private void reorderMoves(List<Action> moves, List<Action> principalVariation) {
        if (principalVariation.isEmpty()) return;

        Action bestMove = principalVariation.get(0);
        moves.sort((a, b) -> a.equals(bestMove) ? -1 : (b.equals(bestMove) ? 1 : 0));
    }
}

