package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/**
 * Classe PublicGameState.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */
public final class GameState extends PublicGameState {

    private final Deck<Ticket> tickets;
    private final Map<PlayerId, PlayerState> playerStateComplet;
    private final CardState cardState;

    /**
     * Construit un publicGameState
     *
     *
     * @param ticket
     *            nombre de tickets
     * @param cardState
     *            état public des cartes
     * @throws IllegalArgumentException
     *             si la taille de la pioche est strictement négative
     *             si playerState ne contient pas exactement pas deux paires clef/valeur
     * @throws NullPointerException
     *             si cardState ou currentPlayerId est null
     */

    private GameState(Deck<Ticket> ticket, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerStateComplet, PlayerId lastPlayer) {
        super(ticket.size(),cardState, currentPlayerId,Map.copyOf(playerStateComplet) , lastPlayer);
        this.playerStateComplet=Map.copyOf(playerStateComplet);
        this.tickets=ticket;
        this.cardState=cardState;

    }


    private static PublicCardState makePublic(CardState a){
        return new PublicCardState(a.faceUpCards(),a.deckSize(), a.discardsSize());
    }



    /**
     * Retourne l'état initial d'une partie de tCHu
     *
     *
     * @param tickets
     *         pioche des billets
     * @param rng
     *         générateur aléatoire
     * @throws IllegalArgumentException
     *
     * @return état initial d'une partie de tCHu
     */

    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        int random = rng.nextInt(PlayerId.COUNT);
        PlayerId currentPlayerId = PlayerId.ALL.get(random);

        //distribution des cartes
        Deck<Card> pioche = Deck.of(Constants.ALL_CARDS, rng);
        PlayerState pers1 = PlayerState.initial(pioche.topCards(Constants.INITIAL_CARDS_COUNT));
        Deck<Card> pioche1 = pioche.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        PlayerState pers2 = PlayerState.initial(pioche1.topCards(Constants.INITIAL_CARDS_COUNT));
        Deck<Card> piochedef = pioche1.withoutTopCards(Constants.INITIAL_CARDS_COUNT);

        CardState cardState = CardState.of(piochedef);


        //Creation de la hash map
        Map<PlayerId, PlayerState> e = new EnumMap<>(PlayerId.class);
        e.put(PlayerId.PLAYER_1, pers1);
        e.put(PlayerId.PLAYER_2, pers2);




        //cration de la pioche ticket
        Deck<Ticket> ticket = Deck.of(tickets, rng);




