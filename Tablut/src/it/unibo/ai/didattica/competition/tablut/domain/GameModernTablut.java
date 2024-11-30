package it.unibo.ai.didattica.competition.tablut.domain;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.exceptions.*;

/**
 * Tablut che segue le regole moderne
 *
 */
public class GameModernTablut implements Game {

	private int movesDraw;
	private int movesWithutCapturing;

	public GameModernTablut() {
		this(0);
	}

	public GameModernTablut(int moves) {
		super();
		this.movesDraw = moves;
		this.movesWithutCapturing = 0;
	}

	@Override
	public State checkMoveServer(State state, Action a) throws BoardException, ActionException, StopException, PawnException, DiagonalException, ClimbingException, ThroneException, OccupitedException, ClimbingCitadelException, CitadelException {
		return null;
	}

	@Override
	public State checkMove(State state, Action a) {
		// this.loggGame.fine(a.toString());
		// controllo la mossa


		return null;
	}

	private State movePawn(State state, Action a) {
		State.Pawn pawn = state.getPawn(a.getRowFrom(), a.getColumnFrom());
		State.Pawn[][] newBoard = state.getBoard();
		// State newState = new State();

		// libero il trono o una casella qualunque
		if (a.getColumnFrom() == 4 && a.getRowFrom() == 4) {
			newBoard[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.THRONE;
		} else {
			newBoard[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.EMPTY;
		}

		// metto nel nuovo tabellone la pedina mossa
		newBoard[a.getRowTo()][a.getColumnTo()] = pawn;
		// aggiorno il tabellone
		state.setBoard(newBoard);
		// cambio il turno
		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			state.setTurn(State.Turn.BLACK);
		} else {
			state.setTurn(State.Turn.WHITE);
		}

		return state;
	}

	private State checkCaptureWhite(State state, Action a) {
		// controllo se mangio a destra
		if (a.getColumnTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("B")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("K"))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			this.movesWithutCapturing = -1;
			// this.loggGame.fine("Pedina nera rimossa in:
			// "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
		}
		// controllo se mangio a sinistra
		if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("B")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("K"))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
			this.movesWithutCapturing = -1;
			// this.loggGame.fine("Pedina nera rimossa in:
			// "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
		}
		// controllo se mangio sopra
		if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("B")
				&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("K"))) {
			state.removePawn(a.getRowTo() - 1, a.getColumnTo());
			this.movesWithutCapturing = -1;
			// this.loggGame.fine("Pedina nera rimossa in:
			// "+state.getBox(a.getRowTo()-1, a.getColumnTo()));
		}
		// controllo se mangio sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("B")
				&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("W")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("K"))) {
			state.removePawn(a.getRowTo() + 1, a.getColumnTo());
			this.movesWithutCapturing = -1;
			// this.loggGame.fine("Pedina nera rimossa in:
			// "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
		}
		// controllo se ho vinto
		if ((a.getRowTo() == 0 && a.getColumnTo() == 0) || (a.getRowTo() == 8 && a.getColumnTo() == 0)
				|| (a.getColumnTo() == 8 && a.getRowTo() == 0) || (a.getColumnTo() == 8 && a.getRowTo() == 8)) {
			if (state.getPawn(a.getRowTo(), a.getColumnTo()).equalsPawn("K")) {
				state.setTurn(State.Turn.WHITEWIN);
			}
		}

		// controllo il pareggio
		if (this.movesWithutCapturing >= this.movesDraw
				&& (state.getTurn().equalsTurn("B") || state.getTurn().equalsTurn("W"))) {
			state.setTurn(State.Turn.DRAW);
			// this.loggGame.fine("Stabilito un pareggio per troppe mosse senza
			// mangiare");
		}
		this.movesWithutCapturing++;
		return state;
	}

	// TODO da controllare dove indexOutOfBound se controllo di mangiare il re
	private State checkCaptureBlack(State state, Action a) {
		// controllo se mangio a destra
		if (a.getColumnTo() < state.getBoard().length - 2
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("K"))
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T"))) {
			// nero-re-trono N.B. No indexOutOfBoundException perch� se il re si
			// trovasse sul bordo il giocatore bianco avrebbe gi� vinto
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("K")
					&& state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")) {
				// ho circondato il re?
				if (state.getPawn(a.getRowTo() + 1, a.getColumnTo() + 1).equalsPawn("B")
						&& state.getPawn(a.getRowTo() - 1, a.getColumnTo() + 1).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					// this.loggGame.fine("Nero vince con re catturato in:
					// "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
				}
			}
			// nero-re-nero
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("K")
					&& state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")) {
				// mangio il re?
				if ((state.getPawn(a.getRowTo() + 1, a.getColumnTo() + 1).equalsPawn("T")
						|| (state.getPawn(a.getRowTo() + 1, a.getColumnTo() + 1).equalsPawn("B")))
						&& (state.getPawn(a.getRowTo() - 1, a.getColumnTo() + 1).equalsPawn("T")
								|| state.getPawn(a.getRowTo() - 1, a.getColumnTo() + 1).equalsPawn("B"))) {
					state.setTurn(State.Turn.BLACKWIN);
					// this.loggGame.fine("Nero vince con re catturato in:
					// "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
				}
			}
			// nero-bianco-trono/nero
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				this.movesWithutCapturing = -1;
				// this.loggGame.fine("Pedina bianca rimossa in:
				// "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
			}
		}
		// controllo se mangio a sinistra
		if (a.getColumnTo() > 1
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("K"))
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T"))) {
			// trono-re-nero
			if (state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("K")
					&& state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")) {
				// ho circondato il re?
				if (state.getPawn(a.getRowTo() + 1, a.getColumnTo() - 1).equalsPawn("B")
						&& state.getPawn(a.getRowTo() - 1, a.getColumnTo() - 1).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					// this.loggGame.fine("Nero vince con re catturato in:
					// "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
				}
			}
			// nero-re-nero
			if (state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("K")
					&& state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")) {
				// mangio il re?
				if ((state.getPawn(a.getRowTo() + 1, a.getColumnTo() - 1).equalsPawn("T")
						|| (state.getPawn(a.getRowTo() + 1, a.getColumnTo() - 1).equalsPawn("B")))
						&& (state.getPawn(a.getRowTo() - 1, a.getColumnTo() - 1).equalsPawn("T")
								|| state.getPawn(a.getRowTo() - 1, a.getColumnTo() - 1).equalsPawn("B"))) {
					state.setTurn(State.Turn.BLACKWIN);
					// this.loggGame.fine("Nero vince con re catturato in:
					// "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
				}
			}
			// trono/nero-bianco-nero
			if (state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
				this.movesWithutCapturing = -1;
				// this.loggGame.fine("Pedina bianca rimossa in:
				// "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
			}
		}
		// controllo se mangio sopra
		if (a.getRowTo() > 1
				&& (state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
						|| state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("K"))
				&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T"))) {
			// nero-re-trono
			if (state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("K")
					&& state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")) {
				// ho circondato re?
				if (state.getPawn(a.getRowTo() - 1, a.getColumnTo() - 1).equalsPawn("B")
						&& state.getPawn(a.getRowTo() - 1, a.getColumnTo() + 1).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					// this.loggGame.fine("Nero vince con re catturato in:
					// "+state.getBox(a.getRowTo()-1, a.getColumnTo()));
				}
			}
			// nero-re-nero
			if (state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("K")
					&& state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")) {
				// mangio il re?
				if ((state.getPawn(a.getRowTo() - 1, a.getColumnTo() - 1).equalsPawn("T")
						|| state.getPawn(a.getRowTo() - 1, a.getColumnTo() - 1).equalsPawn("B"))
						&& (state.getPawn(a.getRowTo() - 1, a.getColumnTo() + 1).equalsPawn("T")
								|| (state.getPawn(a.getRowTo() - 1, a.getColumnTo() + 1).equalsPawn("B")))) {
					state.setTurn(State.Turn.BLACKWIN);
					// this.loggGame.fine("Nero vince con re catturato in:
					// "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
				}
			}
			// nero-bianco-trono/nero
			if (state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")) {
				state.removePawn(a.getRowTo() - 1, a.getColumnTo());
				this.movesWithutCapturing = -1;
				// this.loggGame.fine("Pedina bianca rimossa in:
				// "+state.getBox(a.getRowTo()-1, a.getColumnTo()));
			}
		}
		// controllo se mangio sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& (state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
						|| state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("K"))
				&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T"))) {
			// nero-re-trono
			if (state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("K")
					&& state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")) {
				// ho circondato re?
				if (state.getPawn(a.getRowTo() + 1, a.getColumnTo() - 1).equalsPawn("B")
						&& state.getPawn(a.getRowTo() + 1, a.getColumnTo() + 1).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
					// this.loggGame.fine("Nero vince con re catturato in:
					// "+state.getBox(a.getRowTo()-1, a.getColumnTo()));
				}
			}
			// nero-re-nero
			if (state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("K")
					&& state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")) {
				// mangio il re?
				if ((state.getPawn(a.getRowTo() + 1, a.getColumnTo() - 1).equalsPawn("T")
						|| state.getPawn(a.getRowTo() + 1, a.getColumnTo() - 1).equalsPawn("B"))
						&& (state.getPawn(a.getRowTo() + 1, a.getColumnTo() + 1).equalsPawn("T")
								|| (state.getPawn(a.getRowTo() + 1, a.getColumnTo() + 1).equalsPawn("B")))) {
					state.setTurn(State.Turn.BLACKWIN);
					// this.loggGame.fine("Nero vince con re catturato in:
					// "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
				}
			}
			// nero-bianco-trono/nero
			if (state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")) {
				state.removePawn(a.getRowTo() + 1, a.getColumnTo());
				this.movesWithutCapturing = -1;
				// this.loggGame.fine("Pedina bianca rimossa in:
				// "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
			}
		}
		// controllo regola 11
		if (state.getBoard().length == 9) {
			if (a.getColumnTo() == 4 && a.getRowTo() == 2) {
				if (state.getPawn(3, 4).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K")
						&& state.getPawn(4, 3).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(5, 4).equalsPawn("B")) {
					state.removePawn(3, 4);
					this.movesWithutCapturing = -1;
					// this.loggGame.fine("Pedina bianca rimossa in:
					// "+state.getBox(3, 4));
				}
			}
			if (a.getColumnTo() == 4 && a.getRowTo() == 6) {
				if (state.getPawn(5, 4).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K")
						&& state.getPawn(4, 3).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(3, 4).equalsPawn("B")) {
					state.removePawn(5, 4);
					this.movesWithutCapturing = -1;
					// this.loggGame.fine("Pedina bianca rimossa in:
					// "+state.getBox(5, 4));
				}
			}
			if (a.getColumnTo() == 2 && a.getRowTo() == 4) {
				if (state.getPawn(4, 3).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K")
						&& state.getPawn(3, 4).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B")
						&& state.getPawn(4, 5).equalsPawn("B")) {
					state.removePawn(4, 3);
					this.movesWithutCapturing = -1;
					// this.loggGame.fine("Pedina bianca rimossa in:
					// "+state.getBox(4, 3));
				}
			}
			if (a.getColumnTo() == 6 && a.getRowTo() == 4) {
				if (state.getPawn(4, 5).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K")
						&& state.getPawn(4, 3).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B")
						&& state.getPawn(3, 4).equalsPawn("B")) {
					state.removePawn(4, 5);
					this.movesWithutCapturing = -1;
					// this.loggGame.fine("Pedina bianca rimossa in:
					// "+state.getBox(4, 5));
				}
			}
		}

		// controllo il pareggio
		if (this.movesWithutCapturing >= this.movesDraw
				&& (state.getTurn().equalsTurn("B") || state.getTurn().equalsTurn("W"))) {
			state.setTurn(State.Turn.DRAW);
			// this.loggGame.fine("Stabilito un pareggio per troppe mosse senza
			// mangiare");
		}
		this.movesWithutCapturing++;
		return state;
	}

	// TODO: Implement this
	@Override
	public void endGame(State state) {
	}

	@Override
	public void updateValidatorState(State state) {

	}

	@Override
	public ActionValidator getValidator() {
		return null;
	}

}
