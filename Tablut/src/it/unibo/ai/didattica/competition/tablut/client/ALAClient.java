package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.GameTablut; //DA MODIFICARE CON QUELLO CHE POI SARà IL GAME
import it.unibo.ai.didattica.competition.tablut.player.ALAPlayer;
import it.unibo.ai.didattica.competition.tablut.player.ALABlackPlayer;
import it.unibo.ai.didattica.competition.tablut.player.ALAWhitePlayer;
import it.unibo.ai.didattica.competition.tablut.domain.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

public class ALAClient extends TablutClient{

    private final ALAPlayer alaPlayer;

    public ALAClient(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);

        GameTablut game = new GameTablut();

        if (this.getPlayer().equals(State.Turn.WHITE)) {
            alaPlayer = new ALAWhitePlayer();
        } else {
            alaPlayer = new ALABlackPlayer();
        }
    }

    public ALAClient(String player, int timeout, String ipAddress) throws UnknownHostException, IOException {
        this(player, "ALA", timeout, ipAddress);
    }

    public ALAClient(String player) throws UnknownHostException, IOException {
        this(player, 60, "localhost");
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        String role = "";
        String name = "ALA";
        String ipAddress = "localhost";
        int timeout = 60;
        // TODO: change the behavior?
        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.exit(-1);
        } else {
            System.out.println("Selected client: " + args[0]);
            role = (args[0]);
        }
        if (args.length >= 2) {
            timeout = Integer.parseInt(args[1]);
        }
        if (args.length == 3) {
            ipAddress = args[2];
        }

        System.out.println("Timeout: " + timeout + "s");

        ALAClient client = new ALAClient(role, name, timeout-1, ipAddress);
        client.run();
    }

    private void printALASignature() {
        System.out.println("ALA"); //-----------------------------------------------------------------------------------------------------------------------
    }

    private void sendActionToServer(Action a) {
        try {
            this.write(a);
            System.out.println("Action sent to server.");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        State state = new StateTablut();
        Game rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
        state.setTurn(State.Turn.WHITE);
        printALASignature();

        System.out.println(this.getName() + ", you are player " + this.getPlayer().toString() + "!");

        while(true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(1);
            }
            System.out.println("\nThe current state is:");
            state = this.getCurrentState();
            System.out.println(state.toString());

            if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                if (this.getPlayer().equals(State.Turn.BLACK)) {
                    System.out.println("WIN!");
                    System.exit(0);
                }
                System.out.println("LOSE!");
                System.exit(0);
            }

            if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                if (this.getPlayer().equals(State.Turn.WHITE)) {
                    System.out.println("WIN!");
                    System.exit(0);
                }
                System.out.println("LOSE!");
                System.exit(0);
            }

            if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                System.out.println("DRAW!");
                System.exit(0);
            }
            // Update validator with current state
            rules.updateValidatorState(state);

            if (this.getPlayer().equals(this.getCurrentState().getTurn())) {
                System.out.println("Your turn");
                // get legal actions using the validator
                List<Action> legalMoves = rules.getValidator().getLegalActions();
                if (legalMoves.isEmpty()) {
                    throw new IllegalStateException("No legal moves available");
                }
                alaPlayer.setState(state, legalMoves);
                Action selectedAction = alaPlayer.getNextMove();
                //Action selectedAction = legalMoves.get(new Random().nextInt(legalMoves.size()));
                System.out.println("Mossa scelta: " + selectedAction.toString());
                sendActionToServer(selectedAction);
            } else {
                System.out.println("Waiting for opponent ...");
            }
        }
    }
}
