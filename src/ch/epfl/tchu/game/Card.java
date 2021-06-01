package ch.epfl.tchu.game;


import java.util.List;

/**
 * Classe énumerée carte.
 *
 * @author Elias Naha (326050)
 * @author Eliot Ullmo (312917)
 */



public enum Card {

    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null),
    VOLEUR(Color.VOLEUR);
    private final Color color;


    /**
     * Construit une carte avec la couleur donnée
     *
     * @param color
     *            la couleur
     */
    Card(Color color) {
        this.color = color;

    }

    public final static List<Card> ALL = List.of(Card.values());
    public final static int COUNT = ALL.size();


    public final static List<Card> CARS = ALL.subList(0, COUNT - 2);


    /**
     * Retourne la valeur de la carte dépendant de la couleur.
     * @return la valeur de la carte dépendant de la couleur.
     */

    public static Card of(Color color) {
        return ( color == null ? Card.LOCOMOTIVE : Card.valueOf(color.name()));

    }

    /**
     * Retourne la couleur de la carte.
     * @return la couleur de la carte.
     */



    public Color color() {
            return color;

    }


}
