package ch.epfl.tchu.game;


import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe PublicCardSTate.
 *
 * @author Elias Naha (326050)
 * @author Eliot Ullmo (312917)
 */

public class PublicCardState {
    private List<Card> faceUpCards;
    private int deckSize;
    private int discardsSize;

    /**
     * Construit une PublicCardState
     * @param faceUpCards
     * @param deckSize
     * @param discardsSize
     *
     * @throws IllegalArgumentException si decksize ou discardsSize sont negatifs
     *                                  ou si la taille de faceUpCards est different de 5
     */

    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size()==Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(deckSize >= 0);
        Preconditions.checkArgument(discardsSize >= 0);
        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;


    }



    /**
     *
     * @return  les 5 cartes face visible
     */

    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     *
     * @param slot
     * @throws IndexOutOfBoundsException  si cet index n'est pas compris entre 0 (inclus) et 5 (exclus),
     * @return la carte face visible à l'index donné,
     */
    public Card faceUpCard(int slot){
        Objects.checkIndex(slot,5);
        return faceUpCards.get(slot);
    }


    /**
     *
     * @return la taille de la pioche
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     *
     * @return  vrai ssi la pioche est vide
     */
    public boolean isDeckEmpty(){
        return (deckSize==0) ? true : false;
    }

    /**
     *
     * @return la taille de la défausse.
     */
    public int discardsSize() {
        return this.discardsSize;
    }
}
