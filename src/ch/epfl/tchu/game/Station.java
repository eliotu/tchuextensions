package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Classe Station.
 *
 * @author Elias Naha (326050)
 * @author Eliot Ullmo (312917)
 */


public class Station {



    /**
     * Construit une station avec son id et son nom
     *
     * @param id
     *            l'identifiant
     * @param name
     *            le nom
     * @throws IllegalArgumentException
     *             si l'id est negatif
     */

    private int id;
    private String name;

    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;

    }

    /**
     * Retourne l'identifiant.
     * @return l'identifiant
     */
    public int id() {
        return this.id;
    }


    /**
     * Retourne le nom.
     * @return le nom
     */
    public String name() {
        return name;
    }

    /**
     * Retourne le nom.
     * @return le nom
     */
    @Override
    public String toString() {
        return (this.name);
    }

}
