package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.gui.StringsFr.EN_DASH_SEPARATOR;
import static ch.epfl.tchu.gui.StringsFr.plural;

/**
 * Classe Info.
 *
 * @author Elias Naha (326050)
 * @author Eliot Ullmo (312917)
 */

public final class Info {
    private String playerName;

    /**
     * @param playerName Construit un générateur de messages liés au joueur ayant le nom donné.
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * @param card
     * @param count
     * @return le nom (français) de la carte donnée,
     * au singulier ssi la valeur absolue du second argument vaut 1
     */
    public static String cardName(Card card, int count) {

        String cardFrenchName;

        switch (card) {
            case BLACK:
                cardFrenchName = StringsFr.BLACK_CARD;
                break;
            case BLUE:
                cardFrenchName = StringsFr.BLUE_CARD;
                break;
            case GREEN:
                cardFrenchName = StringsFr.GREEN_CARD;
                break;
            case ORANGE:
                cardFrenchName = StringsFr.ORANGE_CARD;
                break;
            case RED:
                cardFrenchName = StringsFr.RED_CARD;
                break;
            case VIOLET:
                cardFrenchName = StringsFr.VIOLET_CARD;
                break;
            case WHITE:
                cardFrenchName = StringsFr.WHITE_CARD;
                break;
            case YELLOW:
                cardFrenchName = StringsFr.YELLOW_CARD;
                break;
            case LOCOMOTIVE:
                cardFrenchName = StringsFr.LOCOMOTIVE_CARD;
                break;
            case VOLEUR:
                cardFrenchName="voleur";
                break;
            default:
                cardFrenchName = "Invalid card";
                break;
        }

        return cardFrenchName + plural(count);
    }

    /**
     * @param playerNames
     * @param points
     * @return le message déclarant que les joueurs, dont les noms sont ceux donnés,
     * ont terminé la partie ex æqo en ayant chacun remporté les points donnés
     */
    public static String draw(List<String> playerNames, int points) {
        String nom = "";
        for(int i=0;i<playerNames.size();++i){
            if(i==playerNames.size()-2){
                nom += playerNames.get(i) + StringsFr.AND_SEPARATOR;

            }else{
                nom+=playerNames.get(i);
            }
        }

        return (String.format(StringsFr.DRAW, nom, points));
    }

    /**
     * @return le message déclarant que le joueur jouera en premier
     */
    public String willPlayFirst() {
        return (String.format(StringsFr.WILL_PLAY_FIRST, this.playerName));

    }

    /**
     * @param count
     * @return le message déclarant que le joueur a gardé le nombre de billets donné
     */
    public String keptTickets(int count) {
        return (String.format(StringsFr.KEPT_N_TICKETS, this.playerName, count, StringsFr.plural(count)));

    }

    /**
     * @return le message déclarant que le joueur peut jouer
     */
    public String canPlay() {
        return (String.format(StringsFr.CAN_PLAY, this.playerName));
    }

    /**
     * @param count
     * @return qui retourne le message déclarant que le joueur a tiré le nombre donné de billets
     */
    public String drewTickets(int count) {
        return (String.format(StringsFr.DREW_TICKETS, this.playerName, count, StringsFr.plural(count)));

    }

    /**
     * @return le message déclarant que le joueur a tiré une carte «à l'aveugle»,
     * c-à-d du sommet de la pioche
     */
    public String drewBlindCard() {
        return (String.format(StringsFr.DREW_BLIND_CARD, this.playerName));
    }

    /**
     * @param card
     * @return le message déclarant que le joueur a tiré la carte disposée face visible donnée
     */
    public String drewVisibleCard(Card card) {
        return (String.format(StringsFr.DREW_VISIBLE_CARD, this.playerName, cardName(card, 1)));

    }

    /**
     * @param route
     * @return Un string montrant la route passée en parametre
     */
    private String printRoute(Route route) {
        String s = route.station1().name() + EN_DASH_SEPARATOR + route.station2().name();
        return s;
    }

    /**
     * @param cards
     * @return un string  montrant , l'ensemble de cartes formé
     * par les cards passées en parametre.
     * les paires multiplicité/nom de carte sont ordonnées selon l'ordre du type Card,
     */
    private String printCards(SortedBag<Card> cards) {
        String def = "";
        String tmp = "";
        ArrayList<String> phrases = new ArrayList<>();

        for (Card c : cards.toSet()) {

            int n = cards.countOf(c);

            def = n + " " + cardName(c, n);
            phrases.add(def);

        }
        for (int i = 0; i < phrases.size(); i++) {
            tmp += phrases.get(i);

            if (i == phrases.size() - 2) {
                tmp += StringsFr.AND_SEPARATOR;
            } else if (i != phrases.size() - 2 && i != phrases.size() - 1) {

                tmp += ", ";
            }
        }

        return tmp;
    }

    /**
     *
     * @param route
     * @param cards
     * @return  le message déclarant que le joueur s'est emparé de la route donnée
     *          au moyen des cartes données
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return (String.format(StringsFr.CLAIMED_ROUTE, this.playerName, printRoute(route), printCards(cards)));
    }

    /**
     *
     * @param route
     * @param initialCards
     * @return le message déclarant que le joueur désire s'emparer de la route en tunnel
     *          donnée en utilisant initialement les cartes données
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return (String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, this.playerName, printRoute(route), printCards(initialCards)));
    }

    /**
     *
     * @param drawnCards
     * @param additionalCost
     * @return le message déclarant que le joueur a tiré les trois cartes additionnelles données,
     *          et qu'elles impliquent un coût additionel du nombre de cartes donné
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        if (additionalCost != 0) {
            return (String.format(StringsFr.ADDITIONAL_CARDS_ARE, printCards(drawnCards)) + String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, plural(additionalCost)));
        } else {
            return (String.format(StringsFr.ADDITIONAL_CARDS_ARE, printCards(drawnCards)) + String.format(StringsFr.NO_ADDITIONAL_COST));
        }
    }

    /**
     *
     * @param route
     * @return le message déclarant que le joueur n'a pas pu (ou voulu) s'emparer du tunnel donné
     */
    public String didNotClaimRoute(Route route) {
        return (String.format(StringsFr.DID_NOT_CLAIM_ROUTE, this.playerName, printRoute(route)));
    }

    /**
     *
     * @param carCount
     * @return  le message déclarant que le joueur n'a plus que le nombre donné (et inférieur ou égale à 2) de wagons,
     *          et que le dernier tour commence donc (LAST_TURN_BEGINS),
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, plural(carCount));
    }

    /**
     *
     * @param longestTrail
     * @return le message déclarant que le joueur obtient le bonus de fin de partie grâce au chemin donné,
     *          qui est le plus long, ou l'un des plus longs
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return (String.format(StringsFr.GETS_BONUS, playerName,
                longestTrail.station1().name() + EN_DASH_SEPARATOR + longestTrail.station2().name()));
    }

    /**
     *
     * @param points
     * @param loserPoints
     * @return  le message déclarant que le joueur remporte la partie avec le nombre de points donnés,
     *          son adversaire n'en ayant obtenu que loserPoints.
     */
    public String won(int points, int loserPoints) {
        return (String.format(StringsFr.WINS, this.playerName, points, plural(points), loserPoints, plural(loserPoints)));
    }


}
