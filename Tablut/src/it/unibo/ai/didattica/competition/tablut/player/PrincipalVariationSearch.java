package it.unibo.ai.didattica.competition.tablut.player;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.heuristic.ALAHeuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrincipalVariationSearch {
    private static final int INFINITY = 1000000;
    private static final int NEG_INFINITY = -INFINITY;
    private final int maxDepth;
    private final ALAHeuristic heuristic;
    private Game game;

    public PrincipalVariationSearch() {
        this.maxDepth = 5;
        this.heuristic = new ALAHeuristic();
        this.game = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
    }

    public PrincipalVariationSearch(ALAHeuristic heuristic, int maxDepth) {
        this.heuristic = heuristic;
        this.maxDepth = maxDepth;
    }
    public Action findBestMove(State state, List<Action> validMoves, boolean isMaxPlayer){
        Action bestMove = null;
        int bestScore = NEG_INFINITY;
        boolean isFirstMove = true;
        int alpha = NEG_INFINITY;
        int beta = INFINITY;
        for (Action move : validMoves) {

        }
        return bestMove;
    }
    private int pvs(int depth, int alpha, int beta, boolean isMaxPlayer, State state){
        /*if (depth == 0 || state.isTerminal()) {
            return heuristic.evaluateState(state);
        }*/
        int bestScore = NEG_INFINITY;
        return 1;
    }
    private List<Action> sortMoves(State state, List<Action> moves, boolean isMaxPlayer) throws Exception{
        Map<Action, Integer> scoredMoves = new HashMap<>();
        List<Action> result = new ArrayList<>();

        for (Action move : moves) {
            State oldState = state;
            State newState = game.checkMove(oldState, move);
            //int score = isMaxPlayer ? heuristic.evaluate(newState) : -heuristic.evaluate(newState);
            int score = 1;
            scoredMoves.put(move, score);
        }
        if (isMaxPlayer) {
            result = scoredMoves.entrySet().stream()
                    .sorted(Map.Entry.<Action, Integer>comparingByValue().reversed()) // Ordina per valore (decrescente)
                    .map(Map.Entry::getKey) // Estrai solo le chiavi (Action)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        else{
            result = scoredMoves.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue()) // Ordina per valore (crescente)
                    .map(Map.Entry::getKey) // Estrai solo le chiavi (Action)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return result;
    }


}
