package it.unibo.ai.didattica.competition.tablut.player;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;

public abstract class ALAPlayer {
    // private AdversarialSearch<State,Action> solver;
    // private Action previousAction = null;
    private double searchTime = 0.0;
    private int movesMade = 0;

    public Action getOptimalAction (State state) {
        // previousAction = solver.makeDecision(state);
        // movesMade++;
        // return previousAction;
        return null;
    }

 
    public Action getOptimalAction(State state, boolean verbose) throws IOException {
        if (verbose) {
            System.out.println("Choosing optimal action...");
        }
        double startTime = System.currentTimeMillis();
        Action result = getOptimalAction(state);
        searchTime = (System.currentTimeMillis() - startTime);
        printMetrics();

        return result;
    }

    public String getMetrics() {
        // Metrics metrics = solver.getMetrics();

        // String message = "Search summary for my move " + movesMade + ":";
        // message += "\n - Maximum depth: " + metrics.get("maxDepth");
        // message += "\n - Nodes expanded: " + metrics.get("nodesExpanded");
        // message += "\n - Action chosen: " + previousAction.toString();
        // message += "\n - time used for search: " + searchTime + " ms";
        // return message;
        return "";
    }

    public void printMetrics() {
        System.out.println(getMetrics());
    }

    public void setSolver() {}
    // public void setSolver(AdversarialSearch<State,Action> solver) {
    //     this.solver = solver;
    // }
}