package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.util.StringConverter;

import java.util.ArrayList;

import static ch.epfl.tchu.gui.StringsFr.plural;
/**
 * class CardBagStringConverter
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    /**
     * ,
     * @param object multiensemble de cartes
     * @return transforme le multiensemble de cartes en chaînes de la manière adequate
     */
    @Override
    public String toString(SortedBag<Card> object) {
        String def = "";
        String tmp = "";
        ArrayList<String> phrases = new ArrayList<>();

        for (Card c : object.toSet()) {

            int n = object.countOf(c);

            def = n + " " + Info.cardName(c, n);
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
     * fonction jamais utilise
     * @param string
     * @throws new UnsupportedOperationException
     */
    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
