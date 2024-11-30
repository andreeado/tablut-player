package it.unibo.ai.didattica.competition.tablut.player;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;
import java.util.List;

public abstract class ALAPlayer {
    protected State currentState;
    protected List<Action> validMoves;
    protected PrincipalVariationSearch pvs;
    protected Game gameRules;
    protected boolean isMaxPlayer;
    protected int timeout;

    public ALAPlayer(boolean isMaxPlayer) {
        this.gameRules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
        this.pvs = new PrincipalVariationSearch(gameRules);
        this.isMaxPlayer = isMaxPlayer;
    }

    public void setState(State state, List<Action> validMoves) {
        this.currentState = state;
        this.validMoves = validMoves;
    }

    public void setTimer(int timeout) {
        this.timeout = timeout;
    }

    public Action getNextMove(){
        if (currentState == null || validMoves == null || validMoves.isEmpty()) {
            throw new IllegalStateException("State or valid moves not properly initialized");
        }
        return pvs.findBestMove(currentState, validMoves, isMaxPlayer, timeout);
    }
}