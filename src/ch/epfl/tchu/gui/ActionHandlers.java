package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
/**
 * Interface ActionHandlers
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */
public interface ActionHandlers {

    /**
     * ClaimRouteHandler
     */
    public interface ClaimRouteHandler{
        /**
         * Fonction appelée lorsque le joueur désire s'emparer de la route donnée au moyen des cartes (initiales) données,
         * @param route route qui veut etre claim
         * @param cards multiensemble de cartes utilises pour claim la route
         */
        public abstract void onClaimRoute(Route route,SortedBag<Card> cards);

    }

    /**
     * Interface DrawTicketsHandler  onDrawTickets et ne prenant aucun argument, est appelée lorsque le joueur désire tirer des billets,
     */
    public interface DrawTicketsHandler{
        /**
         * Fonction appelée lorsque le joueur désire tirer des billets,
         */
        public abstract void onDrawTickets();

    }

    /**
     * DrawCardHandler,
     */
    public interface DrawCardHandler{
        /**
         * Fonction prenant un numéro d'emplacement (0 à 4, ou -1 pour la pioche),
         *          est appelée lorsque le joueur désire tirer une carte de l'emplacement donné,
         * @param position  numéro d'emplacement (0 à 4, ou -1 pour la pioche)
         */
        public abstract void onDrawCard(int position);

    }

    /**
     * ChooseTicketsHandler
     */
    public interface ChooseTicketsHandler{
        /**
         * Fonction prenant un multiensemble de billets en argument
         *          , est appelée lorsque le joueur a choisi de garder les billets donnés
         *              suite à un tirage de billets,
         * @param tickets un multiensemble de billets
         */
        public abstract void onChooseTickets(SortedBag<Ticket> tickets);

    }

    /**
     * ChooseCardsHandler
     */
    public interface ChooseCardsHandler{
        /**
         * Fonction prenant en argument un multiensemble de cartes, est appelée lorsque le joueur a choisi d'utiliser les cartes données comme cartes initiales ou additionnelles
         *         lors de la prise de possession d'une route; s'il s'agit de cartes additionnelles,
         *         alors le multiensemble peut être vide, ce qui signifie que le joueur renonce à s'emparer du tunnel.
         * @param cards
         */
        public abstract void onChooseCards(SortedBag<Card> cards);

    }
}
