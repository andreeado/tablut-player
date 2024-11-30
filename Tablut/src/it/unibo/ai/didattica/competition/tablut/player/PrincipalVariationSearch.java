package it.unibo.ai.didattica.competition.tablut.player;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.heuristic.ALAHeuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PrincipalVariationSearch {
    private static final float INFINITY = 1000000;
    private static final float NEG_INFINITY = -INFINITY;
    private final ALAHeuristic heuristic;
    private final Game game;
    private static final long TIME_BUFFER_MS = 2000; // 2 second buffer
    private List<Action> previousPV;

    public PrincipalVariationSearch(Game game) {
        this.heuristic = new ALAHeuristic();
        this.game = game;
        this.previousPV = new ArrayList<>();
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
                    bestMove = currentBestMove;
                    //System.out.println("Completed search at depth: " + currentDepth);
                    currentDepth++;
                } else {
                    //System.out.println("NOT completed search at depth: " + currentDepth);
                    return bestMove;
                }
            }
        } catch (RuntimeException e) {
            //System.err.println("Search interrupted, using best move found so far at depth "+ currentDepth);
        }

        //long endTime = System.currentTimeMillis();
        //System.out.println("Search took " + (endTime - startTime) + "ms, reached depth " + (currentDepth - 1));
        return bestMove;
    }
    private List<Action> orderMoves(State state, List<Action> moves, boolean isMaxPlayer) {
        Map<Action, Float> moveScores = new HashMap<>();

        // shallow evaluation
        for (Action move : moves) {
            State newState = game.checkMove(state, move);
            float score = heuristic.evaluate(newState);
            // For black, minimize the score
            moveScores.put(move, isMaxPlayer ? score : -score);
        }
        /*System.out.println("\nScores before sorting (" + (isMaxPlayer ? "MAX" : "MIN") + " player):");
        moveScores.entrySet().forEach(entry ->
                System.out.printf("Move: %-40s Score: %f%n", entry.getKey(), entry.getValue())
        );*/

        // Create new list for ordered moves
        List<Action> orderedMoves = new ArrayList<>(moves);

        // Sort moves based on:
        // 1. Previous principal variation (if available)
        // 2. Heuristic evaluation
        orderedMoves.sort((a, b) -> {
            // prioritize moves from previous PV
            boolean aInPV = previousPV.contains(a);
            boolean bInPV = previousPV.contains(b);
            if (aInPV && !bInPV) return -1; // a first
            if (!aInPV && bInPV) return 1; // b first
            if (aInPV) {
                return previousPV.indexOf(a) - previousPV.indexOf(b);
            }

            // Then sort by heuristic score
            float scoreA = moveScores.getOrDefault(a, 0f);
            float scoreB = moveScores.getOrDefault(b, 0f);
            if (isMaxPlayer) {
                return Float.compare(scoreB, scoreA); // Higher scores first for Max
            } else {
                return Float.compare(scoreA, scoreB); // Lower scores first for Min
            }
        });
        // Print final ordering
        /*System.out.println("\nFinal move order:");
        for (Action move : orderedMoves) {
            System.out.printf("Move: %-40s Score: %f %s\n",
                    move,
                    moveScores.get(move),
                    previousPV.contains(move) ? "(PV Move)" : "");
        }*/
        return orderedMoves;
    }

    private Action findMoveAtDepth(State currentState, List<Action> validMoves, boolean isMaxPlayer,
                                   int depth, long timeLimit) {
        float bestScore = NEG_INFINITY;
        Action bestMove = null;
        float alpha = NEG_INFINITY;
        float beta = INFINITY;
        // Order moves before starting the search
        List<Action> orderedMoves = orderMoves(currentState, validMoves, isMaxPlayer);
        List<Action> currentPV = new ArrayList<>();

        for (Action move : orderedMoves) {
            //System.out.println("MOVE: " + move);
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
                    // Update PV List
                    currentPV.clear();
                    currentPV.add(move);
                }
            } catch (Exception e) {
                //System.err.println("Error evaluating move: " + e.getMessage());
            }
        }
        // Store current PV for next iteration
        if (bestMove != null) {
            previousPV = currentPV;
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
            float score = heuristic.evaluate(state);
            //System.out.println("Depth " + depth + " Evaluation: " + score);
            //System.out.println("State at leaf:\n" + state.toString());
            return isMaxPlayer ? score : -score;
        }

        List<Action> possibleMoves = game.getValidator().getLegalActions();
        //System.out.println("Depth " + depth + " has " + possibleMoves.size() + " moves");

        if (possibleMoves.isEmpty()) {
            return isMaxPlayer ? NEG_INFINITY : INFINITY;
        }

        float bestScore = NEG_INFINITY;
        boolean isFirstMove = true;

        for (Action move : possibleMoves) {
            try {
                State newState = game.checkMove(state, move);
                /*System.out.println("\nDepth " + depth + " trying move: " + move);
                System.out.println("Original state hash: " + System.identityHashCode(state));
                System.out.println("New state hash: " + System.identityHashCode(newState));*/

                //System.out.println("Before move:\n" + state);
                //System.out.println("After move:\n" + newState);
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
            }
        }
        //System.out.println("Depth " + depth + " returning bestScore: " + bestScore);
        return bestScore;
    }

    private boolean isTerminalState(State state) {
        return state.getTurn().equals(State.Turn.BLACKWIN) ||
                state.getTurn().equals(State.Turn.WHITEWIN) ||
                state.getTurn().equals(State.Turn.DRAW);
    }
}
