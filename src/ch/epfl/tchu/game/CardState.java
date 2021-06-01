package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Classe CardState.
 *
 * @author Elias Naha (326050)
 * @author Eliot Ullmo (312917)
 */
public final class CardState extends PublicCardState {

    private Deck<Card> drawnCards;
    private SortedBag<Card> discard;

    /**
     *
     * @param faceUpCards
     * @param cards
     * @param discard
     * Construit un CardsTate
     */

    private CardState(List<Card> faceUpCards, Deck<Card> cards, SortedBag<Card> discard) {
        super(faceUpCards, cards.size(), discard.size());
        this.drawnCards = cards;
        this.discard = discard;
    }

    /**
     *
     * @param deck
     * @throws IllegalArgumentException si le tas donné contient moins de 5 cartes.
     * @return un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières du tas donné,
     *          la pioche est constituée des cartes du tas restantes, et la défausse est vide
     */
    public static CardState of(Deck<Card> deck) {

        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        List<Card> faceUpCards = deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList();

        Deck<Card> cards = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT);

        return new CardState(faceUpCards, cards, SortedBag.of());

    }

    /**
     *
     * @param slot
     * @throws  IndexOutOfBoundsException (!) si l'index donné n'est pas compris entre 0 (inclus) et 5 (exclus),
     * @throws IllegalArgumentException si la pioche est vide,
     * @return  un ensemble de cartes identique au récepteur (this),
     *          si ce n'est que la carte face visible d'index slot a été remplacée par celle se trouvant au sommet de la pioche,
     *          qui en est du même coup retirée;
     */
    public CardState withDrawnFaceUpCard(int slot) {

        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

        Preconditions.checkArgument(!this.drawnCards.isEmpty());

        List<Card> faceUpCards = new ArrayList<>(this.faceUpCards());

        faceUpCards.remove(slot);

        faceUpCards.add(slot,this.drawnCards.topCard());

        Deck<Card> cards = this.drawnCards.withoutTopCard();

        return new CardState(faceUpCards, cards, this.discard);
    }

    /**
     *@throws IllegalArgumentException si la pioche est vide
     * @return la carte se trouvant au sommet de la pioche,
     */

    public Card topDeckCard() {

        Preconditions.checkArgument(!this.drawnCards.isEmpty());

        return this.drawnCards.topCard();
    }

    /**
     * @throws IllegalArgumentException si la pioche est vide
     * @return un ensemble de cartes identique au récepteur (this),
     *          mais sans la carte se trouvant au sommet de la pioche
     */
    public CardState withoutTopDeckCard(){

        Preconditions.checkArgument(!this.drawnCards.isEmpty());


        return new CardState(this.faceUpCards(), this.drawnCards.withoutTopCard(), this.discard);
    }

    /**
     *
     * @param rng
     * @return  un ensemble de cartes identique au récepteur (this), si ce n'est que les cartes de la défausse ont été mélangées
     *          au moyen du générateur aléatoire donné afin de constituer la nouvelle pioche
     *
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(this.drawnCards.isEmpty());

        List<Card> discard = List.copyOf(this.discard.toList());

        return new CardState(this.faceUpCards(),Deck.of(SortedBag.of(discard),rng),SortedBag.of());

    }

    /**
     *
     * @param additionalDiscards
     * @return un ensemble de cartes identique au récepteur (this),
     *         mais avec les cartes données ajoutées à la défausse.
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        SortedBag<Card> discardNew = SortedBag.of(this.discard);
        return new CardState(this.faceUpCards(), this.drawnCards, discardNew.union(additionalDiscards));

    }

}