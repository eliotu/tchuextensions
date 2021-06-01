package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * Classe Player.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public interface Player {

    public abstract void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    public abstract void receiveInfo(String info);

    public abstract void updateState(PublicGameState newState, PlayerState ownState);

    public abstract void setInitialTicketChoice(SortedBag<Ticket> tickets);

    public abstract SortedBag<Ticket> chooseInitialTickets();

    public abstract TurnKind nextTurn();

    public abstract SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    public abstract int drawSlot();

    public abstract Route claimedRoute();

    public abstract SortedBag<Card> initialClaimCards();

    public abstract SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     * Classe énumérée imbriquée TurnKind.
     *
     * @author Elias Naha (326051)
     * @author Eliot Ullmo (312917)
     */

    public enum TurnKind {
        DRAW_TICKETS, // représente un tour durant lequel le joueur tire des billets.
        DRAW_CARDS,   // représente un tour durant lequel le joueur tire des cartes wagon/locomotive
        CLAIM_ROUTE;  // représente un tour durant lequel le joueur s'empare d'une route

        public final static List<TurnKind> ALL = List.of(TurnKind.values());


    }
}