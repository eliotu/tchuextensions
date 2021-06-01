package ch.epfl.tchu.game;

import java.util.List;


/**
 * Classe énumerée carte.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */
public enum Color {

    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE,
    VOLEUR;




    public final static List<Color> ALL = List.of(Color.values());

    public final static int COUNT = ALL.size();

}
