package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Classe Ticket.
 *
 * @author Elias Naha (326051)
 * @author Eliot Ullmo (312917)
 */

public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> trips;
    private final String text;

    /**
     * Construit un billet constitué de la
     * liste de trajets.
     *
     * @param trips liste de trajets donnée
     * @throws IllegalArgumentException si la liste de trajets est nulle ou bien
     *                                  si toutes les gares de départ n'ont pas le
     *                                  même nom.
     */

    public Ticket(List<Trip> trips) {

        this.trips = trips;
        Preconditions.checkArgument(this.trips.size() != 0);


        for (int i = 1; i < trips.size(); i++) {
            Preconditions.checkArgument(trips.get(i).from().name().equals(trips.get(i - 1).from().name()));
        }

        this.text = computeText(this.trips);
    }

    /**
     * Construit un billet constitué d'un unique trajet.
     *
     * @param from   gare de départ
     * @param to     gare d'arrivée
     * @param points nombre de points correspondants au trajet
     */

    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }

    /**
     * Procède au calcul/à la structuration de la String qui va être remise
     * qui va être remise à la méthode text().
     *
     * @param trips liste de trajets
     * @return tripText
     * représentation textuelle du billet
     */


    private static String computeText(List<Trip> trips) {
        TreeSet<String> s = new TreeSet<>();

        for (Trip trip : trips) {
            String sTrip = String.format("%s (%s)", trip.to().name(), trip.points());
            s.add(sTrip);
        }

        String tripText = String.join(", ", s);
        String Text = trips.get(0).from().name();

        tripText = trips.size() == 1 ? Text + " - " + tripText
                : Text + " - {" + tripText + "}";

        return tripText;
    }


    public String text() {
        return this.text;
    }

    @Override
    public String toString() {
        return this.text;
    }

    /**
     * Permet de trouver le minimum de point
     * possible pour un billet.
     *
     * @return points
     * minimum de point possible pour un billet
     */

    private int min() {
        int points = trips.get(0).points();
        for (Trip trips : this.trips) {
            if (points > trips.points()) {
                points = trips.points();
            }

        }
        return points;
    }

    /**
     * Retourne le nombre de points que vaut le billet
     * que vaut le billet
     *
     * @param connectivity connectivité du joueur possédant le billet
     */

    public int points(StationConnectivity connectivity) {
        boolean connected = false;
        int points = 0;
        for (Trip trips : this.trips) {
            if (connectivity.connected(trips.from(), trips.to())) {
                connected = true;
                if (points < trips.points()) {
                    points = trips.points();
                }

            }
        }

        return connected ? points : -min();

    }

    /**
     * Compare le billet auquel on l'applique à
     * celui qui est passé en argument.
     *
     * @param o billet comparé
     */

    @Override
    public int compareTo(Ticket o) {
        return (this.text().compareTo(o.text()));
    }


    public boolean isConnected(StationConnectivity connectivity) {
        boolean connected = false;

        for (Trip trips : this.trips) {
            if (connectivity.connected(trips.from(), trips.to())) {
                connected = true;
            }
            //etape 12


        }
        return connected;

    }


    public List<Station> stations(){
        List<Station> stations1=new ArrayList<>();
        for(Trip trip:trips){
            stations1.addAll(trip.stations());
        }
        return stations1;
    }

}


