package ch.epfl.tchu;

/**
 * Classe Preconditions
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */


public final class Preconditions {
    private Preconditions() {}

    /**
     * Lève l'exception IllegalArgumentException si
     * son argument est faux et ne fait rien sinon.
     *
     * @param shouldBeTrue
     *            boolean
     * @throws IllegalArgumentException
     *             si son argument est faux
     */

    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}

