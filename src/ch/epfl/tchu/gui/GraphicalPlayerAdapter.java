package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;
/**
 * Class GraphicalPlayerAdapter
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */
public class GraphicalPlayerAdapter implements Player {
    /**
     * Le constructeur de GraphicalPlayerAdapter ne prend aucun argument. En dehors de ce constructeur,
     * les seules méthodes publiques offertes par cette classe sont celles de l'interface Player.
     * Elles sont toutes constructeur compris destinées à être exécutée par un fil d'exécution différent du fil JavaFX.
     */
    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<SortedBag<Ticket>> ticketsQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<TurnKind> nextTurnQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Route> routeQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Integer> drawslotQueue=new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Card>> initialCardsQueue=new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Card>> additionalCardsQueue=new ArrayBlockingQueue<>(1);


    /**
     *  Instancie l'instance de graphicalPlayer aver les arguments
     * @param ownId id du jouer
     * @param playerNames tables des noms associe aux id
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(()-> graphicalPlayer=new GraphicalPlayer(ownId,playerNames));
    }

    /**
     *on appele la fonction recieve info de graphicalplayer
     * @param info string dinformation
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));

    }

    /**
     * on appele la fonction setstate de graphicalplayer
     * @param newState publicgamestate
     * @param ownState playerstate
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(()-> graphicalPlayer.setState(newState, ownState));

    }
    /**
     * on appelle la methode chosetickets de graphicalplayer
     * @param tickets multiensemble de tickets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(()-> graphicalPlayer.chooseTickets(tickets,    ticketsQueue::add));


    }
    /**
     *
     * @return l'element de la file bloquante de ticket
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {


        try {
            return ticketsQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * On ajoute des elements dans les queues bloquantes
     * @return  l'element de la file bloquante de turnkind
     */
    @Override
    public TurnKind nextTurn() {
        runLater(() -> graphicalPlayer.startTurn(drawCard -> {
            nextTurnQueue.add(TurnKind.DRAW_CARDS);
            drawslotQueue.add(drawCard);


        }, () -> nextTurnQueue.add(TurnKind.DRAW_TICKETS), ((route, cards) -> {
            nextTurnQueue.add(TurnKind.CLAIM_ROUTE);

            initialCardsQueue.add(cards);

            routeQueue.add(route);

        })));


        try {
            return nextTurnQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }


    /**
     * on  appele les methodes setInitialTicketChoice et donc retourne  chooseInitialTickets
     * @return les tickets introduits dans la queue bloquante ticketsQueue
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();

    }

    /**
     * @return l'integer de la file bloquante drawslotQueue
     */
    @Override
    public int drawSlot() {
        if(drawslotQueue.isEmpty()){
            runLater(()->graphicalPlayer.drawCard(drawslotQueue::add));
            try {
                return drawslotQueue.take();
            } catch (InterruptedException e) {
                throw new Error();
            }
        }
        else{
            return drawslotQueue.remove();
        }
    }
    /**
     * @return la route de la file bloquante  routeQueue
     */
    @Override
    public Route claimedRoute() {

        try {
            return routeQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
    /**
     * @return le multiensemble  de la file bloquante  initialCardsQueue
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return initialCardsQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     *
     * @param options list de multiensemble de cartes
     * @return le multiensemble  de la file bloquante  additionalCardsQueue
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(()->graphicalPlayer.chooseAdditionalCards(options, additionalCardsQueue::add));
        try {
            return additionalCardsQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}
