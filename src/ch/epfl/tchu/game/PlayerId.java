package ch.epfl.tchu.game;

import java.util.List;

/**
 * Classe énumérée PlayerId.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public enum PlayerId {
    PLAYER_1,
    PLAYER_2;

    public final static List<PlayerId> ALL = List.of(PlayerId.values());
    public final static int COUNT = ALL.size();

    /**
     *  Retourne l'identité du joueur
     *  qui suit celui auquel on l'applique.
     *
     * @return PLAYER_1
     *            si le joueur sur le lequel
     *            on applique la fonction est le joueur 2.
     * @return PLAYER_2
     *            si le joueur sur le lequel
     *            on applique la fonction est le joueur 1.
     */

    public PlayerId next() {
        return (this == PLAYER_1 ? PLAYER_2 : PLAYER_1);

    }

}
