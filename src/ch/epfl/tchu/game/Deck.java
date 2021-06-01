package ch.epfl.tchu.game;


import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Classe Deck.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public final class Deck<C extends Comparable<C>> {

    private List<C> cards;


    /** Construit un deck à partir de la liste de cartes.
     *
     * @param cards
     */

    private Deck(List<C> cards) {
        this.cards = cards;

    }

    /** Retourne un tas de cartes ayant
     *  les mêmes cartes que le multiensemble cards.
     *
     * @param cards
     *          deck de cartes rendu par le constructeur privé
     * @param rng
     *          Objet permettant de générer des nombres aléatoires
     * @return Deck<C>(card_deck)
     *          Deck possédant les mêmes cartes que précedémment
     *          mais mélangées grâce à rng
     *
     */


    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> card_deck = new ArrayList<>(cards.toList());

        Collections.shuffle(card_deck, rng);

        return new Deck<C>(card_deck);
    }


    /** Retourne la taille du tas.
     *
     * @return this.cards.size()
     *           taille du tas
     */

    public int size() {
        return this.cards.size();
    }

    /** Retourne vrai si le tas est vide.
     *
     * @return this.cards.isEmpty()
     */

    public boolean isEmpty() {
        return this.cards.isEmpty();
    }


    /** Retourne la carte au sommet du tas.
     *
     * @throws IllegalArgumentException()
     *          ssi le tas est vide
     * @return this.cards.get(0)
     */

    public C topCard() {
        Preconditions.checkArgument(!(isEmpty()));
        return this.cards.get(0);
    }

    /**
     * @throws IllegalArgumentException
     *            ssi le tas est vide
     * @return un tas identique au récepteur (this) mais sans la carte au sommet
     */

    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!(isEmpty()));

        List<C> CardDeck = List.copyOf(this.cards.subList(1,size()));

        return new Deck<C>(CardDeck);

    }

    /**
     * @param count
     * @throws IllegalArgumentException()
     *              ssi count n'est pas compris entre 0 (inclus)
     *              et la taille du tas (incluse)
     * @return retourne un multiensemble contenant
     *         les count cartes se trouvant au sommet du tas
     */

    public SortedBag<C> topCards(int count) {

        Preconditions.checkArgument(count >= 0);
        Preconditions.checkArgument(count <= size());

        List<C> topCards = List.copyOf(this.cards.subList(0,count));

        return SortedBag.of(topCards);
    }

    /**
     *
     * @param count
     * @throws IllegalArgumentException()
     *            ssi count n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     * @return retourne un tas identique au récepteur (this)
     *         mais sans les count cartes du sommet
     */

    public Deck<C> withoutTopCards(int count) {

        Preconditions.checkArgument(count >= 0);
        Preconditions.checkArgument(count <= size());

        List<C> remainingCards =List.copyOf(this.cards.subList(count,size()));

        return new Deck<C>(remainingCards);

    }

}
