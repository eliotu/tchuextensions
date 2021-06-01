package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe Trip.
 *
 * @author Elias Naha (326050)
 * @author Eliot Ullmo (312917)
 */


public final class Trip implements StationConnectivity {

    /**
     * Construit un Voyage avec sa station de depart,
     * <p>
     * sa station d'arrivée et le nombre de points
     *
     * @param from
     * station de depart
     * @param to
     * sa station d'arrivée
     * @param points
     * le nombre de points
     * @throws IllegalArgumentException
     * si les points sont negatifs ou egale a 0
     * si les stations from et to ne sont pas nulles
     */


    private Station from;
    private Station to;
    private int points;

    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);

        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;


    }

    /**
     * Retourne une liste de tous les voyages.
     *
     * @return une liste de tous les voyages
     * @throws IllegalArgumentException si les gares de depart ont des noms differents
     */

    public static List<Trip> all(List<Station> from, List<Station> to, int points) {

        ArrayList<Trip> trips = new ArrayList<Trip>();
        Preconditions.checkArgument(from != null && to != null);
            for (Station depart : from) {
                for (Station arrive : to) {
                    trips.add(new Trip(depart, arrive, points));


                }
            }


        List<Trip> trip = trips;
        return trip;

    }

    /**
     * Retourne la station de depart.
     *
     * @return la station de depart.
     */
    public Station from() {
        return from;
    }


    /**
     * Retourne la station d'arrivee.
     *
     * @return la station d'arrivee.
     */
    public Station to() {
        return to;
    }


    /**
     * Retourne les points.
     *
     * @return les points.
     */

    public int points() {
        return points;
    }


    /**
     * Retourne les points.
     *
     * @param connectivity savoir si les stations sont connectées
     * @return les points.
     */
    public int points(StationConnectivity connectivity) {

        return (connectivity.connected(from, to) ? points : -points);
    }


    /**
     * Retourne si les stations sont connectées
     *
     * @return si les stations sont connectées
     * doit se faire a letape 4
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        return true;
    }

    public List<Station> stations(){
        List<Station> stations=new ArrayList<>();
        stations.add(this.from);
        stations.add(this.to);
        return stations;
    }

}




