package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * Classe PublicGameState.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public class PublicGameState {
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId,PublicPlayerState> playerState;
    private final PlayerId lastPlayer;


    /**
     * Construit un publicGameState
     *
     *
     * @param ticketsCount
     *            nombre de tickets
     * @param cardState
     *            état public des cartes
     *
     * @param currentPlayerId
     *            joueur courant
     * @param playerState
     *            état public des cartes
     * @param lastPlayer
     *            identité du denrier joueur
     *
     * @throws IllegalArgumentException
     *             si la taille de la pioche est strictement négative
     *             si playerState ne contient pas exactement pas deux paires clef/valeur
     * @throws NullPointerException
     *             si cardState ou currentPlayerId est null
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.playerState = playerState;
        this.lastPlayer = lastPlayer;

        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size()==2);

        if (cardState == null || currentPlayerId == null ) {
            throw new NullPointerException();
        }
    }
    /**
     * Retourne la taille de la pioche des billets
     * @return la taille de la pioche des billets.
     */


    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * Retourne vrai ssi il est possible de tirer des billets (pioche non-vide)
     * @return vrai ssi il est possible de tirer des billets.
     */

    public boolean canDrawTickets(){
        return(this.ticketsCount!=0);
    }
    /**
     * Retourne la partie publique de l'état des cartes wagon/locomotive.
     * @return la partie publique de l'état des cartes wagon/locomotive.
     */

    public PublicCardState cardState() {

        return cardState;
    }

    /**
     * Retourne vrai ssi il est possible de tirer des cartes, c-à-d
     * si la pioche et la défausse contiennent au moins 5 cartes
     *
     * @return vrai ssi il est possible de tirer des cartes
     */

    public boolean canDrawCards(){
        return(cardState.deckSize()+cardState.discardsSize()>=5);

    }
    /**
     * Retourne l'identité du joueur actuel.
     * @return l'identité du joueur actuel.
     */


    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }
    /**
     * Retourne la partie publique de l'état du joueur d'identité donnée.
     * @param playerId
     *          joueur d'identié donnée
     * @return la partie publique de l'état du joueur d'identité donnée.
     */
    public PublicPlayerState playerState(PlayerId playerId){
        return this.playerState.get(playerId);
    }

    /**
     * Retourne la partie publique de l'état du joueur courant.
     * @return la partie publique de l'état du joueur courant.
     */
    public PublicPlayerState currentPlayerState() {
        return this.playerState.get(this.currentPlayerId);
    }

    /**
     * Retourne la totalité des routes dont l'un ou l'autre joueur s'est emparé.
     *
     * @return la totalité des routes dont l'un ou l'autre joueur s'est emparé.
     */

    public List<Route> claimedRoutes(){
        List<Route> route=new ArrayList<>(this.playerState.get(this.currentPlayerId).routes());
        route.addAll(this.playerState.get(this.currentPlayerId.next()).routes());
        return route;

    }



    /**
     * Retourne l'identié du dernier joueur ou null si elle
     * n'est pas encore connue car le dernier tour n'a pas commencé
     *
     * @return l'identié du dernier joueur ou null si elle
     *         n'est pas encore connue car le dernier tour n'a pas commencé
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }



}