        return new GameState(ticket, cardState, currentPlayerId, e, null);

    }


    /**
     * Retourne l'état complet du joueur d'identité donnée et pas seulement sa partie publique.
     * @return l'état complet du joueur d'identité donnée et pas seulement sa partie publique.
     */

    @Override
    public  PlayerState playerState(PlayerId playerId){
        return this.playerStateComplet.get(playerId);
    }

    /**
     * Retourne l'état complet du joueur courant et pas seulement sa partie publique.
     * @return l'état complet du joueur courant et pas seulement sa partie publique.
     */
    @Override
    public PlayerState currentPlayerState() {


        return this.playerState(this.currentPlayerId());
    }


    /**
     * Retourne les count billets du sommet de la pioche.
     *
     * @param count
     *            nombre de billets à retourner du sommet de la pioche
     * @throws IllegalArgumentException
     *            count n'est pas compris entre 0 et la taille de pioche
     * @return les count billets du sommet de la pioche
     */
    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count>=0);
        Preconditions.checkArgument(count<=this.ticketsCount());
        return this.tickets.topCards(count);


    }


    /**
     * Retourne un état identique au récepteur, mais sans les count billets du sommet de la pioche
     *
     * @param count
     *            nombre de billets à retirer du sommet de la pioche
     * @throws IllegalArgumentException
     *            count n'est pas compris entre 0 et la taille de pioche
     * @return un état identique au récepteur, mais sans les count billets du sommet de la pioche
     */
    public GameState withoutTopTickets(int count){
        Preconditions.checkArgument(count>=0);
        Preconditions.checkArgument(count<=this.ticketsCount());

        Deck<Ticket> tick=this.tickets.withoutTopCards(count);
        return new GameState(tick,this.cardState,this.currentPlayerId(),this.playerStateComplet,this.lastPlayer());

    }

    /**
     * Retourne la carte au sommet de la pioche.
     *
     * @throws IllegalArgumentException
     *            si la pioche est vide.
     * @return la carte au sommet de la pioche.
     */
    public Card topCard(){
        Preconditions.checkArgument(!this.cardState().isDeckEmpty());
        return this.cardState.topDeckCard();
    }
    /**
     * Retourne un état identique au récepteur mais sans la carte au sommet de la pioche.
     *
     * @throws IllegalArgumentException
     *            si la pioche est vide.
     * @return un état identique au récepteur mais sans la carte au sommet de la pioche.
     */
    public GameState withoutTopCard(){
        Preconditions.checkArgument(!this.cardState().isDeckEmpty());

        CardState card=this.cardState.withoutTopDeckCard();
        return new GameState(this.tickets,card,this.currentPlayerId(),this.playerStateComplet,this.lastPlayer());



    }
    /**
     * Retourne un état identique au récepteur mais avec les cartes données ajoutées à la défausse.
     *
     * @param discardedCards
     *            cartes à ajouter à la défausse
     * @return un état identique au récepteur mais avec les cartes données ajoutées à la défausse.
     */

    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        CardState card=this.cardState.withMoreDiscardedCards(discardedCards);
        return new GameState(this.tickets,card,this.currentPlayerId(),this.playerStateComplet,this.lastPlayer());

    }
    /**
     * Retourne un état identique au récepteur sauf si la pioche de cartes est vide.
     *
     * @param rng
     *            générateur aléatoire
     * @return un état identique au récepteur sauf si la pioche de cartes est vide.
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        if (this.cardState.isDeckEmpty()) {
            CardState card = this.cardState.withDeckRecreatedFromDiscards(rng);
            return new GameState(this.tickets, card, this.currentPlayerId(), this.playerStateComplet, this.lastPlayer());

        } else {
            return this;
        }
    }
    /**
     * Retourne un état identique au récepteur mais dans lequel
     * les billets donnés ont été ajoutés à la main du joueur donné.
     *
     * @param playerId
     *            joueur donné
     * @param chosenTickets
     *            billets donnés
     * @throws IllegalArgumentException
     *            si le joueur possède déjà au moins un billet
     * @return un état identique au récepteur mais dans lequel
     *         les billets donnés ont été ajoutés à la main du joueur donné.
     */


    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(this.playerStateComplet.get(playerId).ticketCount() == 0);
        Map<PlayerId, PlayerState> playerStateComplet1 = new EnumMap<>(playerStateComplet);

        PlayerState player=playerState(playerId).withAddedTickets(chosenTickets);

        playerStateComplet1.put(playerId, player);
        return new GameState(this.tickets, this.cardState, this.currentPlayerId(), playerStateComplet1, this.lastPlayer());
    }
    /**
     * Retourne un état identique au récepteur, mais dans lequel le joueur courant
     * a tiré les billets drawnTickets du sommet de la pioche,
     * et choisi de garder ceux contenus dans chosenTicket.
     *
     * @param drawnTickets
     *            billets tirés
     * @param chosenTickets
     *            billets choisis
     * @throws IllegalArgumentException
     *            si l'ensemble des billets gardés n'est pas inclus dans celui des billets tirés.
     *
     * @return un état identique au récepteur, mais dans lequel le joueur courant
     *         a tiré les billets drawnTickets du sommet de la pioche,
     *         et choisi de garder ceux contenus dans chosenTicket.
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Deck<Ticket> tick = this.tickets.withoutTopCards(drawnTickets.size());


        PlayerState player = currentPlayerState().withAddedTickets(chosenTickets);
        Map<PlayerId, PlayerState> playerStateComplet1 = new EnumMap<>(playerStateComplet);
        playerStateComplet1.put(currentPlayerId(), player);

        return new GameState(tick, this.cardState, this.currentPlayerId(), playerStateComplet1, this.lastPlayer());

    }

    /**
     *  Retourne un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné
     *  a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche.
     *
     * @param slot
     *            emplacement de la carte retournée par le joueur
     * @throws IllegalArgumentException
     *            si canDrawCards retourne faux
     *
     * @return Retourne un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné
     *         a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche.
     */
    public GameState withDrawnFaceUpCard(int slot) {
       // Preconditions.checkArgument(this.canDrawCards());

        Card carte = this.cardState.faceUpCard(slot);
        CardState card = this.cardState.withDrawnFaceUpCard(slot);

        PlayerState player=this.currentPlayerState().withAddedCard(carte);

        Map<PlayerId, PlayerState> playerStateComplet1 = new EnumMap<>(playerStateComplet);
        playerStateComplet1.put(this.currentPlayerId(), player);

        return new GameState(this.tickets, card, this.currentPlayerId(), playerStateComplet1, this.lastPlayer());

    }
    /**
     *  Retourne un état identique au récepteur si ce n'est que la carte du sommet
     *  de la pioche a été placée dans la main du joueur courant.
     *
     * @throws IllegalArgumentException
     *            si canDrawCards retourne faux
     *
     * @return Retourne un état identique au récepteur si ce n'est que la carte du sommet
     *         de la pioche a été placée dans la main du joueur courant.
     */

    public GameState withBlindlyDrawnCard() {
       // Preconditions.checkArgument(this.canDrawCards());
        Card carte = this.cardState.topDeckCard();

        CardState card = this.cardState.withoutTopDeckCard();

        PlayerState player=this.currentPlayerState().withAddedCard(carte);

        Map<PlayerId, PlayerState> playerStateComplet1 = new EnumMap<>(playerStateComplet);
        playerStateComplet1.put(this.currentPlayerId(), player);

        return new GameState(this.tickets, card, this.currentPlayerId(), playerStateComplet1, this.lastPlayer());

    }

    /**
     *  Retourne un état identique au récepteur mais dans lequel le joueur courant
     *  s'est emparé de la route donnée au moyen des cartes données.
     *
     * @param route
     *            route dont le joueur souhaite s'emparer.
     * @param cards
     *            cartes données
     *
     * @return Retourne un état identique au récepteur mais dans lequel le joueur courant
     *         s'est emparé de la route donnée au moyen des cartes données. Ces cartes sont apres introduites dans les discarded
     */

    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {

        PlayerState player=this.currentPlayerState().withClaimedRoute(route, cards);

        Map<PlayerId, PlayerState> playerStateComplet1 = new EnumMap<>(playerStateComplet);
        playerStateComplet1.put(this.currentPlayerId(), player);

        CardState card=this.cardState.withMoreDiscardedCards(cards);
        return new GameState(this.tickets, card, this.currentPlayerId(), playerStateComplet1, this.lastPlayer());

    }

    /**
     * Retourne vrai si le denrier tour commence, c-à-d si l'identité du dernier joueur est
     * actuellement inconnue mais que le joueur courant n'a plus que deux wagons ou moins.
     *
     * @return Retourne vrai si le denrier tour commence, c-à-d si l'identité du dernier joueur est
     *         actuellement inconnue mais que le joueur courant n'a plus que deux wagons ou moins.
     */
    public boolean lastTurnBegins() {
        if (this.lastPlayer() == null) {
            if (this.playerStateComplet.get(this.currentPlayerId()).carCount() <= 2 || this.playerStateComplet.get(this.currentPlayerId().next()).carCount() <= 2 ) {
                return true;
            }

        }
        return false;
    }
    /**
     * Termine le tour du joueur courant : Retourne un état identique au récepteur
     * si ce n'est que le joueur courant est celui qui suit le joueur courant actuel.
     * De plus, si lastTurnBegins retourne vrai, le joueur courant actuel devient le dernier joueur.
     *
     * @return Termine le tour du joueur courant : Retourne un état identique au récepteur
     *         si ce n'est que le joueur courant est celui qui suit le joueur courant actuel.
     *         De plus, si lastTurnBegins retourne vrai, le joueur courant actuel devient le dernier joueur.
     */
    public GameState forNextTurn() {
        if (lastTurnBegins()) {
            PlayerId last = this.currentPlayerId();
            PlayerId current = this.currentPlayerId().next();
            return new GameState(this.tickets, this.cardState, current, this.playerStateComplet, last);


        }
        PlayerId current = this.currentPlayerId().next();

        return new GameState(this.tickets, this.cardState, current, this.playerStateComplet, this.lastPlayer());

    }




}
