package it.unibo.ai.didattica.competition.tablut.player;

import it.unibo.ai.didattica.competition.tablut.domain.Action;

public class ALABlackPlayer extends ALAPlayer{

    @Override
    public Action getNextMove() {
        return pvs.findBestMove(state,validMoves, false);
    }
}
